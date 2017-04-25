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
package com.jagrosh.jmusicbot;

import java.awt.Color;
import javax.security.auth.login.LoginException;
import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.*;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.jagrosh.jmusicbot.commands.*;
import com.jagrosh.jmusicbot.gui.GUI;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class JMusicBot {
    
    public static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                                Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};
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
        
        AboutCommand.IS_AUTHOR = false;
        
        // set up the command client
        
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setOwnerId(config.getOwnerId())
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .addCommands(
                        new AboutCommand(Color.BLUE.brighter(),
                                "a music bot that is [easy to host yourself!](https://github.com/jagrosh/MusicBot)",
                                new String[]{"High-quality music playback", "FairQueue™ Technology", "Easy to host yourself"},
                                RECOMMENDED_PERMS),
                        new PingCommand(),
                        new SettingsCmd(bot),
                        
                        new NowplayingCmd(bot),
                        new PlayCmd(bot),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SCSearchCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),
                        
                        new ForceskipCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        
                        //new GuildlistCommand(waiter),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new ShutdownCmd(bot)
                );
        if(config.getGame()==null)
            cb.useDefaultGame();
        else
            cb.setGame(Game.of(config.getGame()));
        CommandClient client = cb.build();
        
        if(!config.getNoGui())
        {
            try {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } catch(Exception e) {
                SimpleLog.getLog("Startup").fatal("Could not start GUI. If you are "
                        + "running on a server or in a location where you cannot display a "
                        + "window, please run in nogui mode using the -nogui flag.");
            }
        }
        
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
            SimpleLog.getLog("Login").fatal(ex);
        }
    }
}
