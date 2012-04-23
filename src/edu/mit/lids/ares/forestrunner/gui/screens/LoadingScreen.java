package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.gui.ScreenManager;
import edu.mit.lids.ares.forestrunner.nifty.ProgressbarControl;

public class LoadingScreen 
    extends
        ScreenBase
    implements 
        ScreenController
{
    private float               m_pct;
    private ProgressbarControl  m_pb; 
    
    public LoadingScreen( ScreenManager mgr )
    {
        super(mgr);
        m_pct = 0;
    }
    
    @Override
    public void update_impl( float tpf )
    {
        m_pct += tpf;
        m_pct = Math.min(m_pct,1);
        m_pb.setProgress(m_pct);
        if(m_pct >= 1)
            m_nifty.gotoScreen("nick");
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        m_pb = screen.findControl("pb_loading", ProgressbarControl.class);
    }
    
    
    

}
