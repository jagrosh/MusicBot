/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.core.entities.Guild;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class GuildsPanel extends JPanel {
    
    private final Bot bot;
    private final JList guildList;
    private final JTextArea guildQueue;
    private int index = -1;
    
    public GuildsPanel(Bot bot)
    {
        super();
        super.setLayout(new GridBagLayout());
        this.bot = bot;
        
        guildList = new JList();
        guildQueue = new JTextArea();
        guildList.setModel(new DefaultListModel());
        guildList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guildList.setFixedCellHeight(20);
        guildList.setPreferredSize(new Dimension(100,300));
        guildQueue.setPreferredSize(new Dimension(300,300));
        guildQueue.setEditable(false);
        JScrollPane pane = new JScrollPane();
        JScrollPane pane2 = new JScrollPane();
        pane.setViewportView(guildList);
        pane2.setViewportView(guildQueue);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridwidth = 1;
        super.add(pane, c);
        c.gridx = 1;
        c.gridwidth = 3;
        super.add(pane2, c);
        //bot.registerPanel(this);
        guildList.addListSelectionListener((ListSelectionEvent e) -> {
            index = guildList.getSelectedIndex();
            //bot.updatePanel();
        });
    }
    
    public void updateList(List<Guild> guilds)
    {
        String[] strs = new String[guilds.size()];
        for(int i=0; i<guilds.size(); i++)
            strs[i] = guilds.get(i).getName();
        guildList.setListData(strs);
    }
    
    public int getIndex()
    {
        return guildList.getSelectedIndex();
    }
    
    /*public void updatePanel(AudioHandler handler) {
        StringBuilder builder = new StringBuilder("Now Playing: ");
        if(handler==null || handler.getCurrentTrack()==null)
        {
            builder.append("nothing");
        }
        else
        {
            builder.append(handler.getCurrentTrack().getTrack().getInfo().title)
                    .append(" [")
                    .append(FormatUtil.formatTime(handler.getCurrentTrack().getTrack().getDuration()))
                    .append("]\n");
            for(int i=0; i<handler.getQueue().size(); i++)
                builder.append("\n").append(i+1).append(". ").append(handler.getQueue().get(i).getTrack().getInfo().title);
        }
        guildQueue.setText(builder.toString());
        guildQueue.updateUI();
    }*/
    
}
