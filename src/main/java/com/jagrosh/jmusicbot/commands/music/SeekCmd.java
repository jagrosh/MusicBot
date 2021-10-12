package com.jagrosh.jmusicbot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.commands.MusicCommand;
import net.dv8tion.jda.api.entities.User;

public class SeekCmd extends MusicCommand {
    public SeekCmd(Bot bot) {
        super(bot);
        this.name = "seek";
        this.help = "seeks to a given timecode in the current song. Use `(M)M:SS` or `(M)(M)m(S)Ss` e.g. 4:57, 25s, or 3m2s";
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler t = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (t.getPlayer().getPlayingTrack().getInfo().isStream) {
            event.reply("Cannot seek on a livestream!");
            return;
        } else if (!t.getPlayer().getPlayingTrack().isSeekable()) {
            event.reply("Cannot seek on this track!");
            return;
        }
        String args = String.join("", event.getArgs().toLowerCase().trim().split(" ")); // strip out any spaces
        Long ms = 0L;
        Long[] ms_vals = { Long.valueOf(60 * 60000), Long.valueOf(60000), Long.valueOf(1000) };
        if (args.matches("^\\d:[0-5]\\d$") || args.matches("^[0-5]\\d:[0-5]\\d$")
                || args.matches("^\\d{1,3}:[0-5]\\d:[0-5]\\d$")) { // x:yy or xx:yy or w(w)(w):xx:yy (to be improved by someone more skilled than I)
            String[] split_args = args.split(":");
            for (int i = split_args.length - 1; i >= 0; i--) {
                ms += Long.parseLong(split_args[i]) * ms_vals[i+(3-split_args.length)];
            }
        } else if (args.matches("^(\\d{0,3}h)?([0-5]?\\dm)?([0-5]?\\ds)?$")) { //hms notation
            String[] hms = {"h", "m", "s"};
            for (int z = 0; z < 3; z++){
                if(args.contains(hms[z])){
                    int index = args.indexOf(hms[z]);
                    ms += Long.valueOf(args.substring(0, index)) * ms_vals[z];
                    if(z <= 2) args = args.substring(index+1);
                }
            }
        } else {
            event.reply(
                    "I did not recognise that time code format. You can use `(M)M:SS` or `(M)(M)m(S)Ss` e.g. 4:57, 25s, or 3m2s.");
            return;
        }

        if (ms > t.getPlayer().getPlayingTrack().getDuration()) {
            event.reply("You requested a time past the end of the track!");
            return;
        }

        t.getPlayer().getPlayingTrack().setPosition(ms);
        event.reply("Set track position to " + (ms > 60000 * 60 ? String.valueOf(ms / (60000 * 60)) +  ":" : "") + String.valueOf((ms % (60000*60)) / 60000) + ":" + String.format("%02d", (ms % 60000) / 1000)
                + ".");

    }

}
