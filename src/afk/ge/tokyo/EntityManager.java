/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.ge.tokyo;

import afk.ge.AbstractEntity;
import afk.gfx.GfxEntity;
import afk.gfx.GraphicsEngine;
import afk.gfx.Resource;
import afk.london.London;
import afk.london.Robot;
import afk.london.RobotEvent;
import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Jw
 */
public class EntityManager
{

    public ArrayList<TankEntity> entities;
    private ArrayList<AbstractEntity> subEntities;
    private GraphicsEngine gfxEngine;
    Resource tankMesh;
    Resource tankShader;
    Resource floorMesh;
    Resource floorShader;
    Resource explosionProjectile;
    Resource explosionTank;
    Resource particleShader;
    Resource billboardMesh;
    
    London botEngine;
    
    protected AtomicBoolean loaded = new AtomicBoolean(false);

    public EntityManager(London botEngine, GraphicsEngine gfxEngine)
    {
        this.botEngine = botEngine;
        this.gfxEngine = gfxEngine;
        entities = new ArrayList<TankEntity>();
        subEntities = new ArrayList<AbstractEntity>();
    }

    public TankEntity createTank(Vec3 spawnPoint, Vec3 colour)
    {
        float TOTAL_LIFE = 5;
        GfxEntity tankGfxEntity = gfxEngine.createEntity(GfxEntity.NORMAL);
        
        tankGfxEntity.attachResource(tankMesh);
        tankGfxEntity.attachResource(tankShader);
            
        tankGfxEntity.colour = colour;
        tankGfxEntity.setPosition(spawnPoint);
        
        gfxEngine.getRootEntity().addChild(tankGfxEntity);
        
        TankEntity tank = new TankEntity(tankGfxEntity, this, TOTAL_LIFE);
        entities.add(tank);
        tank.name = "tank" + (entities.size() - 1);
        return tank;
    }

    public ProjectileEntity createProjectile(AbstractEntity parent)
    {

        float DAMAGE = 1.5f;
        //dont have a projectile model yet, mini tank will be bullet XD
        GfxEntity projectileGfxEntity = gfxEngine.createEntity(GfxEntity.NORMAL);
        
        projectileGfxEntity.attachResource(tankMesh);
        projectileGfxEntity.attachResource(tankShader);

        projectileGfxEntity.setScale(0.1f, 0.1f, 0.1f);
        projectileGfxEntity.setPosition(5, 10, 5);
        
        gfxEngine.getRootEntity().addChild(projectileGfxEntity);
        ProjectileEntity projectile = new ProjectileEntity(projectileGfxEntity, this, parent, DAMAGE);
        subEntities.add(projectile);
        projectile.name = parent.name + "projectile";
        return projectile;
    }

    void updateEntities(float t, float delta)
    {
        ArrayList commands = getInputs();
        for (int i = 0; i < entities.size(); i++)
        {
            boolean[] flags = (boolean[]) commands.get(i);
            entities.get(i).update(t, delta, flags);
        }
        for (int i = 0; i < subEntities.size(); i++)
        {
            subEntities.get(i).update(t, delta, null);
        }
        
        ArrayList<RobotEvent> events = new ArrayList<RobotEvent>();
        for (int i = 0; i < entities.size(); i++)
        {
            ArrayList<Float> visible = entities.get(i).checkVisible();
            // TODO: need to check hits as well.
            RobotEvent event = new RobotEvent(visible, false, false, entities.get(i).hitwall);
            events.add(event);
        }
        botEngine.feedback(events);

    }

    private ArrayList getInputs()
    {
        ArrayList tempFlags = new ArrayList();
        ArrayList<Robot> bots = botEngine.getRobots();
        for (int i = 0; i < bots.size(); i++)
        {
            bots.get(i).run();
            boolean[] temp = bots.get(i).getActionFlags();
            boolean[] tempArr = new boolean[temp.length];
            System.arraycopy(temp, 0, tempArr, 0, temp.length);
            bots.get(i).clearFlags();
            tempFlags.add(tempArr);
        }
        return tempFlags;
    }

    void renderEntities(float alpha)
    {
        for (int i = 0; i < entities.size(); i++)
        {
            entities.get(i).render(alpha);
        }
        for (int i = 0; i < subEntities.size(); i++)
        {
            subEntities.get(i).render(alpha);
        }
    }

    protected void loadResources()
    {
        tankMesh = gfxEngine.loadResource(Resource.WAVEFRONT_MESH, "tank");
        tankShader = gfxEngine.loadResource(Resource.SHADER, "monkey");
        floorMesh = gfxEngine.loadResource(Resource.PRIMITIVE_MESH, "quad");
        floorShader = gfxEngine.loadResource(Resource.SHADER, "floor");

        explosionProjectile = gfxEngine.loadResource(Resource.PARTICLE_PARAMETERS, "explosionProjectile");
        explosionTank = gfxEngine.loadResource(Resource.PARTICLE_PARAMETERS, "explosionTank");
        particleShader = gfxEngine.loadResource(Resource.SHADER, "particle");
        billboardMesh = gfxEngine.loadResource(Resource.PRIMITIVE_MESH, "billboard");

        gfxEngine.dispatchLoadQueue(new Runnable()
        {
            @Override
            public void run()
            {
                GfxEntity floorGfxEntity = gfxEngine.createEntity(GfxEntity.NORMAL);
                floorGfxEntity.attachResource(floorMesh);
                floorGfxEntity.attachResource(floorShader);
                floorGfxEntity.setScale(
                        Tokyo.BOARD_SIZE,
                        Tokyo.BOARD_SIZE,
                        Tokyo.BOARD_SIZE
                    );
                gfxEngine.getRootEntity().addChild(floorGfxEntity);
                
                /*
                TankEntity tank = createTank();
                tank.setColour(new Vec3(0.8f, 0.0f, 0.0f));
                TankEntity tank2 = createTank();
                tank2.setColour(new Vec3(0.0f, 0.0f, 0.8f));
                tank2.setState(new EntityState(new Vec3(0.0f, 0.0f, 10.0f)));
                */
                
                loaded.set(true);
            }
        });
        
    }

    void RomoveSubEntity(AbstractEntity entity)
    {
        GfxEntity gfxEntity = entity.getgfxEntity();
        gfxEntity.getParent().removeChild(gfxEntity);
        subEntities.remove(entity);
    }

    void RomoveEntity(TankEntity entity)
    {
        GfxEntity gfxEntity = entity.getgfxEntity();
        gfxEntity.getParent().removeChild(gfxEntity);
        entities.remove(entity);
    }

    // TODO: This is horrible! It's just for testing!
    // Please optimise/refactor before the universe ends!
    void makeExplosion(Vec3 where, AbstractEntity parent, int type)
    {
        GfxEntity explostion = gfxEngine.createEntity(GfxEntity.PARTICLE_EMITTER);
        
        if (type == 0)
        {
            explostion.attachResource(explosionProjectile);
        } else if (type == 1)
        {
            explostion.attachResource(explosionTank);
        }
        explostion.attachResource(particleShader);
        explostion.attachResource(billboardMesh);
        
        explostion.colour = parent.getgfxEntity().colour;
        explostion.setScale(Vec3.VEC3_ZERO);
        explostion.setPosition(where);
        gfxEngine.getRootEntity().addChild(explostion);

        explostion.active = true;
    }
}
