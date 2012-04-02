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
import com.jme3.scene.shape.Quad;
import com.jme3.scene.Spatial;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;

import edu.mit.lids.ares.forestrunner.screens.*;
import edu.mit.lids.ares.forestrunner.toonblow.CartoonEdgeProcessor;

public abstract class Game extends SimpleApplication
{
    public enum State
    {
        COLLISION,
        CRASHED,
        RUNNING,
        PAUSED
    }
    
    protected Nifty                           m_nifty;
    protected Map<String,ScreenController>    m_screens;
    protected State                           m_state;
    protected SystemContext                   m_system;
    
    protected Map<String,Integer>             m_params;
    protected FloorPatch[][]                  m_patches;
    protected Node                            m_patchRoot;
    protected Node                            m_patchRotate;
    
    protected float   m_density;
    protected float   m_xAccel;
    protected float   m_xSpeedMax;
    protected float   m_xSpeed;
    protected float   m_ySpeed;
    protected float   m_radius;
    protected float   m_xPos;
    protected float   m_yPos;
    protected float   m_patchWidth;
    protected float   m_patchHeight;
    protected float   m_acSide;
    protected float   m_acRadius;
    protected float   m_acTrans;
    protected float   m_score;
    protected int     m_patchDimX;
    protected int     m_patchDimY;
    
    public Integer getParam(String param)
    {
        return m_params.get(param);
    }
    
    public void setParam(String param, int value)
    {
        m_params.put(param, value);
    }
    
    public State getState()
    {
        return m_state;
    }
    
    public SystemContext getSystem()
    {
        return m_system;
    }
    
    public float getScore()
    {
        return m_score;
    }
    
    public void setState( State state )
    {
        m_state = state;
    }
    
    protected void init()
    {
        m_screens = new HashMap<String,ScreenController>();
        m_params  = new HashMap<String,Integer>();
        m_state   = State.CRASHED;
        
        String[] paramNames = {"velocity","density","radius"};
        for( String paramName : paramNames )
            m_params.put(paramName,0);
                
        m_xAccel    = 10.0f;
        m_xSpeedMax = 6.0f;
        m_xSpeed    = 0f;
        m_ySpeed    = 3.0f;
        m_density   = 20f;
        m_radius    = 0.1f;
        
        m_xPos      = 0;
        m_yPos      = 0;
        m_patchWidth    = 5.1f; //20.1f;
        m_patchHeight   = 8.1f; //20.1f;
        
        m_patchDimX = 5;
        m_patchDimY = 8; //4;
        
        m_acSide    = 0.3f;
        m_acRadius  = (m_acSide/2f) * (float)Math.tan(Math.PI/6.0);
        m_acTrans   = (float)( m_acSide*Math.sin(Math.PI/3) ) - m_acRadius;
    }
    
    public Game(SystemContext ctx)
    {
        m_system = ctx;
        init();
    }
    
    
    
     
     
    public void initPatches()
    {
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        float width     = m_patchWidth;
        float height    = m_patchHeight;
        
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
        m_xSpeed = 0f;
        m_density= 1f  + 1f  * m_params.get("density");
                    //20f  + 10f  * m_params.get("density");
        
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        float width     = m_patchWidth;
        float height    = m_patchHeight;
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
        m_score = 0;
        m_patchRoot.setLocalTranslation(0,0,0);
        m_patchRotate.setLocalRotation(Quaternion.IDENTITY);
        
        System.out.println("initialized a new run");
    }

