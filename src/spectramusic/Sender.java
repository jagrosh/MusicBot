
/*
 * Copyright 2016 jagrosh.
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
package spectramusic;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class Sender {
    private final static ScheduledExecutorService executor = Executors.newScheduledThreadPool(100);
    //for replying to commands
    
    public static void sendReply(String message, GuildMessageReceivedEvent event)
    {
        sendReply(message,event,null);
    }
    
    public static void sendReply(String message, GuildMessageReceivedEvent event, Supplier<String> edit)
    {
        Consumer<Message> cleanup = event.getChannel().checkPermission(event.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE) 
                ? m -> executor.schedule(() -> {
                        event.getMessage().deleteMessage();
                        m.deleteMessage();
                    }, m.getRawContent().split("\n").length*15+10, TimeUnit.SECONDS)
                : null;
        if(edit==null)
            event.getChannel().sendMessageAsync(cleanMessage(message), cleanup);
        else
            event.getChannel().sendMessageAsync(cleanMessage(message), m -> {
                String newmessage = cleanMessage(edit.get());
                m.updateMessageAsync(newmessage, cleanup);
            });
    }
    
    public static void sendReplyNoDelete(String message, GuildMessageReceivedEvent event, Consumer<Message> callback)
    {
        message = cleanMessage(message);
        event.getChannel().sendMessageAsync(message, callback);
    }
    
    public static void sendAlert(String message, GuildMessageReceivedEvent event)
    {
        message = cleanMessage(message);
        event.getChannel().sendMessageAsync(message, m -> {
            executor.schedule(()-> m.deleteMessage(), m.getRawContent().split("\n").length*90, TimeUnit.SECONDS);
        });
    }
    
    //send help (warn if can't send)
    public static void sendHelp(String message, PrivateChannel pchan, GuildMessageReceivedEvent event)//dependency for fallback
    {
        ArrayList<String> bits = splitMessage(message);
        for(int i=0; i<bits.size(); i++)
        {
            boolean first = (i == 0);
            pchan.sendMessageAsync(bits.get(i), m ->
            {
                if(m==null && first)//failed to send
                {
                    sendReply(SpConst.CANT_HELP, event);
                }
            });
        }
    }
    
    public static void sendPrivate(String message, PrivateChannel pchan)
    {
        ArrayList<String> bits = splitMessage(message);
        bits.stream().forEach((bit) -> {
            pchan.sendMessageAsync(bit, null);
        });
    }
    
    private static String cleanMessage(String message)
    {
        message = message.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere").trim();
        if(message.length()>2000)
            message = message.substring(0,1995)+" (...)";
        return message;
    }
    
    private static ArrayList<String> splitMessage(String stringtoSend)
    {
        ArrayList<String> msgs =  new ArrayList<>();
        if(stringtoSend!=null)
        {
            stringtoSend = stringtoSend.replace("@everyone", "@\u200Beveryone").replace("@here", "@\u200Bhere").trim();
            while(stringtoSend.length()>2000)
            {
                int leeway = 2000 - (stringtoSend.length()%2000);
                int index = stringtoSend.lastIndexOf("\n", 2000);
                if(index<leeway)
                    index = stringtoSend.lastIndexOf(" ", 2000);
                if(index<leeway)
                    index=2000;
                String temp = stringtoSend.substring(0,index).trim();
                if(!temp.equals(""))
                    msgs.add(temp);
                stringtoSend = stringtoSend.substring(index).trim();
            }
            if(!stringtoSend.equals(""))
                msgs.add(stringtoSend);
        }
        return msgs;
    }
    
    public static void shutdown()
    {
        executor.shutdown();
    }
}
