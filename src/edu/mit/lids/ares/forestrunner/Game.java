package edu.mit.lids.ares.forestrunner;


import java.util.HashMap;
import java.util.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.Spatial;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.ButtonClickedEvent;

import edu.mit.lids.ares.forestrunner.screens.*;

public class Game extends SimpleApplication
{
    public enum State
    {
        CRASHED,
        RUNNING,
        PAUSED
    }

    private Nifty                           m_nifty;
    private Map<String,ScreenController>    m_screens;
    private State                           m_state;
    
    private Map<String,Integer>             m_params;
    private String                          m_user_hash;
    private FloorPatch[][]                  m_patches;
    private Node                            m_patchRoot;
    
    private float   m_density;
    private float   m_speed;
    private float   m_radius;
    private float   m_xPos;
    private float   m_yPos;
    private float   m_patchSize;
    private int     m_patchDimX;
    private int     m_patchDimY;
    
    public Integer getParam(String param)
    {
        return m_params.get(param);
    }
    
    public String getUserHash()
    {
        return m_user_hash;
    }
    
    public State getState()
    {
        return m_state;
    }
    
    public void setState( State state )
    {
        m_state = state;
    }
    
    public Game()
    {
        m_screens = new HashMap<String,ScreenController>();
        m_params  = new HashMap<String,Integer>();
        m_state   = State.CRASHED;
        
        String[] paramNames = {"velocity","density","radius"};
        for( String paramName : paramNames )
            m_params.put(paramName,1);
                
        m_user_hash = "d0d20817f7f5b26f3637590e7a2e1621";
                
        m_speed     = 3.0f;
        m_density   = 20f;
        m_radius    = 0.3f;
        
        m_xPos      = 0;
        m_yPos      = 0;
        m_patchSize = 20f;
        
        m_patchDimX = 5;
        m_patchDimY = 4;
    }
    
    
    /**
     * Map hotkeys.
     */
    private void initKeys() 
    {
        // don't quit on escape
        inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_EXIT );
        
        //add pause keys which bring up the pause menu
        inputManager.addMapping("Pause",        new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("Pause",        new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Crash",        new KeyTrigger(KeyInput.KEY_Q));
         
        //add the names to the action listener
        inputManager.addListener(pauseListener,new String[]{"Pause", "Crash"});
    }
     
     
    private ActionListener pauseListener = new ActionListener() 
    {
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
            // the pause action is only meaning full if the game is active
            if (name.equals("Pause") && !keyPressed) 
            {
                // if we're in the game screen and we're paused then we can
                // resume, but we may also be in the game screen because we're
                // crashed so we don't resume then
                if( m_nifty.getCurrentScreen().getScreenId().compareTo("game")==0 )
                {
                    GameScreen screen = 
                            GameScreen.class.cast( m_screens.get("game") );
                    
                    if( m_state==State.PAUSED )
                        screen.onButton("game.btn.cancel", new ButtonClickedEvent(null));
                    else
                        screen.onButton("game.btn.new", new ButtonClickedEvent(null));
                }
                    
                else if( m_nifty.getCurrentScreen().getScreenId().compareTo("empty")==0 )
                {
                    m_state = State.PAUSED;
                    m_nifty.gotoScreen("game");
                }
            }
                
            
            if (name.equals("Crash") && !keyPressed )
            {
                m_nifty.gotoScreen("crash");
                m_state = State.CRASHED;
            }
        }
    };
     
