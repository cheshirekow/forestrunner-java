package edu.mit.lids.ares.forestrunner.gui;

import java.util.Map;
import de.lessvoid.nifty.Nifty;
import edu.mit.lids.ares.forestrunner.data.Store;

/**
 *  \brief  provides a consistent state-transition model for the gui
 */
public class ScreenManager
{
    private Boolean                         m_transitioning;
    private ScreenBase                      m_activeScreen;
    private Nifty                           m_nifty;
    private Map<String,ScreenBase>          m_screens;
    private Store                           m_dataStore;
    
    public ScreenManager(Nifty nifty, Store dataStore)
    {
        m_nifty         = nifty;
        m_dataStore     = dataStore;
        m_transitioning = false;
    }
    
    public void update(float tpf)
    {
        m_activeScreen.update(tpf);
    }
    
    public void requestTransition(String screenName)
    {
        if(!m_transitioning)
        {
            m_nifty.gotoScreen(screenName);
            m_activeScreen = m_screens.get(screenName);
        }
    }
    
    public void transitionFinished()
    {
        m_transitioning = false;
    }
    
}
