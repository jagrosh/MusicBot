/*
 * Copyright 2020 SplitPixl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author SplitPixl
 */
public class GuildLookupCmd extends OwnerCommand
{
    private final static String LINESTART = "\u25AB";
    private final static String HASH ="# ";
    private final static String SPEAKER = "\uD83D\uDD0A ";

    public GuildLookupCmd(Bot bot)
    {
        this.name = "guildlookup";
    }

    @Override
    public void execute(CommandEvent event)
    {
        Guild guild = event.getJDA().getGuildById(event.getArgs());
        try {
            EmbedBuilder builder = new EmbedBuilder();
            String title = "\uD83D\uDDA5 Information about **"+guild.getName()+"**:";
            String verif;
            switch(guild.getVerificationLevel()) {
                case VERY_HIGH: verif = "HIGHEST"; break;
                default:      verif = guild.getVerificationLevel().name(); break;
            }
            event.getJDA().retrieveUserById(guild.getOwnerId()).queue(owner ->
            {
                String str = LINESTART + "ID: **" + guild.getId() + "**\n"
                        + LINESTART + "Owner: **" + guild.getOwner().getUser().getName() + "**#" + guild.getOwner().getUser().getDiscriminator() + " (" + guild.getOwnerId() + ")\n"
//                        + LINESTART + "Banned Guild: **" + treehouse.isBanned(guild.getOwnerIdLong()) + "**\n"
                        + LINESTART + "Verification: **" + verif + "**\n"
                        + LINESTART + "Location: " + (guild.getRegion().getEmoji()) + " **" + guild.getRegion().getName() + "**\n"
                        + LINESTART + "Creation: **" + guild.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "**\n"
                        + LINESTART + "Users: **" + guild.getMemberCount() + "**";
                if (!guild.getFeatures().isEmpty())
                    str += "\n" + LINESTART + "Features: **" + String.join("**, **", guild.getFeatures()) + "**";
                if (guild.getSplashId() != null)
                {
                    builder.setImage(guild.getSplashUrl() + "?size=1024");
//                str += "\n"+LINESTART+"Splash: ";
                }
                if (guild.getIconUrl() != null)
                    builder.setThumbnail(guild.getIconUrl());
                builder.setColor(event.getSelfMember().getColor());
                builder.setDescription(str);

                String channels = "";
                List<GuildChannel> catagorisedChannels = new ArrayList<>();
                for (net.dv8tion.jda.api.entities.Category cc : guild.getCategoryCache())
                {
                    channels += "\n**" + cc.getName() + "**\n";
                    List<GuildChannel> chans = new ArrayList<>();
                    List<TextChannel> tcs = new ArrayList<>(cc.getTextChannels());
                    tcs.sort(Comparator.comparingInt(GuildChannel::getPosition));
                    List<VoiceChannel> vcs = new ArrayList<>(cc.getVoiceChannels());
                    vcs.sort(Comparator.comparingInt(GuildChannel::getPosition));
                    chans.addAll(tcs);
                    chans.addAll(vcs);
                    for (GuildChannel chn : chans)
                    {
//                    channels += "\u200B        ";
                        channels += (chn instanceof VoiceChannel) ? SPEAKER : HASH;
                        channels += chn.getName();
                        channels += "\n";
                    }
                    catagorisedChannels.addAll(chans);
                }
                List<GuildChannel> uncatagorisedChannels = new ArrayList<>();
                uncatagorisedChannels.addAll(guild.getTextChannelCache().asList());
                uncatagorisedChannels.addAll(guild.getVoiceChannelCache().asList());
                uncatagorisedChannels.removeAll(catagorisedChannels);
                uncatagorisedChannels.sort(Comparator.comparingInt(GuildChannel::getPosition));
                if (uncatagorisedChannels.size() > 0)
                {
                    String noCatChannels = "";
                    for (GuildChannel chn : uncatagorisedChannels)
                    {
//                    noCatChannels += "\u200B        ";
                        noCatChannels += (chn instanceof VoiceChannel) ? SPEAKER : HASH;
                        noCatChannels += chn.getName();
                        noCatChannels += "\n";
                    }
                    channels = noCatChannels + channels;
                }
                if (channels.length() < 1024)
                {
                    builder.addField("Channels: " + guild.getTextChannelCache().size() + " Text, " + guild.getVoiceChannelCache().size() + " Voice, " + guild.getCategoryCache().size() + " Categories", channels, false);
                    event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).queue();

                } else
                {
                    builder.addField("Channels: " + guild.getTextChannelCache().size() + " Text, " + guild.getVoiceChannelCache().size() + " Voice, " + guild.getCategoryCache().size() + " Categories", "[channel list too long]", false);
                    event.getChannel().sendMessage(new MessageBuilder().append(title).setEmbed(builder.build()).build()).addFile(channels.getBytes(), "channels.txt").queue();
                }
            });
        } catch (Exception e) {
            event.reactError();
        }
    }

}
