/*
 * Copyright 2023 まったりにほんご
 * 
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 *
 * @author John Grosh (jagrosh)
 */
@CommandInfo(name = "About", description = "ボットについての情報を入手する。")
@Author("John Grosh (jagrosh)")
public class AboutCmd extends Command {
    private final Color color;
    private final String description;
    private final Permission[] perms;
    private String oauthLink;

    public AboutCmd(Color color, String description, Permission... perms) {
        this.color = color;
        this.description = description;
        this.name = "about";
        this.help = "ボットについての情報を入手する。";
        this.guildOnly = false;
        this.perms = perms;
        this.botPermissions = new Permission[] { Permission.MESSAGE_EMBED_LINKS };
    }

    @Override
    protected void execute(CommandEvent event) {
        if (oauthLink == null) {
            try {
                ApplicationInfo info = event.getJDA().retrieveApplicationInfo().complete();
                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
            } catch (Exception e) {
                Logger log = LoggerFactory.getLogger("OAuth2");
                log.error("招待リンクを生成出来ませんでした。", e);
                oauthLink = "";
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.isFromType(ChannelType.TEXT) ? event.getGuild().getSelfMember().getColor() : color);
        builder.setAuthor(event.getSelfUser().getName() + "(私）について！", null, event.getSelfUser().getAvatarUrl());
        boolean join = !(event.getClient().getServerInvite() == null || event.getClient().getServerInvite().isEmpty());
        boolean inv = !oauthLink.isEmpty();
        String invline = "\n"
                + (join ? "[`私のサーバー`](" + event.getClient().getServerInvite() + ") に参加してください！"
                        : "")
                + (inv ? (join ? "もしくは、" : "") + "あなたのサーバーに[`私を招待してください！`](" + oauthLink + ")"
                        : "");
        String author = event.getJDA().getUserById(event.getClient().getOwnerId()) == null
                ? "<@" + event.getClient().getOwnerId() + ">"
                : event.getJDA().getUserById(event.getClient().getOwnerId()).getName();
        StringBuilder descr = new StringBuilder()
                .append("ハロー～ **")
                .append(event.getSelfUser().getName())
                .append("**, ")
                .append(description)

                .append("\n**")
                .append(author)
                .append("**")
                .append("が私のご主人さまです。")

                .append("\n\n 私のコマンドを見たいのなら、`")
                .append(event.getClient().getTextualPrefix())
                .append(event.getClient().getHelpWord())
                .append("` を打ってください！")

                .append(join || inv ? invline : "");

        builder.setDescription(descr);

        builder.setFooter("Last restart", null);
        builder.setTimestamp(event.getClient().getStartTime());
        event.reply(builder.build());
    }

}
