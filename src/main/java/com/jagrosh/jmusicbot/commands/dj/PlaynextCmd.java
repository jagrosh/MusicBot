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
package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.PlayCommand;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jmusicbot.audio.QueuedTrack;

import net.dv8tion.jda.core.entities.Message;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class PlaynextCmd extends PlayCommand {
    public PlaynextCmd(Bot bot, String loadingEmoji) {
        super(bot, loadingEmoji);
        this.name = "playnext";
        this.help = "plays a single song next";
    }

    @Override
    public void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
            event.replyWarning("Please include a song title or URL!");
            return;
        }
        String args = getLoadingArgs(event);
        event.reply(loadingEmoji + " Loading... `[" + args + "]`", m -> bot.getPlayerManager()
                .loadItemOrdered(event.getGuild(), args, new ResultHandler(m, event, false)));
    }

    private class ResultHandler extends PlayResultHandler {
        public ResultHandler(Message m, CommandEvent event, boolean ytsearch) {
            super(m, event, ytsearch);
        }

        @Override
        protected void loadSingle(AudioTrack track)
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrackToFront(new QueuedTrack(track, event.getAuthor()))+1;
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()+" Added **"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) "+(pos==0?"to begin playing":" to the queue at position "+pos));
            m.editMessage(addMsg).queue();
        }
    }
}
