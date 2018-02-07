/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.core.entities.Icon;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetavatarCmd extends Command {

    public SetavatarCmd(Bot bot)
    {
        this.name = "setavatar";
        this.help = "sets the avatar of the bot";
        this.arguments = "<url>";
        this.ownerCommand = true;
        this.category = bot.OWNER;
    }
    
    @Override
    protected void execute(CommandEvent event) {
        String url;
        if(event.getArgs().isEmpty())
            if(!event.getMessage().getAttachments().isEmpty() && event.getMessage().getAttachments().get(0).isImage())
                url = event.getMessage().getAttachments().get(0).getUrl();
            else
                url = null;
        else
            url = event.getArgs();
        InputStream s = OtherUtil.imageFromUrl(url);
        if(s==null)
        {
            event.reply(event.getClient().getError()+" Invalid or missing URL");
        }
        else
        {
            try {
            event.getSelfUser().getManager().setAvatar(Icon.from(s)).queue(
                    v -> event.reply(event.getClient().getSuccess()+" Successfully changed avatar."), 
                    t -> event.reply(event.getClient().getError()+" Failed to set avatar."));
            } catch(IOException e) {
                event.reply(event.getClient().getError()+" Could not load from provided URL.");
            }
        }
    }
    
}
