/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.ge.tokyo;

import afk.bot.Robot;
import afk.bot.RobotEngine;
import afk.game.GameMaster;
import afk.ge.GameEngine;
import afk.gfx.GraphicsEngine;
import afk.ge.ems.Engine;
import afk.ge.ems.Entity;
import afk.ge.ems.FactoryException;
import afk.ge.tokyo.ems.components.Camera;
import afk.ge.tokyo.ems.components.GameState;
import afk.ge.tokyo.ems.components.Mouse;
import afk.ge.tokyo.ems.components.NoClipCamera;
import afk.ge.tokyo.ems.components.ScoreBoard;
import afk.ge.tokyo.ems.factories.*;
import afk.ge.tokyo.ems.systems.*;
import afk.gfx.GfxUtils;
import com.hackoeur.jglm.Vec3;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Jw
 */
public class Tokyo implements GameEngine, Runnable
{

    private Engine engine;
    private boolean running = true;
    private boolean paused = false;
    public static final float BOARD_SIZE = 100;
    public final static float GAME_SPEED = 60;
    private float t = 0.0f;
    public final static float DELTA = 1.0f / GAME_SPEED;
    public static float LOGIC_DELTA = DELTA;
    private float speedMultiplier = 1;
    public final static double NANOS_PER_SECOND = (double) GfxUtils.NANOS_PER_SECOND;
    //get NUM_RENDERS from GraphicsEngine average fps..?, currently hard coded
    public final static double TARGET_FPS = 60;
    public final static double MIN_FPS = 25;
    public final static double MIN_FRAMETIME = 1.0f / TARGET_FPS;
    public final static double MAX_FRAMETIME = 1.0f / MIN_FPS;
    private RobotEngine botEngine;
    private RobotFactory robotFactory;
    private GenericFactory genericFactory;
    private ScoreBoard scoreboard;
    private GameState gameState;
    private GameMaster game;

    public Tokyo(GraphicsEngine gfxEngine, RobotEngine botEngine, GameMaster game)
    {
        this.game = game;
        this.botEngine = botEngine;
        engine = new Engine();

        genericFactory = new GenericFactory();
        robotFactory = new RobotFactory(botEngine.getConfigManager(), genericFactory);

        System.out.println("MAX_FRAMETIME = " + MAX_FRAMETIME);
        System.out.println("DELTA = " + DELTA);

        System.out.println("gfx" + gfxEngine.getFPS());

        engine.addLogicSystem(new SpawnSystem());
        engine.addLogicSystem(new PaintSystem());
        engine.addLogicSystem(new RobotSystem(botEngine));
        engine.addLogicSystem(new TracksSystem());
        engine.addLogicSystem(new TurretSystem());
        engine.addLogicSystem(new BarrelSystem());
        engine.addLogicSystem(new HelicopterSystem());
        engine.addLogicSystem(new MovementSystem());
        engine.addLogicSystem(new TerrainDisplacementSystem());
        engine.addLogicSystem(new SnapToTerrainSystem());
        engine.addLogicSystem(new AngleConstraintSystem());
        engine.addLogicSystem(new TurretFeedbackSystem());
        engine.addLogicSystem(new BarrelFeedbackSystem());
        engine.addLogicSystem(new ProjectileSystem());
        engine.addLogicSystem(new LifeSystem());
        engine.addLogicSystem(new CollisionSystem());
        engine.addLogicSystem(new ParticleSystem());
        engine.addLogicSystem(new LifetimeSystem());
        engine.addLogicSystem(new VisionSystem());
        engine.addLogicSystem(new SonarSystem());
        engine.addLogicSystem(new RobotStateFeedbackSystem());
        engine.addLogicSystem(new TextLabelSystem());
        engine.addLogicSystem(new GameStateSystem());

        engine.addSystem(new InputSystem(gfxEngine));
        engine.addSystem(new NoClipCameraSystem());
        engine.addSystem(new RenderSystem(gfxEngine));

        // TODO: if (DEBUG)  ...
        DebugRenderSystem wireFramer = new DebugRenderSystem(gfxEngine);
        engine.addSystem(new DebugSystem(botEngine, wireFramer));
        ///

        engine.addGlobal(new Mouse());
        engine.addGlobal(scoreboard = new ScoreBoard());
        engine.addGlobal(gameState = new GameState());

        // TODO: put this somewhere else?
        Entity entity = new Entity();
        Camera camera = new Camera(
                new Vec3(10f, 10f, 10f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 1f, 0f));
        entity.addComponent(camera);
        entity.addComponent(new NoClipCamera(1, 5, 0.2f));
        engine.addEntity(entity);
        engine.addGlobal(camera);
    }
    // TODO: make these customisable/generic/not hard-coded/just somewhere else!
    public static final int SPAWNVALUE = (int) (BOARD_SIZE * 0.45);
    public static final Vec3[] BOT_COLOURS =
    {
        new Vec3(1, 0, 0),
        new Vec3(0, 0, 1),
        new Vec3(0, 1, 0),
        new Vec3(1, 1, 0),
        new Vec3(1, 0, 1),
        new Vec3(0, 1, 1),
        new Vec3(0.95f, 0.95f, 0.95f),
        new Vec3(0.2f, 0.2f, 0.2f)
    };
    public static final Vec3[] SPAWN_POINTS =
    {
        new Vec3(-SPAWNVALUE, 0, -SPAWNVALUE),
        new Vec3(SPAWNVALUE, 0, SPAWNVALUE),
        new Vec3(-SPAWNVALUE, 0, SPAWNVALUE),
        new Vec3(SPAWNVALUE, 0, -SPAWNVALUE),
        new Vec3(0, 0, -SPAWNVALUE),
        new Vec3(0, 0, SPAWNVALUE),
        new Vec3(-SPAWNVALUE, 0, 0),
        new Vec3(SPAWNVALUE, 0, 0)
    };

