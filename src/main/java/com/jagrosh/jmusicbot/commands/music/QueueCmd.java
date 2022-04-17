/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.music;

import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.JMusicBot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class QueueCmd extends MusicCommand {

    public QueueCmd(Bot bot) {
        super(bot);
        this.name = "queue";
        this.help = "shows the current queue";
        this.arguments = "[pagenum]";
        this.aliases = new String[] { "q" };
        this.bePlaying = true;
        this.botPermissions = new Permission[] { Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EMBED_LINKS };
        // bot.
    }

    @Override
    public void doCommand(CommandEvent event) {
        int pagenum = 1;
        try {
            pagenum = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException ignore) {
        }

        AudioHandler ah = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        List<QueuedTrack> list = ah.getQueue().getList();

        if(list.isEmpty())
        {
            Message nowp = ah.getNowPlaying(event.getJDA());
            Message nonowp = ah.getNoMusicPlaying(event.getJDA());
            Message built = new MessageBuilder()
                    .setContent(event.getClient().getWarning() + " There is no music in the queue!")
                    .setEmbeds((nowp==null ? nonowp : nowp).getEmbeds().get(0)).build();
            event.reply(built, m -> 
            {
                if(nowp!=null)
                    bot.getNowplayingHandler().setLastNPMessage(m);
            });
            return;
        }

        if(pagenum > ah.getQueue().getNumberOfPages()){
            event.reply("That page does not exist. The maximum number of pages on the queue is " + String.valueOf(ah.getQueue().getNumberOfPages()) + ". Showing last page.");
            pagenum = ah.getQueue().getNumberOfPages();
        } 

        Settings settings = event.getClient().getSettingsFor(event.getGuild());

        ActionRow ar = ActionRow.of(QueueCmd.getButtonsForQueue(pagenum, ah.getQueue().getNumberOfPages(), settings.getRepeatMode(), event.getClient().getSuccess()));

        event.getChannel().sendMessage(new MessageBuilder().setEmbeds(getQueueEmbed(pagenum, event, ah, list))
                .setActionRows(ar).build()).queue();

    }

    private static String getQueueTitle(AudioHandler ah, String success, int songslength, long total, RepeatMode repeatmode) {
        StringBuilder sb = new StringBuilder();
        if (ah.getPlayer().getPlayingTrack() != null) {
            sb.append(ah.getPlayer().isPaused() ? JMusicBot.PAUSE_EMOJI : JMusicBot.PLAY_EMOJI).append(" **")
                    .append(ah.getPlayer().getPlayingTrack().getInfo().title).append("**\n");
        }
        return FormatUtil.filter(sb.append(success).append(" Current Queue | ").append(songslength)
                .append(" entries | `").append(FormatUtil.formatTime(total)).append("` ")
                .append(repeatmode.getEmoji() != null ? "| " + repeatmode.getEmoji() : "").toString());
    }

    private static MessageEmbed getQueueEmbed(int pagenum, CommandEvent event, AudioHandler ah, List<QueuedTrack> list){
        String[] songs = new String[list.size()];
        long total = 0;
        for(int i=0; i<list.size(); i++)
        {
            total += list.get(i).getTrack().getDuration();
            songs[i] = list.get(i).toString();
        }
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        int page_deficit = pagenum == ah.getQueue().getNumberOfPages() ? list.size() % 10 : 10;
        String[] this_page = Arrays.copyOfRange(songs, (10*(pagenum-1)), ((10*(pagenum-1)) + page_deficit));
        for(int i = 1; i < this_page.length + 1; i++) this_page[i-1] = String.valueOf(i + (10*(pagenum-1))) + ") " + this_page[i-1];
        return new EmbedBuilder()
                        .setTitle(QueueCmd.getQueueTitle(ah, event.getClient().getSuccess(), songs.length, total, settings.getRepeatMode()))
                        .setDescription(String.join("\n", this_page))
                        .build();
    }

    
    public static MessageEmbed getQueueEmbed(int pagenum, ButtonInteractionEvent event, AudioHandler ah, List<QueuedTrack> list, RepeatMode rm, String success_emoji){
        //Don't waste time by reacquiring them if this is being called from the queue command rather than the button.
        if(ah == null) ah = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
        if(list == null) list = ah.getQueue().getList();
        if(rm == null) rm = RepeatMode.OFF;
        
        //Return nullptr if there's nothing on the queue. This might happen if the queue is cleared whilst someone is browsing it.
        if(list.isEmpty()) return null;
        String[] songs = new String[list.size()];
        long total = 0;
        for(int i=0; i<list.size(); i++)
        {
            total += list.get(i).getTrack().getDuration();
            songs[i] = list.get(i).toString();
        }
        int page_deficit = pagenum == ah.getQueue().getNumberOfPages() ? list.size() % 10 : 10;
        String[] this_page = Arrays.copyOfRange(songs, (10*(pagenum-1)), ((10*(pagenum-1)) + page_deficit));
        for(int i = 1; i < this_page.length + 1; i++) this_page[i-1] = String.valueOf(i + (10*(pagenum-1))) + ") " + this_page[i-1];
        return new EmbedBuilder()
                        .setTitle(QueueCmd.getQueueTitle(ah, success_emoji, songs.length, total, rm))
                        .setDescription(String.join("\n", this_page))
                        .build();
    }

    public static ItemComponent[] getButtonsForQueue(int page_num, int max_queue_pages, RepeatMode rm, String success_emoji){
        ItemComponent[] btns = {null, null};
        btns[0] = page_num == 1 ? Button.secondary("QUEUE_PREV:DISABLED", "Previous").asDisabled() : Button.secondary("QUEUE_PREV:" + page_num + ":" + rm.toString() + ":" + success_emoji, "Previous").withEmoji(Emoji.fromUnicode("⬅️"));
        btns[1] = page_num == max_queue_pages ? Button.secondary("QUEUE_NEXT:DISABLED", "Next").asDisabled() : Button.secondary("QUEUE_NEXT:" + page_num + ":" + rm.toString() + ":" + success_emoji, "Next").withEmoji(Emoji.fromUnicode("➡️"));
        return btns;
    }
}
