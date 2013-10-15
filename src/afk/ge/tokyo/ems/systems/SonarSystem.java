package afk.ge.tokyo.ems.systems;

import afk.ge.BBox;
import afk.ge.ems.Engine;
import afk.ge.ems.ISystem;
import afk.ge.ems.Utils;
import afk.ge.tokyo.ems.components.Controller;
import afk.ge.tokyo.ems.nodes.CollisionNode;
import afk.ge.tokyo.ems.nodes.SonarNode;
import static afk.gfx.GfxUtils.*;
import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Vec3;
import java.util.List;

/**
 *
 * @author Daniel
 */
public class SonarSystem implements ISystem
{

    private Engine engine;

    @Override
    public boolean init(Engine engine)
    {
        this.engine = engine;
        return true;
    }
    private static final Vec3[] POS_AXIS =
    {
        X_AXIS, Y_AXIS, Z_AXIS
    };
    private static final Vec3[] NEG_AXIS =
    {
        X_AXIS.getNegated(),
        Y_AXIS.getNegated(),
        Z_AXIS.getNegated()
    };

    @Override
    public void update(float t, float dt)
    {
        List<SonarNode> nodes = engine.getNodeList(SonarNode.class);
        List<CollisionNode> cnodes = engine.getNodeList(CollisionNode.class);

        for (SonarNode node : nodes)
        {
            Mat4 rot = Utils.getRotationMatrix(node.state);
            Vec3[] posAxis = new Vec3[3];
            Vec3[] negAxis = new Vec3[3];
            for (int i = 0; i < 3; i++)
            {
                posAxis[i] = rot.multiply(POS_AXIS[i].toDirection()).getXYZ();
                negAxis[i] = rot.multiply(NEG_AXIS[i].toDirection()).getXYZ();
            }
            for (CollisionNode cnode : cnodes)
            {
                // stop sonar from picking up itself
                Controller controller = cnode.entity.get(Controller.class);
                if (controller == node.controller)
                {
                    continue;
                }

                BBox bbox = new BBox(cnode.state, cnode.bbox);

                float[] mins = new float[3];
                float[] maxs = new float[3];
                for (int i = 0; i < 3; i++)
                {
                    if (node.sonar.min.get(i) == 0)
                    {
                        mins[i] = Float.NaN;
                    } else
                    {
                        float dist = bbox.getEntrancePointDistance(node.state.pos, negAxis[i]);
                        if (dist > node.sonar.min.get(i))
                        {
                            mins[i] = Float.POSITIVE_INFINITY;
                        } else
                        {
                            mins[i] = dist;
                        }
                    }
                    if (node.sonar.max.get(i) == 0)
                    {
                        maxs[i] = Float.NaN;
                    } else
                    {
                        float dist = bbox.getEntrancePointDistance(node.state.pos, posAxis[i]);
                        if (dist > node.sonar.max.get(i))
                        {
                            maxs[i] = Float.POSITIVE_INFINITY;
                        } else
                        {
                            maxs[i] = dist;
                        }
                    }
                }

                node.controller.events.sonar.left = mins[0];
                node.controller.events.sonar.right = maxs[0];
                node.controller.events.sonar.up = mins[1];
                node.controller.events.sonar.down = maxs[1];
                node.controller.events.sonar.front = mins[2];
                node.controller.events.sonar.back = maxs[2];
            }
        }
    }

    @Override
    public void destroy()
    {
    }
}
