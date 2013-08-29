package afk.bot.london;

/**
 * @author Jessica
 */
public abstract class TankRobot extends AbstractRobot
{

    public static final int NUM_ACTIONS = 5;
    //Index mapping of flag array
    public static final int MOVE_FRONT = 0;
    public static final int MOVE_BACK = 1;
    public static final int TURN_CLOCK = 2;
    public static final int TURN_ANTICLOCK = 3;
    public static final int ATTACK_ACTION = 4;

    public TankRobot()
    {
        super(NUM_ACTIONS);
    }

    // to make the super constructor hidden from subclasses
    private TankRobot(int numActions)
    {
        super(numActions);
    }

    /**
     * Moves the tank forward this game tick.
     */
    protected final void moveForward()
    {
        setFlag(MOVE_FRONT, true);
        setFlag(MOVE_BACK, false);
    }

    /**
     * Moves the tank backward this game tick.
     */
    protected final void moveBackwards()
    {
        setFlag(MOVE_BACK, true);
        setFlag(MOVE_FRONT, false);
    }

    /**
     * Turns the tank clockwise this game tick.
     */
    protected final void turnClockwise()
    {
        setFlag(TURN_CLOCK, true);
        setFlag(TURN_ANTICLOCK, false);
    }

    /**
     * Turns the tank anticlockwise this game tick.
     */
    protected final void turnAntiClockwise()
    {
        setFlag(TURN_ANTICLOCK, true);
        setFlag(TURN_CLOCK, false);
    }

    /**
     * Attempts to fire a projectile this game tick.
     */
    protected final void attack()
    {
        setFlag(ATTACK_ACTION, true);
    }
}