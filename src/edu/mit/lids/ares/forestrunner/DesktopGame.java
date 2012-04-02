package edu.mit.lids.ares.forestrunner;

import com.jme3.system.AppSettings;

public class DesktopGame extends KeyboardGame
{
    public DesktopGame()
    {
        super(SystemContext.DESKTOP);
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

