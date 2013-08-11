/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.ge.tokyo;

import afk.ge.AbstractEntity;
import afk.bot.london.London;
import afk.bot.london.Robot;
import afk.ge.tokyo.ems.Engine;
import afk.ge.tokyo.ems.Entity;
import afk.ge.tokyo.ems.components.BBoxComponent;
import afk.ge.tokyo.ems.components.Bullet;
import afk.ge.tokyo.ems.components.Controller;
import afk.ge.tokyo.ems.components.Life;
import afk.ge.tokyo.ems.components.Renderable;
import afk.ge.tokyo.ems.components.State;
import afk.ge.tokyo.ems.components.TankController;
import afk.ge.tokyo.ems.components.Velocity;
import afk.ge.tokyo.ems.components.Weapon;
import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author Jw
 */
public class EntityManager
{
    public static final int WEAPON_RANGE = 50;
    public static final int WEAPON_DAMAGE = 20;
    public static final int BULLET_SPEED = 10;
    public static final float FIRE_RATE = 1f;
    public static final int WEAPON_AMMO = 0;
    public static final int SMALL_TANK_HP = 80;
    public static final String SMALL_TANK_TYPE = "smallTank";
    public static final Vec3 SMALL_TANK_EXTENTS = new Vec3(0.4385f,0.2505f,0.5f);

    int NUMCUBES = 5;
    int SPAWNVALUE = (int) (Tokyo.BOARD_SIZE * 0.45);
    public ArrayList<TankEntity> entities;
    public ArrayList<TankEntity> obstacles;
    private ArrayList<AbstractEntity> subEntities;
    London botEngine;
    Engine engine;
    private static final Vec3[] BOT_COLOURS =
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
    private Vec3[] SPAWN_POINTS =
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

    public EntityManager(London botEngine, Engine engine)
    {
        this.botEngine = botEngine;
        this.engine = engine;
        entities = new ArrayList<TankEntity>();
        obstacles = new ArrayList<TankEntity>();
        subEntities = new ArrayList<AbstractEntity>();
        System.out.println("SPAWN_POINTS: " + SPAWNVALUE);
    }

    public void spawnStuff()
    {
        createFloor();
        createGraphicWall(new Vec3(0, 0, -25), new Vec3(50, 1, 0.5f));
        createGraphicWall(new Vec3(0, 0, 25), new Vec3(50, 1, 0.5f));
        createGraphicWall(new Vec3(25, 0, 0), new Vec3(0.5f, 1, 50));
        createGraphicWall(new Vec3(-25, 0, 0), new Vec3(0.5f, 1, 50));
    }
    
    public void createFloor()
    {
        Entity floor = new Entity();
        floor.add(new State(Vec3.VEC3_ZERO, Vec3.VEC3_ZERO,
                new Vec3(Tokyo.BOARD_SIZE, Tokyo.BOARD_SIZE, Tokyo.BOARD_SIZE)));
        floor.add(new Renderable("floor", new Vec3(1.0f,1.0f,1.0f)));
        
        engine.addEntity(floor);
    }

    public void createGraphicWall(Vec3 pos, Vec3 scale)
    {
        Entity wall = new Entity();
        wall.add(new State(pos, Vec3.VEC3_ZERO, scale));
        wall.add(new BBoxComponent(scale.scale(0.5f)));
        wall.add(new Renderable("wall", new Vec3(0.75f, 0.75f, 0.75f)));

        engine.addEntity(wall);
    }

    private void createObstacles(Vec3 scale)
    {
        int min = -18;
        int max = 18;
        for (int i = 0; i < NUMCUBES; i++)
        {
            
            Vec3 pos = new Vec3(min + (int) (Math.random() * ((max - min) + 1)), 0, min + (int) (Math.random() * ((max - min) + 1)));

            createGraphicWall(pos, scale);

        }
    }

