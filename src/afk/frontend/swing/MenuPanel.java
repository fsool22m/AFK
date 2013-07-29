/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package afk.frontend.swing;

import afk.bot.london.London;
import afk.bot.london.Robot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Jw
 */
public class MenuPanel extends JPanel implements ActionListener
{
// TODO; define components + parent ref
// TODO: Change MenuPanel to have a BotEngine, to perform testing in relation to laoding bots.

    RootWindow parent;
    JPanel pnlBotSelButtons;
    JPanel pnlAvailable;
    JPanel pnlSelected;
    JLabel lblAvailable;
    JLabel lblSelected;
    JButton btnAddBot;
    JButton btnAddAllBots;
    JButton btnRemoveBot;
    JButton btnRemoveAllBots;
    JButton btnStartMatch;
    JButton btnLoadBot;
    private HashMap<String, String> botMap;
    private JFileChooser fileChooser;
    private JList<String> lstAvailableBots;
    private JList<String> lstSelectedBots;
    private DefaultListModel<String> lsAvailableModel;
    private DefaultListModel<String> lsSelectedModel;
    private London botEngine;

    public MenuPanel(RootWindow parent)
    {
        //TODO; set layout
        this.parent = parent;

//        LayoutManager layout = new MenuPanel_Layout();
        LayoutManager layout = new GridLayout(1, 3);
        this.setLayout(layout);

        botEngine = new London();
    }

    void setup()
    {
        //TODO; call init, add, style
        initComponents();
        addComponents();
        styleComponents();
    }

    private void initComponents()
    {
        //TODO; instatiate components
        //TODO; run components setup(init & add), set visible 
        pnlAvailable = new JPanel();
        pnlAvailable.setLayout(new BorderLayout());
        lblAvailable = new JLabel("Available Bots");

        pnlBotSelButtons = new JPanel();
        pnlBotSelButtons.setLayout(new GridLayout(5, 1, 50, 50));

        pnlSelected = new JPanel();
        lblSelected = new JLabel("Selected Bots");
        pnlSelected.setLayout(new BorderLayout());

        btnAddBot = new JButton(">");
        btnAddAllBots = new JButton(">>");
        btnRemoveBot = new JButton("<");
        btnRemoveAllBots = new JButton("<<");
        btnStartMatch = new JButton("Start");
        btnLoadBot = new JButton("Load Bot");

        botMap = new HashMap<String, String>();

        fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Load Bot");
        fileChooser.setFileFilter(new FileNameExtensionFilter("java class file", "class", "jar"));

        lstAvailableBots = new JList();
        lstSelectedBots = new JList();
        lsAvailableModel = new DefaultListModel();
        lsSelectedModel = new DefaultListModel();
    }

    private void addComponents()
    {
        //TODO; get container, add components to container.
        Iterator it = botMap.keySet().iterator();

        while (it.hasNext())
        {
            lsAvailableModel.addElement((String) it.next());
        }
        lstAvailableBots.setModel(lsAvailableModel);
        lstSelectedBots.setModel(lsSelectedModel);


        this.add(pnlAvailable);
        this.add(pnlBotSelButtons);
        this.add(pnlSelected);

        pnlAvailable.add(lblAvailable, BorderLayout.NORTH);
        pnlAvailable.add(lstAvailableBots, BorderLayout.CENTER);
        pnlAvailable.add(btnLoadBot, BorderLayout.SOUTH);

        pnlBotSelButtons.add(btnAddBot);
        pnlBotSelButtons.add(btnAddAllBots);
        pnlBotSelButtons.add(btnRemoveBot);
        pnlBotSelButtons.add(btnRemoveAllBots);
        pnlBotSelButtons.add(btnStartMatch);

        pnlSelected.add(lblSelected, BorderLayout.NORTH);
        pnlSelected.add(lstSelectedBots, BorderLayout.CENTER);

    }

    private void removeComponents()
    {
        //TODO;
    }

