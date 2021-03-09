package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.YggdrasilIconManager;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class YggIconCmd extends Command {
    private final Bot bot;
    private final Long[] permitted = new Long[]{};

    public YggIconCmd(Bot bot) {
        this.name = "yggicon";
        this.help = "shock the monkey";
        this.guildOnly = false;
        this.bot = bot;
        this.children = new Command[] {
                new UpdateCmd(bot)
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        YggdrasilIconManager iconman = bot.getYggdrasilIconManager();
        event.reply(String.format("Used %s of %s icons. (%.1f%% of bag used)", iconman.getUsedIcons().size(), iconman.getTotal(), ((float)iconman.getUsedIcons().size() / iconman.getTotal()) * 100));
    }

    private static class UpdateCmd extends Command {
        private final Bot bot;

        public UpdateCmd(Bot bot) {
            this.name = "update";
            this.guildOnly = false;
            this.bot = bot;
        }

        @Override
        protected void execute(CommandEvent event) {
            bot.getYggdrasilIconManager().update();
        }
    }
}
