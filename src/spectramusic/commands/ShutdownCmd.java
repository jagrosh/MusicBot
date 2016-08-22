/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author johna
 */
public class ShutdownCmd extends Command {

    public ShutdownCmd()
    {
        this.command = "shutdown";
        this.help = "shuts down the bot safely";
        this.level = PermLevel.OWNER;
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        event.getChannel().sendMessage(SpConst.WARNING+"Shutting down...");
        event.getJDA().shutdown();
    }
    
}
