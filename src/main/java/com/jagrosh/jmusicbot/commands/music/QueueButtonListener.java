package com.jagrosh.jmusicbot.commands.music;

import java.util.List;

import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.settings.RepeatMode;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;

public class QueueButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("QUEUE"))
            return;
        if (event.getComponentId().startsWith("QUEUE_PLAYLIST")) {
            return;
        } else {
            String[] split = event.getComponentId().split(":");
            int this_page_num = Integer.parseInt(split[1]);
            int new_page_num = split[0].equals("QUEUE_PREV") ? this_page_num - 1 : this_page_num + 1;
            RepeatMode rm = RepeatMode.valueOf(split[2]);

            AudioHandler ah = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            List<QueuedTrack> list = ah.getQueue().getList();

            if (list.isEmpty()) {
                ah.getQueue().getLastMessage().editMessage("This queue is empty.").override(true)
                        .queue(msg -> ah.getQueue().setLastMessage(null));

                // ah.getQueue().removeControlsFromLastMessage();
                return;
            }

            if (new_page_num > ah.getQueue().getNumberOfPages())
                new_page_num = ah.getQueue().getNumberOfPages();

            MessageEmbed eb = QueueCmd.getQueueEmbed(new_page_num, event, ah, list, rm, split[3]);
            ItemComponent[] cl = QueueCmd.getButtonsForQueue(new_page_num, ah.getQueue().getNumberOfPages(), rm, split[3]);

            event.getMessage().editMessageEmbeds(eb).setActionRow(cl).queue();
            event.deferEdit().queue();
        }

    }

}
