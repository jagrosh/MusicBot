/*
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
package com.jagrosh.jmusicbot.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.playlist.Playlist;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistCmd extends Command {

    private final Bot bot;
    public PlaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.category = bot.OWNER;
        this.ownerCommand = true;
        this.guildOnly = false;
        this.name = "playlist";
        this.arguments = "<append|delete|make|setdefault>";
        this.help = "playlist management";
        this.children = new Command[]{
            new ListCmd(),
            new AppendlistCmd(),
            new DeletelistCmd(),
            new MakelistCmd(),
            new DefaultlistCmd()
        };
    }

    @Override
    public void execute(CommandEvent event) {
        StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" Playlist Management Commands:\n");
        for(Command cmd: this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments()==null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }
    
    public class MakelistCmd extends Command {
        
        public MakelistCmd()
        {
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "makes a new playlist";
            this.arguments = "<name>";
            this.category = bot.OWNER;
            this.ownerCommand = true;
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(Playlist.loadPlaylist(pname)==null)
            {
                try
                {
                    Files.createFile(Paths.get("Playlists"+File.separator+pname+".txt"));
                    event.reply(event.getClient().getSuccess()+" Successfully created playlist `"+pname+"`!");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" I was unable to create the playlist: "+e.getLocalizedMessage());
                }
            }
            else
                event.reply(event.getClient().getError()+" Playlist `"+pname+"` already exists!");
        }
    }
    
    public class DeletelistCmd extends Command {
        
        public DeletelistCmd()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "deletes an existing playlist";
            this.arguments = "<name>";
            this.guildOnly = false;
            this.ownerCommand = true;
            this.category = bot.OWNER;
        }

        @Override
        protected void execute(CommandEvent event) {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(Playlist.loadPlaylist(pname)==null)
                event.reply(event.getClient().getError()+" Playlist `"+pname+"` doesn't exist!");
            else
            {
                try
                {
                    Files.delete(Paths.get("Playlists"+File.separator+pname+".txt"));
                    event.reply(event.getClient().getSuccess()+" Successfully deleted playlist `"+pname+"`!");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" I was unable to delete the playlist: "+e.getLocalizedMessage());
                }
            }
        }
    }
    
    public class AppendlistCmd extends Command {
        
        public AppendlistCmd()
        {
            this.name = "append";
            this.aliases = new String[]{"add"};
            this.help = "appends songs to an existing playlist";
            this.arguments = "<name> <URL> | <URL> | ...";
            this.guildOnly = false;
            this.ownerCommand = true;
            this.category = bot.OWNER;
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.reply(event.getClient().getError()+" Please include a playlist name and URLs to add!");
                return;
            }
            String pname = parts[0];
            Playlist playlist = Playlist.loadPlaylist(pname);
            if(playlist==null)
                event.reply(event.getClient().getError()+" Playlist `"+pname+"` doesn't exist!");
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
                    Files.write(Paths.get("Playlists"+File.separator+pname+".txt"), builder.toString().trim().getBytes());
                    event.reply(event.getClient().getSuccess()+" Successfully added "+urls.length+" songs to playlist `"+pname+"`!");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" I was unable to append to the playlist: "+e.getLocalizedMessage());
                }
            }
        }
    }
    
    public class DefaultlistCmd extends Command {
        
        public DefaultlistCmd()
        {
            this.name = "setdefault";
            this.aliases = new String[]{"default"};
            this.help = "sets the default playlist for the server";
            this.arguments = "<playlistname|NONE>";
            this.guildOnly = true;
            this.ownerCommand = true;
            this.category = bot.OWNER;
        }

        @Override
        protected void execute(CommandEvent event) {
            if(event.getArgs().isEmpty())
            {
                event.reply(event.getClient().getError()+" Please include a playlist name or NONE");
            }
            if(event.getArgs().equalsIgnoreCase("none"))
            {
                bot.setDefaultPlaylist(event.getGuild(), null);
                event.reply(event.getClient().getSuccess()+" Cleared the default playlist for **"+event.getGuild().getName()+"**");
                return;
            }
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(Playlist.loadPlaylist(pname)==null)
            {
                event.reply(event.getClient().getError()+" Could not find `"+pname+".txt`!");
            }
            else
            {
                bot.setDefaultPlaylist(event.getGuild(), pname);
                event.reply(event.getClient().getSuccess()+" The default playlist for **"+event.getGuild().getName()+"** is now `"+pname+"`");
            }
        }
    }
    
    public class ListCmd extends Command {
        
        public ListCmd()
        {
            this.name = "all";
            this.aliases = new String[]{"available","list"};
            this.help = "lists all available playlists";
            this.guildOnly = true;
            this.ownerCommand = true;
            this.category = bot.OWNER;
        }

        @Override
        protected void execute(CommandEvent event) {
            if(!Playlist.folderExists())
                Playlist.createFolder();
            if(!Playlist.folderExists())
            {
                event.reply(event.getClient().getWarning()+" Playlists folder does not exist and could not be created!");
                return;
            }
            List<String> list = Playlist.getPlaylists();
            if(list==null)
                event.reply(event.getClient().getError()+" Failed to load available playlists!");
            else if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" There are no playlists in the Playlists folder!");
            else
            {
                StringBuilder builder = new StringBuilder(event.getClient().getSuccess()+" Available playlists:\n");
                list.forEach(str -> builder.append("`").append(str).append("` "));
                event.reply(builder.toString());
            }
        }
    }
}
