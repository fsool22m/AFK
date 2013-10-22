package afk.ge.tokyo.ems.components;

/**
 *
 * @author Daniel
 */
public class HUDTag
{
    public int x = 0;
    public int y = 0;
    
    public boolean centerX = false;
    public boolean centerY = false;

    public HUDTag()
    {
    }

    public HUDTag(int x, int y, boolean centerX, boolean centerY)
    {
        this.x = x;
        this.y = y;
        this.centerX = centerX;
        this.centerY = centerY;
    }
}
