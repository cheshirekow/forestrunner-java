package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.Game;

public class CountdownScreen implements ScreenController
{
    Game    m_game;
    Nifty   m_nifty;
    Screen  m_screen;
    
    public CountdownScreen(Game game)
    {
        m_game      = game;
        m_nifty     = null;
        m_screen    = null;
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
    
    public void onStep()
    {
        System.out.println("Countdown effect end [" + m_screen.getScreenId() + "]");
        
        if(m_screen == null)
        {
            System.out.println("Countdown screen was never bound!");
        }
        else
        {
            if( m_screen.getScreenId().compareTo("countdown3") ==0 )
                m_nifty.gotoScreen("countdown2");
            if( m_screen.getScreenId().compareTo("countdown2") ==0 )
                m_nifty.gotoScreen("countdown1");
            if( m_screen.getScreenId().compareTo("countdown1") ==0 )
                m_nifty.gotoScreen("highscore");
        }
    }
    
}
