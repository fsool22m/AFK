/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.ge;

import afk.gfx.GraphicsEngine;
import afk.bot.london.London;

/**
 *
 * @author Jw
 */
public abstract class GameEngine implements Runnable
{

    protected GraphicsEngine gfxEngine;
    protected London botEngine;

//    protected abstract void loadResources();

//    public abstract void addEntity(AbstractEntity tankEntity); //done by entityManager now

    public abstract void startGame();
    
    @Override
    public abstract void run();

    protected abstract void gameLoop();

    protected abstract void updateGame();

    protected abstract void render(double alpha);
}
