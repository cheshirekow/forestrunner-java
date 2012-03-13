package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.Game.State;

public class GameScreen implements ScreenController
{
    Game    m_game;
    Nifty   m_nifty;
    Screen  m_screen;
    Boolean m_resumeImmediately;
    
    public GameScreen(Game game)
    {
        m_game              = game;
        m_resumeImmediately = false;
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
        m_resumeImmediately = false;
    }
    
    @NiftyEventSubscriber(pattern="game.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("game button [" + id +"] pressed ");
        
        if( id.compareTo("game.btn.new")==0 )
        {
            m_nifty.gotoScreen("countdown3");
            m_game.initRun();
        }
        else
        {
            if(m_game.getState() == State.PAUSED)
            {
                m_nifty.gotoScreen("empty");
                m_resumeImmediately = true;
            }
            else
                m_game.initRun();
        }
    }
    
    public void onEndScreenEffect()
    {
        if( m_resumeImmediately )
        {
            System.out.println("Game is just paused, resuming now");
            m_game.setState(State.RUNNING);
        }
    }

}
