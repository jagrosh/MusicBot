package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SuggestCmd extends Command {

    public SuggestCmd() {
        this.name = "suggest";
        this.help = "give a suggestion for the stream";
        this.guildOnly = false;
        this.arguments = "<your text>";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getGuild().getIdLong() != 800906126233042994L) {
            event.replyError("This command only works in The SplitPixl Zone!");
            return;
        }
        if (event.getArgs().isEmpty()) {
            event.replyError("You need to provide a suggestion!");
            return;
        }
        event.getJDA().getTextChannelById(806402816813432832L).sendMessage(event.getAuthor().getAsMention() + " - " + event.getArgs()).queue(e -> {
            event.getMessage().delete().queue(e2 -> {
                event.replySuccess("Thanks for your suggestion, " + event.getAuthor().getAsMention());
            });
        });
    }
}
