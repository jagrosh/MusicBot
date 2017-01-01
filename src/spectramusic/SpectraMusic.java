
/*
 * Copyright 2016 jagrosh.
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
package spectramusic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.utils.SimpleLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class SpectraMusic {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try{
            JSONObject config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
            JSONArray array = config.getJSONArray("prefixes");
            String[] prefixes = new String[array.length()];
            for(int i=0; i<prefixes.length; i++)
                prefixes[i] = array.getString(i).toLowerCase().trim();
            String token = config.getString("bot_token");
            String ownerId = config.getString("owner_id");
            String youtubeApiKey = config.has("youtube_api_key") ? config.getString("youtube_api_key") : null;
            
            if(token==null || token.equals("INSERT_TOKEN_HERE"))
            {
                System.out.println("Looks like you didn't set a token...");
            }
            else if(ownerId==null || !ownerId.matches("\\d{10,}"))
            {
                System.out.println("That owner ID doesn't look right...");
            }
            else if(prefixes.length==0)
            {
                System.out.println("Please specify at least 1 prefix");
            }
            else 
            {
                if(youtubeApiKey==null || youtubeApiKey.equals("INSERT_YOUTUBE_API_KEY_HERE_FOR_FASTER_SEARCHES"))
                {
                    SimpleLog.getLog("Youtube").warn("No Youtube API key found; The search command could be faster if one was provided.");
                    youtubeApiKey = null;
                }
                JDA jda = new JDABuilder().setBotToken(token).addListener(new Bot(ownerId, prefixes, youtubeApiKey)).buildAsync();
            }
        }
        catch(IOException e)
        {
            JSONObject newconfig = new JSONObject();
            newconfig.put("bot_token", "INSERT_TOKEN_HERE");
            newconfig.put("owner_id", "INSERT_OWNER'S_DISCORD_ID_HERE");
            newconfig.put("prefixes", new JSONArray().put("%").put("/"));
            newconfig.put("youtube_api_key","INSERT_YOUTUBE_API_KEY_HERE_FOR_FASTER_SEARCHES");
            try
            {
                Files.write(Paths.get("config.json"), newconfig.toString(4).getBytes());
                System.out.println("No config file was found. Config.json has been generated, please populate it!");
            }
            catch (IOException e1)
            {
                System.out.println("No config file was found and we failed to generate one.");
            }
        }
        catch(JSONException e)
        {
            System.out.println("There was an error reading the config file. Please fix this or delete it so a new one can be created.");
        }
        catch(LoginException e)
        {
            System.out.println("Cannot log in with given token.");
        }
    }
    
}
