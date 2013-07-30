/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.bot.london;

import java.util.ArrayList;

/**
 * @author Jessica
 */
public abstract class Robot
{
    //Method user will have to implement

    public static final int NUM_ACTIONS = 5;
    //Index mapping of flag array
    public static final int MOVE_FRONT = 0;
    public static final int MOVE_BACK = 1;
    public static final int TURN_CLOCK = 2;
    public static final int TURN_ANTICLOCK = 3;
    public static final int ATTACK_ACTION = 4;
    private boolean[] actionFlags;
    protected RobotEvent events;

    /*
     * Abstract method that will be implemented by user
     */
    public abstract void run();

    /*
     * Methods bot actions
     * Declared as final to prevent overriding
     */
    public Robot()
    {
        actionFlags = new boolean[NUM_ACTIONS];
        events = new RobotEvent(new ArrayList<Float>(), false, false, false);
    }

    private void setFlag(int index)
    {
        if (index <= NUM_ACTIONS && index >= 0)
        {
            actionFlags[index] = true;
        }
    }

    public boolean[] getActionFlags()
    {
        return actionFlags.clone();
    }

    protected final void moveForward()
    {
        //throw new UnsupportedOperationException();
        setFlag(MOVE_FRONT);
    }

    protected final void moveBackwards()
    {
        //throw new UnsupportedOperationException();
        setFlag(MOVE_BACK);
    }

    protected final void turnClockwise()
    {
        //throw new UnsupportedOperationException();
        setFlag(TURN_CLOCK);
    }

    protected final void turnAntiClockwise()
    {
        //throw new UnsupportedOperationException();
        setFlag(TURN_ANTICLOCK);
    }

    protected final void attack()
    {
        //throw new UnsupportedOperationException();
        setFlag(ATTACK_ACTION);
    }

    public void clearFlags()
    {
        for (int x = 0; x < NUM_ACTIONS; x++)
        {
            actionFlags[x] = false;
        }
    }

    public boolean[] getBotInputs()
    {
        run();
        boolean[] temp = getActionFlags();
        boolean[] botFlags = new boolean[temp.length];
        System.arraycopy(temp, 0, botFlags, 0, temp.length);
        clearFlags();
        return botFlags;
    }

    public void feedback(RobotEvent event)
    {
        this.events = event;
    }
}