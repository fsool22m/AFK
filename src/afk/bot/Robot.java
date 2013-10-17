package afk.bot;

import afk.bot.london.RobotEvent;
import java.util.UUID;

/**
 * This is the interface to all user-written Robot classes.
 * 
 * @author Daniel
 */
public interface Robot
{

    /**
     * Sets all flags to there default "false" position.
     */
    public void clearActions();

    /**
     * Sets the feedback object.
     * @param event 
     */
    public void feedback(RobotEvent event);

    /**
     * Gets a copy of the action array.
     * @return 
     */
    public boolean[] getActions();

    /**
     * Gets the robot's unique ID.
     * @return 
     */
    public UUID getId();
    
    /**
     * Get the robot's number.
     * @return the robot's number.
     */
    public int getBotNum();

    /**
     * Main execution method of the robot implemented by the user. This is
     * called once every game tick to calculate the actions to take for that
     * game tick.
     */
    public void run();
    
    /**
     * Initialisation code is implemented by the user here. This is where any
     * robot configuration properties may be set.
     */
    public void init();
    
    /**
     * Gets the RobotConfigManager associated with this robot.
     * @return the RobotConfigManager associated with this robot.
     */
    public RobotConfigManager getConfigManager();
    
}