    public void initPatches()
    {
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        float width     = m_patchSize;
        float height    = m_patchSize;
        
        m_patchRoot = new Node("patch_root");
        m_patches = new FloorPatch[dimx][dimy];
        
        rootNode.attachChild(m_patchRoot);
        
        for(int i=0; i < dimx; i++)
        {
            for(int j=0; j < dimy; j++)
            {
                String      patchName   = "floorpatch_" + i + "_" + j;
                FloorPatch  patch       = new FloorPatch(patchName,width,height); 
                m_patches[i][j]         = patch; 
                m_patchRoot.attachChild(patch);
            }
        }
        
        initRun();
        
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.White.mult(2f));
        lamp_light.setRadius(100f);
        lamp_light.setPosition(new Vector3f(-10f, 2f, 6f));
        rootNode.addLight(lamp_light);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);
        
        rootNode.setShadowMode(ShadowMode.CastAndReceive);
    }
    
    public void initRun()
    {
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        float width     = m_patchSize;
        float height    = m_patchSize;
        float density   = m_density;
        float radius    = m_radius;
        
        m_yPos = 0;
        m_xPos = 0;
        
        for(int i=0; i < dimx; i++)
        {
            for(int j=0; j < dimy; j++)
            {
                FloorPatch patch = m_patches[i][j];
                patch.setLocalTranslation((i-dimx/2)*width, -0.7f, -j*height);
                patch.regenerate(assetManager,density,radius);
            }
        }
        
        m_state = State.PAUSED;
        m_patchRoot.setLocalTranslation(0,0,0);
        
        System.out.println("initialized a new run");
    }

    public void simpleInitApp() 
    {
        setDisplayFps(false);
        setDisplayStatView(false);
        
        
        if(true)
        {
            Quad q=new Quad(1f, 1f);
            Geometry geom=new Geometry("bg", q);
            Material bgMaterial = new Material(assetManager, "Shaders/fixedBg/Gradient.j3md");
            bgMaterial.setColor("FirstColor", new ColorRGBA(  1f,  1f,  1f,1f) );   // dark gray
            bgMaterial.setColor("SecondColor", new ColorRGBA(  1f,  1f,  1f,1f) );  // light gray
            geom.setMaterial(bgMaterial);
            geom.setQueueBucket(RenderQueue.Bucket.Sky);
            geom.setCullHint(Spatial.CullHint.Never);
            rootNode.attachChild(geom);
        }
        
        if(true)
        {
            Quad q=new Quad(1f, 0.5f);
            Geometry geom=new Geometry("bg", q);
            Material bgMaterial = new Material(assetManager, "Shaders/fixedBg/Gradient.j3md");
            bgMaterial.setColor("FirstColor", new ColorRGBA(0.3f,0.3f,0.3f,1f) );   // dark gray
            bgMaterial.setColor("SecondColor", new ColorRGBA(0.7f,0.7f,0.7f,1f) );  // light gray
            geom.setMaterial(bgMaterial);
            geom.setQueueBucket(RenderQueue.Bucket.Sky);
            geom.setCullHint(Spatial.CullHint.Never);
            rootNode.attachChild(geom);
        }
        
        
        /*
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        geom.setLocalTranslation(-5f,-5f,-10f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        mat.setColor("Color", new ColorRGBA(1.0f,0f,0f,1f) );
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
        */

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          guiViewPort);
        m_nifty = niftyDisplay.getNifty();
        
        m_screens.put("disclaimer", new DisclaimerScreen(this));
        m_screens.put("game",       new GameScreen(this));
        m_screens.put("highscore",  new HighScoreScreen(this));
        m_screens.put("countdown",  new CountdownScreen(this));
        m_screens.put("empty",      new EmptyScreen(this));
        m_screens.put("crash",      new CrashScreen(this));
        
        for( ScreenController sc : m_screens.values() )
        {
            m_nifty.registerScreenController(sc);
            m_nifty.subscribeAnnotations(sc);
        }
        
        m_nifty.fromXml("Interface/Nifty/ui.xml", "disclaimer");
        
        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);

        // disable the fly cam
        flyCam.setEnabled(false);
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        
        // changes the camera "zoom" by setting the viewing angle to 20deg
        // so that cylinders don't get clipped when they get close to the
        // camera
        cam.setFrustumPerspective(30f, 640f/480f, 1f, 1000f);
        
        initPatches();
        initKeys();
        setupProcessor();
    }
    
    private void setupProcessor() {
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        CartoonEdgeFilter cartoon=new CartoonEdgeFilter();
        cartoon.setDepthThreshold(.2f);
        cartoon.setEdgeWidth(0.5f);
        fpp.addFilter(cartoon);
        viewPort.addProcessor(fpp);
    }
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        if(m_state != State.RUNNING)
            return;
        
        m_yPos += m_speed*tpf;
        
        // if we've passed the end if the first row, then shuffle it back
        // to the last row
        if(m_yPos > m_patchSize)
        {
            // reduce the yposition by one patch length
            m_yPos -= m_patchSize;
            
            // shuffle patches
            int   dimx      = m_patchDimX;
            int   dimy      = m_patchDimY;
            float width     = m_patchSize;
            float height    = m_patchSize;
            
            for(int i=0; i < dimx; i++)
            {
                FloorPatch temp = m_patches[i][0];
                FloorPatch patch;
                for(int j=0; j < dimy-1; j++)
                {
                    patch = m_patches[i][j] = m_patches[i][j+1];
                    patch.setLocalTranslation((i-dimx/2)*width, -0.7f, -j*height);
                }
                
                m_patches[i][dimy-1] = temp;
                temp.setLocalTranslation((i-dimx/2)*width, -0.7f, -(dimy-1)*height);
                temp.regenerate(assetManager, m_density, m_radius);
            }
        }
        
        m_patchRoot.setLocalTranslation(0,0,m_yPos);
    }

}

