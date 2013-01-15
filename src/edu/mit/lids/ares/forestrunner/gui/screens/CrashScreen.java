package edu.mit.lids.ares.forestrunner.gui.screens;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class CrashScreen
    extends ScreenBase
{
    public CrashScreen(Game game)
    {
        super(game,true,true);
    }
    
    @Override
    public void onStart_impl()
    {
//        System.out.print("Changing to highscore");
        m_nifty.gotoScreen("highscore");
    }
}
