/*
 * Copyright 2016 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import com.jagrosh.jmusicbot.Bot;


/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class GUI extends JFrame {
    
    private final ConsolePanel console;
    private final GuildsPanel guilds;
    private final Bot bot;
    
    public GUI(Bot bot) {
        super();
        this.bot = bot;
        console = new ConsolePanel();
        guilds = new GuildsPanel(bot);
    }
    
    public void init()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("JMusicBot");
        JTabbedPane tabs = new JTabbedPane();
        //tabs.add("Guilds", guilds);
        tabs.add("Console", console);
        getContentPane().add(tabs);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {bot.shutdown();}
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
    }
}
