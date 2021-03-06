package edu.mit.lids.ares.forestrunner;


import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.Label;

import edu.mit.lids.ares.forestrunner.data.Store;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.gui.screens.*;
import edu.mit.lids.ares.forestrunner.screens.*;

public abstract class Game extends Application
{
    public enum State
    {
        COLLISION,
        CRASHED,
        RUNNING,
        PAUSED
    }
    
    // this should NEVER decrease, increment when parameter equations are
    // changed, or when storage backend changes
    public static final int   s_version    = 3;
    
    public static final float s_pad        = 0.08f;
    public static final float s_cPad       = 0.03f;
    public static final float s_farPlane   = 35f;
    public static final float s_treeHeight = 0.5f;
    
    // stuff taken from jme.app.SimpleApplication
    protected Node          rootNode    = new Node("Root Node");
    protected Node          guiNode     = new Node("Gui Node");
    protected boolean       showSettings= true;
    
    protected Nifty                           m_nifty;
    protected Map<String,ScreenBase>          m_screens;
    protected State                           m_state;
    protected SystemContext                   m_system;

    protected Store                           m_dataStore;
    
    protected Map<String,Integer>             m_params;
    protected FloorPatch[][]                  m_patches;
    protected Node                            m_patchRoot;
    protected Node                            m_patchRotate;
    protected Node                            m_acRotate;
   
    protected PointLight        m_pointLight;
    protected AmbientLight      m_ambientLight;
    
    FilterPostProcessor         m_fpp;
    FogFilter                   m_fogFilter;
    Geometry                    m_gridNode;
    
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
    protected Boolean m_worldRotate;
    
    protected AircraftMesh      m_acBaseMesh;
    protected AircraftMesh      m_acOutlineMesh;
    protected AircraftWireMesh  m_acWireMesh;
    protected CylinderOutline   m_acBumperMesh;
    
    protected Geometry      m_acBaseNode;
    protected Geometry      m_acOutlineNode;
    protected Geometry      m_acWireNode;
    protected Geometry      m_acBumperNode;
    
    protected Cylinder          m_cylinderBaseMesh;
    protected Cylinder          m_cylinderOutlineMesh;
    protected CylinderOutline   m_cylinderWireMesh;
    
    protected GradientQuad      m_gradient;
    protected Geometry          m_gradientNode;
    
    protected AdvancedSettings   m_advancedSettings;
    
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
    
    public Store getDataStore()
    {
        return m_dataStore;
    }
    
    public Game(SystemContext ctx)
    {
        super();
        
        if( ctx != SystemContext.APPLET)
        {
            java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(java.util.logging.Level.WARNING);
            java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(java.util.logging.Level.WARNING);
            java.util.logging.Logger.getLogger(ScreenBase.class.getName()).setLevel(java.util.logging.Level.ALL);
        }
        
        m_screens= new HashMap<String,ScreenBase>();
        m_params  = new HashMap<String,Integer>();
        
        m_system            = ctx;
        m_advancedSettings  = new AdvancedSettings();
    }
    
    
    public AdvancedSettings getAdvancedSettings()
    {
        return m_advancedSettings;
    }
    
