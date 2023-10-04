/*
 * Copyright 2023 まったりにほんご
 *
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.commands.MusicCommand;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SkipCmd extends MusicCommand {
    public SkipCmd(Bot bot) {
        super(bot);
        this.name = "skip";
        this.help = "再生中の曲をスキップする要求を出します。";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        RequestMetadata rm = handler.getRequestMetadata();
        if (event.getAuthor().getIdLong() == rm.getOwner()) {
            event.reply(event.getClient().getSuccess() + " **" + handler.getPlayer().getPlayingTrack().getInfo().title
                    + "**をスキップしました。");
            handler.getPlayer().stopTrack();
        } else {
            int listeners = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> !m.getUser().isBot() && !m.getVoiceState().isDeafened()).count();
            String msg;
            if (handler.getVotes().contains(event.getAuthor().getId()))
                msg = event.getClient().getWarning() + " あなたはすでに同意しています。 `[";
            else {
                msg = event.getClient().getSuccess() + " スキップする要求を出しました！ `[";
                handler.getVotes().add(event.getAuthor().getId());
            }
            int skippers = (int) event.getSelfMember().getVoiceState().getChannel().getMembers().stream()
                    .filter(m -> handler.getVotes().contains(m.getUser().getId())).count();
            int required = (int) Math
                    .ceil(listeners * bot.getSettingsManager().getSettings(event.getGuild()).getSkipRatio());
            msg += skippers + " 同意数, " + required + "/" + listeners + " 必要数]`";
            if (skippers >= required) {
                msg += "\n" + event.getClient().getSuccess()
                        + (rm.getOwner() == 0L ? "(autoplay)"
                                : "**" + rm.user.username + "**" + "さんに要求された曲:**"
                                        + handler.getPlayer().getPlayingTrack().getInfo().title + "**をスキップしました。");
                handler.getPlayer().stopTrack();
            }
            event.reply(msg);
        }
    }

}
