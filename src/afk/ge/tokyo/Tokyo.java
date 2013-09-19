/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.ge.tokyo;

import afk.bot.RobotEngine;
import afk.ge.GameEngine;
import afk.gfx.GraphicsEngine;
import afk.ge.tokyo.ems.Engine;
import afk.ge.tokyo.ems.systems.CollisionSystem;
import afk.ge.tokyo.ems.systems.SnapToTerrainSystem;
import afk.ge.tokyo.ems.systems.DebugSystem;
import afk.ge.tokyo.ems.systems.LifeSystem;
import afk.ge.tokyo.ems.systems.LifetimeSystem;
import afk.ge.tokyo.ems.systems.MovementSystem;
import afk.ge.tokyo.ems.systems.ParticleSystem;
import afk.ge.tokyo.ems.systems.ProjectileSystem;
import afk.ge.tokyo.ems.systems.RenderSystem;
import afk.ge.tokyo.ems.systems.RobotSystem;
import afk.ge.tokyo.ems.systems.TankControllerSystem;
import afk.ge.tokyo.ems.systems.VisionSystem;
import afk.gfx.GfxUtils;
import java.util.UUID;

/**
 *
 * @author Jw
 */
public class Tokyo implements GameEngine, Runnable
{

    private EntityManager entityManager;
    private Engine engine;
    private GraphicsEngine gfxEngine;
    private boolean running = true;
    private boolean paused = false;
    public static final float BOARD_SIZE = 100;
    public final static float GAME_SPEED = 30;
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
//test()
    private long lastUpdate;
    private float time = 0.0f;
    private float lastFPS = 0.0f;
    private float fps = 0.0f;
//

    public Tokyo(GraphicsEngine gfxEngine, RobotEngine botEngine)
    {
        engine = new Engine();

        System.out.println("MAX_FRAMETIME = " + MAX_FRAMETIME);
        System.out.println("DELTA = " + DELTA);

        this.gfxEngine = gfxEngine;

        entityManager = new EntityManager(engine);
        System.out.println("gfx" + gfxEngine.getFPS());

        ///possible move somewhere else later///
        engine.addSystem(new RobotSystem(botEngine)); // FIXME: remove passing of bot engine once db is done
        engine.addSystem(new TankControllerSystem(entityManager));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new SnapToTerrainSystem());
        engine.addSystem(new ProjectileSystem(entityManager));
        engine.addSystem(new LifeSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new ParticleSystem(entityManager));
        engine.addSystem(new LifetimeSystem(entityManager));
        engine.addSystem(new VisionSystem());
        engine.addSystem(new RenderSystem(gfxEngine));
        // TODO: if (DEBUG)  ...
        engine.addSystem(new DebugSystem(botEngine, entityManager));
        ///
    }

    @Override
    public void startGame(UUID[] participants)
    {
        entityManager.spawnStuff();
        //entityManager.createObstacles(new Vec3(5, 5, 5));
        for (int i = 0; i < participants.length; i++)
        {
            engine.addEntity(entityManager.createTankEntityNEU(
                    participants[i],
                    EntityManager.SPAWN_POINTS[i],
                    EntityManager.BOT_COLOURS[i]));
        }

        new Thread(this).start();
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

    @Override
    public void run()
    {
        double currentTime = System.nanoTime();
        lastUpdate = System.nanoTime();//
        double counter = 0.0f;//
        double accumulator = 0.0f;
        int i = 0;
        while (running)
        {
            while (paused)
            {
                gfxEngine.redisplay();
            }
//            System.out.println(speedMultiplier+"    "+ LOGIC_DELTA + "   " + DELTA);
            double newTime = System.nanoTime();
            double frameTime = (newTime - currentTime) / NANOS_PER_SECOND;
//                    System.out.println("frameTime: "+ frameTime);
            if (frameTime > MAX_FRAMETIME)
            {
                frameTime = MAX_FRAMETIME;
            }
            currentTime = newTime;

            accumulator += frameTime;

            if (speedMultiplier < 1)
            {
                while (accumulator >= DELTA)
                {
                    engine.update(t, DELTA);
                    t += DELTA;
                    accumulator -= DELTA;
                    counter += DELTA;
                    while (counter >= LOGIC_DELTA)
                    {
                        //any function called in this block should run at the fixed DELTA rate
                        test();
                        counter -= LOGIC_DELTA;
                    }
                }
            } else
            {
                while (accumulator >= LOGIC_DELTA)
                {
                    engine.update(t, DELTA);
                    t += LOGIC_DELTA;
                    accumulator -= LOGIC_DELTA;
                    counter += LOGIC_DELTA;
                    while (counter >= DELTA)
                    {
                        //any function called in this block should run at the fixed DELTA rate
                        test();
                        counter -= DELTA;
                    }
                }
            }
            double alpha = accumulator / LOGIC_DELTA;
            gfxEngine.redisplay();
        }
    }

    private void test()
    {
        long nTime = System.nanoTime();
        long nanos = nTime - lastUpdate;
        lastUpdate = nTime;
        float delta = nanos / (float) NANOS_PER_SECOND;
        //time += delta;
        lastFPS += delta;

        if (lastFPS >= 1.0f)
        {
            fps = (1.0f / delta);
            System.out.println(String.format(delta + "   DELTA timing: %.0f", fps));
            lastFPS = 0;
        }
    }
}
