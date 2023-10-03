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

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.commands.DJCommand;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlayCmd extends MusicCommand
{
    private final static String LOAD = "\uD83D\uDCE5"; // 📥
    private final static String CANCEL = "\uD83D\uDEAB"; // 🚫

    private final String loadingEmoji;

    public PlayCmd(Bot bot)
    {
        super(bot);
        this.loadingEmoji = bot.getConfig().getLoading();
        this.name = "play";
        this.arguments = "<曲名|URL|サブコマンド>";
        this.help = "指定された曲を再生します。";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.beListening = true;
        this.bePlaying = false;
        this.children = new Command[]{new PlaylistCmd(bot)};
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
        if(event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty())
        {
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            if(handler.getPlayer().getPlayingTrack()!=null && handler.getPlayer().isPaused())
            {
                if(DJCommand.checkDJPermission(event))
                {
                    handler.getPlayer().setPaused(false);
                    event.replySuccess("**"+handler.getPlayer().getPlayingTrack().getInfo().title+"**を再開しました。");
                }
                else
                    event.replyError("プレイヤーを再開するにはDJロール、または管理者の権限が必要です。");
                return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning()+"再生コマンド:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <曲名>` - YouTubeの最初の検索結果を再生します。");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" <URL>` - 指定された曲、プレイリスト、またはストリームを再生します。");
            for(Command cmd: children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName()).append(" ").append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
            return;
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">") 
                ? event.getArgs().substring(1,event.getArgs().length()-1) 
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
        event.reply(loadingEmoji+" 読み込み中… `["+args+"]`", m -> bot.getPlayerManager().loadItemOrdered(event.getGuild(), args, new ResultHandler(m,event,false)));
    }

    private class ResultHandler implements AudioLoadResultHandler
    {
        private final Message m;
        private final CommandEvent event;
        private final boolean ytsearch;

        private ResultHandler(Message m, CommandEvent event, boolean ytsearch)
        {
            this.m = m;
            this.event = event;
            this.ytsearch = ytsearch;
        }

        private void loadSingle(AudioTrack track, AudioPlaylist playlist)
        {
            if(bot.getConfig().isTooLong(track))
            {
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+" 曲 (**"+track.getInfo().title+"**)は長すぎます。最大許容長さを超えています。 `"
                        +FormatUtil.formatTime(track.getDuration())+"` > `"+FormatUtil.formatTime(bot.getConfig().getMaxSeconds()*1000)+"`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor()))+1;
            String trackTitle = "**"+track.getInfo().title
                    +"** (`"+FormatUtil.formatTime(track.getDuration())+"`) ";
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()+(pos==0?
                    "キューに"+trackTitle+"を追加し、再生を開始します。":
                    "キューの"+pos+"番目に"+trackTitle+"を追加しました"));
            if(playlist==null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION))
                m.editMessage(addMsg).queue();
            else
            {
                new ButtonMenu.Builder()
                        .setText(addMsg+"\n"+event.getClient().getWarning()+" この曲は曲数**"+playlist.getTracks().size()+"**もあるプレイリストの中にあります。プレイリストを読み込むには"+LOAD+"を選択してください。")
                        .setChoices(LOAD, CANCEL)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if(re.getName().equals(LOAD))
                                m.editMessage(addMsg+"\n"+event.getClient().getSuccess()+" 追加曲を**"+loadPlaylist(playlist, track)+"**件読み込みました。").queue();
                            else
                                m.editMessage(addMsg).queue();
                        }).setFinalAction(m ->
                        {
                            try{ m.clearReactions().queue(); }catch(PermissionException ignore) {}
                        }).build().display(m);
            }
        }

        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude)
        {
            int[] count = {0};
            playlist.getTracks().stream().forEach((track) -> {
                if(!bot.getConfig().isTooLong(track) && !track.equals(exclude))
                {
                    AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                    handler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }

        @Override
        public void trackLoaded(AudioTrack track)
        {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist)
        {
            if(playlist.getTracks().size()==1 || playlist.isSearchResult())
            {
                AudioTrack single = playlist.getSelectedTrack()==null ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            }
            else if (playlist.getSelectedTrack()!=null)
            {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            }
            else
            {
                int count = loadPlaylist(playlist, null);
                if(playlist.getTracks().size() == 0)
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+"プレイリスト"+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+"は項目がないか、読み込みに失敗しました。")).queue();
                }
                else if(count==0)
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getWarning()+"プレイリスト"+(playlist.getName()==null ? "" : "(**"+playlist.getName()
                            +"**) ")+"にあるすべての項目は"+"最大値(`"+bot.getConfig().getMaxTime()+"`)")+"を超えていました。").queue();
                }
                else
                {
                    m.editMessage(FormatUtil.filter(event.getClient().getSuccess()+" 成功："
                            +(playlist.getName()==null?"プレイリスト":"プレイリスト **"+playlist.getName()+"**")+" は項目数`"
                            + playlist.getTracks().size()+"`で; キューに追加されました。!"
                            + (count<playlist.getTracks().size() ? "\n"+event.getClient().getWarning()+" 最大許容長さ (`"
                            + bot.getConfig().getMaxTime()+"`) を超えた曲は排除されました。" : ""))).queue();
                }
            }
        }

        @Override
        public void noMatches()
        {
            if(ytsearch)
                m.editMessage(FormatUtil.filter(event.getClient().getWarning()+"`"+event.getArgs()+"`に一致する結果は見つかりませんでした。")).queue();
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"+event.getArgs(), new ResultHandler(m,event,true));
        }

        @Override
        public void loadFailed(FriendlyException throwable)
        {
            if(throwable.severity==Severity.COMMON)
                m.editMessage(event.getClient().getError()+" 読み込みエラー: "+throwable.getMessage()).queue();
            else
                m.editMessage(event.getClient().getError()+" 曲の読み込みに失敗しました。").queue();
        }
    }

    public class PlaylistCmd extends MusicCommand
    {
        public PlaylistCmd(Bot bot)
        {
            super(bot);
            this.name = "playlist";
            this.aliases = new String[]{"pl"};
            this.arguments = "<名>";
            this.help = "指定されたプレイリストを再生します。";
            this.beListening = true;
            this.bePlaying = false;
        }

        @Override
        public void doCommand(CommandEvent event) 
        {
            if(event.getArgs().isEmpty())
            {
                event.reply(event.getClient().getError()+" プレイリストの名を指定してください。");
                return;
            }
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs());
            if(playlist==null)
            {
                event.replyError("`"+event.getArgs()+".txt`は、フォルダー「Playlists」にありません。");
                return;
            }
            event.getChannel().sendMessage(loadingEmoji+" プレイリストを読み込み中…**"+event.getArgs()+"** ("+playlist.getItems().size()+" 個)").queue(m -> 
            {
                AudioHandler handler = (AudioHandler)event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(), (at)->handler.addTrack(new QueuedTrack(at, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty() 
                            ? event.getClient().getWarning()+" 曲は読み込まれませんでした。" 
                            : event.getClient().getSuccess()+" 曲を**"+playlist.getTracks().size()+"**個読み込みました。");
                    if(!playlist.getErrors().isEmpty())
                        builder.append("\n次の曲は読み込みに失敗しました：");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex()+1).append("]` **").append(err.getItem()).append("**: ").append(err.getReason()));
                    String str = builder.toString();
                    if(str.length()>2000)
                        str = str.substring(0,1994)+" (...)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }
    }
}
