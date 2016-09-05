
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

/**
 *
 * @author John Grosh (jagrosh)
 */
public class SpConst {
    final public static String VERSION = "0.2";
    
    //command responses
    final public static String SUCCESS = (char)9989+" ";
    final public static String WARNING = (char)9888+" ";
    final public static String ERROR   = (char)9940+" ";
    
    final public static String LINESTART = "  ➣  ";
    
    final public static String TC_JSON = "text_channel_id";
    final public static String VC_JSON = "voice_channel_id";
    final public static String DJ_JSON = "dj_role_id";
    
    final public static String MUST_BE_PLAYING = ERROR+"There must be a song playing to use that!";
    final public static String MUST_BE_IN_VC = ERROR+"You must be listening in %s to use that!";
    
    
    final public static String MULTIPLE_FOUND = WARNING + "**Multiple %s found matching \"%s\":**";
    final public static String NONE_FOUND = WARNING + "**No %s found matching \"%s\"**";
    
    final public static String CANT_HELP = WARNING + "Help could not be sent because you are blocking Direct Messages!";
}