    void createBots()
    {
        spawnStuff();
        createObstacles(new Vec3(5, 5, 5));
        Robot[] bots = botEngine.getRobotInstances();
        for (int i = 0; i < bots.length; i++)
        {
            UUID id = bots[i].getId();
            createTankEntityNEU(id, SPAWN_POINTS[i], BOT_COLOURS[i]);
        }
    }
    private void createTankEntityNEU(UUID id,Vec3 spawnPoint, Vec3 colour)
    {
        Vec3 scale = new Vec3(2, 2, 2);

        Entity tank = new Entity();
        tank.add(new State(spawnPoint, Vec3.VEC3_ZERO, scale));
        // tank.add(new BBoxComponent(new Vec3(1.0f,0.127f,0.622f).multiply(scale))); // big tank collision box
        tank.add(new BBoxComponent(SMALL_TANK_EXTENTS.multiply(scale))); // small tank collision box
        tank.add(new Velocity(Vec3.VEC3_ZERO, Vec3.VEC3_ZERO));
        tank.add(new Weapon(WEAPON_RANGE, WEAPON_DAMAGE, BULLET_SPEED, 1.0f/FIRE_RATE, WEAPON_AMMO));
        tank.add(new Life(SMALL_TANK_HP));
        tank.add(new Renderable(SMALL_TANK_TYPE, colour));
        tank.add(new Controller(id));
        tank.add(new TankController());

        engine.addEntity(tank);
    }

//    public TankEntity createSmallTank(Robot botController, Vec3 spawnPoint, Vec3 colour)
//    {
//        float TOTAL_LIFE = 8;
//        float SCALE = 2.0f;
//        
//        GfxEntity oBBEntity = gfxEngine.createEntity(GfxEntity.NORMAL);
//        GfxEntity visionEntity = gfxEngine.createEntity(GfxEntity.NORMAL);
//
//        //OBB
//        oBBEntity.attachResource(cubeMesh);
//        oBBEntity.attachResource(primativeShader);
//        oBBEntity.yScale = 0.55f;
//        oBBEntity.xScale = 0.80f;
//        oBBEntity.colour = colour;
//        oBBEntity.opacity = 0.2f;
//
//        //vision sphere
//        visionEntity.attachResource(ringMesh);
//        visionEntity.attachResource(primativeShader);
//        visionEntity.setScale(5, 5, 5);
//        visionEntity.colour = colour;
////        visionEntity.opacity = 0.1f;
//        
//        tankGfxEntity.addChild(visionEntity);
//        tankGfxEntity.addChild(oBBEntity);
//
//        gfxEngine.getRootEntity().addChild(tankGfxEntity);
//        
//        tank.name = "tank" + (entities.size() - 1);
//        tank.setScaleForOBB(oBBEntity.getScale().scale(SCALE));
//    }

    public void createProjectileNEU(Entity parent, Weapon weapon, State current)
    {
        Entity projectile = new Entity();
        State state = new State(current, new Vec3(0, 0.8f, 0));
        state.scale = new Vec3(0.3f, 0.3f, 0.3f);
        projectile.add(state);
        float angle = -(float) Math.toRadians(state.rot.getY());
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        projectile.add(new Velocity(new Vec3(-weapon.speed*sin, 0, weapon.speed*cos), Vec3.VEC3_ZERO));
        projectile.add(new Renderable("projectile", new Vec3(0.5f,0.5f,0.5f)));
        projectile.add(new Bullet(weapon.range, weapon.damage, parent));

        engine.addEntity(projectile);
    }
    
    void updateEntities(float t, float delta)
    {
        for (int i = 0; i < entities.size(); i++)
        {
            entities.get(i).update(t, delta);
        }
        for (int i = 0; i < entities.size(); i++)
        {
            entities.get(i).checkCollisions();
        }
        for (int i = 0; i < subEntities.size(); i++)
        {
            subEntities.get(i).update(t, delta);
        }
    }

    void renderEntities(double alpha)
    {
        for (int i = 0; i < entities.size(); i++)
        {
            entities.get(i).render(alpha);
        }
        for (int i = 0; i < subEntities.size(); i++)
        {
            subEntities.get(i).render(alpha);
        }
        for (int i = 0; i < obstacles.size(); i++)
        {
            obstacles.get(i).render(alpha);
        }
    }

    void removeSubEntity(AbstractEntity entity)
    {
        subEntities.remove(entity);
    }

    void removeEntity(TankEntity entity)
    {
        entities.remove(entity);
    }

    void makeExplosion(Vec3 where, AbstractEntity parent, int type)
    {
        // FIXME?
    }
}
