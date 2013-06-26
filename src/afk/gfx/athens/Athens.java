package afk.gfx.athens;


import afk.gfx.GfxEntity;
import afk.gfx.GfxInputListener;
import afk.gfx.GraphicsEngine;
import afk.gfx.Resource;
import afk.gfx.ResourceNotLoadedException;
import afk.gfx.Updatable;
import afk.gfx.athens.particles.ParticleEmitter;
import com.hackoeur.jglm.*;
import com.jogamp.opengl.util.Animator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

public class Athens extends GraphicsEngine
{
    
    // loading queue
    private Queue<Resource> loadQueue = new LinkedList<Resource>();
    // TODO: unload queue as well
    
    // this gets called when loading is done
    private Runnable onLoadCallback;
    
    // mesh resources
    private Map<String, Mesh>[] meshResources = new Map[Resource.NUM_MESH_TYPES];
    
    // texture resources
    private Map<String, Texture>[] texResources = new Map[Resource.NUM_TEX_TYPES];
    
    // TODO: placeholder for material resources in the future
    // could add more here dependant on different shader types and whatnot...?
    private Map<String, Object> matResources;

    private Map<String, Shader> shaderResources;
    
    // TODO: decide on list implementation
    private List<AthensEntity> entities = new ArrayList<AthensEntity>();
    
    // TODO: add other resource types here later maybe

    // flag indicating that the load queue has been dispatched
    private boolean loadQueueDispatched = false;
    
    private int w_width, w_height;
    private float aspect;
    
    // TODO: 1024 is enough, but there are some obscure codes in the region of 65000 that WILL make this program crash
    private boolean[] keys = new boolean[1024];
    
    private long lastUpdate;
    private float time = 0.0f;
    private float lastFPS = 0.0f;
    private float fpsInterval = 1.0f;

    // TODO: move this into a camera object
    /* Camera location. */
    Vec3 cameraEye = new Vec3(10f, 10f, 10f);
    /* Vector the camera is looking along. */
    Vec3 cameraAt = new Vec3(0f, 0f, 0f);
    /* Up direction from the camera. */
    Vec3 cameraUp = new Vec3(0f, 1f, 0f);

    /* Vertical field-of-view. */
    float FOVY = 60.0f;
    /* Near clipping plane. */
    float NEAR = 0.1f;
    /* Far clipping plane. */
    float FAR = 200.0f;
           
    Mat4 view, projection;
    
    Mat4 monkeyWorld, skyboxWorld;
    
    // TODO: move this to a sun/light position
    Vec3 origin_sun = new Vec3(-2.0f,1.5f,5.0f);
    Vec3 sun = new Vec3(origin_sun);
    
    float daytime = 0.0f;
    
    /*Shader sbShader;
    TextureCubeMap skymap;
    SkyBox skybox;*/

    static final float MOUSE_SENSITIVITY = 60.0f;
    /* Amount to move / scale by in one step. */
    static final float DELTA = 5f, ANGLE_DELTA = 30.0f;

    private JFrame jFrame;
    private GLProfile glProfile;
    private GLCapabilities glCaps;
    private GLCanvas glCanvas;
    private Animator animator;
    private String title;
    
    /// TESTING STUFF ///
    
    ParticleEmitter emitter;
    
