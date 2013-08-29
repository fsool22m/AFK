package afk.ge.tokyo.ems.systems;

import afk.ge.tokyo.ems.Engine;
import afk.ge.tokyo.ems.ISystem;
import afk.ge.tokyo.ems.nodes.HUDNode;
import afk.ge.tokyo.ems.nodes.RenderNode;
import afk.gfx.GfxEntity;
import afk.gfx.GfxHUD;
import afk.gfx.GraphicsEngine;
import java.util.List;

/**
 *
 * @author daniel
 */
public class RenderSystem implements ISystem
{
    Engine engine;
    GraphicsEngine gfxEngine;

    public RenderSystem(GraphicsEngine gfxEngine)
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
        gfxEngine.prime();
        List<RenderNode> nodes = engine.getNodeList(RenderNode.class);
        for (RenderNode node : nodes)
        {
            GfxEntity gfx = gfxEngine.getGfxEntity(node.renderable);
            
            gfx.position = node.state.pos;
            gfx.rotation = node.state.rot;
            gfx.scale = node.state.scale;
            gfx.colour = node.renderable.colour;
        }
        
        List<HUDNode> hnodes = engine.getNodeList(HUDNode.class);
        for (HUDNode hnode : hnodes)
        {
            GfxHUD hud = gfxEngine.getGfxHUD(hnode.image);
            
            hud.setPosition((int)hnode.state.pos.getX(),
                    (int)hnode.state.pos.getY());
            
            if (hnode.image.isUpdated())
            {
                hud.setImage(hnode.image.getImage());
                hnode.image.setUpdated(false);
            }
        }
        
        gfxEngine.post();
    }

    @Override
    public void destroy()
    {
    }
    
}
