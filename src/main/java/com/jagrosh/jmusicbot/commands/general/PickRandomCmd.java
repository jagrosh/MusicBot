package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PickRandomCmd extends Command {
    public String[] choices = new String[] {
            "apple", "orange", "bananana", "tjhkghr",
            "jfikrjlgk", "eeeeeeee", "fjkhnsdklfj"
    };

    public PickRandomCmd() {
        this.name = "random";
        this.help = "an annoying noise";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        int n = (int) (Math.random() * choices.length);
        String asfg = choices[n];
        event.reply("Picked: " + asfg);
    }
}
