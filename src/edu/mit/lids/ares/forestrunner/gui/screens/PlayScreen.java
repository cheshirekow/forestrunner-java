package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.screen.ScreenController;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class PlayScreen
    extends
        ScreenBase
    implements 
        ScreenController
{
    public PlayScreen( Game game )
    {
        super(game,false,false);
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
