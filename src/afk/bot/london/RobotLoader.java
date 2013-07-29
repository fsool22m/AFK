
package afk.bot.london;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

/**
 *
 * @author Jessica
 */

public class RobotLoader extends ClassLoader
{
    private final String ROBOT_CLASS = "afk.bot.london.Robot";
    
    //private ArrayList<Class<?>> robotClasses = new ArrayList<Class<?>>(); 
    private byte[] tempByteArray = null;
    private Map<String, Class<?>> robotMap = new HashMap<String, Class<?>>();
    private Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
    private boolean robotExists;
    
    //Loads all necessary classes needed for the robot specified by path
    public void AddRobot(String path)
    {
        if(path.endsWith(".class"))
        {
            robotExists = false;
            loadFromClass(path);
            if(!robotExists)
            {
                //TODO: throw RobotException
            }
        }
        else if(path.endsWith(".jar"))
        {
            robotExists = false;
            loadJar(path);
            if(!robotExists)
            {
                //TODO: throw RobotException
            }
        }
        else
        {
            // TODO: throw RobotException
        }     
    }
    
    private void loadFromClass(String path)
    {
        InputStream in;
        File tempFile;
        try
        {
            tempFile = new File(path);
            in = new FileInputStream(tempFile);
            tempByteArray = new byte[(int)tempFile.length()];
        }
        catch(FileNotFoundException e)
        {
            //TODO: throw RobotException
            return;
        }
        loadClass(in, tempFile.getName().substring(0, tempFile.getName().lastIndexOf('.')));
    }
    
    //Only loads class files in root directory at present
    //private Robot loadFromJar(String path)
    private void loadJar(String path)
    {
        try
        {
            JarFile jarFile = new JarFile(path);       
            Enumeration e = jarFile.entries();

            while(e.hasMoreElements()) 
            {
                    JarEntry je = (JarEntry)e.nextElement();
                    if(je.isDirectory())
                    {
                        System.out.println("Directory: " + je.getName());
                        // TODO: Load nested files
                    }
                    else if(je.getName().endsWith(".class"))
                    {
                        //TODO: Check for multiple Robot classes within a jar - report on this or only use the first one found - prevent cheating
                        InputStream in = jarFile.getInputStream(je);
                        String name = je.getName().substring(0, je.getName().lastIndexOf('.'));
                        loadClass(in, name);
                      }
                    else if(je.getName().endsWith(".jar"))
                    {
                        System.out.println("Jar" + je.getName());
                        //TODO: Load nested jars to allow use of libraries etc.
                    }
            }
        }
        catch(Exception e)
        {
            //TODO: throw RobotException
        }
    }
    
    public void loadClass(InputStream in, String name)
    {    
        if(!classMap.containsKey(name))
        {
            try
            {
                in.read(tempByteArray);
            }
            catch(Exception e)
            {
                  //TODO: throw Robot Exception      
            }
            Class loadedClass = defineClass(null, tempByteArray, 0, tempByteArray.length);
            classMap.put(loadedClass.getName(), loadedClass);
            System.out.println("Class name: " + loadedClass.getSuperclass().getSuperclass().getName());
            if(loadedClass.getSuperclass().getSuperclass().getName().equals(ROBOT_CLASS))
            {
                System.out.println("It is a bot");
                robotExists = true;
                //System.out.println("Adding class reference " + loadedClass.toString() + " with name " + name + " to robotMap");
                robotMap.put(name, loadedClass);
                System.out.println("loaded class: " + loadedClass.getName());
            }
        }
    }
    
    //Returns instances of robots that are in robotClasses - to be used when game is started
    public Robot getRobotInstance(String name)
    {
        Robot newBot = null;
        //System.out.println("Attempting to retrieve reference with name " + name + " from map");
        Class tempClass = robotMap.get(name);
        
        Object obj = null;
        try
        {
            obj = (tempClass.getDeclaredConstructor().newInstance());
            System.out.println("obj: " + obj.toString());
        }
        catch(Exception e)
        {
            //TODO: throw RobotException
            e.printStackTrace();
        }
        
        return (Robot)obj;
        /*Robot[] bots = new Robot[robotClasses.size()];
        for(int y = 0; y < robotClasses.size(); y++) 
        {
            Object obj = null;
            try
            {
                obj = (robotClasses.get(y)).getDeclaredConstructor().newInstance();
            }
            catch(Exception e)
            {
                //TODO: throw RobotException   
            }
            // TODO: Catch exceptions individually
            bots[y] = (Robot)obj;

        }
        return bots;*/
    }
    
    public void clearMaps()
    {
        classMap.clear();
        robotMap.clear();
        
        //Hint to garbage collector
        System.gc();
        System.gc();
    }
}
