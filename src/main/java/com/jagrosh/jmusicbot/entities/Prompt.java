/*
 * Copyright 2018 John Grosh (jagrosh)
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
package com.jagrosh.jmusicbot.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.awt.HeadlessException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Prompt {
    private final String title;
    private final String noGuiMessage;

    private boolean noGui;
    private final boolean noPrompt;
    private Scanner scanner;

    public Prompt(String title) {
        this(title, null);
    }

    public Prompt(String title, String noGuiMessage) {
        this(
            title,
            noGuiMessage,
            Boolean.parseBoolean(System.getProperty("nogui")),
            Boolean.parseBoolean(System.getProperty("noprompt"))
        );
    }

    public Prompt(String title, String noGuiMessage, boolean noGui, boolean noPrompt) {
        this.title = title;
        this.noGuiMessage = noGuiMessage == null ?
            "Switching to nogui mode. You can manually start in nogui mode by including the -Dnogui=true flag." :
            noGuiMessage;
        this.noGui = noGui;
        this.noPrompt = noPrompt;
    }

    public boolean isNoGui() {
        return noGui;
    }

    public void alert(Level level, String context, String message) {
        if(noGui) {
            Logger log = LoggerFactory.getLogger(context);
            switch(level) {
                case WARNING:
                    log.warn(message);
                    break;
                case ERROR:
                    log.error(message);
                    break;
                default:
                    log.info(message);
                    break;
            }
        }
        else {
            int option;
            switch(level) {
                case INFO:
                    option = JOptionPane.INFORMATION_MESSAGE;
                    break;
                case WARNING:
                    option = JOptionPane.WARNING_MESSAGE;
                    break;
                case ERROR:
                    option = JOptionPane.ERROR_MESSAGE;
                    break;
                default:
                    option = JOptionPane.PLAIN_MESSAGE;
                    break;
            }
            try {
                JOptionPane.showMessageDialog(null, "<html><body><p style='width: 400px;'>" + message, title, option);
            }
            catch(HeadlessException ignored) {
                noGui = true;
                alert(Level.WARNING, context, noGuiMessage);
                alert(level, context, message);
            }
        }
    }

    public String prompt(String content) {
        if(noPrompt) {
            return null;
        }
        if(noGui) {
            if(scanner == null) {
                scanner = new Scanner(System.in);
            }
            System.out.println(content);
            try {
                if(scanner.hasNextLine()) {
                    return scanner.nextLine();
                }
                return null;
            }
            catch(IllegalStateException | NoSuchElementException e) {
                alert(Level.ERROR, title, "Unable to read input from command line.");
                e.printStackTrace();
                return null;
            }
        }
        else {
            try {
                return JOptionPane.showInputDialog(null, content, title, JOptionPane.QUESTION_MESSAGE);
            }
            catch(HeadlessException e) {
                noGui = true;
                alert(Level.WARNING, title, noGuiMessage);
                return prompt(content);
            }
        }
    }

    public enum Level {
        INFO,
        WARNING,
        ERROR
    }
}
