/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.frontend.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author Jessica
 */
public class AFKListCellRenderer extends JLabel implements ListCellRenderer
{
     @Override
     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
     {
         setText(value instanceof File ? ((File)value).getName() : value.toString());

         this.setName("listCell");
         
         Color background;
         Color foreground;

         FontUIResource tempFont = CustomFonts.createFont("fonts/MyriadPro-Regular.otf");
         
         setFont(tempFont);
         // check if this cell represents the current DnD drop location
        /* JList.DropLocation dropLocation = list.getDropLocation();
         if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) 
         {

             background = Color.BLUE;
             foreground = Color.WHITE;
         } */
         
         // check if this cell is selected
         //else 
         if (isSelected) 
         {
             /*background = new Color(153, 153, 153);  //204
             foreground = new Color(0, 0, 0);*/
             background = new Color(153, 153, 153);  //204
             foreground = new Color(0, 0, 0);
             this.setOpaque(true);
         } 
         
         // unselected, and not the DnD drop location
         else 
         {
             /*background = new Color(51, 51, 51);
             foreground = new Color(204, 204, 204);*/
             background = new Color(51, 51, 51);
             foreground = new Color(204, 204, 204);
         }

         
         setBackground(background);
         setForeground(foreground);

         return this;
     }
}
