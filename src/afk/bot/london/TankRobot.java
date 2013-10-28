/*
 * Copyright (c) 2013 Triforce
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
 package afk.bot.london;

import afk.bot.Aimable;
import afk.bot.Attackable;
import afk.bot.Movable;
import afk.bot.Turnable;
import static afk.bot.london.HeliRobot.AIM_ANTICLOCK;
import static afk.bot.london.HeliRobot.AIM_CLOCK;
import static afk.bot.london.HeliRobot.AIM_DOWN;
import static afk.bot.london.HeliRobot.AIM_UP;
import static afk.bot.london.HeliRobot.ATTACK_ACTION;
import static afk.bot.london.HeliRobot.MOVE_BACK;
import static afk.bot.london.HeliRobot.MOVE_FRONT;
import static afk.bot.london.HeliRobot.MOVE_LEFT;
import static afk.bot.london.HeliRobot.MOVE_RIGHT;
import static afk.bot.london.HeliRobot.TURN_ANTICLOCK;
import static afk.bot.london.HeliRobot.TURN_CLOCK;

/**
 * @author Jessica
 */
public abstract class TankRobot extends EventBasedBot implements Movable,
        Turnable, Aimable, Attackable
{

    public static final int NUM_ACTIONS = 9;
    //Index mapping of flag array
    public static final int MOVE_FRONT = 0;
    public static final int MOVE_BACK = 1;
    public static final int TURN_CLOCK = 2;
    public static final int TURN_ANTICLOCK = 3;
    public static final int ATTACK_ACTION = 4;
    public static final int AIM_CLOCK = 5;
    public static final int AIM_ANTICLOCK = 6;
    public static final int AIM_UP = 7;
    public static final int AIM_DOWN = 8;

    public TankRobot()
    {
        super(NUM_ACTIONS);
    }

    @Override
    public void init()
    {
        super.init();
        setType("largeTank");
    }

    // to make the super constructor hidden from subclasses
    private TankRobot(int numActions)
    {
        super(numActions);
    }
    
    @Override
    public final void moveForward(int amount)
    {
        setActionValue(MOVE_FRONT, amount);
        setActionValue(MOVE_BACK, 0);
    }

    @Override
    public final void moveBackward(int amount)
    {
        setActionValue(MOVE_BACK, amount);
        setActionValue(MOVE_FRONT, 0);
    }
    
    @Override
    public final void turnClockwise(int amount)
    {
        setActionValue(TURN_CLOCK, amount);
        setActionValue(TURN_ANTICLOCK, 0);
    }

    @Override
    public final void turnAntiClockwise(int amount)
    {
        setActionValue(TURN_ANTICLOCK, amount);
        setActionValue(TURN_CLOCK, 0);
    }

    @Override
    public final void attack()
    {
        setActionValue(ATTACK_ACTION, 1);
    }

    @Override
    public final void aimClockwise(int amount)
    {
        setActionValue(AIM_CLOCK, amount);
        setActionValue(AIM_ANTICLOCK, 0);
    }

    @Override
    public final void aimAntiClockwise(int amount)
    {
        setActionValue(AIM_ANTICLOCK, amount);
        setActionValue(AIM_CLOCK, 0);
    }

    @Override
    public final void aimUp(int amount)
    {
        setActionValue(AIM_UP, amount);
        setActionValue(AIM_DOWN, 0);
    }

    @Override
    public final void aimDown(int amount)
    {
        setActionValue(AIM_DOWN, amount);
        setActionValue(AIM_UP, 0);
    }

    public final void target(VisibleRobot target, float give)
    {
        RobotUtils.target(this, this, target, events, give);
    }
}