    @Override
    public void startGame()
    {
        spawnStuff();
        Robot[] participants = botEngine.getParticipants();
        try
        {

            for (int i = 0; i < participants.length; i++)
            {
                engine.addEntity(robotFactory.create(
                        new RobotFactoryRequest(participants[i], SPAWN_POINTS[i], BOT_COLOURS[i])));
                scoreboard.scores.put(participants[i].getId(), 0);
            }
            new Thread(this).start();

        } catch (FactoryException ex)
        {
            // FIXME: this needs to propagate somewhere else!
            ex.printStackTrace(System.err);
        }
    }

    // TODO: this should be put in a map file
    private void spawnStuff()
    {
        engine.addEntity(new HeightmapFactory().create(new HeightmapFactoryRequest("hm2")));
        ObstacleFactory factory = new ObstacleFactory();

        engine.addEntity(factory.create(new ObstacleFactoryRequest(new Vec3(0, 0, -Tokyo.BOARD_SIZE / 2), new Vec3(Tokyo.BOARD_SIZE, 20, 0.5f), "wall", false)));
        engine.addEntity(factory.create(new ObstacleFactoryRequest(new Vec3(0, 0, Tokyo.BOARD_SIZE / 2), new Vec3(Tokyo.BOARD_SIZE, 20, 0.5f), "wall", false)));
        engine.addEntity(factory.create(new ObstacleFactoryRequest(new Vec3(Tokyo.BOARD_SIZE / 2, 0, 0), new Vec3(0.5f, 20, Tokyo.BOARD_SIZE), "wall", false)));
        engine.addEntity(factory.create(new ObstacleFactoryRequest(new Vec3(-Tokyo.BOARD_SIZE / 2, 0, 0), new Vec3(0.5f, 20, Tokyo.BOARD_SIZE), "wall", false)));
    }

    private void gameOverDebug()
    {
        if (gameState.gameOver)
        {
            if (gameState.winner == null)
            {
                System.out.println("DRAW :(");
            } else
            {
                System.out.println("WINNER " + gameState.winner);
            }
            for (Map.Entry<UUID, Integer> score : scoreboard.scores.entrySet())
            {
                System.out.println(score.getKey() + " scored " + score.getValue() + " points.");
            }
        }
    }

    @Override
    public void playPause()
    {
        System.out.println("playPause() - " + paused);
        paused = !paused;
        System.out.println("paused: " + paused);
    }

    @Override
    public float getSpeed()
    {
        return speedMultiplier;
    }

    @Override
    public void increaseSpeed()
    {
        speedMultiplier *= 2;
        LOGIC_DELTA = 1 / (GAME_SPEED * speedMultiplier);
    }

    @Override
    public void decreaseSpeed()
    {
        speedMultiplier /= 2;
        LOGIC_DELTA = 1.0f / (GAME_SPEED * speedMultiplier);
    }

    // TODO: put this code into a separate timer class
    @Override
    public void run()
    {
        double currentTime = System.nanoTime();
        double accumulator = 0.0f;
        double logicAccumulator = 0.0f;
        while (running)
        {
            double newTime = System.nanoTime();
            double frameTime = (newTime - currentTime) / NANOS_PER_SECOND;
            if (frameTime > MAX_FRAMETIME)
            {
                frameTime = MAX_FRAMETIME;
            }
            currentTime = newTime;

            logicAccumulator += frameTime;
            accumulator += frameTime;

            //any function called in this block should run at the fixed DELTA rate
            while (accumulator >= DELTA)
            {
                engine.update(t, DELTA);
                accumulator -= DELTA;
            }

            //any function called in this block run at the current speedMultiplier speed
            while (logicAccumulator >= LOGIC_DELTA)
            {
                if (!paused && !gameState.gameOver)
                {
                    engine.updateLogic(t, DELTA);
                    if (gameState.gameOver)
                    {
                        gameOverDebug();
                        game.gameOver(new GameResult(gameState.winner, scoreboard.scores));
                    }
                }
                t += DELTA;
                logicAccumulator -= LOGIC_DELTA;
            }
        }
    }
}
