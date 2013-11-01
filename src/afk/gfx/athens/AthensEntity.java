/*
 * Copyright (c) 2013 Triforce - in association with the University of Pretoria and Epi-Use <Advance/>
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
 package afk.gfx.athens;

import afk.gfx.AbstractCamera;
import afk.gfx.GfxEntity;
import afk.gfx.Resource;
import com.hackoeur.jglm.Mat4;
import static com.hackoeur.jglm.Matrices.*;
import com.hackoeur.jglm.Vec3;
import java.util.ArrayList;
import java.util.Collection;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import static afk.gfx.GfxUtils.*;
import com.hackoeur.jglm.Vec4;

/**
 *
 * @author daniel
 */
public class AthensEntity extends GfxEntity
{

    protected Mesh mesh = null;
    protected Texture texture = null;
    protected Object material = null; // TODO: placeholder for materials in future
    protected Shader shader = null;
    // collection of composite entities
    private Collection<AthensEntity> children;
    protected AthensEntity parent = null;
    
    protected Mat4 worldMatrix = Mat4.MAT4_IDENTITY;
    protected boolean used = true;

    @Override
    public Vec3 getWorldPosition()
    {
        if (parent != null)
        {
            return parent.worldMatrix.multiply(position.toPoint()).getXYZ();
        }
        return position;
    }

    protected Mat4 createWorldMatrix()
    {
        Mat4 monkeyWorld = new Mat4(1f);

        monkeyWorld = translate(monkeyWorld, getWorldPosition());

        Vec4 worldRot = getWorldRotation();

        monkeyWorld = rotate(monkeyWorld, worldRot.getX(), X_AXIS);
        monkeyWorld = rotate(monkeyWorld, worldRot.getZ(), Z_AXIS);
        monkeyWorld = rotate(monkeyWorld, worldRot.getY(), Y_AXIS);
        // don't ask...
        monkeyWorld = rotate(monkeyWorld, worldRot.getW(), X_AXIS);

        monkeyWorld = scale(monkeyWorld, getWorldScale());

        return monkeyWorld;
    }

    @Override
    public Mat4 getWorldMatrix()
    {
        return worldMatrix;
    }
    
    protected void update(float delta)
    {
        if (children != null)
        {
            for (AthensEntity entity : children)
            {
                entity.update(delta);
            }
        }
    }
    protected void draw(GL2 gl, AbstractCamera camera, Vec3 sun)
    {
        // by default, active sets visibility of entity
        if (!active || opacity <= 0.0f)
        {
            return;
        }

        worldMatrix = createWorldMatrix();

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
            {
                shader.updateUniform(gl, "colour", colour);
            }

            shader.updateUniform(gl, "opacity", opacity);
        }

        if (opacity < 1.0f)
        {
            gl.glEnable(GL.GL_BLEND);
        }

        if (mesh != null)
        {
            mesh.draw(gl);
        }

        if (opacity < 1.0f)
        {
            gl.glDisable(GL.GL_BLEND);
        }

        if (children != null)
        {
            for (AthensEntity entity : children)
            {
                entity.draw(gl, camera, sun);
            }
        }
    }

    @Override
    public void attachResource(Resource resource)
    {
        switch (resource.getType())
        {
            case Resource.WAVEFRONT_MESH:
            case Resource.PRIMITIVE_MESH:
            case Resource.HEIGHTMAP_MESH:
                mesh = (Mesh) resource;
                break;
            case Resource.TEXTURE_2D:
            case Resource.TEXTURE_CUBE:
                texture = (Texture) resource;
                break;
            case Resource.SHADER:
                shader = (Shader) resource;
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
    public void addChild(GfxEntity entity)
    {
        if (children == null)
        {
            children = new ArrayList<AthensEntity>();
        }
        AthensEntity athensEntity = (AthensEntity) entity;
        children.add(athensEntity);
        athensEntity.parent = this;
    }

    @Override
    public void removeChild(GfxEntity entity)
    {
        if (children == null)
        {
            return;
        }
        AthensEntity athensEntity = (AthensEntity) entity;
        if (children.remove(athensEntity))
        {
            athensEntity.parent = null;
        }
    }

    @Override
    public Collection<? extends GfxEntity> removeAllChildren()
    {
        if (children == null)
        {
            return new ArrayList<AthensEntity>();
        }
        for (AthensEntity entity : children)
        {
            entity.parent = null;
        }
        Collection<? extends GfxEntity> result = children;
        children = null;
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
        {
            return parent.getMesh();
        }
        return mesh;
    }

    public Shader getShader()
    {
        if (shader == null && parent != null)
        {
            return parent.getShader();
        }
        return shader;
    }

    public Object getMaterial()
    {
        if (material == null && parent != null)
        {
            return parent.getMaterial();
        }
        return material;
    }

    public Texture getTexture()
    {
        if (texture == null && parent != null)
        {
            return parent.getTexture();
        }
        return texture;
    }
}