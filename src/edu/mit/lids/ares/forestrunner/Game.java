package edu.mit.lids.ares.forestrunner;


import java.util.HashMap;
import java.util.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.Spatial;
import com.jme3.renderer.queue.RenderQueue;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;

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
    
    public Game()
    {
        m_screens = new HashMap<String,ScreenController>();
        m_params  = new HashMap<String,Integer>();
        m_state   = State.CRASHED;
        
        String[] paramNames = {"velocity","density","radius"};
        for( String paramName : paramNames )
            m_params.put(paramName,1);
                
        m_user_hash = "d0d20817f7f5b26f3637590e7a2e1621";
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
         
        //add the names to the action listener
        inputManager.addListener(pauseListener,new String[]{"Pause"});
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
                if( m_nifty.getCurrentScreen().getScreenId().compareTo("game")==0 
                       && m_state==State.PAUSED )
                {
                    m_nifty.gotoScreen("empty");
                    m_state = State.RUNNING;
                }
                    
                else if( m_nifty.getCurrentScreen().getScreenId().compareTo("empty")==0 )
                {
                    m_state = State.PAUSED;
                    m_nifty.gotoScreen("game");
                }
            }
         }
     };

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
        
        
        
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        geom.setLocalTranslation(-5f,-5f,-10f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        mat.setColor("Color", new ColorRGBA(1.0f,0f,0f,1f) );
        geom.setMaterial(mat);
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

}

