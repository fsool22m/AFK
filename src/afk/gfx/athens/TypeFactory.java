package afk.gfx.athens;

import afk.gfx.GfxEntity;
import com.hackoeur.jglm.Vec3;
import java.util.HashMap;
import java.util.Map;
import static afk.gfx.Resource.*;
import static afk.gfx.GfxEntity.*;

/**
 *
 * @author daniel
 */
public class TypeFactory
{
    /// TEMPORARY (maybe) CLASS UNTIL CONFIG ENGINE/DATABASE/SYSTEM/WHATEVER ACTUALLY HAPPENS
    /// (so sort of a placeholder then)
    
    
    private Map<String, TypeFactoryThing> factories = new HashMap<String, TypeFactoryThing>();
    private ResourceManager resourceManager;

    public TypeFactory(ResourceManager resourceManager)
    {
        this.resourceManager = resourceManager;
        
        factories.put("heli", new HeliFactory());
        factories.put("heliBlades", new RotorFactory());
        factories.put("largeTankBase", new LargeTankBaseFactory());
        factories.put("largeTankTurret", new LargeTankTurretFactory());
        factories.put("largeTankBarrel", new LargeTankBarrelFactory());
        factories.put("smallTankBase", new SmallTankBaseFactory());
        factories.put("smallTankTurret", new SmallTankTurretFactory());
        factories.put("smallTankBarrel", new SmallTankBarrelFactory());
        factories.put("projectile", new ProjectileFactory());
        factories.put("wall", new WallFactory());
        factories.put("floor", new FloorFactory());
        factories.put("particle", new ParticleFactory());
    }
    
    public AthensEntity createInstance(String type)
    {
        return factories.get(type).create();
    }
    
    private interface TypeFactoryThing
    {
        public AthensEntity create();
    }
    private class LargeTankBaseFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity rootGfxEntity = createEntity(NORMAL);
            AthensEntity tankTracksEntity = createEntity(NORMAL);
            AthensEntity tankWheelsEntity = createEntity(NORMAL);
            AthensEntity tankShadowEntity = createEntity(NORMAL);

            rootGfxEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "large_tank_body"));
            rootGfxEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/large_tank_body"));
            rootGfxEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));

            tankTracksEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "large_tank_tracks"));
            tankTracksEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/large_tank_tracks"));
            tankTracksEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            tankTracksEntity.colour = new Vec3(0.4f, 0.4f, 0.4f);

            tankWheelsEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "large_tank_wheels"));
            tankWheelsEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/large_tank_wheels"));
            tankWheelsEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));

            tankShadowEntity.attachResource(resourceManager.getResource(PRIMITIVE_MESH, "quad"));
            tankShadowEntity.attachResource(resourceManager.getResource(SHADER, "simpleshadow"));
            tankShadowEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/large_tank_shadow"));
            tankShadowEntity.colour = Vec3.VEC3_ZERO;
            tankShadowEntity.opacity = 0.99f;
            tankShadowEntity.position = new Vec3(0,0.01f,0);
            tankShadowEntity.scale = new Vec3(1.5f, 0, 1.5f);
            
            rootGfxEntity.addChild(tankWheelsEntity);
            rootGfxEntity.addChild(tankTracksEntity);
            rootGfxEntity.addChild(tankShadowEntity);
            
            return rootGfxEntity;
        }
        
    }
    private class LargeTankTurretFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity tankTurretEntity = createEntity(NORMAL);
            tankTurretEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "large_tank_turret"));
            tankTurretEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/large_tank_turret"));
            tankTurretEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            //tankTurretEntity.position = new Vec3(0.0f, 0.17623f, -0.15976f);
            return tankTurretEntity;
        }
    }
    private class LargeTankBarrelFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity tankBarrelEntity = createEntity(NORMAL);
            tankBarrelEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "large_tank_barrel"));
            tankBarrelEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/large_tank_barrel"));
            tankBarrelEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            //tankBarrelEntity.position = new Vec3(0.0f, 0.03200f, 0.22199f);
            return tankBarrelEntity;
        }
    }
    private class SmallTankBaseFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity rootGfxEntity = createEntity(NORMAL);
            AthensEntity tankTracksEntity = createEntity(NORMAL);
            AthensEntity tankWheelsEntity = createEntity(NORMAL);
            AthensEntity tankShadowEntity = createEntity(NORMAL);

            rootGfxEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "small_tank_body"));
            rootGfxEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/small_tank_body"));
            rootGfxEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));

            tankTracksEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "small_tank_tracks"));
            tankTracksEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/small_tank_tracks"));
            tankTracksEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            tankTracksEntity.colour = new Vec3(0.4f, 0.4f, 0.4f);

            tankWheelsEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "small_tank_wheels"));
            tankWheelsEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/small_tank_wheels"));
            tankWheelsEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));

            tankShadowEntity.attachResource(resourceManager.getResource(PRIMITIVE_MESH, "quad"));
            tankShadowEntity.attachResource(resourceManager.getResource(SHADER, "simpleshadow"));
            tankShadowEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/small_tank_shadow"));
            tankShadowEntity.colour = Vec3.VEC3_ZERO;
            tankShadowEntity.opacity = 0.99f;
            tankShadowEntity.position = new Vec3(0,0.01f,0);
            tankShadowEntity.scale = new Vec3(1.5f, 0, 1.5f);
            
            rootGfxEntity.addChild(tankWheelsEntity);
            rootGfxEntity.addChild(tankTracksEntity);
            rootGfxEntity.addChild(tankShadowEntity);
            
            return rootGfxEntity;
        }
        
    }
    private class SmallTankTurretFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity tankTurretEntity = createEntity(NORMAL);
            tankTurretEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "small_tank_turret"));
            tankTurretEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/small_tank_turret"));
            tankTurretEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            //tankTurretEntity.position = new Vec3(0.0f, 0.17623f, -0.15976f);
            return tankTurretEntity;
        }
    }
    private class SmallTankBarrelFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity tankBarrelEntity = createEntity(NORMAL);
            tankBarrelEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "small_tank_barrel"));
            tankBarrelEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/small_tank_barrel"));
            tankBarrelEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            //tankBarrelEntity.position = new Vec3(0.0f, 0.03200f, 0.22199f);
            return tankBarrelEntity;
        }
    }
    private class ProjectileFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity projectileGfxEntity = createEntity(GfxEntity.NORMAL);

            projectileGfxEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "shell"));
            projectileGfxEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            
            return projectileGfxEntity;
        }
        
    }
    private class WallFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity gfxEntity = createEntity(GfxEntity.NORMAL);
            gfxEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "cube"));
            gfxEntity.attachResource(resourceManager.getResource(SHADER, "primatives"));
            
            return gfxEntity;
        }
        
    }
    private class FloorFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity floorGfxEntity = createEntity(GfxEntity.NORMAL);
            floorGfxEntity.attachResource(resourceManager.getResource(HEIGHTMAP_MESH, "hm2"));
            floorGfxEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "heightmaps/hm2"));
            floorGfxEntity.attachResource(resourceManager.getResource(SHADER, "floor"));
            
            return floorGfxEntity;
        }
        
    }
    private class HeliFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity heliGfxEntity = createEntity(GfxEntity.NORMAL);
            heliGfxEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "heli_base"));
            heliGfxEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            heliGfxEntity.attachResource(resourceManager.getResource(TEXTURE_2D, "lightmaps/heli_base"));
            
            return heliGfxEntity;
        }
        
    }
    private class RotorFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity heliGfxEntity = createEntity(GfxEntity.NORMAL);
            heliGfxEntity.attachResource(resourceManager.getResource(WAVEFRONT_MESH, "heli_blades"));
            heliGfxEntity.attachResource(resourceManager.getResource(SHADER, "monkey"));
            
            return heliGfxEntity;
        }
        
    }
    private class ParticleFactory implements TypeFactoryThing
    {

        @Override
        public AthensEntity create()
        {
            AthensEntity particle = createEntity(GfxEntity.BILLBOARD_SPHERICAL);
            
            particle.attachResource(resourceManager.getResource(SHADER, "texturedParticle"));
            particle.attachResource(resourceManager.getResource(PRIMITIVE_MESH, "billboard"));
            particle.attachResource(resourceManager.getResource(TEXTURE_2D, "explosion"));
            
            return particle;
        }
        
    }

    protected AthensEntity createEntity(int behaviour)
    {
        // TODO: research possible performance hit is we use Class.newInstance() instead of using ints

        AthensEntity entity;
        switch (behaviour)
        {
            case NORMAL:
                entity = new AthensEntity();
                break;
            case BILLBOARD_SPHERICAL:
                entity = new BillboardEntity(true);
                break;
            case BILLBOARD_CYLINDRICAL:
                entity = new BillboardEntity(false);
                break;
            default:
                // TODO: throw new InvalidEntityBehaviourException();
                return null;
        }

        return entity;
    }
}
