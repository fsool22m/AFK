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
 package afk.ge.tokyo.ems.systems;

import afk.ge.BBox;
import afk.ge.ems.Engine;
import afk.ge.ems.Entity;
import afk.ge.ems.ISystem;
import afk.ge.tokyo.HeightmapLoader;
import afk.ge.tokyo.ems.components.Controller;
import afk.ge.tokyo.ems.components.Life;
import afk.ge.tokyo.ems.components.State;
import afk.ge.tokyo.ems.events.DamageEvent;
import afk.ge.tokyo.ems.factories.GenericFactory;
import afk.ge.tokyo.ems.factories.GenericFactoryRequest;
import afk.ge.tokyo.ems.nodes.CollisionNode;
import afk.ge.tokyo.ems.nodes.HeightmapNode;
import afk.ge.tokyo.ems.nodes.ProjectileNode;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import com.hackoeur.jglm.support.FastMath;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Jw
 */
public class ProjectileSystem implements ISystem
{

    private Engine engine;
    private GenericFactory factory = new GenericFactory();
    private GenericFactoryRequest explosionRequest;

    @Override
    public boolean init(Engine engine)
    {
        this.engine = engine;
        try
        {
            explosionRequest = GenericFactoryRequest.load("explosionProjectile");
        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    @Override
    public void update(float t, float dt)
    {

        List<ProjectileNode> bullets = engine.getNodeList(ProjectileNode.class);
        List<CollisionNode> nodes = engine.getNodeList(CollisionNode.class);
        HeightmapNode hnode = engine.getNodeList(HeightmapNode.class).get(0);

        bulletLoop:
        for (ProjectileNode bnode : bullets)
        {
            // collision with terrain
            if (hnode != null)
            {
                if (HeightmapLoader.under(bnode.state.prevPos, hnode.heightmap))
                {
                    bang(bnode, bnode.state.prevPos);
                    continue;
                } else if (HeightmapLoader.under(bnode.state.pos, hnode.heightmap))
                {
                    bang(bnode, bnode.state.pos);
                    continue;
                } else
                {
                    Vec3 intersection = HeightmapLoader.getIntersection(bnode.state.prevPos, bnode.state.pos, 0.1f, hnode.heightmap);
                    if (intersection != null)
                    {
                        bang(bnode, intersection);
                        continue;
                    }
                }
            }

            // collision testing for box
            for (CollisionNode cnode : nodes)
            {
                // to stop shells from exploding inside the tank's barrel:
                Controller controller = cnode.entity.getComponent(Controller.class);
                if (controller != null && controller == bnode.bullet.parent)
                {
                    continue;
                }

                BBox bbox = new BBox(cnode.state, cnode.bbox);
                if (bbox.isLineInBox(bnode.state.prevPos, bnode.state.pos))
                {
                    
                    Life life = cnode.entity.getComponent(Life.class);
                    if (life != null)
                    {
                        cnode.entity.addEvent(new DamageEvent(bnode.bullet.damage, bnode.bullet.parent));
                    }

                    // notify the victim that they got shot
                    // (only if the victim was not an innocent wall)
                    Controller victim = cnode.entity.getComponent(Controller.class);
                    if (victim != null)
                    {
                        victim.events.gotHit = true;
                        // notify the shooter that they shot someone
                        bnode.bullet.parent.events.didHit = true;
                    }


                    bang(bnode);
                    continue bulletLoop;
                }
            }

            // range testing
            float dist = bnode.state.prevPos.subtract(bnode.state.pos).getLength();
            bnode.bullet.rangeLeft -= dist;
            if (Float.compare(bnode.bullet.rangeLeft, 0) <= 0)
            {
                engine.removeEntity(bnode.entity);
            }
        }
    }

    @Override
    public void destroy()
    {
    }

    /**
     * This function determines whether entity A will, and has crossed paths
     * with entity B. We make use of the following formulas:
     * <pre>
     * A = a1 - b1
     * B = (a2 - a1) - (b2 - b1)
     * d^2 = A^2 - ((A · B)^2 / B^2)
     * t = (-(A·B) - Sqr((A·B)^2 - B^2 (A^2 - (r(a) + r(b))^2))) / B^2
     * </pre> if B^2 = 0, then either both a and b are: stationary or moving in
     * the same direction at the same speed, and can thus not collide.
     * <p>
     * if d^2 > (r(a) + r(b))^2 - the sum of the entities radii squared. then
     * the entities can never collide on there current trajectories.
     * </p><p>
     * if t is greater or equal to 0, and smaller than 1. Then entities a and b
     * intersect in the current time step.
     * </p>
     *
     * @param a
     * @param b
     * @return
     */
    protected boolean intersectionTesting(State a, State b)
    {
        Vec3 B = (a.pos.subtract(a.prevPos)).subtract(b.pos.subtract(b.prevPos));
        double bSqr = B.getLengthSquared();
        if (Double.compare(bSqr, 0.0f) == 0)
        {
            return false;
        }
        Vec3 A = a.prevPos.subtract(b.prevPos);
        double aSqr = A.getLengthSquared();
        double rrSqr = ((a.scale.add(b.scale)).getLengthSquared()) / 2;
        double aDotb = (A.dot(B));
        double aDotbSqr = aDotb * aDotb;
        double d2 = aSqr - (aDotbSqr / bSqr);

        if (Double.compare(d2, rrSqr) > 0)
        {
            return false;
        }
        //find fastInv double implimentation
        double t = (-(aDotb) - FastMath.sqrtFast((float) ((aDotbSqr) - bSqr * (aSqr - (rrSqr))))) / bSqr;
        if (Double.compare(t, 0) < 0 || Double.compare(t, 1) >= 0)
        {
            return false;
        }
        return true;
    }

    private void bang(ProjectileNode bullet)
    {
        this.bang(bullet, bullet.state.pos);
    }

    private void bang(ProjectileNode bullet, Vec3 where)
    {
        Entity entity = factory.create(explosionRequest);
        entity.addComponent(new State(where, Vec4.VEC4_ZERO, new Vec3(1)));
        engine.addEntity(entity);
        engine.removeEntity(bullet.entity);
    }
}
