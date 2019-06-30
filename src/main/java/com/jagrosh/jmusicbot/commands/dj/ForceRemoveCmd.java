package com.jagrosh.jmusicbot.commands.dj;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.MusicCommand;


public class ForceRemoveCmd extends MusicCommand {
    public ForceRemoveCmd(Bot bot) {
        super(bot);
        this.name = "forceremove";
        this.help = "removes all entries by the mentioned user from the queue";
        this.arguments = "<@user|id>";
        this.aliases = new String[]{"forcedelete"};
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("You need to mention a user!");
        }

        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty()) {
            event.replyError("There is nothing in the queue!");
            return;
        }

        long target = -1;
        String args = event.getArgs();

        if (args.startsWith("<@") && args.endsWith(">")) {
            try {
                target = Long.parseLong(args.substring(2, args.length() - 1));
            } catch (NumberFormatException ignored) {}
        } else if (args.startsWith("<@!") && args.endsWith(">")) {
            try {
                target = Long.parseLong(args.substring(3, args.length() - 1));
            } catch (NumberFormatException ignored) {}
        } else {
            try {
                target = Long.parseLong(args);
            } catch (NumberFormatException ignored) {}
        }
        if (target <= 0) {
            event.replyError("You need to mention a user!");
            return;
        }

        int count = handler.getQueue().removeAll(target);
        if (count == 0)
            event.replyWarning("This user doesn't have any songs in the queue!");
        else
            event.replySuccess("Successfully removed their " + count + " entries.");

    }
}
