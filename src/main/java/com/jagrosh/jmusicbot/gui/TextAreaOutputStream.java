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

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
/**
 *
 * @author Lawrence Dol
 */
public class TextAreaOutputStream extends OutputStream {

// *************************************************************************************************
// INSTANCE MEMBERS
// *************************************************************************************************

private byte[]                          oneByte;                                                    // array for write(int val);
private Appender                        appender;                                                   // most recent action

public TextAreaOutputStream(JTextArea txtara) {
    this(txtara,1000);
    }

public TextAreaOutputStream(JTextArea txtara, int maxlin) {
    if(maxlin<1) { throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value="+maxlin+")"); }
    oneByte=new byte[1];
    appender=new Appender(txtara,maxlin);
    }

/** Clear the current console text area. */
public synchronized void clear() {
    if(appender!=null) { appender.clear(); }
    }

@Override
public synchronized void close() {
    appender=null;
    }

@Override
public synchronized void flush() {
    /* empty */
    }

@Override
public synchronized void write(int val) {
    oneByte[0]=(byte)val;
    write(oneByte,0,1);
    }

@Override
public synchronized void write(byte[] ba) {
    write(ba,0,ba.length);
    }

@Override
public synchronized void write(byte[] ba,int str,int len) {
    if(appender!=null) { appender.append(bytesToString(ba,str,len)); }
    }

//@edu.umd.cs.findbugs.annotations.SuppressWarnings("DM_DEFAULT_ENCODING")
static private String bytesToString(byte[] ba, int str, int len) {
    try { 
        return new String(ba,str,len,"UTF-8"); 
    } catch(UnsupportedEncodingException thr) { 
        return new String(ba,str,len); 
    } // all JVMs are required to support UTF-8
    }

// *************************************************************************************************
// STATIC MEMBERS
// *************************************************************************************************

    static class Appender
    implements Runnable
    {
    static private final String         EOL1="\n";
    static private final String         EOL2=System.getProperty("line.separator",EOL1);
    
    private final JTextArea             textArea;
    private final int                   maxLines;                                                   // maximum lines allowed in text area
    private final LinkedList<Integer>   lengths;                                                    // length of lines within text area
    private final List<String>          values;                                                     // values waiting to be appended

    private int                         curLength;                                                  // length of current line
    private boolean                     clear;
    private boolean                     queue;

    Appender(JTextArea txtara, int maxlin) {
        textArea =txtara;
        maxLines =maxlin;
        lengths  =new LinkedList<>();
        values   =new ArrayList<>();

        curLength=0;
        clear    =false;
        queue    =true;
        }

    private synchronized void append(String val) {
        values.add(val);
        if(queue) { 
            queue=false; 
            EventQueue.invokeLater(this); 
        }
        }

    private synchronized void clear() {
        clear=true;
        curLength=0;
        lengths.clear();
        values.clear();
        if(queue) { 
            queue=false; 
            EventQueue.invokeLater(this); 
        }
        }

    // MUST BE THE ONLY METHOD THAT TOUCHES textArea!
    @Override
    public synchronized void run() {
        if(clear) { textArea.setText(""); }
        values.stream().map((val) -> {
            curLength+=val.length();
            return val;
        }).map((val) -> {
            if(val.endsWith(EOL1) || val.endsWith(EOL2)) {
                if(lengths.size()>=maxLines) { textArea.replaceRange("",0,lengths.removeFirst()); }
                lengths.addLast(curLength);
                curLength=0;
            }
            return val;
        }).forEach((val) -> {
            textArea.append(val);
        });
        values.clear();
        clear =false;
        queue =true;
        }
    }
} /* END PUBLIC CLASS */