    public void changeAdvancedSettings()
    {
        System.out.println("Updating advanced settings\n------------------");
        
        // hack: get settings from data store, not from the
        // passed object
        for( String key : AdvancedSettings.parameters )
            m_advancedSettings.put(key, m_dataStore.getBoolean(key));
        
        //m_advancedSettings = newSettings;
        m_worldRotate      = m_advancedSettings.get("worldRotate");
        
        // first, clear out all extra processors and post processing filters
        m_fpp.removeAllFilters();
        //viewPort.removeProcessor(m_toonBlow);
        viewPort.removeProcessor(m_fpp);
        
        // remove the grid if it's attached
        m_gridNode.removeFromParent();
        m_gradientNode.removeFromParent();
        m_acOutlineNode.removeFromParent();
       
        // remove lights
        rootNode.removeLight(m_ambientLight);
        rootNode.removeLight(m_pointLight);
        
        // remove logging
        if( m_system != SystemContext.APPLET)
        {
            java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(java.util.logging.Level.WARNING);
            java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(java.util.logging.Level.WARNING);
            java.util.logging.Logger.getLogger(ScreenBase.class.getName()).setLevel(java.util.logging.Level.ALL);
            
            if(m_advancedSettings.get("verbose"))
            {
                java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(java.util.logging.Level.ALL);
                java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(java.util.logging.Level.ALL);
            }
        }
        
        // now add them one by one according to the settings
        if(m_advancedSettings.get("postProcessor"))
        {
            System.out.println("Adding post processor");
            viewPort.addProcessor(m_fpp);
            
            if(m_advancedSettings.get("fogFilter"))
            {
                System.out.println("Adding fog filter");
                m_fpp.addFilter(m_fogFilter);
            }
                
        }
        
        if(m_advancedSettings.get("mainGrid"))
        {
            System.out.println("Adding main grid");
            m_patchRoot.attachChild(m_gridNode);
        }
        
        if(m_advancedSettings.get("gradientFloor"))
        {
            System.out.println("Adding gradient floor");
            m_patchRotate.attachChild(m_gradientNode);
        }
        
        
        FloorPatch.setUseGrid( m_advancedSettings.get("debugGrids") );
        FloorPatch.setUseOutline( m_advancedSettings.get("cartoon") );
        
        if(m_advancedSettings.get("cartoon"))
            m_acRotate.attachChild(m_acOutlineNode);
        
        FloorPatch.setUseLighting( m_advancedSettings.get("lighting") );
        if(m_advancedSettings.get("lighting"))
        {
            System.out.println("setting cylinders to lighting material");
            rootNode.addLight(m_ambientLight);
            rootNode.addLight(m_pointLight);
        }
        
        System.out.println("rebuilding patches");
        for(int i=0; i < m_patchDimX; i++)
        {
            for(int j=0; j < m_patchDimY; j++)
            {
                FloorPatch patch = m_patches[i][j];
                patch.reattach();
            }
        }
    }
    
    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        boolean loadSettings = false;
        if (settings == null) {
            setSettings(new AppSettings(true));
            loadSettings = true;
        }

        // show settings dialog
        if (showSettings) {
            if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
                return;
            }
        }
        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }
    
    
    
    
    public boolean isShowSettings() {
        return showSettings;
    }

    /**
     * Toggles settings window to display at start-up
     * @param showSettings Sets true/false
     *
     */
    public void setShowSettings(boolean showSettings) {
        this.showSettings = showSettings;
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks
        if (speed == 0 || paused) {
            return;
        }

        float tpf = timer.getTimePerFrame() * speed;

        // update states
        stateManager.update(tpf);
        
        // simple update and root node
        // simpleUpdate(tpf);
 
        rootNode.updateLogicalState(tpf);
        guiNode.updateLogicalState(tpf);
        
        rootNode.updateGeometricState();
        guiNode.updateGeometricState();

        // render states
        stateManager.render(renderManager);
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();    
    }
    
    
    
    
    
    
    
    
    
    
    
