package com.jagrosh.jmusicbot.commands.jankbot;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FistChordEventListener extends ListenerAdapter {

    private FistchordCmd fistchord_command;

    public FistChordEventListener(FistchordCmd cc) {
        super();
        this.fistchord_command = cc;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent ev) {
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfWeek().equals(DayOfWeek.FRIDAY) && ev.getChannelJoined().getIdLong() == 638309927005323287L
                && ev.getChannelJoined().getMembers().size() > 3 && !this.fistchord_command.getIsFistchord()) {
            this.fistchord_command.setIsFistchord(true);
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent ev) {
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfWeek().equals(DayOfWeek.SATURDAY) 
                && ev.getChannelLeft().getIdLong() == 638309927005323287L
                && (ev.getChannelJoined().getMembers() == null ? true : ev.getChannelJoined().getMembers().size() < 4)
                && this.fistchord_command.getIsFistchord()) {
            this.fistchord_command.setIsFistchord(false);
        }
    }

}