    @Override
    public void simpleInitApp() 
    {
        setDisplayFps(false);
        setDisplayStatView(false);
        
        viewPort.setBackgroundColor(new ColorRGBA(.9f,.9f,.9f,1f));
        
        AircraftMesh    ac      = new AircraftMesh(m_acSide);
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
        geom.setLocalTranslation(0f, 0f, -m_acTrans);
        rootNode.attachChild(geom);
        
        geom = new Geometry("aircraft_wf",ac);
        material = material.clone();
        material.setColor("Color", ColorRGBA.Black);
        material.getAdditionalRenderState().setWireframe(true);
        geom.setMaterial(material);
        geom.setLocalScale(1.1f);
        geom.setLocalTranslation(0f, 0f, -m_acTrans-0.01f);
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
        cam.setFrustumPerspective(30f, 640f/480f, 1f, 40f);
        cam.lookAt(new Vector3f(0f,0f,-4f), new Vector3f(0f,1f,0f) );
        
        initPatches();
        
        // note the quads aren't visible with fogs
        if(true)
        {
            float w = 40f;
            float h = 42f;
            Quad q=new Quad(w, h);
            geom=new Geometry("bg", q);
            Material bgMaterial = new Material(assetManager, "Shaders/quadGradient/Gradient.j3md");
            bgMaterial.setColor("FirstColor", new ColorRGBA(0.3f,0.3f,0.3f,1f) );   // dark gray
            bgMaterial.setColor("SecondColor", new ColorRGBA(0.65f,0.65f,0.65f,1f) );  // light gray
            //Material bgMaterial = new Material(assetManager,
            //        "Common/MatDefs/Misc/Unshaded.j3md");
            //bgMaterial.setColor("Color", ColorRGBA.Gray);
            geom.setMaterial(bgMaterial);
            //geom.setLocalTranslation(0,-30f,-40f);

            float angle = -(1/2f)*((float)(Math.PI));
            Quaternion quat = new Quaternion();
            quat.fromAngleAxis(angle, new Vector3f(1f,0f,0f));
            
            geom.setLocalTranslation(-w/2f,-0.01f,2f);
            geom.setLocalRotation(quat);
            
            //geom.setQueueBucket(RenderQueue.Bucket.Sky);
            //geom.setCullHint(Spatial.CullHint.Never);
            m_patchRotate.attachChild(geom);
        }
        
        setupProcessor();
    }
    
    abstract protected void setupProcessor();
    abstract protected void updateSpeed(float tpf);
    abstract protected void onCrash(float tpf);
    
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        // if we're paused or crashed don't update the scene
        if(m_state != State.RUNNING)
            return;
        
        // update the score as we've survived for tpf
        m_score += tpf;
        
        // update the gui with the socre
        Label lbl = m_nifty.getCurrentScreen().
                        findNiftyControl("lbl.timer", Label.class);
        if(lbl != null)
            lbl.setText(String.format("Score: %6.2f",m_score));
        
        // update the xspeed if necessary
        updateSpeed(tpf);
        
        // update the position
        m_yPos += m_ySpeed*tpf;
        m_xPos += m_xSpeed*tpf;
        
        // if we've passed the end if the first row, then shuffle it back
        // to the last row
        if(m_yPos > m_patchHeight)
        {
            // reduce the yposition by one patch length
            m_yPos -= m_patchHeight;
            
            // shuffle patches
            int   dimx      = m_patchDimX;
            int   dimy      = m_patchDimY;
            float width     = m_patchWidth;
            float height    = m_patchHeight;
            
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
        
        if(m_xPos > m_patchWidth)
        {
            // reduce the xposition by one patch length
            m_xPos -= m_patchWidth;
            
            // shuffle patches
            // shuffle patches
            int   dimx      = m_patchDimX;
            int   dimy      = m_patchDimY;
            float width     = m_patchWidth;
            float height    = m_patchHeight;
            
            for(int j=0; j < dimy; j++)
            {
                FloorPatch temp = m_patches[0][j];
                FloorPatch patch;
                for(int i=0; i < dimx-1; i++)
                {
                    patch = m_patches[i][j] = m_patches[i+1][j];
                    patch.setLocalTranslation((i-dimx/2f)*width, 0f, -j*height);
                }
                
                m_patches[dimx-1][j] = temp;
                temp.setLocalTranslation((dimx/2f-1)*width, 0f, -j*height);
            }
        }
        
        if(m_xPos < -m_patchWidth)
        {
            // reduce the xposition by one patch length
            m_xPos += m_patchWidth;
            
            // shuffle patches
            // shuffle patches
            int   dimx      = m_patchDimX;
            int   dimy      = m_patchDimY;
            float width     = m_patchWidth;
            float height    = m_patchHeight;
            
            for(int j=0; j < dimy; j++)
            {
                FloorPatch temp = m_patches[dimx-1][j];
                FloorPatch patch;
                for(int i=dimx-1; i > 0; i--)
                {
                    patch = m_patches[i][j] = m_patches[i-1][j];
                    patch.setLocalTranslation((i-dimx/2f)*width, 0f, -j*height);
                }
                
                m_patches[0][j] = temp;
                temp.setLocalTranslation((-dimx/2f)*width, 0f, -j*height);
            }
        }
        
        m_patchRoot.setLocalTranslation(-m_xPos,0,m_yPos);
        rootNode.updateGeometricState();
        
        Boolean collision = false;
        for( int i=0; i < m_patchDimX && !collision; i++)
        {
            for(int j=0; j < 2 && !collision; j++)
            {
                collision 
                    = m_patches[i][j].collisionCheck(m_xPos, m_yPos, m_radius + m_acRadius);
                if(collision)
                    System.out.println("Collision in loop");
            }
        }
        
        if(collision)
            onCrash(tpf);
    }
    
}

