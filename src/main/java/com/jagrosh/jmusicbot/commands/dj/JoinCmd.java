package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.DJCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
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
        Member member = event.getMember();

        GuildVoiceState state = member.getVoiceState();

        if(state == null) {
            event.reply(":x: Couldn't find a channel you are in! Please join a channel");
            return;
        }

        VoiceChannel vc = member.getVoiceState().getChannel();

        if(vc == null) {
            event.reply(":x: You are not in a channel! Please join one");
            return;
        }

        manager.openAudioConnection(vc);
        event.reply("Joined " + vc.getName());

    }
}
