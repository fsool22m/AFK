package afk.ge.tokyo.ems.systems;

import afk.ge.ems.Engine;
import afk.ge.ems.Entity;
import afk.ge.ems.ISystem;
import afk.ge.tokyo.ems.components.Selection;
import afk.ge.tokyo.ems.components.TextLabel;
import afk.ge.tokyo.ems.nodes.TextLabelNode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Jw
 */
public class TextLabelSystem implements ISystem
{
    public static final Color BG_COLOUR = new Color(0x2C2A2BFF, true);
    public static final Color TEXT_COLOUR = new Color(0xD1D2D4);
    private final FontRenderContext FRC = new FontRenderContext(null, true, true);
    private final Font FONT = new Font("Myriad Pro", Font.BOLD, 12);
    private Engine engine;

    private final int PAD = 3;
    
    private Entity selectedEntity = null;
    private TextLabel selectedLabel = null;
    private boolean selectionChanged = true;
    
    @Override
    public boolean init(Engine engine)
    {
        this.engine = engine;
        return true;
    }

    @Override
    public void update(float t, float dt)
    {
        Selection selection = engine.getGlobal(Selection.class);
        if (selection.getEntity() != selectedEntity)
        {
            selectedEntity = selection.getEntity();
            selectionChanged = true;
        }
        else
        {
            selectionChanged = false;
        }
        List<TextLabelNode> nodes = engine.getNodeList(TextLabelNode.class);
        for (TextLabelNode node : nodes)
        {
            if (selectionChanged)
            {
                if (node.label == selectedLabel)
                {
                    node.label.setSelected(false);
                }
                if (node.entity == selectedEntity)
                {
                    node.label.setSelected(true);
                }
            }
            if (node.label.isUpdated())
            {
                node.image.setImage(createTextLabel(node.label));
                node.label.setUpdated(false);
            }
        }
    }
    
    private BufferedImage createTextLabel(TextLabel label)
    {
        String str = label.getText();
        
        LineMetrics metrics = FONT.getLineMetrics(str, FRC);
        Rectangle r = FONT.getStringBounds(str, FRC).getBounds();
        int width = r.width;
        int height = (int)(metrics.getAscent()+metrics.getDescent());
        
        BufferedImage image = new BufferedImage(width+PAD*2, height+PAD*2, BufferedImage.TRANSLUCENT);
        Graphics2D g = image.createGraphics();
        g.setFont(FONT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

//        g.setBackground(Color.BLACK);
//        g.clearRect(0, 0, image.getWidth(), image.getHeight());

        g.setColor(BG_COLOUR);
        g.fillRoundRect(0, 0, image.getWidth(), image.getHeight(), 5, 5);
        g.setColor(label.isSelected() ? Color.GREEN : TEXT_COLOUR);
        g.drawString(str, PAD, PAD+metrics.getAscent());

        g.dispose();

        return image;
    }

    @Override
    public void destroy()
    {
    }
}
