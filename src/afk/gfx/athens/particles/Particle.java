package afk.gfx.athens.particles;

import afk.gfx.Camera;
import afk.gfx.athens.Mesh;
import afk.gfx.athens.Shader;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.support.FastMath;
import com.jogamp.graph.geom.AABBox;
import javax.media.opengl.GL2;

/**
 *
 * @author Daniel
 */
public class Particle
{
    Vec3 position;
    Vec3 velocity;
    float lifetime, maxLife;
    boolean alive = false;
    
    protected Particle()
    {
    }
    
    protected void set(Vec3 position, Vec3 velocity, float maxLife)
    {
        this.position = position;
        this.velocity = velocity;
        this.lifetime = 0;
        this.maxLife = maxLife;
        alive = true;
    }
    
    protected void update(float delta, Vec3 acceleration, AABBox bbox)
    {
        // TODO: possibly do interpolation or other fancy physics stuff
        position = position.add(velocity.scale(delta));
        velocity = velocity.add(acceleration.scale(delta));
        
        lifetime += delta;
        
        if (lifetime > maxLife
                || (bbox != null
                    && !bbox.contains(
                        position.getX(), position.getY(), position.getZ())))
            alive = false;
        
        // TODO: check stopping conditions
        // for now (testing) we'll just destroy when reaching y=0
        if (position.getY() < 0)
            alive = false;
    }
    
    private Mat4 createWorldMatrix(Camera camera)
    {
        Mat4 world = new Mat4(1f);

        world = Matrices.translate(world, position);
        
        float x = camera.dir.getX();
        float y = camera.dir.getY();
        float z = camera.dir.getZ();
        
        float roty, rotr;
        
        if (z >= 0)
            roty = -(float)FastMath.toDegrees(FastMath.atan2(z, x));
        else
            roty = (float)FastMath.toDegrees(FastMath.atan2(-z, x));
        
        if (y >= 0)
            rotr = -(float)FastMath.toDegrees(FastMath.atan2(-y, FastMath.sqrtFast(x*x+z*z)));
        else
            rotr = (float)FastMath.toDegrees(FastMath.atan2(y, FastMath.sqrtFast(x*x+z*z)));
        
        world = Matrices.rotate(
                world,
                rotr,
                camera.right);
        world = Matrices.rotate(
                world,
                roty,
                new Vec3(0,1,0));
        
        //TODO: world = Matrices.scale(world, getScale());
        
        return world;
    }
    
    protected void draw(GL2 gl, Mesh mesh, Camera camera, Shader shader)
    {
        
        shader.updateUniform(gl, "world", createWorldMatrix(camera));
        
        mesh.draw(gl);
    }
}
