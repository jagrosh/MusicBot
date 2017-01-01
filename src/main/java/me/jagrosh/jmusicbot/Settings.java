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
package me.jagrosh.jmusicbot;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class Settings {
    public final static Settings DEFAULT_SETTINGS = new Settings(null, null, null);
    
    private String textId;
    private String voiceId;
    private String roleId;
    
    public Settings(String textId, String voiceId, String roleId)
    {
        this.textId = textId;
        this.voiceId = voiceId;
        this.roleId = roleId;
    }
    
    public String getTextId()
    {
        return textId;
    }
    
    public String getVoiceId()
    {
        return voiceId;
    }
    
    public String getRoleId()
    {
        return roleId;
    }
    
    public void setTextId(String id)
    {
        this.textId = id;
    }
    
    public void setVoiceId(String id)
    {
        this.voiceId = id;
    }
    
    public void setRoleId(String id)
    {
        this.roleId = id;
    }
}
