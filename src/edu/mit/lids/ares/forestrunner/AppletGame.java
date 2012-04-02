package edu.mit.lids.ares.forestrunner;

import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.post.filters.FogFilter;


public class AppletGame extends KeyboardGame
{
    public AppletGame()
    {
        super(SystemContext.APPLET);
    }
    
    @Override
    protected void setupProcessor()
    {
        FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
        //CartoonEdgeFilter cartoon=new CartoonEdgeFilter();
        //cartoon.setDepthSensitivity(0f);
        //cartoon.setNormalSensitivity(10f);
        
        // if we choose to set the camera to show a lot of the plane, then
        // we may want a fog filter to make stuff disappear in the distance
        FogFilter   fog = new FogFilter();
        fog.setFogColor(new ColorRGBA(0.65f,0.65f,0.65f,1f));
        fog.setFogDensity(10f);
        fog.setFogDistance(400f);
        
        //CartoonEdgeProcessor cartoonEdgeProcess = new CartoonEdgeProcessor();
        //viewPort.addProcessor(cartoonEdgeProcess);
        
        //fpp.addFilter(cartoon);
        fpp.addFilter(fog);
        viewPort.addProcessor(fpp);
        viewPort.setBackgroundColor(ColorRGBA.Gray);
    }
}