//-----------------------------------------------------------------------------
//                        Primary Init Sequence
//-----------------------------------------------------------------------------
    
    public void initViews()
    {
        guiNode.setQueueBucket(Bucket.Gui);
        guiNode.setCullHint(CullHint.Never);
        viewPort.attachScene(rootNode);
        guiViewPort.attachScene(guiNode);
        
        viewPort.setBackgroundColor(new ColorRGBA(.9f,.9f,.9f,1f));
    }
    
    
    
    
    public void initNifty()
    {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);

        m_nifty = niftyDisplay.getNifty();
        m_nifty.addXml("Interface/Nifty/ui.xml");
        
        // create a data store
        m_dataStore = Store.createStore(m_system);
        
        // attach the nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        
        LoadingScreen loadingScreen = new LoadingScreen(this);

        m_nifty.registerScreenController(loadingScreen);
        m_nifty.addXml("Interface/Nifty/Screens/loading.xml");
        m_screens.put("loading", loadingScreen);

        
        m_nifty.gotoScreen("loading");
    }
    
    
    
    
    @Override
    public void initialize() {
        super.initialize();

        // call user code
        initViews();
        initNifty();
        /*
        initConstants();
        initSceneGraph();
        initStaticMeshes();
        initPatches();
        setupLights();
        setupCamera();
        setupProcessor();
        setupNifty();
        initRun();
        
        changeAdvancedSettings(AdvancedSettings.s_default);
        */
    }
    
    
    
