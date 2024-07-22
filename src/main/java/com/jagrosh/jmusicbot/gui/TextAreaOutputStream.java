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

import org.jetbrains.annotations.NotNull;

import javax.swing.JTextArea;
import java.awt.EventQueue;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lawrence Dol
 */
public class TextAreaOutputStream extends OutputStream {
    // array for write(int val);
    private final byte[] oneByte;

    // most recent action
    private Appender appender;

    public TextAreaOutputStream(JTextArea textArea) {
        this(textArea, 1000);
    }

    public TextAreaOutputStream(JTextArea textArea, int maxlin) {
        if(maxlin < 1) {
            throw new IllegalArgumentException(
                "TextAreaOutputStream maximum lines must be positive (value=" + maxlin + ")");
        }
        oneByte = new byte[1];
        appender = new Appender(textArea, maxlin);
    }

    /**
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if(appender != null) {
            appender.clear();
        }
    }

    @Override
    public synchronized void close() {
        appender = null;
    }

    @Override
    public synchronized void flush() {
        /* empty */
    }

    @Override
    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    @Override
    public synchronized void write(byte @NotNull [] ba) {
        write(ba, 0, ba.length);
    }

    @Override
    public synchronized void write(byte @NotNull [] ba, int str, int len) {
        if(appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
    }

    //@edu.umd.cs.findbugs.annotations.SuppressWarnings("DM_DEFAULT_ENCODING")
    static private String bytesToString(byte[] ba, int str, int len) {
        return new String(ba, str, len, StandardCharsets.UTF_8);
    }

    static class Appender implements Runnable {
        static private final String UNIX_NEWLINE = "\n";
        static private final String SYSTEM_NEWLINE = System.lineSeparator();

        private final JTextArea textArea;

        // maximum lines allowed in text area
        private final int maxLines;

        // length of lines within text area
        private final LinkedList<Integer> lengths;

        // values waiting to be appended
        private final List<String> values;

        // length of current line
        private int curLength;
        private boolean clear;
        private boolean queue;

        Appender(JTextArea textArea, int maxLines) {
            this.textArea = textArea;
            this.maxLines = maxLines;
            lengths = new LinkedList<>();
            values = new ArrayList<>();

            curLength = 0;
            clear = false;
            queue = true;
        }

        private synchronized void append(String val) {
            values.add(val);
            if(queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        private synchronized void clear() {
            clear = true;
            curLength = 0;
            lengths.clear();
            values.clear();
            if(queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        // MUST BE THE ONLY METHOD THAT TOUCHES textArea!
        @Override
        public synchronized void run() {
            if(clear) {
                textArea.setText("");
            }
            values.stream()
                .peek(val -> curLength += val.length())
                .peek(val -> {
                    if(val.endsWith(SYSTEM_NEWLINE) || val.endsWith(UNIX_NEWLINE)) {
                        if(lengths.size() >= maxLines) {
                            textArea.replaceRange("", 0, lengths.removeFirst());
                        }
                        lengths.addLast(curLength);
                        curLength = 0;
                    }
                })
                .forEach(textArea::append);
            values.clear();
            clear = false;
            queue = true;
        }
    }
}
