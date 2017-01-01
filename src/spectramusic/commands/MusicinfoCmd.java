/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic.commands;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import spectramusic.Command;
import spectramusic.Sender;
import spectramusic.SpConst;
import spectramusic.entities.ClumpedMusicPlayer;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class MusicinfoCmd extends Command {
    private final String ownerId;
    public MusicinfoCmd(String ownerId)
    {
        this.ownerId = ownerId;
        this.command = "musicinfo";
        this.help = "shows info about the bot";
    }
    
    @Override
    protected void execute(String args, GuildMessageReceivedEvent event, PermLevel caller, ClumpedMusicPlayer player) {
        User owner = event.getJDA().getUserById(ownerId);
        String str = "\uD83E\uDD16 **"+event.getJDA().getSelfInfo().getUsername()+"** is an instance of the **Spectra Music** bot written by jagrosh in Java using the JDA-Player library."
                + "\nThe source and instructions are available here: <https://github.com/jagrosh/Spectra-Music>"
                + "\nThis bot's owner is "+(owner==null ? "unknown" : "**"+owner.getUsername()+"**#"+owner.getDiscriminator())+" and it is running version **"+SpConst.VERSION+"**.";
        Sender.sendReply(str, event);
    }
    
}
