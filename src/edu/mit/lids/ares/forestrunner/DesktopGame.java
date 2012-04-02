package edu.mit.lids.ares.forestrunner;

import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.system.AppSettings;

public class DesktopGame extends KeyboardGame
{
    public DesktopGame()
    {
        super(SystemContext.DESKTOP);
    }
    

    @Override
    protected void setupProcessor()
    {
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        CartoonEdgeFilter cartoon=new CartoonEdgeFilter();
        cartoon.setDepthSensitivity(0f);
        cartoon.setNormalSensitivity(10f);
        
        // if we choose to set the camera to show a lot of the plane, then
        // we may want a fog filter to make stuff disappear in the distance
        FogFilter   fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.65f,0.65f,0.65f,1f));
        fog.setFogDensity(10f);
        fog.setFogDistance(1000f);
        
        //CartoonEdgeProcessor cartoonEdgeProcess = new CartoonEdgeProcessor();
        //viewPort.addProcessor(cartoonEdgeProcess);
        
        //fpp.addFilter(cartoon);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
    }
    
    public static void main(String[] args)
    {
        AppSettings settings = new AppSettings(true);
        settings.put("Width",   640);
        settings.put("Height",  480);
        settings.put("Title",   "Forest Runner");
        settings.put("VSync",   true);
        settings.setSamples(4);
        
        DesktopGame app = new DesktopGame();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.setPauseOnLostFocus(true);
        
        java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(java.util.logging.Level.SEVERE);
        
        app.start();
    }
    
    

}

