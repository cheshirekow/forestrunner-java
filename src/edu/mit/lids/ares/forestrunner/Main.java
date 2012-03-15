package edu.mit.lids.ares.forestrunner;

import com.jme3.system.AppSettings;

public class Main
{
    public static void main(String[] args)
    {
        AppSettings settings = new AppSettings(true);
        settings.put("Width",   640);
        settings.put("Height",  480);
        settings.put("Title",   "Forest Runner");
        settings.put("VSync",   true);
        settings.setSamples(4);
        
        Game app = new Game(SystemContext.DESKTOP);
        app.setShowSettings(false);
        app.setSettings(settings);
        app.setPauseOnLostFocus(true);
        
        java.util.logging.Logger.getAnonymousLogger().getParent().setLevel(java.util.logging.Level.SEVERE);
        java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(java.util.logging.Level.SEVERE);
        
        app.start();
    }
}
