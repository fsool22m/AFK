package afk.rome;

import afk.ge.ems.Engine;
import afk.ge.ems.Entity;
import afk.ge.ems.ISystem;
import afk.ge.tokyo.FlagSources;
import afk.ge.tokyo.HeightmapLoader;
import static afk.ge.tokyo.Tokyo.BOARD_SIZE;
import static afk.ge.tokyo.Tokyo.DELTA;
import static afk.ge.tokyo.Tokyo.MAX_FRAMETIME;
import static afk.ge.tokyo.Tokyo.NANOS_PER_SECOND;
import afk.ge.tokyo.ems.components.Camera;
import afk.ge.tokyo.ems.components.Display;
import afk.ge.tokyo.ems.components.Mouse;
import afk.ge.tokyo.ems.components.NoClipCamera;
import afk.ge.tokyo.ems.components.Renderable;
import afk.ge.tokyo.ems.components.State;
import afk.ge.tokyo.ems.factories.GenericFactory;
import afk.ge.tokyo.ems.factories.HeightmapFactory;
import afk.ge.tokyo.ems.factories.HeightmapFactoryRequest;
import afk.ge.tokyo.ems.nodes.HeightmapNode;
import afk.ge.tokyo.ems.systems.InputSystem;
import afk.ge.tokyo.ems.systems.NoClipCameraSystem;
import afk.ge.tokyo.ems.systems.RenderSystem;
import afk.ge.tokyo.ems.systems.SelectionSystem;
import afk.gfx.GfxEntity;
import static afk.gfx.Resource.HEIGHTMAP_MESH;
import static afk.gfx.Resource.SHADER;
import static afk.gfx.Resource.TEXTURE_2D;
import afk.gfx.athens.Athens;
import afk.gfx.athens.AthensEntity;
import afk.gfx.athens.ResourceManager;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import com.jogamp.newt.event.MouseEvent;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author daniel
 */
public class RomeEngine implements Runnable
{
    private Engine engine;
    private GenericFactory genericFactory;
    private boolean running;
    private float t = 0.0f;
    private EditableAthensTerrain terrain;
    private Entity ball;

    public RomeEngine(Athens athens)
    {
        engine = new Engine();
        genericFactory = new GenericFactory();
        
        ball = new Entity();
        ball.addComponent(new State(Vec3.VEC3_ZERO, Vec4.VEC4_ZERO, new Vec3(1)));
        ball.addComponent(new Renderable("sphere", GfxEntity.MAGENTA, 0.5f));
        engine.addEntity(ball);

        engine.addSystem(new NoClipCameraSystem());
        engine.addSystem(new InputSystem(athens));
        engine.addSystem(new ISystem() {
            public static final float STRENGTH = 0.01f;

            @Override
            public boolean init(Engine engine)
            {
                return true;
            }

            @Override
            public void update(float t, float dt)
            {
                Display display = engine.getGlobal(Display.class);
                Camera camera = engine.getGlobal(Camera.class);
                Mouse mouse = engine.getGlobal(Mouse.class);
                
                Vec3[] mp
                        = SelectionSystem.mouseToWorld(display, camera, mouse);
                
                HeightmapNode n = engine.getNodeList(HeightmapNode.class).get(0);
                Vec3 p = HeightmapLoader.getIntersection(mp[1], mp[0], 0.2f, n.heightmap);
                if (p == null) p = Vec3.VEC3_ZERO;
                State ballState = ball.getComponent(State.class);
                ballState.pos = p;
                
                if (engine.getFlag(FlagSources.KEYBOARD, KeyEvent.VK_DOWN))
                {
                    ballState.scale = ballState.scale.add(new Vec3(-0.1f));
                }
                if (engine.getFlag(FlagSources.KEYBOARD, KeyEvent.VK_UP))
                {
                    ballState.scale = ballState.scale.add(new Vec3(01f));
                }
                
                if (engine.getFlag(FlagSources.MOUSE, MouseEvent.BUTTON1))
                {
                    BufferedImage img = terrain.getHeightmap();
                    float x = ((p.getX()+BOARD_SIZE/2)/BOARD_SIZE)*img.getWidth();
                    float y = ((p.getZ()+BOARD_SIZE/2)/BOARD_SIZE)*img.getHeight();
                    float r = (ballState.scale.getX()*img.getWidth())/BOARD_SIZE;
                    Graphics2D g = img.createGraphics();
                    g.setPaint(new RadialGradientPaint(new Point2D.Float(x,y),
                            r, new float[]{0.5f,1.0f},
                            engine.getFlag(FlagSources.KEYBOARD, KeyEvent.VK_CONTROL)
                            ? new Color[]{new Color(0f,0f,0f, STRENGTH),new Color(0f,0f,0f,0f)}
                            : new Color[]{new Color(1f,1f,1f,STRENGTH),new Color(1f,1f,1f,0f)}
                    ));
                    g.fillOval((int)x, (int)y, (int)(r*2), (int)(r*2));
                    g.dispose();
                    terrain.update();
                }
            }

            @Override
            public void destroy()
            {
            }
        });
        engine.addSystem(new RenderSystem(athens));
        
        Entity cameraEntity = new Entity();
        Camera camera = new Camera(
                new Vec3(BOARD_SIZE, 60, 0), // eye
                new Vec3(0f, -10f, 0f), // at
                new Vec3(0f, 1f, 0f)); // up
        cameraEntity.addComponent(camera);
        cameraEntity.addComponent(new NoClipCamera(1, 5, 0.2f));
        engine.addEntity(cameraEntity);
        engine.addGlobal(camera);
        
        createFloor(athens);
    }
    
    private void createFloor(Athens athens)
    {
        String hm = "hm2";
        
        ResourceManager resourceManager = athens.getResourceManager();
        
        AthensEntity floorGfxEntity = new AthensEntity();
        terrain = (EditableAthensTerrain) resourceManager.getResource(HEIGHTMAP_MESH, "?"+hm);
        floorGfxEntity.attachResource(terrain);
        floorGfxEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "grass"));
        floorGfxEntity.attachResource(resourceManager.getResource(SHADER, "floor"));

        AthensEntity floorSidesEntity = new AthensEntity();
        floorSidesEntity.attachResource(terrain.getSides());
        floorSidesEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "dirt"));
        floorSidesEntity.attachResource(resourceManager.getResource(SHADER, "sides"));

        floorGfxEntity.addChild(floorSidesEntity);
        
        Entity entity = new HeightmapFactory().create(new HeightmapFactoryRequest(hm));
        engine.addEntity(entity);
        
        athens.injectAthensEntity(floorGfxEntity, entity.getComponent(Renderable.class));
    }

    public void start()
    {
        running = true;
        new Thread(this).start();
    }

    public void stop()
    {
        running = false;
    }

    @Override
    public void run()
    {
        double currentTime = System.nanoTime();
        double accumulator = 0.0f;
        while (running)
        {

            double newTime = System.nanoTime();
            double frameTime = (newTime - currentTime) / NANOS_PER_SECOND;
            if (frameTime > MAX_FRAMETIME)
            {
                frameTime = MAX_FRAMETIME;
            }
            currentTime = newTime;

            accumulator += frameTime;

            //any function called in this block should run at the fixed DELTA rate
            while (accumulator >= DELTA)
            {
                engine.update(t, DELTA);
                accumulator -= DELTA;
            }
        }
        engine.shutDown();
    }
}
