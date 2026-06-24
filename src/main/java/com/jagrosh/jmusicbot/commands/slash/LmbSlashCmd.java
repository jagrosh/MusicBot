package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class LmbSlashCmd extends SlashCommand {

    public LmbSlashCmd(Bot bot) {
        this.name = "lmb";
        this.help = "shows LMusicBot credits and acknowledgements";
        this.guildOnly = false;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.MAGENTA);
        eb.setTitle(
            "LMusicBot (Lukas' Music Bot) v" + OtherUtil.getCurrentVersion()
        );
        eb.setDescription(
            "A modern, actively maintained fork of JMusicBot with slash commands, Spotify support, webhook notifications, and optional Redis/PostgreSQL storage."
        );

        eb.addField("Credits", "", false);
        eb.addField("\uD83D\uDD17", "**Lukas** — Owner of LMusicBot", false);
        eb.addField(
            "\uD83C\uDFA8",
            "**IMStudios** — Sponsor, Maintainer & Developer of LMusicBot",
            false
        );
        eb.addField(
            "\uD83C\uDFB6",
            "**John Grosh (jagrosh)** — original creator of JMusicBot",
            false
        );

        eb.addField(
            "",
            "Powered by [JDA](https://github.com/DV8FromTheWorld/JDA), [lavaplayer](https://github.com/sedmelluq/lavaplayer), and the open-source community.",
            false
        );

        event.replyEmbeds(eb.build()).queue();
    }
}
