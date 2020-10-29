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
import java.awt.GridLayout;
import java.io.PrintStream;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class ConsolePanel extends JPanel {
    
    public ConsolePanel()
    {
        super();
        JTextArea text = new JTextArea();
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        PrintStream con=new PrintStream(new TextAreaOutputStream(text));
        System.setOut(con);
        System.setErr(con);
        
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(text);
        
        super.setLayout(new GridLayout(1,1));
        super.add(pane);
        super.setPreferredSize(new Dimension(400,300));
    }
}
