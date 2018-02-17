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
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.*;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.commands.*;
import com.jagrosh.jmusicbot.gui.GUI;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.LoggerFactory;

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
        Bot bot = new Bot(waiter, config);
        
        AboutCommand ab = new AboutCommand(Color.BLUE.brighter(),
                                "a music bot that is [easy to host yourself!](https://github.com/jagrosh/MusicBot) (v0.1.3)",
                                new String[]{"High-quality music playback", "FairQueue™ Technology", "Easy to host yourself"},
                                RECOMMENDED_PERMS);
        ab.setIsAuthor(false);
        ab.setReplacementCharacter("\uD83C\uDFB6");
        AudioHandler.STAY_IN_CHANNEL = config.getStay();
        AudioHandler.SONG_IN_STATUS = config.getSongInStatus();
        AudioHandler.MAX_SECONDS = config.getMaxSeconds();
        AudioHandler.USE_NP_REFRESH = !config.useNPImages();
        // set up the command client
        
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(config.getOwnerId())
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .addCommands(
                        ab,
                        new PingCommand(),
                        new SettingsCmd(bot),
                        
                        new NowplayingCmd(bot),
                        new PlayCmd(bot, config.getLoading()),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot, config.getSearching()),
                        new SCSearchCmd(bot, config.getSearching()),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),
                        
                        new ForceskipCmd(bot),
                        new PauseCmd(bot),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        
                        //new GuildlistCommand(waiter),
                        new AutoplaylistCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot)
                );
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if(config.getStatus()!=OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if(config.getGame()==null)
            cb.useDefaultGame();
        else if(config.getGame().getName().equalsIgnoreCase("none"))
        {
            cb.setGame(null);
            nogame = true;
        }
        else
            cb.setGame(config.getGame());
        CommandClient client = cb.build();
        
        if(!config.getNoGui())
        {
            try {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } catch(Exception e) {
                LoggerFactory.getLogger("Startup").error("Could not start GUI. If you are "
                        + "running on a server or in a location where you cannot display a "
                        + "window, please run in nogui mode using the -nogui flag.");
            }
        }
        
        // attempt to log in and start
        try {
            new JDABuilder(AccountType.BOT)
                    .setToken(config.getToken())
                    .setAudioEnabled(true)
                    .setGame(nogame ? null : Game.playing("loading..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE||config.getStatus()==OnlineStatus.OFFLINE ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListener(client)
                    .addEventListener(waiter)
                    .addEventListener(bot)
                    .buildAsync();
        } catch (LoginException ex)
        {
            LoggerFactory.getLogger("Startup").error(ex+"\nPlease make sure you are "
                    + "editing the correct config.txt file, and that you have used the "
                    + "correct token (not the 'secret'!)");
        }
        catch(IllegalArgumentException ex)
        {
            LoggerFactory.getLogger("Startup").error(""+ex);
        }
    }
}
