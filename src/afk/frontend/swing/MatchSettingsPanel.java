/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.frontend.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 *
 * @author Jessica
 */
public class MatchSettingsPanel 
{
    RootWindow root;
    //COMPONENTS!!!
    
    public MatchSettingsPanel(RootWindow _root)
    {
        root = _root;  
        setup();
    }
    
    public void initComponents()
    {
                
    }
    
    public void addComponents()
    {
        
    }
    
    public void styleComponents()
    {
        
    }

    private void setup()
    {
        initComponents();
        addComponents();
        styleComponents();
    }
    
    class MatchSettingsPanel_Layout implements LayoutManager
    {
        int panelWidth = 1280;
        int panelHeight = 786;
        @Override
        public void addLayoutComponent(String name, Component comp) 
        {
            
        }

        @Override
        public void removeLayoutComponent(Component comp) 
        {
            
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) 
        {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void layoutContainer(Container parent) 
        {
            
        }
    }    
}
