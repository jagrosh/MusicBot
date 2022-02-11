package com.jagrosh.jmusicbot.commands.owner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.settings.Settings;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class SetemojiCmd extends OwnerCommand 
{
  private final Bot bot;

  private final static String[] messageTypes = new String[] { "success", "warning", "error", "loading", "searching" };
  private final ArrayList<Function<Settings, Settings.EmojiOption[]>> messageTypeGetters;
  private final ArrayList<BiConsumer<Settings, Settings.EmojiOption[]>> messageTypeSetters;

  public SetemojiCmd(Bot bot)
  {
    this.bot = bot;
    this.name = "setemoji";
    this.help = "sets an emoji that the bot uses";
    this.arguments = "[guild: <guildname>|<guildid>|all] [<emojitype: success|warning|error|loading|searching> [emoji] [emoji] [emoji] ...]";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;

    messageTypeGetters = new ArrayList<Function<Settings, Settings.EmojiOption[]>>();
    messageTypeGetters.add((set) -> set.getSuccessEmojis());
    messageTypeGetters.add((set) -> set.getWarningEmojis());
    messageTypeGetters.add((set) -> set.getErrorEmojis());
    messageTypeGetters.add((set) -> set.getLoadingEmojis());
    messageTypeGetters.add((set) -> set.getSearchingEmojis());

    messageTypeSetters = new ArrayList<BiConsumer<Settings, Settings.EmojiOption[]>>();
    messageTypeSetters.add((set, val) -> set.setSuccessEmojis(val));
    messageTypeSetters.add((set, val) -> set.setWarningEmojis(val));
    messageTypeSetters.add((set, val) -> set.setErrorEmojis(val));
    messageTypeSetters.add((set, val) -> set.setLoadingEmojis(val));
    messageTypeSetters.add((set, val) -> set.setSearchingEmojis(val));
  }

  @Override
  protected void execute(CommandEvent event) 
  {
    String guildName = null;
    String[] emojis = null;
    int iMessageType = -1;
    String args = event.getArgs().trim().toLowerCase();
    String[] argsParts = args.split("\\s+");
    if(!args.isEmpty()) {
      for (int i = 0; i < messageTypes.length; i++) {
        for (int j = argsParts.length - 1; j >= 0; j--) {
          if (argsParts[j].equals(messageTypes[i])) {
            iMessageType = i;
            if (j < argsParts.length - 1) {
              emojis = new String[argsParts.length - j - 1];
              for (int k = 0; k < emojis.length; k++) {
                emojis[k] = argsParts[j+1+k];
              }
            }
            String[] remainingParts = new String[j];
            for (int k = 0; k < j; k++) remainingParts[k] = argsParts[k];
            args = String.join(" ", remainingParts);
            break;
          }
        }
      }
      if (!args.isEmpty()) {
        guildName = args;
      }
    }

    selectGuildAndContinue(event, guildName, iMessageType, emojis);
  }

  private void selectGuildAndContinue(CommandEvent event, String guildName, int iMessageType, String[] emojis) {
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
      selectMessageTypeAndContinue(event, null, iMessageType, emojis);
    } else if (bot.getJDA().getGuilds().size() == 1) {
      Guild g = bot.getJDA().getGuilds().get(0);
      if (guildName != null && guildOptions.size() == 0) {
        event.reply(bot.getError(event)+" Only one guild is accessible ("+g.getName()+"), but its name does not match \""+guildName+"\".");
      } else {
        selectMessageTypeAndContinue(event, g, iMessageType, emojis);
      }
    } else {
      String message = null;
      if (guildName != null && guildOptions.size() == 0) {
        message = bot.getWarning(event)+" Couldn't find any guilds matching \""+guildName+"\".";
      } else if (guildOptions.size() > 1) {
        message = bot.getWarning(event)+" Multiple guilds match \""+guildName+"\".";
      }
  
      if (guildOptions.size() == 1) {
        selectMessageTypeAndContinue(event, guildOptions.get(0), iMessageType, emojis);
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
            selectMessageTypeAndContinue(event, g, iMessageType, emojis);
          })
          .useCancelButton(true)
          .build().display(event.getChannel());
      }
    }
  }

  private void selectMessageTypeAndContinue(CommandEvent event, Guild guild, int iMessageType, String[] emojis) {
    if (iMessageType != -1) {
      loadExistingEmojisAndContinue(event, guild, iMessageType, emojis);
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
          loadExistingEmojisAndContinue(event, guild, selectedIMessageType - 1, emojis);
        })
        .useCancelButton(true)
        .build().display(event.getChannel());
    }
  }

  private void loadExistingEmojisAndContinue(CommandEvent event, Guild guild, int iMessageType, String[] emojis) {
    String[] existingEmojis = null;
    if (guild == null) {
      for (Guild g : bot.getJDA().getGuilds()) {
        Settings.EmojiOption[] guildEmojis = messageTypeGetters.get(iMessageType).apply(bot.getSettingsManager().getSettings(g));
        if (existingEmojis == null) {
          if (guildEmojis != null) {
            existingEmojis = new String[guildEmojis.length];
            for (int i = 0; i < existingEmojis.length; i++) existingEmojis[i] = guildEmojis[i].emoji;
          }
        } else {
          int newCount = 0;
          boolean[] founds = new boolean[existingEmojis.length];
          for (int i = 0; i < existingEmojis.length; i++) {
            boolean found = false;
            for (int j = 0; j < guildEmojis.length; j++) {
              if (existingEmojis[i].equals(guildEmojis[j].emoji)) {
                found = true;
                break;
              }
            }
            founds[i] = found;
            if (found) newCount++;
          }
          if (newCount != existingEmojis.length) {
            String[] oldEmojis = existingEmojis;
            existingEmojis = new String[newCount];
            int j = 0;
            for (int i = 0; i < oldEmojis.length; i++) {
              if (founds[i]) {
                existingEmojis[j++] = oldEmojis[i];
              }
            }
          }
        }
      }
    } else {
      Settings.EmojiOption[] guildEmojis = messageTypeGetters.get(iMessageType).apply(bot.getSettingsManager().getSettings(guild));
      if (guildEmojis == null) existingEmojis = null;
      else {
        existingEmojis = new String[guildEmojis.length];
        for (int i = 0; i < existingEmojis.length; i++) existingEmojis[i] = guildEmojis[i].emoji;
      }
    }
    if (existingEmojis == null) existingEmojis = new String[0];

    selectEmojiAndContinue(event, guild, iMessageType, existingEmojis, emojis, null, null, null);
  }

  private static class AtomicFlag {
    private boolean flag = false;
    public synchronized boolean setAsFirst() { 
      if (flag) return false; 
      flag = true; 
      return true;
    }
  }

  private boolean isAvailable(String emoji) {
    if (emoji.startsWith("<") && emoji.endsWith(">")) {
      List<Emote> availableEmotes = bot.getJDA().getEmotes();
      for (Emote availableEmote : availableEmotes) {
        if (availableEmote.getAsMention().toLowerCase().equals(emoji.toLowerCase())) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  private void selectEmojiAndContinue(CommandEvent event, Guild guild, int iMessageType, String[] existingEmojis, String[] newEmojis, Message m, String mText, String removeIgnore)
  {
    if (newEmojis != null) {
      updateEmojis(event, guild, iMessageType, existingEmojis, true, newEmojis, null);
    } else if (!event.getChannelType().isGuild()) {
      String text = bot.getError(event)+" Include the emoji when using this command in a DM, because interactive menus don't work in DMs.";
      if (m != null) {
        m.editMessage(text).queue();
      } else {
        event.reply(text); 
      }
    } else {
      Consumer<Message> addReactHandlers = (m2) -> {
        final AtomicFlag firedFlag = new AtomicFlag();
        bot.getWaiter().waitForEvent(MessageReactionAddEvent.class, 
          reactEvent -> { 
            if (!reactEvent.getMessageId().equals(m2.getId()) || !reactEvent.getUserId().equals(event.getAuthor().getId())) return false;
            String reactedEmoji = (reactEvent.getReaction().getReactionEmote().isEmoji())
              ? reactEvent.getReaction().getReactionEmote().getEmoji()
              : reactEvent.getReaction().getReactionEmote().getEmote().getAsMention().toLowerCase();
            return isAvailable(reactedEmoji);
          },          
          reactEvent -> {
            // What happens next is after a valid event is fired and processed above.
            if (!firedFlag.setAsFirst()) return;

            String reactedEmoji = (reactEvent.getReaction().getReactionEmote().isEmoji())
            ? reactEvent.getReaction().getReactionEmote().getEmoji()
            : reactEvent.getReaction().getReactionEmote().getEmote().getAsMention().toLowerCase();

            updateEmojis(event, guild, iMessageType, existingEmojis, false, new String[] { reactedEmoji }, m2);
          },
          30, TimeUnit.SECONDS, () -> {});
        bot.getWaiter().waitForEvent(MessageReactionRemoveEvent.class, 
          reactEvent -> { 
            if (!reactEvent.getMessageId().equals(m2.getId()) || !reactEvent.getUserId().equals(event.getAuthor().getId())) return false;
            String reactedEmoji = (reactEvent.getReaction().getReactionEmote().isEmoji())
              ? reactEvent.getReaction().getReactionEmote().getEmoji()
              : reactEvent.getReaction().getReactionEmote().getEmote().getAsMention().toLowerCase();
            if (removeIgnore != null && reactedEmoji.equals(removeIgnore)) return false;
            return isAvailable(reactedEmoji);
          },
          reactEvent -> {
            // What happens next is after a valid event is fired and processed above.
            if (!firedFlag.setAsFirst()) return;

            String reactedEmoji = (reactEvent.getReaction().getReactionEmote().isEmoji())
              ? reactEvent.getReaction().getReactionEmote().getEmoji()
              : reactEvent.getReaction().getReactionEmote().getEmote().getAsMention().toLowerCase();

            updateEmojis(event, guild, iMessageType, existingEmojis, false, new String[] { reactedEmoji }, m2);
          },
          30, TimeUnit.SECONDS, () -> {});
      };

      String text = mText == null ? "" : mText + "\n";
      text += bot.getLoading(event)+" React to this message to add a new emoji "+(existingEmojis != null && existingEmojis.length > 0 ? "or remove an existing emoji " : "")+"for \""+messageTypes[iMessageType]+"\" in "+(guild == null ? "all guilds" : guild.getName())+".";
      if (existingEmojis != null && existingEmojis.length != 0) {
        text += " (currently "+String.join(", ", existingEmojis)+")";
      }

      if (m == null) {
        event.reply(text, m2 -> {
          addReactHandlers.accept(m2);
          if (existingEmojis != null && existingEmojis.length != 0) {
            List<Emote> availableEmotes = bot.getJDA().getEmotes();
            for (String existingEmoji : existingEmojis) {
              if (existingEmoji.startsWith("<") && existingEmoji.endsWith(">")) {
                for (Emote availableEmote : availableEmotes) {
                  if (availableEmote.getAsMention().toLowerCase().equals(existingEmoji.toLowerCase())) {
                    m2.addReaction(availableEmote).queue();
                    break;
                  }
                }
              } else {
                m2.addReaction(existingEmoji).queue();
              }
            }
          }
        });
      } else {
        m.editMessage(text).queue();
        addReactHandlers.accept(m);
      }
    }
  }

  /**
   * @param add True to add all the emojis. False to toggle them.
   */
  private void updateEmojis(CommandEvent event, Guild guild, int iMessageType, String[] existingEmojis, boolean add, String[] emojis, Message m) 
  {
    // Do it
    boolean added = false;
    ArrayList<String> emojisChanged = new ArrayList<String>();

    List<Guild> singleGuild = new ArrayList<Guild>();
    singleGuild.add(guild);
    
    for (Guild g : (guild == null ? bot.getJDA().getGuilds() : singleGuild)) {
      Settings settings = bot.getSettingsManager().getSettings(g);
      Settings.EmojiOption[] existingGuildEmojis = messageTypeGetters.get(iMessageType).apply(settings);
      if (existingGuildEmojis == null) existingGuildEmojis = new Settings.EmojiOption[0];
      int targetedExistingCount = 0;
      boolean[] emojiExists = new boolean[emojis.length];
      boolean[] existingEmojiTargeted = new boolean[existingGuildEmojis.length];
      if (existingGuildEmojis != null && existingGuildEmojis.length != 0) {
        for (int i = 0; i < existingGuildEmojis.length; i++) {
          for (int j = 0; j < emojis.length; j++) {
            if (existingGuildEmojis[i].emoji.equals(emojis[j])) {
              targetedExistingCount++;
              emojiExists[j] = true;
              existingEmojiTargeted[i] = true;
              break;
            }
          }
        }
      }

      Settings.EmojiOption[] newEmojis = new Settings.EmojiOption[existingGuildEmojis.length + emojis.length - targetedExistingCount + (add ? 0 : -targetedExistingCount)];
      int k = 0;
      double existingWeightSum = 0;
      for (int i = 0; i < existingGuildEmojis.length; i++) {
        if (add || !existingEmojiTargeted[i]) {
          newEmojis[k++] = existingGuildEmojis[i];
          existingWeightSum += existingGuildEmojis[i].weight;
          if (add && existingEmojiTargeted[i]) added = true;
        } else {
          emojisChanged.add(existingGuildEmojis[i].emoji);
        }
      }
      double addWeight = k == 0 ? 1 : existingWeightSum / k;
      for (int j = 0; j < emojis.length; j++) {
        if (!emojiExists[j]) { // if adding, then we should add missing emojis. if toggling, then we should add missing emojis.
          newEmojis[k++] = new Settings.EmojiOption(emojis[j], addWeight);
          emojisChanged.add(emojis[j]);
          added = true;
        }
      }

      messageTypeSetters.get(iMessageType).accept(bot.getSettingsManager().getSettings(g), newEmojis);
    }

    ArrayList<String> unionEmojis = new ArrayList<String>();
    for (int i = 0; i < existingEmojis.length; i++) {
      boolean targeted = false;
      for (int j = 0; j < emojis.length; j++) {
        if (existingEmojis[i].equals(emojis[j])) {
          targeted = true;
          break;
        }
      }
      if (add || !targeted) {
        unionEmojis.add(existingEmojis[i]);
      }
    }
    for (int j = 0; j < emojis.length; j++) {
      boolean exists = false;
      for (int i = 0; i < existingEmojis.length; i++) {
        if (existingEmojis[i].equals(emojis[j])) {
          exists = true;
          break;
        }
      }
      if (!exists) {
        unionEmojis.add(emojis[j]);
      }
    }
    String[] unionEmojisArr = Arrays.copyOf(unionEmojis.toArray(), unionEmojis.size(), String[].class);

    final boolean fAdded = added;

    BiConsumer<Message, Consumer<String>> updateReactions = (m2, callback) -> {
      if (emojisChanged.size() != 0) {
        List<Emote> availableEmotes = bot.getJDA().getEmotes();
        for (String existingEmoji : emojisChanged) {
          if (existingEmoji.startsWith("<") && existingEmoji.endsWith(">")) {
            for (Emote availableEmote : availableEmotes) {
              if (availableEmote.getAsMention().toLowerCase().equals(existingEmoji.toLowerCase())) {
                if (fAdded) {
                  m2.addReaction(availableEmote).queue();
                } else {
                  m2.removeReaction(availableEmote).queue();
                }
                if (m != null) { // this reaction came from a user event
                  try { m2.removeReaction(availableEmote, event.getAuthor()).queue((v) -> callback.accept(existingEmoji)); } catch (PermissionException ignore) { callback.accept(null); }
                }
                break;
              }
            }
          } else {
            if (fAdded) {
              m2.addReaction(existingEmoji).queue();
            } else {
              m2.removeReaction(existingEmoji).queue();
            }
            if (m != null) { // this reaction came from a user event
              try { m2.removeReaction(existingEmoji, event.getAuthor()).queue((v) -> callback.accept(existingEmoji)); } catch (PermissionException ignore) { callback.accept(null); }
            }
          }
        }
      }
    };

    String messageText = bot.getSuccess(event)+" "+(added ? "Added" : "Removed")+": "+String.join(", ", emojis)+".";
    if (m != null) {
      updateReactions.accept(m, (removeIgnore) -> selectEmojiAndContinue(event, guild, iMessageType, unionEmojisArr, null, m, messageText, removeIgnore));
    } else {
      event.reply(messageText, m2 -> {
        if (add) updateReactions.accept(m2, (removeIgnore) -> selectEmojiAndContinue(event, guild, iMessageType, unionEmojisArr, null, m2, messageText, removeIgnore));
        else selectEmojiAndContinue(event, guild, iMessageType, unionEmojisArr, null, m2, messageText, null);
      });
    }
  }
}
