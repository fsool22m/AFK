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
 
import afk.bot.london.TankRobot;
import static afk.bot.london.TankRobot.MOVE_BACK;
import afk.bot.london.VisibleRobot;
import java.util.List;

/**
 * Sample class of what coded bot will look like
 *
 * @author Jessica
 *
 */
public class RandomBot extends TankRobot
{

    private int AVOIDANCE = 50;

    public RandomBot()
    {
        super();
    }

    @Override
    public void init()
    {
        super.init();
        setName("Pampoen");
    }

    @Override
    public void start()
    {
        idle();
    }

    @Override
    public void hitObject()
    {
        if (getActionValue(MOVE_BACK) > 0)
        {
            clearActions();
            moveForward(AVOIDANCE);
        } else
        {
            clearActions();
            moveBackward(AVOIDANCE);
        }
    }

    @Override
    public void robotVisible(List<VisibleRobot> visibleBots)
    {
        clearActions();
        target(visibleBots.get(0), 0.6f);
    }

    @Override
    public void idle()
    {
        startTimer((int) (Math.random() * 100), new Runnable() {
            @Override
            public void run()
            {
                turnAntiClockwise((int) (Math.random() * 360));
            }
        });

        moveForward(200);
    }
}
