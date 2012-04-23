package edu.mit.lids.ares.forestrunner.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.data.Store;
import edu.mit.lids.ares.forestrunner.gui.screens.*;

/**
 *  \brief  provides a consistent state-transition model for the gui
 */
public class ScreenManager 
{
    private ScreenBase                      m_activeScreen;
    private Nifty                           m_nifty;
    private Map<String,ScreenBase>          m_screens;
    private Store                           m_dataStore;
    
    public ScreenManager(Nifty nifty, Store dataStore)
    {
        m_nifty         = nifty;
        m_dataStore     = dataStore;
        m_screens       = new HashMap<String,ScreenBase>();
        
        m_screens.put("disclaimer", new DisclaimerScreen(this));
        m_screens.put("loading",    new LoadingScreen(this));
        m_screens.put("nick",       new NickScreen(this,dataStore));
        
        for( ScreenController sc : m_screens.values() )
            m_nifty.registerScreenController(sc);

        for( String screenName : m_screens.keySet() )
            m_nifty.addXml( "Interface/Nifty/Screens/" + screenName + ".xml" );
        
        m_activeScreen = m_screens.get("disclaimer");
        m_nifty.gotoScreen("disclaimer");
    }
    
    public void update(float tpf)
    {
        if(m_activeScreen != null)
            m_activeScreen.update(tpf);
    }
    
    public void advance(String nextScreen)
    {
        m_activeScreen.setActive(false);
        m_activeScreen = m_screens.get(nextScreen);
        ScreenBase.s_logger.log(Level.INFO, 
                        "Changed active screen to " + nextScreen);
        m_nifty.gotoScreen(nextScreen);
    }
    
    
}
