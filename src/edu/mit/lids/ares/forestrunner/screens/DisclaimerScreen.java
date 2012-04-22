package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.Game;

public class DisclaimerScreen implements ScreenController
{
    Game    m_game;
    Nifty   m_nifty;
    Screen  m_screen;
    
    public DisclaimerScreen(Game game)
    {
        m_game = game;
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
    
    @NiftyEventSubscriber(pattern="disclaimer.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("disclaimer button [" + id +"] pressed ");
        if( id.compareTo("disclaimer.btn.disagree")==0 )
            System.exit(0);
        m_nifty.gotoScreen("nick");
    }

}
