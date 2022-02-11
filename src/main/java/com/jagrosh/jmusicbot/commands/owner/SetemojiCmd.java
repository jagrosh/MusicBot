package com.jagrosh.jmusicbot.commands.owner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.settings.Settings;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class SetemojiCmd extends OwnerCommand 
{

  private final Bot bot;

  public SetemojiCmd(Bot bot)
  {
    this.bot = bot;
    this.name = "setemoji";
    this.help = "sets an emoji that the bot uses";
    this.arguments = "[guild: <guildname>|<guildid>|all] [emojitype: success|warning|error|loading|searching]";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = true;

    messageTypeSetters = new ArrayList<BiConsumer<Settings, String>>();
    messageTypeSetters.add((set, val) -> set.setSuccess(val));
    messageTypeSetters.add((set, val) -> set.setWarning(val));
    messageTypeSetters.add((set, val) -> set.setError(val));
    messageTypeSetters.add((set, val) -> set.setLoading(val));
    messageTypeSetters.add((set, val) -> set.setSearching(val));
  }
  
  private final static String[] messageTypes = new String[] { "success", "warning", "error", "loading", "searching" };
  private final ArrayList<BiConsumer<Settings, String>> messageTypeSetters;

  @Override
  protected void execute(CommandEvent event) 
  {
    String guildName = null;
    int iMessageType = -1;
    String args = event.getArgs().trim().toLowerCase();
    if(!args.isEmpty()) {
      for (int i = 0; i < messageTypes.length; i++) {
        if (args.toLowerCase().endsWith(messageTypes[i])) {
          iMessageType = i;
          args = args.substring(0, args.length() - messageTypes[i].length()).trim();
          break;
        }
      }
      if (!args.isEmpty()) {
        guildName = args;
      }
    }

    boolean allGuilds = false;
    ArrayList<Guild> guildOptions = new ArrayList<Guild>();
    if (guildName != null) {
      if (guildName.equals("all")) {
        allGuilds = true;
      } else {
        for (Guild g : bot.getJDA().getGuilds()) {
          if (g.getName().toLowerCase().contains(guildName.toLowerCase()) || g.getId().toLowerCase().equals(guildName.toLowerCase())) {
            guildOptions.add(g);
          }
        }
      }
    }
    final boolean fAllGuilds = allGuilds;
    final int fIMessageType = iMessageType;
    final String fGuildName = guildName;

    if (fAllGuilds) {
      continueWithGuildSelection(event, null, fIMessageType);
    } else {
      String message = null;
      if (fGuildName != null && guildOptions.size() == 0) {
        message = bot.getWarning(event)+" Couldn't find any guilds matching \""+fGuildName+"\".";
      } else if (guildOptions.size() > 1) {
        message = bot.getWarning(event)+" Multiple guilds match \""+fGuildName+"\".";
      }
  
      if (guildOptions.size() == 1) {
        continueWithGuildSelection(event, guildOptions.get(0), fIMessageType);
      } else {
        if (guildOptions.size() == 0) {
          for (Guild g : bot.getJDA().getGuilds()) guildOptions.add(g);
        }

        String[] guildNameChoices = new String[guildOptions.size() + 1];
        int i = 0;
        for (Guild g : guildOptions) guildNameChoices[i++] = g.getName();
        guildNameChoices[guildNameChoices.length - 1] = "all guilds";

        if (message == null) message = "";
        else message += "\n";
        message += bot.getLoading(event)+" Select the target guild of this change:";

        new OrderedMenu.Builder()
          .setText(message)
          .setChoices(guildNameChoices)
          .setEventWaiter(bot.getWaiter())
          .setTimeout(30, TimeUnit.SECONDS)
          .setSelection((m2, iGuild) -> {
            Guild g;
            if (iGuild == guildNameChoices.length) g = null;
            else g = guildOptions.get(iGuild - 1);
            continueWithGuildSelection(event, g, fIMessageType);
          })
          .useCancelButton(true)
          .build().display(event.getChannel());
      }
    }
  }

  private void continueWithGuildSelection(CommandEvent event, Guild guild, int iMessageType) {
    if (iMessageType != -1) {
      continueWithAllSelections(event, guild, iMessageType);
    } else {
      new OrderedMenu.Builder()
        .setText(bot.getLoading(event)+" Select the emoji type to change on " + (guild == null ? "all guilds" : guild.getName()) + ":")
        .setChoices(messageTypes)
        .setEventWaiter(bot.getWaiter())
        .setTimeout(30, TimeUnit.SECONDS)
        .setSelection((m2, selectedIMessageType) -> {
          try { m2.clearReactions().queue(); } catch (PermissionException ignore) {}
          continueWithAllSelections(event, guild, selectedIMessageType - 1);
        })
        .useCancelButton(true)
        .build().display(event.getChannel());
    }
  }

  private void continueWithAllSelections(CommandEvent event, Guild guild, int iMessageType)
  {
    event.reply(bot.getLoading(event)+" React to this message with the \""+messageTypes[iMessageType]+"\" emoji you wish to use in "+(guild == null ? "all guilds" : guild.getName())+"...", m -> {
      bot.getWaiter().waitForEvent(MessageReactionAddEvent.class, 
        reactEvent -> reactEvent.getMessageId().equals(m.getId()) && reactEvent.getUserId().equals(event.getAuthor().getId()),
        reactEvent -> {
          // What happens next is after a valid event
          // is fired and processed above.

          String re = (reactEvent.getReaction().getReactionEmote().isEmoji())
            ? reactEvent.getReaction().getReactionEmote().getEmoji()
            : reactEvent.getReaction().getReactionEmote().getEmote().getAsMention();

          // Preform the specified action with the ReactionEmote
          if (guild == null) {
            for (Guild g : bot.getJDA().getGuilds()) {
              messageTypeSetters.get(iMessageType).accept(bot.getSettingsManager().getSettings(g), re);
            }
          } else {
            messageTypeSetters.get(iMessageType).accept(bot.getSettingsManager().getSettings(guild), re);
          }
          
          m.editMessage(bot.getSuccess(event)+" Set the \""+messageTypes[iMessageType]+"\" emoji in "+(guild == null ? "all guilds" : guild.getName())+" to: "+re).queue();
        },
        30, TimeUnit.SECONDS, () -> {
          m.delete().queue();
        });
    });
  }
}
