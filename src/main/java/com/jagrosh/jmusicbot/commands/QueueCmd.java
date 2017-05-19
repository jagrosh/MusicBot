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

import java.util.List;
import java.util.concurrent.TimeUnit;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.menu.pagination.PaginatorBuilder;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class QueueCmd extends MusicCommand {

    private final PaginatorBuilder builder;
    public QueueCmd(Bot bot)
    {
        super(bot);
        this.name = "queue";
        this.help = "shows the current queue";
        this.arguments = "[pagenum]";
        this.aliases = new String[]{"list"};
        this.bePlaying = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_ADD_REACTION,Permission.MESSAGE_EMBED_LINKS};
        builder = new PaginatorBuilder()
                .setColumns(1)
                .setFinalAction(m -> {try{m.clearReactions().queue();}catch(PermissionException e){}})
                .setItemsPerPage(10)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .showPageNumbers(true)
                .setEventWaiter(bot.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES)
                ;
    }

    @Override
    public void doCommand(CommandEvent event) {
        int pagenum = 1;
        try{
            pagenum = Integer.parseInt(event.getArgs());
        }catch(NumberFormatException e){}
        List<QueuedTrack> list = ((AudioHandler)event.getGuild().getAudioManager().getSendingHandler()).getQueue().getList();
        if(list.isEmpty())
        {
            event.reply(event.getClient().getWarning()+" There is no music in the queue!");
            return;
        }
        String[] songs = new String[list.size()];
        long total = 0;
        for(int i=0; i<list.size(); i++)
        {
            total += list.get(i).getTrack().getDuration();
            songs[i] = list.get(i).toString();
        }
        builder.setText(event.getClient().getSuccess()+" Current Queue | "+songs.length+" entries | `"+FormatUtil.formatTime(total)+"` ")
                .setItems(songs)
                .setUsers(event.getAuthor())
                .setColor(event.getSelfMember().getColor())
                ;
        builder.build().paginate(event.getChannel(), pagenum);
    }
    
}
