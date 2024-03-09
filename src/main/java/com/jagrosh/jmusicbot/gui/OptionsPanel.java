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

import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * @author Wolfgang Schwendtbauer
 */
public class OptionsPanel extends JPanel
{
    private static final String KEY_MINIMIZE_TO_SYSTEM_TRAY = "minimizeToSystemTray";

    private final Preferences prefs;
    private final JCheckBox minimizeToTrayCheckbox;

    public OptionsPanel()
    {
        prefs = Preferences.userNodeForPackage(getClass());

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        minimizeToTrayCheckbox = new JCheckBox("Minimize to system tray");
        add(minimizeToTrayCheckbox);

        loadPreferences();
        addListeners();
    }

    private void loadPreferences()
    {
        minimizeToTrayCheckbox.setSelected(prefs.getBoolean(KEY_MINIMIZE_TO_SYSTEM_TRAY, false));
    }

    private void addListeners()
    {
        minimizeToTrayCheckbox.addActionListener(e -> prefs.putBoolean(KEY_MINIMIZE_TO_SYSTEM_TRAY, minimizeToTrayCheckbox.isSelected()));
    }

    public boolean isMinimizeToTraySelected()
    {
        return minimizeToTrayCheckbox.isSelected();
    }
}
