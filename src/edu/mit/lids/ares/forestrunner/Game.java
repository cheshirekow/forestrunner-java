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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.ButtonClickedEvent;

import edu.mit.lids.ares.forestrunner.screens.*;
import edu.mit.lids.ares.forestrunner.toonblow.CartoonEdgeProcessor;

public class Game extends SimpleApplication
{
    public enum State
    {
        COLLISION,
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
    private Node                            m_patchRotate;
    private Spatial                         m_aircraftNode;
    
    private Boolean m_leftDown;
    private Boolean m_rightDown;
    
    private float   m_density;
    private float   m_xAccel;
    private float   m_xSpeedMax;
    private float   m_xSpeed;
    private float   m_ySpeed;
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
    
    public void setParam(String param, int value)
    {
        m_params.put(param, value);
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
            m_params.put(paramName,0);
                
        m_user_hash = "d0d20817f7f5b26f3637590e7a2e1621";
                
        m_leftDown  = false;
        m_rightDown = false;
                
        m_xAccel    = 10.0f;
        m_xSpeedMax = 6.0f;
        m_xSpeed    = 0f;
        m_ySpeed    = 3.0f;
        m_density   = 20f;
        m_radius    = 0.1f;
        
        m_xPos      = 0;
        m_yPos      = 0;
        m_patchSize = 20.1f;
        
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
        
        inputManager.addMapping("Left",         new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Left",         new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",        new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Right",        new KeyTrigger(KeyInput.KEY_RIGHT));
         
        //add the names to the action listener
        inputManager.addListener(pauseListener,new String[]{"Pause", "Crash"});
        inputManager.addListener(dodgeListener,new String[]{"Left", "Right"});
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
    
    private ActionListener dodgeListener = new ActionListener() 
    {
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
            // the pause action is only meaning full if the game is active
            if (name.equals("Left") ) 
            {
                if(keyPressed)
                    m_leftDown = true;
                else
                    m_leftDown = false;
            }
                
            else // (name.equals("Right") )
            {
                if(keyPressed)
                    m_rightDown = true;
                else
                    m_rightDown = false;
            }
        }
    };
     
    public void initPatches()
    {
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        float width     = m_patchSize;
        float height    = m_patchSize;
        
        m_patchRoot     = new Node("patch_root");
        m_patchRotate   = new Node("patch_rotate");
        m_patches       = new FloorPatch[dimx][dimy];
        
        m_patchRotate.attachChild(m_patchRoot);
        rootNode.attachChild(m_patchRotate);
        
        for(int i=0; i < dimx; i++)
        {
            for(int j=0; j < dimy; j++)
            {
                String      patchName   = "floorpatch_" + i + "_" + j;
                FloorPatch  patch       = new FloorPatch(patchName,width,height,assetManager); 
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
        m_radius = 0.1f + 0.05f * m_params.get("radius");
        m_ySpeed = 3.0f + 1.0f * m_params.get("velocity");
        m_density= 20f  + 10f  * m_params.get("density");
        
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
                patch.setLocalTranslation((i-dimx/2f)*width, 0f, -j*height);
                patch.fullRegenerate(assetManager,density,radius);
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
        
        // the quads aren't visible with fog anyway
        /*
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
            Quad q=new Quad(1f, 0.95f);
            Geometry geom=new Geometry("bg", q);
            Material bgMaterial = new Material(assetManager, "Shaders/fixedBg/Gradient.j3md");
            bgMaterial.setColor("FirstColor", new ColorRGBA(0.3f,0.3f,0.3f,1f) );   // dark gray
            bgMaterial.setColor("SecondColor", new ColorRGBA(0.65f,0.65f,0.65f,1f) );  // light gray
            geom.setMaterial(bgMaterial);
            geom.setQueueBucket(RenderQueue.Bucket.Sky);
            geom.setCullHint(Spatial.CullHint.Never);
            rootNode.attachChild(geom);
        }
        */
        
        AircraftMesh    ac      = new AircraftMesh();
        Geometry        geom    = new Geometry("aircraft",ac);
        /*
        Material material   = new Material(assetManager,                
                                    "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors",true);    
        material.setColor("Ambient", ColorRGBA.Gray);     
        material.setColor("Diffuse", ColorRGBA.Gray);
        */
        Material material   = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Gray);
        geom.setMaterial(material);
        geom.setLocalTranslation(0f, 0f, 0f);
        rootNode.attachChild(geom);
        m_aircraftNode = geom;
        
        geom = new Geometry("aircraft_wf",ac);
        material = material.clone();
        material.setColor("Color", ColorRGBA.Black);
        material.getAdditionalRenderState().setWireframe(true);
        geom.setMaterial(material);
        geom.setLocalScale(1.1f);
        geom.setLocalTranslation(0f, 0f, -0.01f);
        rootNode.attachChild(geom);
        
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
            // apparently registerScreenController also subscribes annotations
            //m_nifty.subscribeAnnotations(sc);
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
        cam.setLocation(new Vector3f(0f,2.5f,5f));
        cam.setFrustumPerspective(30f, 640f/480f, 1f, 1000f);
        cam.lookAt(new Vector3f(0f,0f,-4f), new Vector3f(0f,1f,0f) );
        
        initPatches();
        initKeys();
        setupProcessor();
    }
    
    private void setupProcessor() {
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        CartoonEdgeFilter cartoon=new CartoonEdgeFilter();
        cartoon.setDepthSensitivity(0f);
        cartoon.setNormalSensitivity(10f);
        
        // if we choose to set the camera to show a lot of the plane, then
        // we may want a fog filter to make stuff disappear in the distance
        FogFilter   fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.65f,0.65f,0.65f,1f));
        fog.setFogDensity(10f);
        fog.setFogDistance(400f);
        
        CartoonEdgeProcessor cartoonEdgeProcess = new CartoonEdgeProcessor();
        viewPort.addProcessor(cartoonEdgeProcess);
        
        //fpp.addFilter(cartoon);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        // if we're paused or crashed don't update the scene
        if(m_state != State.RUNNING)
            return;
        
        // update the xspeed if necessary
        if(m_leftDown || m_rightDown)
        {
            if(m_leftDown)
                m_xSpeed -= m_xAccel*tpf;
            if(m_rightDown)
                m_xSpeed += m_xAccel*tpf;
        }
        else
        {
            float sign = Math.signum(m_xSpeed); 
            m_xSpeed -= sign*m_xAccel*tpf;
            
            // avoid overshoot
            if( sign != Math.signum(m_xSpeed) )
                m_xSpeed = 0;
        }
        
        m_xSpeed = Math.min(m_xSpeed, m_xSpeedMax);
        m_xSpeed = Math.max(m_xSpeed, -m_xSpeedMax);
        
        // rotate the scene according to xspeed
        float angle = (float)(Math.PI / 9) * m_xSpeed / m_xSpeedMax;
        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle, new Vector3f(0f,0f,1f));
        m_patchRotate.setLocalRotation(q);
        
        // update the position
        m_yPos += m_ySpeed*tpf;
        m_xPos += m_xSpeed*tpf;
        
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
                    patch.setLocalTranslation((i-dimx/2f)*width, 0f, -j*height);
                }
                
                m_patches[i][dimy-1] = temp;
                temp.setLocalTranslation((i-dimx/2f)*width, 0f, -(dimy-1)*height);
                temp.regenerate(assetManager, m_density, m_radius);
            }
        }
        
        m_patchRoot.setLocalTranslation(-m_xPos,0,m_yPos);
        rootNode.updateGeometricState();
        
        Boolean collision = false;
        for( int i=0; i < m_patchDimX && !collision; i++)
        {
            for(int j=0; j < m_patchDimY && !collision; j++)
            {
                collision 
                    = m_patches[i][j].collisionCheck(m_xPos, m_yPos, m_radius);
                if(collision)
                    System.out.println("Collision in loop");
            }
        }
        
        if(collision)
            pauseListener.onAction("Crash", false, tpf);
    }

}

