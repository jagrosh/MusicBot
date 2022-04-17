package com.jagrosh.jmusicbot.commands.jankbot;

import java.io.File;
import java.net.SocketPermission;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jmusicbot.Bot;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

public class OtherCommandListener extends ListenerAdapter {

    Bot bot;
    CommandClient cc;

    public OtherCommandListener(CommandClient cc, Bot bot){
        super();
        this.bot = bot;
        this.cc = cc;
    }

    private String removeStringOf(String src_string, Character... chars){
        List<Character> char_list = Arrays.asList(chars);
        StringBuilder str = new StringBuilder();
        for(CharacterIterator it = new StringCharacterIterator(src_string); it.current() != CharacterIterator.DONE; it.next()){
            if(char_list.indexOf(it.current()) == -1){
                str.append(it.current());
            }
        }
        return str.toString();
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(!event.getMessage().getContentRaw().startsWith("j!")){
            List<String> split_msg = Arrays.asList(event.getMessage().getContentRaw().toLowerCase().split(" "));
            String stripped_msg = this.removeStringOf(event.getMessage().getContentRaw().toLowerCase(), '?', ',', '.');
            List<String> stripped_split_msg = Arrays.asList(stripped_msg.split(" "));
            boolean has_found = false;
            for(String wake_word : new String[] {"<@" + bot.getJDA().getSelfUser().getId() + ">" , "<@!" + bot.getJDA().getSelfUser().getId() + ">", "jankbot", "janky jeff"}){
                if(stripped_split_msg.indexOf(wake_word) != -1){
                    split_msg = split_msg.subList(stripped_split_msg.indexOf(wake_word)+1, split_msg.size());
                    stripped_split_msg = stripped_split_msg.subList(stripped_split_msg.indexOf(wake_word)+1, stripped_split_msg.size());
                    has_found = true;
                    break;
                }
            }
            if(!has_found) return;
            for(Command command : cc.getCommands()) {
                if(stripped_split_msg.indexOf(command.getName()) != -1 || this.isInAliases(stripped_split_msg, command.getAliases())) {
                    split_msg = split_msg.subList(stripped_split_msg.indexOf(command.getName())+1, split_msg.size());
                    String argz = String.join(" ", split_msg);
                    MessageReceivedEvent mre = new MessageReceivedEvent(bot.getJDA(), event.getResponseNumber(), event.getMessage());
                    CommandEvent ev = new CommandEvent(mre, "j!", argz, cc);
                    command.run(ev);
                    return;
                }
            }

        }
    }

    private boolean isInAliases(List<String> msg, String[] aliases){
        for(String s : aliases) if(msg.indexOf(s) != -1) return true;
        return false;
    }
    
}
