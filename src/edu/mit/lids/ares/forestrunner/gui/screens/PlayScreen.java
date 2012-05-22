package edu.mit.lids.ares.forestrunner.gui.screens;

import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.screen.ScreenController;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class PlayScreen
    extends
        ScreenBase
    implements 
        ScreenController
{
    private AppStateManager m_mgr;
    private Game            m_game;
    
    public PlayScreen( Game game, AppStateManager mgr )
    {
        super();
        m_mgr   = mgr;
        m_game  = game;
        m_hasEntranceAnim   = false;
        m_hasExitAnim       = false;
    }
    
    @Override
    public void onStart_impl()
    {
        m_mgr.attach(this);
    }
    
    @Override
    public void onEnd_impl()
    {
        m_mgr.detach(this);
    }
    
    @Override
    public void update_impl( float tpf )
    {
        m_game.simpleUpdate(tpf);
    }
}
