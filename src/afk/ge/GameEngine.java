/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.ge;

import afk.gfx.GraphicsEngine;
import afk.london.London;

/**
 *
 * @author Jw
 */
public abstract class GameEngine implements Runnable
{

    protected GraphicsEngine gfxEngine;

    public abstract void startGame();

    @Override
    public abstract void run();

    protected abstract void gameLoop();

    protected abstract void updateGame();

    protected abstract void render(double alpha);
}
