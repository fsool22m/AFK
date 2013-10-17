package afk.bot.london;

import java.util.List;

/**
 *
 * @author Jw
 */
public abstract class EventBasedBot extends AbstractRobot
{

    private int numActions;
    private boolean first = true;
    private float sonarWarningDistance = 3.0f;
    private long time;
    private boolean idle;

    public EventBasedBot(int numActions)
    {
        super(numActions);
        this.numActions = numActions;
    }

    @Override
    public final void run()
    {
        
        // "use up" a tick in each action value
        for (int i = 0; i < numActions; i++)
        {
            int value = getActionValue(i) - 1;
            if (value >= 0)
            {
                setActionValue(i, value);
            }
        }

        idle = true;
        if (first)
        {
            idle = false;
            start();
            first = false;
        }

        for (int d = 0; d < events.sonar.distance.length; d++)
        {
            if (events.sonar.distance[d] < sonarWarningDistance)
            {
                idle = false;
                sonarWarning(events.sonar.distance);
                break;
            }
        }
        if (events.hitWall)
        {
            idle = false;
            hitObject();
        }
        if (events.didHit)
        {
            idle = false;
            didHit();
        }
        if (!events.visibleBots.isEmpty())
        {
            idle = false;
            robotVisible(events.visibleBots);
        }
        if (events.gotHit)
        {
            idle = false;
            gotHit();
        }
        for (int i = 0; i < numActions; i++)
        {
            if (getActionValue(i) > 0 )
            {
                idle = false;
                break;
            }
        }
        
        if (idle)
        {
            idle();
        }

        time++;
    }

    /**
     * Sets the sonar warning distance for sonar events. Sonar warning events
     * are handled by the sonarWarning(int) method.
     *
     * @param sonarWarningDistance the new sonar warning distance.
     */
    public final void setSonarWarningDistance(float sonarWarningDistance)
    {
        this.sonarWarningDistance = sonarWarningDistance;
    }

    /**
     * Get the number of ticks since the start of the game.
     *
     * @return the number of ticks since the start of the game.
     */
    public long getTime()
    {
        return time;
    }

    /**
     * Called at the start of the game. User must give the robot's first
     * commands here.
     */
    public void start()
    {
        idle = true;
    }

    /**
     * Called when the robot hits an object (e.g. a wall or another bot).
     */
    public void hitObject()
    {
        idle = true;
    }

    /**
     * Called when the robot gets hit by another robot's projectile.
     */
    public void gotHit()
    {
        idle = true;
    }

    /**
     * Called when the robot successfully landed a shot on another robot.
     */
    public void didHit()
    {
        idle = true;
    }

    /**
     * Called when one of the sonars reads a measurment below the given
     * threshold. The threshold can be set through
     * setSonarWarningDistance(float).
     * @param distance the distances of the sonar in each direction.
     * Distance indices can be found in afk.bot.london.Sonar.
     */
    public void sonarWarning(float[] distance)
    {
        idle = true;
    }

    /**
     * Called when at least one robot is visible.
     * @param visibleBots the list of visible bots. Guaranteed to not be empty.
     */
    public void robotVisible(List<VisibleRobot> visibleBots)
    {
        idle = true;
    }

    /**
     * Called when no events were triggered during the tick AND the robot has no
     * commands left to execute,
     */
    public abstract void idle();
}