package com.jagrosh.jmusicbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

/**
 * Abstract class for music-related commands.
 * Author: John Grosh <john.a.grosh@gmail.com>
 */
public abstract class MusicCommand extends Command
{
    protected final Bot bot;
    protected boolean bePlaying;
    protected boolean beListening;

    public MusicCommand(Bot bot)
    {
        this.bot = bot;
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        TextChannel tchannel = settings.getTextChannel(event.getGuild());
        if (tchannel != null && !event.getTextChannel().equals(tchannel))
        {
            try
            {
                event.getMessage().delete().queue();
            }
            catch (PermissionException ignore) {}
            event.replyInDm(event.getClient().getError() + " You can only use that command in " + tchannel.getAsMention() + "!");
            return;
        }

        bot.getPlayerManager().setUpHandler(event.getGuild()); // No point constantly checking for this later
        if (bePlaying && !((AudioHandler) event.getGuild().getAudioManager().getSendingHandler()).isMusicPlaying(event.getJDA()))
        {
            event.reply(event.getClient().getError() + " There must be music playing to use that!");
            return;
        }

        if (beListening)
        {
            VoiceChannel current = null;
            if (event.getGuild().getSelfMember().getVoiceState().getChannel() instanceof VoiceChannel)
            {
                current = (VoiceChannel) event.getGuild().getSelfMember().getVoiceState().getChannel();
            }

            if (current == null)
                current = settings.getVoiceChannel(event.getGuild());

            GuildVoiceState userState = event.getMember().getVoiceState();
            if (userState.getChannel() == null || userState.isDeafened() ||
                (current != null && !userState.getChannel().equals(current)))
            {
                event.replyError("You must be listening in " +
                    (current == null ? "a voice channel" : current.getAsMention()) + " to use that!");
                return;
            }

            VoiceChannel afkChannel = userState.getGuild().getAfkChannel();
            if (afkChannel != null && afkChannel.equals(userState.getChannel()))
            {
                event.replyError("You cannot use that command in an AFK channel!");
                return;
            }

            if (event.getGuild().getSelfMember().getVoiceState().getChannel() == null)
            {
                try
                {
                    event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                }
                catch (PermissionException ex)
                {
                    event.reply(event.getClient().getError() + " I am unable to connect to " +
                        userState.getChannel().getAsMention() + "!");
                    return;
                }
            }
        }

        doCommand(event);
    }
    
    public abstract void doCommand(CommandEvent event);
}