    public Athens(int width, int height, String title, boolean autodraw)
    {
        for (int i = 0; i < meshResources.length; i++)
        {
            meshResources[i] = new HashMap<String, Mesh>();
        }
        for (int i = 0; i < texResources.length; i++)
        {
            texResources[i] = new HashMap<String, Texture>();
        }
        matResources = new HashMap<String, Object>();
        shaderResources = new HashMap<String, Shader>();
        
        this.w_width = width;
        this.w_height = height;
        this.title = title;
        
        glProfile = GLProfile.getDefault();
        
        glCaps = new GLCapabilities(glProfile);
        glCaps.setDoubleBuffered(true);
        
        glCanvas = new GLCanvas(glCaps);
        glCanvas.setPreferredSize(new Dimension(width, height));
        
        jFrame = new JFrame(title);
        
        jFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent windowevent ) {
                jFrame.dispose();
                System.exit(0);
            }
        });
        jFrame.setResizable(false);
        
        glCanvas.addKeyListener(new KeyAdapter()
        {
            // TODO: rather make Athens implement KeyListener

            @Override
            public void keyPressed(KeyEvent e) {
                Athens.this.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                Athens.this.keyReleased(e);
            }
            
        });
        
        // TODO: rather make Athens implement MouseListener and MouseMotionListener
        MouseAdapter mouse = new MouseAdapter()
        {
            int cx = glCanvas.getWidth()/2;
            int cy = glCanvas.getHeight()/2;

            @Override
            public void mousePressed(MouseEvent e) {

                cx = e.getX();
                cy = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {

                if (e.getX() == cx && e.getY() == cy) return;

                Athens.this.mouseMoved(cx-e.getX(), cy-e.getY());

                cx = e.getX();
                cy = e.getY();
            }
        };
        
        glCanvas.addMouseListener(mouse);
        glCanvas.addMouseMotionListener(mouse);

        glCanvas.addGLEventListener( new GLEventListener() {

            // TODO: rather make Athens implement GLEventListener
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                Athens.this.reshape( glautodrawable.getGL().getGL2(), width, height );
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {
                GL gl = glautodrawable.getGL().getGL2();
                System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION));
                Athens.this.init( gl.getGL2() );
            }
            
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
                Athens.this.dispose( glautodrawable.getGL().getGL2() );
            }
            
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                Athens.this.display( glautodrawable.getGL().getGL2());
            }
        });

        jFrame.getContentPane().add( glCanvas, BorderLayout.CENTER );
        jFrame.pack();
        jFrame.setVisible(true);
        
        if (autodraw)
        {
            animator = new Animator(glCanvas);
            animator.start();
        }
    }

    @Override
    public void redisplay()
    {
        glCanvas.display();
    }
    
    private void dispose(GL2 gl)
    {
        // TODO: release meshes and shaders here. EEEK!!!
        // unloadEverything(gl); // maybe?
    }

    private void display(GL2 gl)
    {
        
        long nTime = System.nanoTime();
        long nanos = nTime-lastUpdate;
        lastUpdate = nTime;
        float delta = nanos/(float)NANOS_PER_SECOND;
        time += delta;
        lastFPS += delta;
        
        if (lastFPS > fpsInterval)
        {
            jFrame.setTitle(title + " - " + (1.0f/delta) + " FPS");
            lastFPS -= fpsInterval;
        }
        
        // TODO: move loading into a loading state rather.
        // This should allow for loading progress bars and such.
        // just put it here for now.
        if (loadQueueDispatched)
        {
            loadResources(gl);
            
            loadQueueDispatched = false;
            
            // notify caller that load is complete
            onLoadCallback.run();
        }
        else
        {
            update(delta);
            
            for (Updatable u :updatables)
                u.update(delta);
            
            render(gl);
        }
    }
    
    private void loadResources(GL2 gl)
    {
        while (!loadQueue.isEmpty())
        {
            Resource resource = loadQueue.poll();
            System.out.println("Loading: " + resource.getName() + " - " + resource.getType());
            switch (resource.getType())
            {
                case Resource.WAVEFRONT_MESH:
                    try {
                        meshResources[resource.getType()].put(resource.getName(), new WavefrontMesh(gl, resource.getName()+".obj"));
                    } catch (IOException ioe)
                    {
                        // TODO: load "default" model, like a cube or something...
                        throw new RuntimeException("Error loading mesh: " + ioe.getMessage());
                    }
                    break;
                case Resource.PRIMITIVE_MESH:
                    if ("quad".equals(resource.getName()))
                        meshResources[resource.getType()].put(resource.getName(), new Quad(gl, 1, 1, 0));
                    break;
                    // TODO: add more primitive types
                    // TODO: it may be feasible to have all primitive types preloaded at all times?
                case Resource.HEIGHTMAP_MESH:
                    // TODO: for future use
                    throw new RuntimeException("Could not load heightmap: feature not implemented");
                case Resource.TEXTURE_2D:
                    try {
                        texResources[resource.getType()-Resource.TEXTURE_2D].put(resource.getName(), Texture2D.fromFile(gl, new File("textures/"+resource)));
                    } catch (IOException ioe)
                    {
                        // TODO: load "default" texture, like a magenta checkerboard or something
                        throw new RuntimeException("Error loading texture: " + ioe.getMessage());
                    }
                    break;
                case Resource.TEXTURE_CUBE:
                    try
                    {
                        texResources[resource.getType()-Resource.TEXTURE_2D].put(resource.getName(), TextureCubeMap.fromFiles(gl, new File[]{
                            new File("textures/" + resource + "_positive_x"),
                            new File("textures/" + resource + "_negative_x"),
                            new File("textures/" + resource + "_positive_y"),
                            new File("textures/" + resource + "_negative_y"),
                            new File("textures/" + resource + "_positive_z"),
                            new File("textures/" + resource + "_negative_z")
                        }));
                    } catch (IOException ioe)
                    {
                        // TODO: load a "default" texture, like a magenta checkerboard or something
                        throw new RuntimeException("Error loading cube map: " + ioe.getMessage());
                    }
                    break;
                case Resource.MATERIAL:
                    // TODO: for future use (maybe)
                    throw new RuntimeException("Could not load heightmap: feature not implemented");
                case Resource.SHADER:
                    shaderResources.put(resource.getName(), new Shader(gl, resource.getName()));
                    break;
                default:
                    break;
            }
        }
        
        // TODO: unload resources as well?
        // do similar thing but with an unloadQueue
        
        // TODO: on a similar note, unload everything?
        // if (shouldUnloadEverything) unloadEverything(gl); // <- something like?
    }
    
    private void update(float delta)
    {
        
        float step = DELTA;
        
        if (keys[KeyEvent.VK_SHIFT]) step *= 5;
        
        Vec3 d = cameraAt.subtract(cameraEye).getUnitVector().multiply(step*delta);
        Vec3 r = d.cross(cameraUp).getUnitVector().multiply(step*delta);

        if (keys[KeyEvent.VK_W])
        {
           cameraEye = cameraEye.add(d);
           cameraAt = cameraAt.add(d);
        }
        else if (keys[KeyEvent.VK_S])
        {
           cameraEye = cameraEye.subtract(d);
           cameraAt = cameraAt.subtract(d);
        }

        if (keys[KeyEvent.VK_D])
        {
           cameraEye = cameraEye.add(r);
           cameraAt = cameraAt.add(r);
        }
        else if (keys[KeyEvent.VK_A])
        {
           cameraEye = cameraEye.subtract(r);
           cameraAt = cameraAt.subtract(r);
        }
        else if (keys[KeyEvent.VK_2])
        {
            daytime += 1.0f;
            updateSun();
        }
        else if (keys[KeyEvent.VK_3])
        {
            daytime -= 1.0f;
            updateSun();
        }
        
        updateView();
        
        emitter.update(delta);
    }
    
    private void render(GL2 gl)
    {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        renderScene(gl);

        gl.glFlush();
    }
    
    private void renderScene(GL2 gl)
    {
        renderScene(gl, view);
    }
    private void renderScene(GL2 gl, Mat4 camera)
    {
        renderScene(gl, camera, projection);
    }
    private void renderScene(GL2 gl, Mat4 camera, Mat4 proj)
    {
        //renderSkybox(gl, camera, proj);
        //renderFloor(gl, camera, proj);
        //renderMesh(gl, camera, proj);
        
        for (AthensEntity entity :entities)
        {
            entity.draw(gl, camera, proj, sun, cameraEye);
        }
        
        emitter.draw(gl, camera, proj, sun, cameraEye);
    }
    
    /*void renderSkybox(GL2 gl, Mat4 camera, Mat4 proj)
    {
        gl.glDisable(GL.GL_DEPTH_TEST);
        sbShader.use(gl);
        
        skymap.use(gl, GL.GL_TEXTURE0);
        
        shader.updateUniform(gl, "tex", 0);
        sbShader.updateUniform(gl, "view", camera);
        sbShader.updateUniform(gl, "projection", proj);
        sbShader.updateUniform(gl, "world", skyboxWorld);
        sbShader.updateUniform(gl, "sun", sun);
        
        skybox.draw(gl);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }*/
    
    /*private void renderFloor(GL2 gl, Mat4 camera, Mat4 proj)
    {
        floorShader.use(gl);
        
        //floor.use(gl, GL.GL_TEXTURE0);
        
        //floorShader.updateUniform(gl, "skymap", 0);
        floorShader.updateUniform(gl, "world", new Mat4(1.0f));
        floorShader.updateUniform(gl, "view", camera);
        floorShader.updateUniform(gl, "projection", proj);
        
        quad.draw(gl);
        
    }
    
    private void renderMesh(GL2 gl, Mat4 camera, Mat4 proj)
    {
        tankShader.use(gl);
        
        //tex.use(gl, GL.GL_TEXTURE0);
        
        //shader.updateUniform(gl, "tex", 0);
        tankShader.updateUniform(gl, "world", monkeyWorld);
        tankShader.updateUniform(gl, "view", camera);
        tankShader.updateUniform(gl, "projection", proj);
        
        tankShader.updateUniform(gl, "sun", sun);
        tankShader.updateUniform(gl, "eye", cameraEye); // TODO: not necessary as we don't render specular
        
        tankMesh.draw(gl);
    } */

    private void init(GL2 gl)
    {
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        /*gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);*/
        //gl.glEnable(GL.GL_MULTISAMPLE);
        //gl.glEnable(GL.GL_TEXTURE_CUBE_MAP);
        //gl.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_LINE );
        
        // set background colour to white
        // TODO: allow this to be set through an interface
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        
        // load textures
        // not necessary now
        /*sbShader = new Shader(gl, "skybox");
        skybox = new SkyBox(gl);
        
        try
        {
            skymap = TextureCubeMap.fromFiles(gl, new File[]{
                new File("textures/siege/siege_front.jpg"),
                new File("textures/siege/siege_back.jpg"),
                new File("textures/siege/siege_top.jpg"),
                new File("textures/siege/siege_top.jpg"),
                new File("textures/siege/siege_left.jpg"),
                new File("textures/siege/siege_right.jpg")
            });
            skymap.setParameters(gl, Texture.texParamsSkyMap);
            
            floor = Texture2D.fromFile(gl, new File("textures/floor.jpg"));
            floor.setParameters(gl, Texture.texParamsDefault);
            
            tex = Texture2D.fromFile(gl, new File("textures/tank.jpg"));
            tex.setParameters(gl, Texture.texParamsDefault);
            
        } catch (IOException ex)
        {
            System.err.println("Could not load texture: " + ex.toString());
        }*/
        
        /// TESTING ///
        
        emitter = new ParticleEmitter(
                new Vec3(0, 50.0f, 0),  // position
                new Vec3(30.0f, 0, 30.0f), // pos jitter
                new Vec3(0, -1, 0), // direction
                (float)Math.toRadians(10.0f), // dir jitter
                1f, // speed
                0.2f, // speed jitter
                new Vec3(0, -1f, 0), // acceleration
                1000, // num particles
                0.03f, // rate
                gl
            );
        emitter.shader = new Shader(gl, "particle");
        emitter.active = true;
        
        /// ....... ///

        // initial setup of matrices
        updateProjection(w_width, w_height);
        updateView();
        
        // initialise update time with current time
        lastUpdate = System.nanoTime();
    }

    private void keyPressed(KeyEvent ke)
    {
        char key = ke.getKeyChar();
        if (ke.getKeyCode() < keys.length)
            keys[ke.getKeyCode()] = true;

        switch (ke.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                System.out.printf("Bye!\n");
                System.exit(0);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isKeyDown(int keyCode)
    {
        return keys[keyCode];
    }
    
    private void keyReleased(KeyEvent ke)
    {
        if (ke.getKeyCode() < keys.length)
            keys[ke.getKeyCode()] = false;
    }

    private void mouseMoved(int x, int y)
    {
        final float LEFT_RIGHT_ROT = (2.0f*(float)x/(float)w_width) * MOUSE_SENSITIVITY;
        final float UP_DOWN_ROT = (2.0f*(float)y/(float)w_height) * MOUSE_SENSITIVITY;

        Vec3 tempD = cameraAt.subtract(cameraEye);
        Vec4 d = new Vec4(tempD.getX(), tempD.getY(), tempD.getZ(), 0.0f);

        Vec3 right = tempD.cross(cameraUp);

        Mat4 rot = new Mat4(1.0f);
        rot = Matrices.rotate(rot, UP_DOWN_ROT, right);
        rot = Matrices.rotate(rot, LEFT_RIGHT_ROT, cameraUp);

        d = rot.multiply(d);

        cameraAt = cameraEye.add(new Vec3(d.getX(),d.getY(),d.getZ()));
        
        updateView();

    }

    @Override
    public boolean isMouseDown(int button)
    {
        // TODO: implement isMouseDown
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMouseX()
    {
        // TODO: implement getMouseX
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMouseY()
    {
        // TODO: implement getMouseY
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void reshape(GL2 gl, int newWidth, int newHeight)
    {
        w_width = newWidth;
        w_height = newHeight;
        
        /* Correct the viewport. */
        gl.glViewport(0, 0, newWidth, newHeight);

        /* Correct the projection matrix. */
        updateProjection(newWidth, newHeight);

        /* Note the new width and height of the window. */
        System.out.printf("Resized. New width = %d and new height = %d.\n", newWidth, newHeight);
    }

    private void updateProjection(int width, int height)
    {
        aspect = (float) width / (float) height;

        projection = Matrices.perspective(FOVY, aspect, NEAR, FAR);
    }

    private void updateView()
    {
        view = Matrices.lookAt(cameraEye, cameraAt, cameraUp);
        
        // shift skybox with camera
        skyboxWorld = new Mat4(1.0f);
        skyboxWorld = Matrices.translate(skyboxWorld, cameraEye);
        // also scale it a bit so it doesn't intersect with near clip
        skyboxWorld = Matrices.scale(skyboxWorld, new Vec3(1.5f,1.5f,1.5f));
    }
    
    private void updateSun()
    {
        Mat4 sunWorld = new Mat4(1.0f);
        
        sunWorld = Matrices.rotate(sunWorld, daytime, new Vec3(1,0.2f,0).getUnitVector());
        
        Vec4 sun4 = sunWorld.multiply(new Vec4(origin_sun, 0.0f));
        
        sun = new Vec3(sun4.getX(),sun4.getY(), sun4.getZ());
    }

    @Override
    public Resource loadResource(int type, String name)
    {
        if (loadQueueDispatched)
        {
            // TODO: throw something about already dispatchign a load queue
            return null;
        }
        
        Resource resource = new Resource(type, name);
        
        loadQueue.add(resource);
        
        return resource;
    }

    @Override
    public void unloadResource(Resource resource)
    {
        // TODO: implement unloadResource
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unloadEverything()
    {
        // TODO: implement unloadEverything
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GfxEntity createEntity()
    {
        AthensEntity entity = new AthensEntity();
        
        entities.add(entity);
        
        return entity;
    }

    @Override
    public void deleteEntity(GfxEntity entity)
    {
        entities.remove((AthensEntity)entity);
    }

    @Override
    public void attachResource(GfxEntity entity, Resource resource)
            throws ResourceNotLoadedException
    {
        AthensEntity athensEntity = (AthensEntity)entity;
        
        int type = resource.getType();
        String resName = resource.getName();
        
        switch (type)
        {
            case Resource.WAVEFRONT_MESH:
            case Resource.PRIMITIVE_MESH:
            case Resource.HEIGHTMAP_MESH:
                if (!meshResources[type].containsKey(resName))
                {
                    throw new ResourceNotLoadedException(resource);
                }
                athensEntity.mesh = meshResources[type].get(resName);
                break;
            case Resource.TEXTURE_2D:
            case Resource.TEXTURE_CUBE:
                if (!texResources[type-Resource.TEXTURE_2D].containsKey(resName))
                {
                    throw new ResourceNotLoadedException(resource);
                }
                athensEntity.texture = texResources[type-Resource.TEXTURE_2D].get(resName);
                break;
            case Resource.MATERIAL:
                if (!matResources.containsKey(resName))
                {
                    throw new ResourceNotLoadedException(resource);
                }
                athensEntity.material = matResources.get(resName);
                break;
            case Resource.SHADER:
                if (!shaderResources.containsKey(resName))
                {
                    throw new ResourceNotLoadedException(resource);
                }
                athensEntity.shader = shaderResources.get(resName);
                break;
            default:
                // TODO: throw something here as well?
                break;
        }
    }

    @Override
    public void dispatchLoadQueue(Runnable callback)
    {
        if (loadQueue == null || loadQueue.isEmpty())
        {
            // TODO: throw something
        }
        loadQueueDispatched = true;
        onLoadCallback = callback;
    }
    
    
}
