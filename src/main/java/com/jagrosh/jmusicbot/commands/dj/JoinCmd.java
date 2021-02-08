package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCmd extends DJCommand {

    public JoinCmd(Bot bot)
    {
        super(bot);
        this.name = "join";
        this.help = "moves the bot to your channel";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {

        AudioManager manager = event.getGuild().getAudioManager();
        VoiceChannel vc = event.getMember().getVoiceState().getChannel();

        manager.openAudioConnection(vc);
        event.reply("Joined " + vc.getName());

    }
}
