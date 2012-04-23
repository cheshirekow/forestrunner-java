package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.gui.ScreenManager;

public class DisclaimerScreen 
    extends
        ScreenBase
    implements 
        ScreenController
{
    public DisclaimerScreen( ScreenManager mgr )
    {
        super(mgr);
    }
    
   
    @NiftyEventSubscriber(pattern="disclaimer.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("disclaimer button [" + id +"] pressed ");
        if( id.compareTo("disclaimer.btn.disagree")==0 )
            System.exit(0);
        m_nifty.gotoScreen("loading");
    }

}
