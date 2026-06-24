package com.jagrosh.jmusicbot.commands.slash;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.settings.Settings;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

public abstract class SlashMusicCommand extends SlashCommand
{
    protected final Bot bot;
    protected boolean bePlaying;
    protected boolean beListening;

    public SlashMusicCommand(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected void execute(SlashCommandEvent event)
    {
        Settings settings = getClient().getSettingsFor(event.getGuild());
        TextChannel tchannel = settings.getTextChannel(event.getGuild());
        if (tchannel != null && !event.getTextChannel().equals(tchannel))
        {
            event.reply(getClient().getError() + " You can only use that command in " + tchannel.getAsMention() + "!")
                .setEphemeral(true).queue();
            return;
        }
        bot.getPlayerManager().setUpHandler(event.getGuild());

        if (bePlaying)
        {
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (!handler.isMusicPlaying(event.getJDA()))
            {
                event.reply(getClient().getError() + " There must be music playing to use that!")
                    .setEphemeral(true).queue();
                return;
            }
        }

        if (beListening)
        {
            VoiceChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();
            if (current == null)
                current = settings.getVoiceChannel(event.getGuild());
            GuildVoiceState userState = event.getMember().getVoiceState();
            if (!userState.inVoiceChannel() || userState.isDeafened() || (current != null && !userState.getChannel().equals(current)))
            {
                event.reply(getClient().getError() + " You must be listening in " + (current == null ? "a voice channel" : current.getAsMention()) + " to use that!")
                    .setEphemeral(true).queue();
                return;
            }
            VoiceChannel afkChannel = userState.getGuild().getAfkChannel();
            if (afkChannel != null && afkChannel.equals(userState.getChannel()))
            {
                event.reply(getClient().getError() + " You cannot use that command in an AFK channel!")
                    .setEphemeral(true).queue();
                return;
            }
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel())
            {
                try
                {
                    event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                }
                catch (PermissionException ex)
                {
                    event.reply(getClient().getError() + " I am unable to connect to " + userState.getChannel().getAsMention() + "!")
                        .setEphemeral(true).queue();
                    return;
                }
            }
        }

        doCommand(event);
    }

    protected abstract void doCommand(SlashCommandEvent event);
}
