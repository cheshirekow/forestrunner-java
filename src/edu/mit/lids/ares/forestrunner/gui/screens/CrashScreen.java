package edu.mit.lids.ares.forestrunner.gui.screens;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class CrashScreen
    extends ScreenBase
{
    private int m_step;
    
    public CrashScreen(Game game)
    {
        super(game,true,true);
    }
    
    @Override
    public void onStart_impl()
    {
        m_step = 0;
        m_mgr.attach(this);
    }
    
    @Override
    public void update_impl(float tpf)
    {
        switch(m_step)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                // force garbage collection
                System.gc();
                break;
            
            case 6:
                m_nifty.gotoScreen("highscore");
        }
        
        m_step++;
    }
    
    @Override
    public void onEnd_impl()
    {
        m_mgr.detach(this);
    }
}
