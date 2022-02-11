package com.jagrosh.jmusicbot.commands.owner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.settings.Settings;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
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
    this.arguments = "[guild: <guildname>|<guildid>|all] [(emojitype: success|warning|error|loading|searching) [emoji]]";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;

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
    String emoji = null;
    int iMessageType = -1;
    String args = event.getArgs().trim().toLowerCase();
    String argsMinusEnd = args.contains(" ") ? args.substring(0, args.lastIndexOf(" ")).trim() : args;
    if(!args.isEmpty()) {
      for (int i = 0; i < messageTypes.length; i++) {
        if (args.endsWith(messageTypes[i])) {
          iMessageType = i;
          args = args.substring(0, args.length() - messageTypes[i].length()).trim();
          break;
        } else if (argsMinusEnd.endsWith(messageTypes[i])) {
          iMessageType = i;
          emoji = args.substring(args.lastIndexOf(" ") + 1);
          args = argsMinusEnd.substring(0, argsMinusEnd.length() - messageTypes[i].length()).trim();
          break;
        }
      }
      if (!args.isEmpty()) {
        guildName = args;
      }
    }

    selectGuildAndContinue(event, guildName, iMessageType, emoji);
  }

  private void selectGuildAndContinue(CommandEvent event, String guildName, int iMessageType, String emoji) {
    boolean allGuilds = false;
    ArrayList<Guild> guildOptions = new ArrayList<Guild>();
    if (guildName != null) {
      if (guildName.equals("all")) {
        allGuilds = true;
      } else {
        for (Guild g : bot.getJDA().getGuilds()) {
          if (g.getName().toLowerCase().contains(guildName) || g.getId().toLowerCase().equals(guildName)) {
            guildOptions.add(g);
          }
        }
      }
    }
    final boolean fAllGuilds = allGuilds;

    if (fAllGuilds) {
      selectMessageTypeAndContinue(event, null, iMessageType, emoji);
    } else if (bot.getJDA().getGuilds().size() == 1) {
      Guild g = bot.getJDA().getGuilds().get(0);
      if (guildName != null && guildOptions.size() == 0) {
        event.reply(bot.getError(event)+" Only one guild is accessible ("+g.getName()+"), but its name does not match \""+guildName+"\".");
      } else {
        selectMessageTypeAndContinue(event, g, iMessageType, emoji);
      }
    } else {
      String message = null;
      if (guildName != null && guildOptions.size() == 0) {
        message = bot.getWarning(event)+" Couldn't find any guilds matching \""+guildName+"\".";
      } else if (guildOptions.size() > 1) {
        message = bot.getWarning(event)+" Multiple guilds match \""+guildName+"\".";
      }
  
      if (guildOptions.size() == 1) {
        selectMessageTypeAndContinue(event, guildOptions.get(0), iMessageType, emoji);
      } else if (!event.getChannelType().isGuild()) {
        message = message == null ? "" : message + "\n";
        event.reply(message+bot.getError(event)+" Include the guild name when using this command in a DM, because interactive menus don't work in DMs."); 
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
            selectMessageTypeAndContinue(event, g, iMessageType, emoji);
          })
          .useCancelButton(true)
          .build().display(event.getChannel());
      }
    }
  }

  private void selectMessageTypeAndContinue(CommandEvent event, Guild guild, int iMessageType, String emoji) {
    if (iMessageType != -1) {
      selectEmojiAndContinue(event, guild, iMessageType, emoji);
    } else if (!event.getChannelType().isGuild()) {
      event.reply(bot.getError(event)+" Include the emojitype when using this command in a DM, because interactive menus don't work in DMs."); 
    } else {
      new OrderedMenu.Builder()
        .setText(bot.getLoading(event)+" Select the emoji type to change on " + (guild == null ? "all guilds" : guild.getName()) + ":")
        .setChoices(messageTypes)
        .setEventWaiter(bot.getWaiter())
        .setTimeout(30, TimeUnit.SECONDS)
        .setSelection((m2, selectedIMessageType) -> {
          try { m2.clearReactions().queue(); } catch (PermissionException ignore) {}
          selectEmojiAndContinue(event, guild, selectedIMessageType - 1, emoji);
        })
        .useCancelButton(true)
        .build().display(event.getChannel());
    }
  }

  private void selectEmojiAndContinue(CommandEvent event, Guild guild, int iMessageType, String emoji)
  {
    if (emoji != null) {
      commitSelections(event, guild, iMessageType, emoji, null);
    } else if (!event.getChannelType().isGuild()) {
      event.reply(bot.getError(event)+" Include the emoji when using this command in a DM, because interactive menus don't work in DMs."); 
    } else {
      event.reply(bot.getLoading(event)+" React to this message with the \""+messageTypes[iMessageType]+"\" emoji you wish to use in "+(guild == null ? "all guilds" : guild.getName())+"...", m -> {
        bot.getWaiter().waitForEvent(MessageReactionAddEvent.class, 
          reactEvent -> reactEvent.getMessageId().equals(m.getId()) && reactEvent.getUserId().equals(event.getAuthor().getId()),
          reactEvent -> {
            // What happens next is after a valid event
            // is fired and processed above.
  
            String reactedEmoji = (reactEvent.getReaction().getReactionEmote().isEmoji())
              ? reactEvent.getReaction().getReactionEmote().getEmoji()
              : reactEvent.getReaction().getReactionEmote().getEmote().getAsMention();
  
            commitSelections(event, guild, iMessageType, reactedEmoji, m);
          },
          30, TimeUnit.SECONDS, () -> {
            m.delete().queue();
          });
      });
    }
  }

  private void commitSelections(CommandEvent event, Guild guild, int iMessageType, String emoji, Message m) 
  {
    // Do it
    if (guild == null) {
      for (Guild g : bot.getJDA().getGuilds()) {
        messageTypeSetters.get(iMessageType).accept(bot.getSettingsManager().getSettings(g), emoji);
      }
    } else {
      messageTypeSetters.get(iMessageType).accept(bot.getSettingsManager().getSettings(guild), emoji);
    }

    String messageText = bot.getSuccess(event)+" Set the \""+messageTypes[iMessageType]+"\" emoji in "+(guild == null ? "all guilds" : guild.getName())+" to: "+emoji;
    if (m != null) {
      m.editMessage(messageText).queue();
    } else {
      event.reply(messageText);
    }
  }
}
