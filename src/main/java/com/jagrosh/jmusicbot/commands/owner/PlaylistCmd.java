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
package com.jagrosh.jmusicbot.commands.owner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.Paginator;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.JMusicBot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.playlist.PlaylistDetailedItem;
import com.jagrosh.jmusicbot.playlist.PlaylistLoader.Playlist;
import com.jagrosh.jmusicbot.audio.PlayerManager;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class PlaylistCmd extends OwnerCommand 
{
    private final Bot bot;

    private final Paginator.Builder builder;

    public PlaylistCmd(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = false;
        this.name = "playlist";
        this.arguments = "<append|delete|make|setdefault|show>";
        this.help = "playlist management";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.children = new OwnerCommand[]{
            new ListCmd(),
            new AppendlistCmd(),
            new DeletelistCmd(),
            new MakelistCmd(),
            new DefaultlistCmd(bot),
            new ShowListCmd()
        };
        builder = new Paginator.Builder()
                .setColumns(1)
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException ignore){}})
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .wrapPageEnds(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void execute(CommandEvent event) 
    {
        StringBuilder builder = new StringBuilder(event.getClient().getWarning()+" Playlist Management Commands:\n");
        for(Command cmd: this.children)
            builder.append("\n`").append(event.getClient().getPrefix()).append(name).append(" ").append(cmd.getName())
                    .append(" ").append(cmd.getArguments()==null ? "" : cmd.getArguments()).append("` - ").append(cmd.getHelp());
        event.reply(builder.toString());
    }

    private static String getPlaylistTitle(String playlistName, int songslength, long total)
    {
        StringBuilder sb = new StringBuilder();

        return FormatUtil.filter(sb.append(" " + playlistName).append(" | ").append(songslength)
                .append(" entries | `").append(FormatUtil.formatTime(total)).append("` ").toString());
    }
    
    public class MakelistCmd extends OwnerCommand 
    {
        public MakelistCmd()
        {
            this.name = "make";
            this.aliases = new String[]{"create"};
            this.help = "makes a new playlist";
            this.arguments = "<name>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(bot.getPlaylistLoader().getPlaylist(pname)==null)
            {
                try
                {
                    bot.getPlaylistLoader().createPlaylist(pname);
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
    
    public class DeletelistCmd extends OwnerCommand 
    {
        public DeletelistCmd()
        {
            this.name = "delete";
            this.aliases = new String[]{"remove"};
            this.help = "deletes an existing playlist";
            this.arguments = "<name>";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String pname = event.getArgs().replaceAll("\\s+", "_");
            if(bot.getPlaylistLoader().getPlaylist(pname)==null)
                event.reply(event.getClient().getError()+" Playlist `"+pname+"` doesn't exist!");
            else
            {
                try
                {
                    bot.getPlaylistLoader().deletePlaylist(pname);
                    event.reply(event.getClient().getSuccess()+" Successfully deleted playlist `"+pname+"`!");
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" I was unable to delete the playlist: "+e.getLocalizedMessage());
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
            this.help = "appends songs to an existing playlist";
            this.arguments = "<name> <URL> | <URL> | ...";
            this.guildOnly = false;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<2)
            {
                event.reply(event.getClient().getError()+" Please include a playlist name and URLs to add!");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(pname);
            if(playlist==null)
                event.reply(event.getClient().getError()+" Playlist `"+pname+"` doesn't exist!");
            else
            {
                List<String> newList = new ArrayList<String>();
                playlist.getUserViewItems().forEach(item -> newList.add(item.toString()));
                List<String> errors = new ArrayList<String>();
                String[] urls = parts[1].split("\\|");

                PlayerManager manager = bot.getPlayerManager();
                List<Future> jobs = new ArrayList<>();

                for(String url: urls)
                {
                    Future<Void> job = manager.loadItem(url, new AudioLoadResultHandler() {
                          @Override
                          public void trackLoaded(AudioTrack track) {
                              newList.add((new QueuedTrack(track, event.getAuthor())).toString());
                          }

                          @Override
                          public void playlistLoaded(AudioPlaylist playlist) {
                            for (AudioTrack track : playlist.getTracks()) {
                                newList.add((new QueuedTrack(track, event.getAuthor())).toString());
                            }
                          }

                          @Override
                          public void noMatches() {
                              errors.add("Unable to find track for " + url);
                          }

                          @Override
                          public void loadFailed(FriendlyException throwable) {
                            // Notify the user that everything exploded
                              errors.add("Unable to find track for " + url);
                          }
                        });
                    jobs.add(job);
                }

                for (int i = 0; i < jobs.size(); i++) {
                    try 
                    {
                        jobs.get(i).get();
                    } 
                    catch (InterruptedException | ExecutionException e)
                    {
                        errors.add("Unable to find track for " + urls[i] + " \r\n");
                    }
                }

                try
                {
                    bot.getPlaylistLoader().writePlaylist(pname, String.join("\r\n", newList));

                    long total = 0;
                    for (PlaylistDetailedItem item : playlist.getUserViewItems()) {
                        total += item.getDuration();
                    }

                    long finalTotal = total;
                    builder.setText((i1,i2) -> getPlaylistTitle(pname, newList.size(), finalTotal))
                            .setItems(newList.toArray(new String[0]))
                            .setUsers(event.getAuthor())
                            .setColor(event.getSelfMember().getColor())
                    ;
                    builder.build().paginate(event.getChannel(), 1);

                    //event.reply(event.getClient().getSuccess()+" Successfully added "+urls.length+" items to playlist `"+pname+"`!" + outputBuilder);
                }
                catch(IOException e)
                {
                    event.reply(event.getClient().getError()+" I was unable to append to the playlist: "+e.getLocalizedMessage());
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
            this.arguments = "<playlistname|NONE>";
            this.guildOnly = true;
        }
    }
    
    public class ListCmd extends OwnerCommand 
    {
        public ListCmd()
        {
            this.name = "all";
            this.aliases = new String[]{"available","list"};
            this.help = "lists all available playlists";
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event) 
        {
            if(!bot.getPlaylistLoader().folderExists())
                bot.getPlaylistLoader().createFolder();
            if(!bot.getPlaylistLoader().folderExists())
            {
                event.reply(event.getClient().getWarning()+" Playlists folder does not exist and could not be created!");
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames();
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

    public class ShowListCmd extends OwnerCommand
    {
        public ShowListCmd()
        {
            this.name = "show";
            this.aliases = new String[]{};
            this.help = "show all songs in a specific playlist";
            this.guildOnly = true;
        }

        @Override
        protected void execute(CommandEvent event)
        {
            if(!bot.getPlaylistLoader().folderExists())
                bot.getPlaylistLoader().createFolder();
            if(!bot.getPlaylistLoader().folderExists())
            {
                event.reply(event.getClient().getWarning()+" Playlists folder does not exist and could not be created!");
                return;
            }
            List<String> list = bot.getPlaylistLoader().getPlaylistNames();
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


            String[] parts = event.getArgs().split("\\s+", 2);
            if(parts.length<1)
            {
                event.reply(event.getClient().getError()+" Please include a playlist name!");
                return;
            }
            String pname = parts[0];
            Playlist playlist = bot.getPlaylistLoader().getPlaylist(pname);
            if(playlist==null)
                event.reply(event.getClient().getError()+" Playlist `"+pname+"` doesn't exist!");
            else
            {
                long total = 0;
                for (PlaylistDetailedItem item : playlist.getUserViewItems()) {
                    total += item.getDuration();
                }

                List<String> itemsInPage = new ArrayList<String>();

                playlist.getUserViewItems().forEach(item -> itemsInPage.add(item.toString()));

                long finalTotal = total;
                builder.setText((i1,i2) -> getPlaylistTitle(pname, playlist.getUserViewItems().size(), finalTotal))
                        .setItems(itemsInPage.toArray(new String[0]))
                        .setUsers(event.getAuthor())
                        .setColor(event.getSelfMember().getColor());
                builder.build().paginate(event.getChannel(), 1);
            }
        }
    }
}
