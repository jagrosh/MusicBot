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
package com.jagrosh.jmusicbot.commands.owner;

import java.io.IOException;
import java.util.List;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistCmd extends OwnerCommand 
{
    private final Bot bot;
    public PlaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = false;
        this.name = "playlist";
        this.arguments = "<append(追加)|delete(削除)|make(作成)|setdefault(デフォルトを設定)>";
        this.help = "プレイリストの管理";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new OwnerCommand[]{
            new ListCmd(),
            new AppendlistCmd(),
            new DeletelistCmd(),
            new MakelistCmd(),
            new DefaultlistCmd(bot)
        };
    }

    @Override
    public void execute(CommandEvent event) 
    {
        StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" プレイリスト管理コマンド:\n");
        for(Command cmd: this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments()==null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }

    public class MakelistCmd extends OwnerCommand 
    {
        public MakelistCmd()
        {
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "プレイリストを新しく作成します。";
            this.arguments = "<name>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            pname = pname.replaceAll("[*?|\\/\":<>]", "");
            if(pname == null || pname.isEmpty()) 
            {
                event.replyError("プレイリストの名を指定してください。");
            } 
            else if(bot.getPlaylistLoader().getPlaylist(pname) == null)
            {
                try
                {
                    bot.getPlaylistLoader().createPlaylist(pname);
                    event.reply(event.getClient().getSuccess()+" 正常に プレイリスト`"+pname+"`を作りました。");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+"プレイリスト"+e.getLocalizedMessage()+"の作成に失敗しました。");
                }
            }
            else
                event.reply(event.getClient().getError()+" プレイリスト `"+pname+"` は既に存在します");
        }
    }

    public class DeletelistCmd extends OwnerCommand 
    {
        public DeletelistCmd()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "既存のプレイリストを削除します。";
            this.arguments = "<name>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(bot.getPlaylistLoader().getPlaylist(pname)==null)
                event.reply(event.getClient().getError()+" プレイリスト `"+pname+"` は存在しません。");
            else
            {
                try
                {
                    bot.getPlaylistLoader().deletePlaylist(pname);
                    event.reply(event.getClient().getSuccess()+" プレイリスト `"+pname+"`を正常に削除しました。");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+"プレイリスト"+e.getLocalizedMessage()+"の削除に失敗しました。");
                }
            }
        }
    }

    public class AppendlistCmd extends OwnerCommand 
    {
        public AppendlistCmd()
        {
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "既存のプレイリストに曲を追加します。";
            this.arguments = "<名> <URL> | <URL> | ...";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.reply(event.getClient().getError()+" URLまたはプレイリスト名を指定してください。");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(pname);
            if(playlist==null)
                event.reply(event.getClient().getError()+" プレイリスト `"+pname+"` は存在しません。");
            else
            {
                StringBuilder builder = new StringBuilder();
                playlist.getItems().forEach(item -> builder.append("\r\n").append(item));
                String[] urls = parts[1].split("\\|");
                for(String url: urls)
                {
                    String u = url.trim();
                    if(u.startsWith("<") && u.endsWith(">"))
                        u = u.substring(1, u.length()-1);
                    builder.append("\r\n").append(u);
                }
                try
                {
                    bot.getPlaylistLoader().writePlaylist(pname, builder.toString());
                    event.reply(event.getClient().getSuccess()+"正常にプレイリスト `"+pname+"`に`"+urls.length+"`件の項目を追加しました。");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+"プレイリスト"+e.getLocalizedMessage()+"に追加することに失敗しました。");
                }
            }
        }
    }

    public class DefaultlistCmd extends AutoplaylistCmd 
    {
        public DefaultlistCmd(Bot bot)
        {
            super(bot);
            this.name = "setdefault";
            this.aliases = new String[]{"default"};
            this.arguments = "<プレイリスト名|NONE(無効)>";
            this.guildOnly = true;
        }
    }

    public class ListCmd extends OwnerCommand 
    {
        public ListCmd()
        {
            this.name = "all";
            this.aliases = new String[]{"available","list"};
            this.help = "利用可能なプレイリストをすべて表示します。";
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            if(!bot.getPlaylistLoader().folderExists())
                bot.getPlaylistLoader().createFolder();
            if(!bot.getPlaylistLoader().folderExists())
            {
                event.reply(event.getClient().getWarning()+" フォルダー「Playlists」は存在せず、作成することもできませんでした。");
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames();
            if(list==null)
                event.reply(event.getClient().getError()+" 利用可能なプレイリストの読み込みに失敗しました。");
            else if(list.isEmpty())
                event.reply(event.getClient().getWarning()+"フォルダー「Playlists」にプレイリストがありません。");
            else
            {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess()+"利用可能なプレイリスト:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }
    }
}