//-----------------------------------------------------------------------------
//                      Secondary Init Sequence
//-----------------------------------------------------------------------------
    public void initDataStore()
    {
        m_dataStore.init(this);
    }
    
    public void initConstants()
    {
        m_state   = State.CRASHED;
        
        String[] paramNames = {"velocity","density","radius"};
        for( String paramName : paramNames )
            m_params.put(paramName,0);
                
        m_xAccel    = 20.0f;
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
        m_acRadius  = (m_acSide/2f);
        m_acTrans   = (float)( m_acSide*Math.sin(Math.PI/3) )*2/3;
        m_worldRotate   = m_advancedSettings.get("worldRotate");
    }
    
    
    
    
    public void initStaticMeshes()
    {
        m_cylinderBaseMesh      = new Cylinder(4,10,m_radius,s_treeHeight,true,false);
        m_cylinderWireMesh      = new CylinderOutline(10,m_radius,s_treeHeight);
        m_cylinderOutlineMesh   = new Cylinder(4,10,m_radius+s_cPad,s_treeHeight+s_cPad,true,true);
        
        m_acBaseMesh    = new AircraftMesh(m_acSide);
        m_acWireMesh    = new AircraftWireMesh(m_acSide);
        m_acOutlineMesh = new AircraftMesh(m_acSide+s_pad,true);
        m_acBumperMesh  = new CylinderOutline(10,m_acRadius,0);

        m_acBaseNode    = new Geometry("aircraft",          m_acBaseMesh);
        m_acWireNode    = new Geometry("aircraft_wireframe",m_acWireMesh);
        m_acOutlineNode = new Geometry("aircraft_outline",  m_acOutlineMesh);
        m_acBumperNode  = new Geometry("aircraft_bumper",   m_acBumperMesh);
        
        Quaternion q = new Quaternion();
        q.fromAngles(FastMath.PI/2, 0, 0);
        m_acBumperNode.setLocalRotation(q);
        
        m_acBaseNode.setLocalTranslation(0f, 0f, -m_acTrans);
        m_acOutlineNode.setLocalTranslation(0f, 0f, -m_acTrans-s_pad/2f);
        m_acWireNode.setLocalTranslation(0f, 0f, -m_acTrans-0.01f);
        
        Material material   = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.Gray);
        m_acBaseNode.setMaterial(material);
        
        material = material.clone();
        material.setColor("Color", ColorRGBA.Black);
        m_acOutlineNode.setMaterial(material);
        
        material = material.clone();
        material.setColor("Color", ColorRGBA.Black);
        material.getAdditionalRenderState().setWireframe(true);
        m_acWireNode.setMaterial(material);
        m_acBumperNode.setMaterial(material);
        
        int     width   = (int)(m_patchWidth*(m_patchDimX+2));
        int     height  = (int)(m_patchHeight*m_patchDimY);
        float   backup  = 2f;
        float   drop    = 0.01f;

        m_gridNode  = new Geometry("wireframe grid", 
                                        new Grid( height, width, 1f) );
        material = material.clone();
        material.getAdditionalRenderState().setDepthWrite(false);
        m_gridNode.setMaterial(material);
        m_gridNode.setLocalTranslation(-width/2f, 0f, -height+backup);
        
        m_gradient      = new GradientQuad(width,s_farPlane+backup);
        m_gradientNode  = new Geometry("gradient",m_gradient);
        m_gradientNode.setLocalTranslation(-width/2f, -drop, backup);
        m_gradientNode.setCullHint(CullHint.Never);
        
        material = new Material(assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md");
        material.setBoolean("VertexColor", true);
        m_gradientNode.setMaterial(material);
        
        m_acRotate.attachChild(m_acBaseNode);
        m_acRotate.attachChild(m_acWireNode);
        m_acRotate.attachChild(m_acOutlineNode);
        m_patchRotate.attachChild(m_acBumperNode);
        
        m_patchRoot.attachChild(m_gridNode);
        m_patchRotate.attachChild(m_gradientNode);
    }
    
    

    
    public void initSceneGraph()
    {
        m_patchRoot     = new Node("patch_root");
        m_patchRotate   = new Node("patch_rotate");
        m_acRotate      = new Node("ac_rotate");
        
        
        m_patchRotate.attachChild(m_patchRoot);
        rootNode.attachChild(m_patchRotate);
        rootNode.attachChild(m_acRotate);
    }
    
    
    
    
    public void initPatches()
    {
        FloorPatch.buildMaterialList(assetManager);
        FloorPatch.setDim(m_patchWidth, m_patchHeight);
        FloorPatch.setMeshes(m_cylinderBaseMesh, 
                                m_cylinderOutlineMesh, 
                                m_cylinderWireMesh);
        
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        
        m_patches       = new FloorPatch[dimx][dimy];
        
        for(int i=0; i < dimx; i++)
        {
            for(int j=0; j < dimy; j++)
            {
                String      patchName   = "floorpatch_" + i + "_" + j;
                FloorPatch  patch       = new FloorPatch(patchName); 
                m_patches[i][j]         = patch; 
                m_patchRoot.attachChild(patch);
            }
        }
    }
    
    

    
    public void setupLights()
    {
        m_pointLight = new PointLight();
        m_pointLight.setColor(ColorRGBA.White.mult(2f));
        m_pointLight.setRadius(100f);
        m_pointLight.setPosition(new Vector3f(-10f, 2f, 6f));
        //rootNode.addLight(m_pointLight);
        
        m_ambientLight = new AmbientLight();
        m_ambientLight.setColor(ColorRGBA.White.mult(0.3f));
        //rootNode.addLight(m_ambientLight);
        
        rootNode.setShadowMode(ShadowMode.Off);
    }
    
    
    
    
    public void initRun()
    {
        m_radius = 0.1f + 0.03f * m_params.get("radius");
        m_ySpeed = 10.0f + 1.0f * m_params.get("velocity");
        m_xSpeed = 0f;
        m_density= 5f  + 0.5f  * m_params.get("density");
                    //20f  + 10f  * m_params.get("density");
       
        Long seed = System.currentTimeMillis();
        FloorPatch.setSeed(seed);      
        m_dataStore.initRunLog(seed);
        
        int   dimx      = m_patchDimX;
        int   dimy      = m_patchDimY;
        float width     = m_patchWidth;
        float height    = m_patchHeight;
        float density   = m_density;
        
        m_cylinderBaseMesh.updateGeometry(m_radius);
        m_cylinderWireMesh.updateGeometry(m_radius);
        m_cylinderOutlineMesh.updateGeometry(m_radius+s_cPad);
        
        
        m_yPos = 0;
        m_xPos = 0;
        
        for(int i=0; i < dimx; i++)
        {
            for(int j=0; j < dimy; j++)
            {
                FloorPatch patch = m_patches[i][j];
                patch.setLocalTranslation((i-dimx/2f)*width, 0f, -j*height);
                patch.shuffle(density, j<2);
            }
        }
        
        m_state = State.PAUSED;
        m_score = 0;
        m_patchRoot.setLocalTranslation(0,0,0);
        m_patchRotate.setLocalRotation(Quaternion.IDENTITY);
        
        System.out.println("initialized a new run");
    }
    
    
    
    
    public void setupNifty()
    {
        // remove loading screen from the list so we dont attempt to
        // reinitialize it
        ScreenBase loadingScreen = m_screens.get("loading");
        m_screens.remove("loading");
        
        m_screens.put("disclaimer", new DisclaimerScreen(this));
        //m_screens.put("loading",    new LoadingScreen(stateManager));
        m_screens.put("nick",       new NickScreen(this));
        m_screens.put("game",       new GameScreen(this));
        m_screens.put("countdown",  new CountdownScreen(this));
        m_screens.put("play",       new PlayScreen(this));
        m_screens.put("crash",      new CrashScreen(this));
        m_screens.put("advanced",   new AdvancedScreen(this));
        m_screens.put("highscore",  new HighScoreScreen(this));
        
        for( ScreenController sc : m_screens.values() )
            m_nifty.registerScreenController(sc);

        for( String screenName : m_screens.keySet() )
            m_nifty.addXml( "Interface/Nifty/Screens/" + screenName + ".xml" );

        // add loading screen back to the list
        m_screens.put("loading",loadingScreen);
        //m_nifty.gotoScreen("loading");
    }
    
    
    
    
    public void setupCamera()
    {
        // disable the fly cam
        inputManager.setCursorVisible(true);
        
        // changes the camera "zoom" by setting the viewing angle to 20deg
        // so that cylinders don't get clipped when they get close to the
        // camera
        float ar = settings.getWidth()/(float)settings.getHeight();
        cam.setLocation(new Vector3f(0f,2.5f,5f));
        cam.setFrustumPerspective(30f, ar, 1f, s_farPlane);
        cam.lookAt(new Vector3f(0f,0f,-4f), new Vector3f(0f,1f,0f) );
        
    }

    public void setupProcessor()
    {
        m_fpp=new FilterPostProcessor(assetManager);
        
        
        //m_cartoonFilter=new CartoonEdgeFilter();
        //m_cartoonFilter.setDepthSensitivity(0f);
        //m_cartoonFilter.setNormalSensitivity(10f);
        
        // if we choose to set the camera to show a lot of the plane, then
        // we may want a fog filter to make stuff disappear in the distance
        m_fogFilter = new FogFilter();
        m_fogFilter.setFogColor(new ColorRGBA(0.75f,0.75f,0.75f,1f));
        m_fogFilter.setFogDensity(10f);
        m_fogFilter.setFogDistance(1000f);
        
        //m_toonBlow = new CartoonEdgeProcessor();
        
        //fpp.addFilter(cartoon);
        //m_fpp.addFilter(m_fogFilter);
        //viewPort.addProcessor(m_fpp);
    }
    
    public void fixWireframe()
    {
        Material mat = m_acWireNode.getMaterial();
        mat.getAdditionalRenderState().setWireframe(true);
    }
    
    
    
    
    
    
    
    
    
    
    
    
//-----------------------------------------------------------------------------
//                      Game logic
//-----------------------------------------------------------------------------
    abstract protected void updateSpeed(float tpf);
    abstract protected void onCrash(float tpf);
    
    
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
        
        float oldX = m_xPos;
        float oldY = m_yPos;
        
        // update the position
        m_yPos += m_ySpeed*tpf;
        m_xPos += m_xSpeed*tpf;
        
        m_dataStore.logState(m_score, m_xPos, m_yPos, m_xSpeed);
        
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
                temp.shuffle(m_density);
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
                    = m_patches[i][j].collisionCheck2(m_xSpeed*tpf, m_ySpeed*tpf, m_radius + m_acRadius);
                if(collision)
                    System.out.println("Collision in loop");
            }
        }
        
        if(collision)
            onCrash(tpf);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

























