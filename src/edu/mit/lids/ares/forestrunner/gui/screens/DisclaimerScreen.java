package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class DisclaimerScreen 
    extends
        ScreenBase
    implements 
        ScreenController
{
    public DisclaimerScreen()
    {

    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        System.out.println("bind( " + screen.getScreenId() + ")");
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
        //m_nifty.gotoScreen("nick");
    }

}
