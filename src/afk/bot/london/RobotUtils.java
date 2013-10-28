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

import afk.bot.Attackable;
import afk.bot.Aimable;

/**
 *
 * @author Jw
 */
public class RobotUtils
{

    public static void target(Aimable aimable, Attackable attackable, VisibleRobot target, RobotEvent events, float give)
    {
        float bearing = target.bearing;
        float elevation = target.elevation - events.barrel;
        float diff = bearing * bearing + elevation * elevation;

        if (Float.compare(diff, give * give) < 0)
        {
            attackable.attack();
            return;
        }

        if (Float.compare(bearing, 0) < 0)
        {
            aimable.aimAntiClockwise(1);
        }
        if (Float.compare(bearing, 0) > 0)
        {
            aimable.aimClockwise(1);
        }

        if (Float.compare(elevation, 0) < 0)
        {
            aimable.aimDown(1);
        }
        if (Float.compare(elevation, 0) > 0)
        {
            aimable.aimUp(1);
        }
    }
}
