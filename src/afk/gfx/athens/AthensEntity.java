package afk.gfx.athens;

import afk.gfx.Camera;
import afk.gfx.GfxEntity;
import afk.gfx.Resource;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import java.util.Collection;
import javax.media.opengl.GL2;

/**
 *
 * @author daniel
 */
public class AthensEntity extends GfxEntity
{
    public static final Vec3 X_AXIS = new Vec3(1,0,0);
    public static final Vec3 Y_AXIS = new Vec3(0,1,0);
    public static final Vec3 Z_AXIS = new Vec3(0,0,1);
    
    protected Mesh mesh = null;
    protected Texture texture = null;
    protected Object material = null; // TODO: placeholder for materials in future
    protected Shader shader = null;
    
    // collection of composite entities
    private Collection<AthensEntity> subEntities;
    protected AthensEntity parent;
    
    protected Mat4 createWorldMatrix()
    {
        Mat4 monkeyWorld = new Mat4(1f);

        monkeyWorld = Matrices.translate(monkeyWorld, getPosition());

        monkeyWorld = Matrices.rotate(monkeyWorld, yRot, Y_AXIS);
        
        monkeyWorld = Matrices.rotate(monkeyWorld, xRot, X_AXIS);

        monkeyWorld = Matrices.rotate(monkeyWorld, zRot, Z_AXIS);

        monkeyWorld = Matrices.scale(monkeyWorld, getScale());
        
        return monkeyWorld;
    }
    
    protected void update(float delta)
    {
        if (subEntities != null)
            for (AthensEntity entity :subEntities)
                entity.update(delta);
    }
    
    protected void draw(GL2 gl, Camera camera, Vec3 sun) // TODO: replace with Camera and Sun/Light objects later
    {
        this.draw(gl,camera,sun,createWorldMatrix());
    }
    
    protected void draw(GL2 gl, Camera camera, Vec3 sun, Mat4 worldMatrix)
    {
        // by default, active sets visibility of entity
        if (!active) return;
        
        if (shader != null)
        {
            shader.use(gl);

            if (texture != null)
            {
                texture.use(gl, GL2.GL_TEXTURE0);
                shader.updateUniform(gl, "tex", 0);
            }

            shader.updateUniform(gl, "world", worldMatrix);
            shader.updateUniform(gl, "view", camera.view);
            shader.updateUniform(gl, "projection", camera.projection);

            shader.updateUniform(gl, "sun", sun);
            shader.updateUniform(gl, "eye", camera.eye);

            if (colour != null)
                shader.updateUniform(gl, "colour", colour);
        }
        
        if (mesh != null)
            mesh.draw(gl);
        
        if (subEntities != null)
            for (AthensEntity entity :subEntities)
            {
                Mat4 subWorldMatrix = entity.createWorldMatrix();

                entity.draw(gl, camera, sun, worldMatrix.multiply(subWorldMatrix));
            }
    }
    
    public void attachResource(AthensResource resource)
    {
        switch (resource.getType())
        {
            case Resource.WAVEFRONT_MESH:
            case Resource.PRIMITIVE_MESH:
            case Resource.HEIGHTMAP_MESH:
                mesh = (Mesh)resource;
                break;
            case Resource.TEXTURE_2D:
            case Resource.TEXTURE_CUBE:
                texture = (Texture)resource;
                break;
            case Resource.SHADER:
                shader = (Shader)resource;
                break;
            case Resource.MATERIAL:
                material = resource; // TODO: change this when we create an actual Material class.
                break;
            default:
                // TODO: throw new ResourceNotCompatableException();
                break;
        }
    }
    
    @Override
    public void addEntity(GfxEntity entity)
    {
        if (subEntities == null)
            subEntities = new ArrayList<AthensEntity>();
        AthensEntity athensEntity = (AthensEntity)entity;
        subEntities.add(athensEntity);
        athensEntity.parent = this;
    }
    
    @Override
    public void removeEntity(GfxEntity entity)
    {
        if (subEntities == null) return;
        AthensEntity athensEntity = (AthensEntity)entity;
        if (subEntities.remove(athensEntity))
            athensEntity.parent = null;
    }

    @Override
    protected Collection<? extends GfxEntity> removeAllEntities()
    {
        if (subEntities == null) return new ArrayList<AthensEntity>();
        for (AthensEntity entity :subEntities)
        {
            entity.parent = null;
        }
        Collection<? extends GfxEntity> result = subEntities;
        subEntities = null;
        return result;
    }
    
    @Override
    public GfxEntity getParent()
    {
        return parent;
    }

    public Mesh getMesh()
    {
        if (mesh == null && parent != null)
            return parent.getMesh();
        return mesh;
    }

    public Shader getShader()
    {
        if (shader == null && parent != null)
            return parent.getShader();
        return shader;
    }

    public Object getMaterial()
    {
        if (material == null && parent != null)
            return parent.getMaterial();
        return material;
    }

    public Texture getTexture()
    {
        if (texture == null && parent != null)
            return parent.getTexture();
        return texture;
    }
    
}