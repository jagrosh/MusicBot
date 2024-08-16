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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jagrosh.jmusicbot.Bot;


/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class GUI extends JFrame
{
    private final ConsolePanel console;
    private final Bot bot;
    private final OptionsPanel optionsPanel;

    public GUI(Bot bot)
    {
        super();
        this.bot = bot;
        console = new ConsolePanel();
        optionsPanel = new OptionsPanel();
    }

    public void init()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("JMusicBot");
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Console", console);
        tabs.add("Options", optionsPanel);
        getContentPane().add(tabs);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowListener()
        {
            @Override public void windowOpened(WindowEvent e) { /* unused */ }
            @Override public void windowClosing(WindowEvent e)
            {
                shutdown();
            }
            @Override public void windowClosed(WindowEvent e) { /* unused */ }
            @Override public void windowIconified(WindowEvent e) { /* unused */ }
            @Override public void windowDeiconified(WindowEvent e) { /* unused */ }
            @Override public void windowActivated(WindowEvent e) { /* unused */ }
            @Override public void windowDeactivated(WindowEvent e) { /* unused */ }
        });

        if (SystemTray.isSupported())
        {
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            setupMinimizeToTray();
        }
    }

    private void setupMinimizeToTray()
    {
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon16.png"));
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> shutdown());

        MenuItem nameLabel = new MenuItem("JMusicBot");
        nameLabel.setEnabled(false);

        PopupMenu menu = new PopupMenu();
        menu.add(nameLabel);
        menu.addSeparator();
        menu.add(exit);

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(icon, "JMusicBot", menu);

        // Restore the window when the user clicks the tray icon
        trayIcon.addMouseListener(new MouseAdapter()
        {
            @Override public void mouseClicked(MouseEvent e)
            {
                if (e.getButton() != MouseEvent.BUTTON1)
                {
                    return;
                }

                setVisible(true);
                setExtendedState(JFrame.NORMAL);
                tray.remove(trayIcon);
            }
        });

        // Minimize the window to the system tray when the user clicks the minimize button
        // and the option "minimize to tray" is active
        addWindowStateListener(e -> {
            if (e.getNewState() == JFrame.ICONIFIED && optionsPanel.isMinimizeToTraySelected())
            {
                try
                {
                    setVisible(false);
                    tray.add(trayIcon);
                }
                catch (AWTException ex)
                {
                    System.err.println("TrayIcon could not be added.");
                }
            }
        });
    }

    private void shutdown()
    {
        try
        {
            bot.shutdown();
        }
        catch(Exception ex)
        {
            System.exit(0);
        }
    }
}
