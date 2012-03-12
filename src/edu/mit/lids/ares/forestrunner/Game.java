package edu.mit.lids.ares.forestrunner;


import java.util.HashMap;
import java.util.Map;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.screens.*;

public class Game extends SimpleApplication
{
    public enum Mode
    {
        DEMO,
        PLAY
    }

    private Nifty                           m_nifty;
    private Map<String,ScreenController>    m_screens;
    private Mode                            m_mode;
    
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
    
    public Game()
    {
        m_screens = new HashMap<String,ScreenController>();
        m_params  = new HashMap<String,Integer>();
        m_mode    = Mode.DEMO;
        
        String[] paramNames = {"velocity","density","radius"};
        for( String paramName : paramNames )
            m_params.put(paramName,1);
                
        m_user_hash = "d0d20817f7f5b26f3637590e7a2e1621";
    }
    
    public Map<String,ScreenController> screenMap()
    {
        return m_screens;
    }
    
    public Nifty nifty()
    {
        return m_nifty;
    }

    public void simpleInitApp() 
    {
        setDisplayFps(false);
        setDisplayStatView(false);
        
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
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
    }

}

