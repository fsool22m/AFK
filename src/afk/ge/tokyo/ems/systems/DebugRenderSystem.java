package afk.ge.tokyo.ems.systems;

import afk.ge.BBox;
import afk.ge.ems.Engine;
import afk.ge.ems.ISystem;
import afk.ge.tokyo.ems.nodes.CollisionNode;
import afk.gfx.GfxEntity;
import afk.gfx.GraphicsEngine;
import com.hackoeur.jglm.Vec3;
import java.util.List;

/**
 *
 * @author daniel
 */
public class DebugRenderSystem implements ISystem
{

    Engine engine;
    GraphicsEngine gfxEngine;

    public DebugRenderSystem(GraphicsEngine gfxEngine)
    {
        this.gfxEngine = gfxEngine;
    }

    @Override
    public boolean init(Engine engine)
    {
        this.engine = engine;
        return true;
    }

    @Override
    public void update(float t, float dt)
    {
        gfxEngine.primeDebug();
        List<CollisionNode> nodes = engine.getNodeList(CollisionNode.class);
        for (CollisionNode node : nodes)
        {
            BBox bbox = new BBox(node.state, node.bbox);
            
            GfxEntity gfx = gfxEngine.getDebugEntity(bbox);

            gfx.position = bbox.getCenterPoint();
            gfx.rotation = node.state.rot;
            gfx.scale = new Vec3(1);
            gfx.colour = GfxEntity.MAGENTA;
        }

        gfxEngine.postDebug();
    }

    @Override
    public void destroy()
    {
        gfxEngine.primeDebug();
        gfxEngine.postDebug();
    }
}
