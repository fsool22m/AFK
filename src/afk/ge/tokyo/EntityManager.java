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
import afk.ge.tokyo.ems.components.Bullet;
import afk.ge.tokyo.ems.components.Controller;
import afk.ge.tokyo.ems.components.ParentEntity;
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
        wall.add(new Renderable("wall", new Vec3(0.75f, 0.75f, 0.75f)));

        engine.addEntity(wall);
        System.out.println("added graphic wall");
    }

    private void createObstacles(Vec3 scale)
    {
        int min = -18;
        int max = 18;
        for (int i = 0; i < NUMCUBES; i++)
        {
            
            Vec3 pos = new Vec3(min + (int) (Math.random() * ((max - min) + 1)), 0, min + (int) (Math.random() * ((max - min) + 1)));

            Entity cube = new Entity();
            cube.add(new State(pos, Vec3.VEC3_ZERO, scale));
            cube.add(new Renderable("wall", new Vec3(0.75f, 0.75f, 0.75f)));

            engine.addEntity(cube);
            System.out.println("added Obstacles wall");

            //;TankEntity obsticleCube = new TankEntity(null, gfxEntity, this, 100);


            // TODO: add collision System.
//            obsticleCube.setOBB();
//            obstacles.add(obsticleCube);

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
        tank.add(new Velocity(Vec3.VEC3_ZERO, Vec3.VEC3_ZERO));
        tank.add(new Weapon(10, 20, 10, 2, 2, 0));
        tank.add(new Renderable("smallTank", colour));
        tank.add(new Controller(id));
        tank.add(new TankController());

        engine.addEntity(tank);
        System.out.println("added Entity wall");
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
        //dont have a projectile model yet, mini tank will be bullet XD

        Entity projectile = new Entity();
        State state = new State(current, new Vec3(0, 0.8f, 0));
        state.scale = new Vec3(0.3f, 0.3f, 0.3f);
        projectile.add(state);
        float angle = -(float) Math.toRadians(state.rot.getY());
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        projectile.add(new Velocity(new Vec3(-weapon.speed*sin, 0, weapon.speed*cos), Vec3.VEC3_ZERO));
        projectile.add(new Renderable("projectile", new Vec3(0.5f,0.5f,0.5f)));
        projectile.add(new Bullet(weapon.range, weapon.damage));
        projectile.add(new ParentEntity(parent));

        engine.addEntity(projectile);
        System.out.println("added Bullet");
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
