package edu.mit.lids.ares.forestrunner.gui.screens;

import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class CrashScreen
    extends ScreenBase
{
    public CrashScreen()
    {
        super();
        m_hasEntranceAnim   = true;
        m_hasExitAnim       = true;
    }
    
    @Override
    public void onStart_impl()
    {
        System.out.print("Changing to highscore");
        m_nifty.gotoScreen("highscore");
    }
}
