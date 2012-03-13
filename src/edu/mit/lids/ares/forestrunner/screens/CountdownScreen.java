package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.Game.State;

public class CountdownScreen implements ScreenController
{
    Game        m_game;
    Nifty       m_nifty;
    Screen[]    m_screen;
    
    public CountdownScreen(Game game)
    {
        m_game      = game;
        m_nifty     = null;
        m_screen    = new Screen[3];
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        System.out.println("bind( " + screen.getScreenId() + ")");
        m_nifty     = nifty;

        if( screen.getScreenId().compareTo("countdown3") ==0 )
            m_screen[2] = screen;
        if( screen.getScreenId().compareTo("countdown2") ==0 )
            m_screen[1] = screen;
        if( screen.getScreenId().compareTo("countdown1") ==0 )
            m_screen[0] = screen;
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
    
    public void onStep(String num)
    {
        int screenNum = Integer.parseInt(num);
        
        System.out.println("Countdown effect end [" + screenNum + "]");
        
        switch(screenNum)
        {
            case 3:
                m_nifty.gotoScreen("countdown2");
                break;
            case 2:
                m_nifty.gotoScreen("countdown1");
                break;
            case 1:
                m_nifty.gotoScreen("empty");
                m_game.setState(State.RUNNING);
                break;
            default:
                assert(false);
        }
    }
    
}
