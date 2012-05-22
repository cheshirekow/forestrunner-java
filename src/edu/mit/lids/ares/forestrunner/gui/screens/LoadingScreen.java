package edu.mit.lids.ares.forestrunner.gui.screens;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.nifty.ProgressbarControl;

public class LoadingScreen 
    extends
        ScreenBase
    implements 
        ScreenController
{
    private ProgressbarControl  m_pb; 
    private int                 m_processed;
    private int                 m_total;
    private int                 m_phase;
    private static final int    s_totalPhases = 500;
    private AppStateManager     m_mgr;
    
    public LoadingScreen( AppStateManager mgr )
    {
        super();
        m_mgr               = mgr;
        m_hasEntranceAnim   = false;
        m_hasExitAnim       = false;
    }
    
    @Override
    public void onStart_impl()
    {
        m_mgr.attach(this);
        m_processed = 0;
        m_phase     = 0;
        m_total     = s_totalPhases;
    }
    
    @Override
    public void onEnd_impl()
    {
        m_mgr.detach(this);
    }
    
    @Override
    public void update_impl( float tpf )
    {
        switch(m_phase)
        {
            case s_totalPhases:
                m_nifty.gotoScreen("disclaimer");
                break;
                
            default:
                break;
        }
        
        m_phase++;
        m_pb.setProgress( (m_processed + m_phase) / (float)m_total );
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        m_pb = screen.findControl("pb_loading", ProgressbarControl.class);
    }
    
    
    

}
