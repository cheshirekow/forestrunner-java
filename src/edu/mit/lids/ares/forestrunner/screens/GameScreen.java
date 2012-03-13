package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
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
        
        String[] params = {"velocity", "density", "radius"};
        
        for( String param : params )
        {
            String idName = "game.sldr." + param;
            Slider slider = m_screen.findNiftyControl(idName, Slider.class);
            slider.setValue( m_game.getParam(param) );
        }
    }
    
    @NiftyEventSubscriber(pattern="game.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("game button [" + id +"] pressed ");
        
        if( id.compareTo("game.btn.new")==0 )
        {
            m_game.initRun();
            m_nifty.gotoScreen("countdown3");
        }
        else
        {
            if(m_game.getState() == State.PAUSED)
            {
                m_resumeImmediately = true;
                m_nifty.gotoScreen("empty");
            }
            else
            {
                m_game.initRun();
                m_nifty.gotoScreen("countdown3");
            }
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
    
    @NiftyEventSubscriber(pattern="game.sldr.*")
    public void onSlider( String id, SliderChangedEvent event )
    {
        String[] params = {"velocity", "density", "radius"};
        
        for( String param : params )
        {
            String idName = "game.sldr." + param;
            if( id.compareTo(idName)== 0)
            {
                System.out.println("Setting value of " + param + " to " + (int)event.getValue() );
                m_game.setParam(param, (int)event.getValue());
            }
        }
    }

}
