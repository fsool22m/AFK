package afk.gfx.athens.particles;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.media.opengl.GL2;

/**
 *
 * @author Daniel
 */
public class ParticleEmitter
{
    /// PARAMETERS ///
    
    Vec3 minPosition, maxPosition;
    Vec3 direction, acceleration;
    float angleJitter;
    float minSpeed, maxSpeed;
    
    Random rand = new Random();
    
    /** Time between spawns. Use zero for "explosion" */
    float spawnRate;
    
    /// STUFF ///
    
    Vec3 tangent, bitangent;
    
    private float timeSinceLastSpawn = 0;
    boolean active = false;
    
    int numParticles = 0;
    
    // TODO: make a global object pool?
    Particle[] particles;
    private Queue<Particle> available = new ConcurrentLinkedQueue<Particle>();

    public ParticleEmitter(Vec3 position, Vec3 positionJitter, Vec3 direction,
            float angleJitter, float speed, float speedJitter, Vec3 acceleration,
            int maxParticles, float spawnRate, GL2 gl)
    {
        minPosition = position.subtract(positionJitter);
        maxPosition = position.add(positionJitter);
        this.direction = direction;
        this.acceleration = acceleration;
        
        // find best cardinal axis
        float xs = position.getX(); xs *= xs;
        float ys = position.getY(); ys *= ys;
        float zs = position.getZ(); zs *= zs;
        Vec3 cardinal;
        if (ys < zs && ys < xs)
            cardinal = new Vec3(0,1,0);
        else if (zs < xs && zs < ys)
            cardinal = new Vec3(0,0,1);
        else
            cardinal = new Vec3(1,0,0);
        
        tangent = direction.cross(cardinal).getUnitVector();
        bitangent = direction.cross(bitangent).getUnitVector();
        
        this.angleJitter = angleJitter;
        minSpeed = speed - speedJitter;
        maxSpeed = speed + speedJitter;
        this.spawnRate = spawnRate;
        
        particles = new Particle[maxParticles];
        for (int i = 0; i < maxParticles; i++)
        {
            particles[i] = new Particle(gl);
            available.add(particles[i]);
        }
    }
    
    public void update(float delta)
    {
        // update each particle
        for (int i = 0; i < particles.length; i++)
        {
            if (particles[i].alive)
                particles[i].update(delta, acceleration);
            
            // check if particle still alive after update
            if (!particles[i].alive)
                available.add(particles[i]);
        }
        
        // spawn new particles
        if (active)
        {
            if (spawnRate > 0)
            {
                timeSinceLastSpawn += delta;

                while (timeSinceLastSpawn > spawnRate)
                {
                    spawn();
                    timeSinceLastSpawn -= spawnRate;
                }
            }
            else // explode!
            {
                for (int i = 0; i < particles.length; i++)
                {
                    spawn();
                }
                active = false;
            }
        }
    }
    
    public void draw(GL2 gl, Mat4 camera, Mat4 proj, Vec3 sun, Vec3 eye) // TODO: replace with Camera and Sun/Light objects later
    {
        for (int i = 0; i < particles.length; i++)
        {
            if (particles[i].alive)
            {
                // TODO: draw particle
            }
        }
    }
    
    private void spawn()
    {
        Particle p = available.poll();
        if (p != null)
        {
            Vec3 pos = minPosition.lerp(maxPosition, (float)Math.random());
            
            Vec3 dir;
            
            if (angleJitter > 0)
            {
                // uniform cone distribution
                
                float phi = (rand.nextFloat()*2.0f-1.0f)*(float)Math.PI;
                float theta = rand.nextFloat()*angleJitter;

                // sinθ(cosϕu+sinϕv)+cosθa 
                dir = (
                        tangent.scale((float)Math.cos(phi))
                        .add(bitangent.scale((float)Math.sin(phi)))
                    ).scale((float)Math.sin(theta))
                    .add(direction.scale((float)Math.sin(theta)))
                    .getUnitVector();
            }
            else
            {
                 // uniform sphere distribution
                dir = new Vec3(
                        (float)rand.nextGaussian(),
                        (float)rand.nextGaussian(),
                        (float)rand.nextGaussian()
                    )
                    .getUnitVector();
            }
            
            float speed = minSpeed + (maxSpeed - minSpeed) * (float)Math.random();
            
            p.set(pos, dir.scale(speed));
        }
    }
}
