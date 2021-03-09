package com.jagrosh.jmusicbot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CorruptCmd extends Command {
    Random random = new Random();
    public CorruptCmd() {
        this.name = "corrupt";
        this.help = "corrupt text";
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> out = new ArrayList<>();
        for (String input :  event.getArgs().split("\n")) {
            List<String> stringList = new ArrayList<>(Arrays.asList(input.split("")));
            for (int i = 0; i < input.length() / 15; i++) {
                int start = Math.abs(random.nextInt()) % stringList.size();
                int length = Math.abs(random.nextInt()) % (stringList.size() - start);
                List<String> subList = new ArrayList<>();
                for (int j = 0; j < length; j++) {
                    subList.add(stringList.remove(start));
                }
                int insert = Math.min(Math.max(start + (random.nextInt() % (stringList.size() / 4)), 0), stringList.size());
                stringList.addAll(insert, subList);
            }
            out.add(String.join("", stringList));
        }
        event.reply(String.join("\n", out));
    }
}
