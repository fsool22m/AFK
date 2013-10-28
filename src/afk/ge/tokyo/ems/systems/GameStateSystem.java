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
 package afk.ge.tokyo.ems.systems;

import afk.game.GameListener;
import afk.ge.ems.Engine;
import afk.ge.ems.ISystem;
import afk.ge.tokyo.ems.components.GameState;
import afk.ge.tokyo.ems.components.ScoreBoard;
import afk.ge.tokyo.ems.nodes.ControllerNode;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Jw
 */
public class GameStateSystem implements ISystem
{

    Engine engine;

    @Override
    public boolean init(Engine engine)
    {
        this.engine = engine;
        return true;
    }

    @Override
    public void update(float t, float dt)
    {
        List<ControllerNode> nodes = engine.getNodeList(ControllerNode.class);
        ScoreBoard scoreboard = engine.getGlobal(ScoreBoard.class);
        GameState gameState = engine.getGlobal(GameState.class);

        int size = nodes.size();
        if (size <= 1)
        {
            if (size == 1)
            {
                gameState.winner = nodes.get(0).controller.id;
                Integer score = scoreboard.scores.get(gameState.winner);
                score += scoreboard.scores.size()-2;
                scoreboard.scores.put(gameState.winner, score);
            } else
            {
                gameState.winner = null;
            }
            gameState.gameOver = true;
        }

    }

    @Override
    public void destroy()
    {
    }
}
