package com.jagrosh.jmusicbot.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jmusicbot.BotConfig;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.settings.RepeatMode;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.commands.music.QueueCmd;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Component;

public class QueueButtonListener extends ListenerAdapter {
    private SettingsManager settings_manager;
    private CommandClient command_client;

    public QueueButtonListener(SettingsManager sm, CommandClient cc){
        super();
        this.settings_manager = sm;
        this.command_client = cc;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (!event.getComponentId().startsWith("QUEUE"))
            return;
        if (event.getComponentId().startsWith("QUEUE_PLAYLIST")) { //playlist confirm is a one-and-done event so EventWaiter is more suited (supports timeout also).
            return;
        } else {
            RepeatMode rm = this.settings_manager.getSettings(event.getGuild()).getRepeatMode();
            String success = this.command_client.getSuccess();
            String[] split = event.getComponentId().split(":");
            int this_page_num = Integer.parseInt(split[1]);
            int new_page_num = split[0].equals("QUEUE_PREV") ? this_page_num - 1 : this_page_num + 1;
            AudioHandler ah = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            List<QueuedTrack> list = ah.getQueue().getList();

            if (list.isEmpty()) {
                ah.getQueue().getLastMessage().editMessage("This queue is empty.").override(true)
                        .queue(msg -> ah.getQueue().setLastMessage(null));
                return;
            }

            if (new_page_num > ah.getQueue().getNumberOfPages())
                new_page_num = ah.getQueue().getNumberOfPages();

            MessageEmbed eb = QueueCmd.getQueueEmbed(new_page_num, event, ah, list, rm, success);
            Component[] cl = QueueCmd.getButtonsForQueue(new_page_num, ah.getQueue().getNumberOfPages(), rm, success);

            event.getMessage().editMessage(eb).setActionRow(cl).queue();
            event.deferEdit().queue();
        }

    }

}
