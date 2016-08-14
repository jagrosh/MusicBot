/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectramusic;

/**
 *
 * @author John Grosh (jagrosh)
 */
public abstract class Command {
    private String command;
    private String[] aliases;
    private PermLevel level;
    private String help;
    
    
    
    public enum PermLevel {
        EVERYONE(0), DJ(1), ADMIN(2), OWNER(3);
        
        private final int value;
        
        private PermLevel(int value)
        {
            this.value = value;
        }
        
        public boolean isAtLeast(PermLevel level)
        {
            return value >= level.value;
        }
    }
}
