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
        settings.put("Samples", 4);
        
        Game app = new Game();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.setPauseOnLostFocus(true);
        app.start();
    }
}
