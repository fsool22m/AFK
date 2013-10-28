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
 
import afk.bot.london.HeliRobot;
import afk.bot.london.VisibleRobot;
import java.util.List;

/**
 * Sample class of what coded bot will look like
 *
 * @author Jessica
 *
 */
public class AimHeliBot extends HeliRobot
{

    public AimHeliBot()
    {
        super();
    }

    @Override
    public void init()
    {
        super.init();
        setName("Hovering Duck");
    }

    @Override
    public void gotHit()
    {
        System.out.println("I got hit!");
    }

    @Override
    public void didHit()
    {
        System.out.println("I hit someone!");
    }

    @Override
    public void start()
    {
    }

    @Override
    public void hitObject()
    {
    }

    @Override
    public void sonarWarning(float[] distance)
    {
    }

    @Override
    public void robotVisible(List<VisibleRobot> visibleBots)
    {
        VisibleRobot visible = visibleBots.get(0);
        System.out.println("elevation: " + visible.elevation);
        System.out.println("barrel: " + events.barrel);
        target(visible, 0.6f);
    }

    @Override
    public void idle()
    {
    }
}
