package edu.mit.lids.ares.forestrunner;

import java.util.prefs.BackingStoreException;

import com.jme3.system.AppSettings;

public class DesktopGame extends KeyboardGame
{
    public DesktopGame()
    {
        super(SystemContext.DESKTOP);
        m_advancedSettings.put("postProcessor",true);
        m_advancedSettings.put("fogFilter", true);
        m_advancedSettings.put("lighting",true);
        m_advancedSettings.put("mainGrid", true);
        m_advancedSettings.put("verbose",false);
    }
    

    public static void main(String[] args)
    {
        AppSettings settings = new AppSettings(true);
        settings.put("Width",   640);
        settings.put("Height",  480);
        settings.put("Title",   "Forest Runner");
        settings.put("VSync",   false);
        settings.setSamples(1);
        
        try { settings.save("edu.lids.mit.ares.forestrunner"); } 
        catch (BackingStoreException ex) 
        { 
            System.err.println("Failed to save settings to file");
        }
        
        DesktopGame app = new DesktopGame();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        
        app.start();
    }
    
    

}

