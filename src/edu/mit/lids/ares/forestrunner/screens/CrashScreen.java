package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.Game;

public class CrashScreen implements ScreenController
{
    Game        m_game;
    Nifty       m_nifty;
    Screen      m_screen;
    
    public CrashScreen(Game game)
    {
        m_game      = game;
        m_nifty     = null;
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        System.out.println("bind( " + screen.getScreenId() + ")");
        m_nifty     = nifty;
        m_screen    = screen;
    }

    @Override
    public void onEndScreen()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStartScreen()
    {
        // TODO Auto-generated method stub
        
    }
    
    public void onEndEffect()
    {
        System.out.println("Crash entrance effect end");
        m_nifty.gotoScreen("highscore");
    }
    
}
