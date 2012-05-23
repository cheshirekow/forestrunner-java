package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.Game.State;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class CountdownScreen
    extends
        ScreenBase
{
    Screen[]    m_screens;
    
    public CountdownScreen(Game game)
    {
        super(game,true,true);
        m_screens           = new Screen[3];
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        System.out.println("bind( " + screen.getScreenId() + ")");
        m_nifty     = nifty;
        m_screen    = screen;

        if( screen.getScreenId().compareTo("countdown3") ==0 )
            m_screens[2] = screen;
        if( screen.getScreenId().compareTo("countdown2") ==0 )
            m_screens[1] = screen;
        if( screen.getScreenId().compareTo("countdown1") ==0 )
            m_screens[0] = screen;
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
                m_nifty.gotoScreen("play");
                m_game.setState(State.RUNNING);
                break;
            default:
                assert(false);
        }
    }
}
