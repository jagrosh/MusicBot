package com.jagrosh.jmusicbot.commands.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class sm extends Command
{
    public sm(Bot bot)
    {
        this.name = "smr";
    }
    
    @Override
    protected void execute(CommandEvent event) 
    { 
        List<Role> user_roles = event.getMember().getRoles();

        if(!user_roles.contains(event.getJDA().getGuildById("638309926225313832").getRoleById(736622853797052519L))) return;
        String[] spl = event.getArgs().split(" ");
        TextChannel channel_to_send = event.getJDA().getTextChannelById(spl[0]);
        String m_id = spl[1];
        List<String> spll = new ArrayList<String>(Arrays.asList(spl));
        spll.remove(0);
        spll.remove(0);
        String msg = String.join(" ", spll);
        channel_to_send.retrieveMessageById(m_id).queue((mesg) -> {
            mesg.reply(msg).queue();
        });
    }
}