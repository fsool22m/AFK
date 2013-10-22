package afk.ge.tokyo.ems.systems;

import afk.ge.ems.Engine;
import afk.ge.ems.ISystem;
import afk.ge.ems.Utils;
import afk.ge.tokyo.ems.components.Camera;
import afk.ge.tokyo.ems.components.Display;
import afk.ge.tokyo.ems.components.Lifetime;
import afk.ge.tokyo.ems.components.Parent;
import afk.ge.tokyo.ems.components.Renderable;
import afk.ge.tokyo.ems.nodes.HUDNode;
import afk.ge.tokyo.ems.nodes.HUDTagNode;
import afk.ge.tokyo.ems.nodes.RenderNode;
import afk.gfx.AbstractCamera;
import afk.gfx.GfxEntity;
import afk.gfx.GfxHUD;
import afk.gfx.GraphicsEngine;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import com.hackoeur.jglm.Vec4;
import java.awt.Point;
import java.awt.image.BufferedImage;
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
        if (!gfxEngine.isReady())
        {
            return;
        }

        AbstractCamera gfxCamera = gfxEngine.getCamera();
        Camera camera = engine.getGlobal(Camera.class);
        gfxCamera.at = camera.at;
        gfxCamera.eye = camera.eye;
        gfxCamera.up = camera.up;

        Display display = engine.getGlobal(Display.class);
        display.screenWidth = gfxEngine.getWidth();
        display.screenHeight = gfxEngine.getHeight();

        gfxEngine.prime();
        doRenderables();
        doHUDTags(gfxCamera);
        doHUD();

        gfxEngine.post();
        gfxEngine.redisplay();
    }

    @Override
    public void destroy()
    {
    }

    private void doHUD()
    {
        List<HUDNode> nodes = engine.getNodeList(HUDNode.class);
        for (HUDNode node : nodes)
        {
            BufferedImage img = node.image.getImage();

            if (img == null)
            {
                continue;
            }
            
            GfxHUD hud = gfxEngine.getGfxHUD(node.image);
            
            hud.setPosition(node.hud.x, node.hud.y);

            if (node.image.isUpdated())
            {
                hud.setImage(img);
                node.image.setUpdated(false);
            }
        }
    }
    
    private void doHUDTags(AbstractCamera gfxCamera)
    {
        List<HUDTagNode> nodes = engine.getNodeList(HUDTagNode.class);
        for (HUDTagNode node : nodes)
        {
            BufferedImage img = node.image.getImage();

            if (img == null)
            {
                continue;
            }

            // TODO: pre-multiply ther matrices for performance
            Mat4 world = Utils.getMatrix(node.state);

            Vec4 p = gfxCamera.projection.multiply(
                    gfxCamera.view.multiply(
                    world.multiply(
                    new Vec3(0, node.tag.worldY, 0).toPoint())));

            Vec4 center = gfxCamera.projection.multiply(
                    gfxCamera.view.multiply(
                    world.multiply(
                    Vec3.VEC3_ZERO.toPoint())));

            float depth = p.getZ() / p.getW();
            if (depth < -1 || depth > 1)
            {
                continue;
            }

            GfxHUD hud = gfxEngine.getGfxHUD(node.image);

            Point q = toScreen(p);
            Point r = toScreen(center);
            int yDiff = Math.abs(q.y - r.y);
            if (yDiff < node.tag.minY)
            {
                q.y = r.y - node.tag.minY * (node.tag.worldY < 0 ? -1 : 1);
            }
            q.x += (int) node.tag.xOffset;
            if (node.tag.centerX)
            {
                q.x -= node.image.getImage().getWidth() / 2.0f;
            }
            if (node.tag.centerY)
            {
                q.y -= node.image.getImage().getHeight() / 2.0f;
            }

            hud.setPosition(q.x, q.y);

            if (node.image.isUpdated())
            {
                hud.setImage(img);
                node.image.setUpdated(false);
            }
        }
    }

    private Point toScreen(Vec4 ndc)
    {
        int x = (int) ((ndc.getX() / ndc.getW() + 1.0f) * 0.5f * gfxEngine.getWidth());
        int y = (int) ((1.0f - (ndc.getY() / ndc.getW() + 1.0f) * 0.5f) * gfxEngine.getHeight());
        return new Point(x, y);
    }

    private void doRenderables()
    {
        List<RenderNode> nodes = engine.getNodeList(RenderNode.class);
        for (RenderNode node : nodes)
        {
            GfxEntity gfx = gfxEngine.getGfxEntity(node.renderable);

            Parent parent = node.entity.getComponent(Parent.class);
            if (parent != null && parent.entity.hasComponent(Renderable.class))
            {
                GfxEntity parentGfx = gfxEngine.getGfxEntity(parent.entity.getComponent(Renderable.class));
                if (parentGfx != gfx.getParent())
                {
                    parentGfx.addChild(gfx);
                }
            } else
            {
                GfxEntity parentGfx = gfx.getParent();
                if (parentGfx != null)
                {
                    parentGfx.removeChild(gfx);
                }
            }

            gfx.position = node.state.pos;
            gfx.rotation = node.state.rot;
            gfx.scale = node.state.scale;
            gfx.colour = node.renderable.colour;
            gfx.opacity = node.renderable.opacity;
            Lifetime lifetime = node.entity.getComponent(Lifetime.class);
            if (lifetime != null)
            {
                gfx.life = 1.0f - (lifetime.life / lifetime.maxLife);
            } else
            {
                gfx.life = 0.0f;
            }
        }
    }
}
