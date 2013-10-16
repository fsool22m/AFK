
import afk.frontend.Frontend;
import afk.frontend.swing.RootWindow;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;


public class Main
{
    private static String LAF_FILE = "LAF.xml";
    public static void main(String[] args)
    {
        initLookAndFeel();
        Frontend frontend = new RootWindow();
        frontend.showMain();
    }
    
    private static void initLookAndFeel() 
    {
        SynthLookAndFeel lookAndFeel = new SynthLookAndFeel();
        
        try 
        {
            lookAndFeel.load(RootWindow.class.getResourceAsStream(LAF_FILE), RootWindow.class);
            UIManager.setLookAndFeel(lookAndFeel);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        System.out.println("SYNTH INIT DONE");
    }
}