    private void styleComponents()
    {
        //TODO; set colours of components(here if you can)

//        pnlAvailable.setBorder(BorderFactory.createLineBorder(Color.RED));
//        pnlAvailable.setBackground(Color.LIGHT_GRAY);

//        pnlSelected.setBackground(Color.cyan);

        pnlBotSelButtons.setBorder(new EmptyBorder(150, 150, 150, 150));
//        pnlBotSelButtons.setBackground(Color.LIGHT_GRAY);



        btnAddBot.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String selectedBot = lstAvailableBots.getSelectedValue();
                System.out.println("selectedBot: " + selectedBot);
//                JOptionPane.showMessageDialog(parent, "Select a bot");
                if (selectedBot != null)
                {
                    lsSelectedModel.addElement(selectedBot);
                }
            }
        });

        btnAddAllBots.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (int i = 0; i < lsAvailableModel.size(); i++)
                {
                    lsSelectedModel.addElement(lsAvailableModel.getElementAt(i));
                }
            }
        });

        btnRemoveBot.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String selectedBot = lstSelectedBots.getSelectedValue();
                lsSelectedModel.removeElement(selectedBot);
            }
        });

        btnRemoveAllBots.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                while (!lsSelectedModel.isEmpty())
                {
                    lsSelectedModel.removeElementAt(0);
                }
            }
        });

        btnLoadBot.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int option = fileChooser.showOpenDialog(parent.getContentPane());
                if (option == JFileChooser.APPROVE_OPTION)
                {
                    System.out.println("file opened!");

                    String botPath = fileChooser.getSelectedFile().getAbsolutePath();
                    String botName = (fileChooser.getSelectedFile().getName()).split("\\.")[0];
                    //if (checkBots(botPath))
                    //{
                    botEngine.addRobot(botPath);
                    botMap.put(botName, botPath);
                    lsAvailableModel.addElement(botName);
                    System.out.println("added to available" + botName);
                    //} 
                    //else
                    //{
                        //System.out.println("Error Loading Bots" + botEngine.getBotLoadingError());
                    //}
                }
            }
        });

        btnStartMatch.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO: Change tab - use selected list model as bots for match - names map to paths in botMap
                // TODO: Change MenuPanel to have a BotEngine, to perform testing in relation to laoding bots.
                //ArrayList<String> bots = getParticipatingBots();
                botEngine.setParticipatingBots(getParticipatingBots());
                //for (int i = 0; i < bots.size(); i++)
                //{
                    //String path = bots.get(i);
                    //Robot loadedBot = botEngine.addRobot(path);
                    // TODO: call - arraylist.add(botengine.createBot(path)) , do check on created bot instance!
                    //botEngine.registerBot(loadedBot);
                //}

                parent.spawnGamePanel(botEngine);

            }
        });

    }

    public ArrayList<String> getParticipatingBots()
    {
        ArrayList<String> bots = new ArrayList<String>();
        for (int x = 0; x < lsSelectedModel.size(); x++)
        {
            bots.add(lsSelectedModel.getElementAt(x));
        }
        return bots;
    }

   /* private boolean checkBots(String path)
    {
        Robot loadedBot = botEngine.loadBot(path);
        if (loadedBot == null)
        {
            return false;
        }
        return true;
    }*/

    @Override
    public void actionPerformed(ActionEvent e)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.


    }

    class MenuPanel_Layout implements LayoutManager
    {

        @Override
        public void addLayoutComponent(String name, Component comp)
        {
        }

        @Override
        public void removeLayoutComponent(Component comp)
        {
        }

//    @Override
//    public Dimension preferredLayoutSize(Container parent) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
        @Override
        public Dimension preferredLayoutSize(Container parent)
        {
            Dimension dim = new Dimension(0, 0);

            Insets insets = parent.getInsets();
            dim.width = 640 + insets.left + insets.right;
            dim.height = 360 + insets.top + insets.bottom;

            return dim;
        }

        @Override
        public Dimension minimumLayoutSize(Container parent)
        {
            Dimension dim = new Dimension(0, 0);
            return dim;
        }

        @Override
        public void layoutContainer(Container parent)
        {
            Insets insets = parent.getInsets();

            int w = parent.getSize().width;
            int h = parent.getSize().height;

            int num1 = 2;
            Component c;

            //heading

            try
            {
                int count = 0;
                while ((c = parent.getComponent(count)) != null)
                {
                    c = parent.getComponent(count);
                    if (c.isVisible())
                    {
                        c.setBounds(insets.left + 2, insets.top + num1, w - 4, (int) 60);
                        num1 += c.getSize().height + 2;
                    }
                    count++;
                }
            } catch (Exception er)
            {
            }
        }
    }
}