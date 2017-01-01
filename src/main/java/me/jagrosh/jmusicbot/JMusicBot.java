/*
 * Copyright 2016 John Grosh (jagrosh).
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
package me.jagrosh.jmusicbot;

import java.awt.Color;
import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;
import me.jagrosh.jdautilities.commandclient.CommandClient;
import me.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import me.jagrosh.jdautilities.commandclient.examples.*;
import me.jagrosh.jdautilities.waiter.EventWaiter;
import me.jagrosh.jmusicbot.commands.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class JMusicBot {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        // check run mode(s)
        boolean nogui = false;
        for(String arg: args)
            if("-nogui".equalsIgnoreCase(arg))
                nogui = true;
        
        // load config
        Config config = new Config(nogui);
        
        // set up the listener
        EventWaiter waiter = new EventWaiter();
        Bot bot = new Bot(waiter);
        
        // set up the command client
        CommandClient client = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setOwnerId(config.getOwnerId())
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .addCommands(
                        new AboutCommand(Color.BLUE.brighter(),
                                "a music bot that is [easy to host yourself!](https://github.com/jagrosh/MusicBot)",
                                "https://github.com/jagrosh/MusicBot",
                                new String[]{"High-quality music playback", "FairQueue™ Technology", "Easy to host yourself"}),
                        new PingCommand(),
                        new SettingsCmd(bot),
                        
                        new NowplayingCmd(bot),
                        new PlayCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SkipCmd(bot),
                        
                        new ForceskipCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        
                        new SetavatarCmd(bot),
                        new SetnameCmd(bot),
                        new ShutdownCmd(bot)
                ).build();
        
        // attempt to log in and start
        try {
            new JDABuilder(AccountType.BOT)
                    .setToken(config.getToken())
                    .setAudioEnabled(true)
                    .setGame(Game.of("loading..."))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .addListener(client)
                    .addListener(waiter)
                    .addListener(bot)
                    .buildAsync();
        } catch (LoginException | IllegalArgumentException | RateLimitedException ex) {
            if(nogui)
                System.out.println("[ERROR] Could not log in: "+ex);
            else
                JOptionPane.showMessageDialog(null, "Could not log in:\n"+ex, "JMusicBot", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
    }
}
