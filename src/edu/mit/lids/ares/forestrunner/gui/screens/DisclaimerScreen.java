package edu.mit.lids.ares.forestrunner.gui.screens;

import java.util.logging.Level;

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
    
   
    /**
     *  \brief  event handler for two button presses, either moves to the
     *          next screen or exits the game 
     *  @param id
     *  @param event
     */
    @NiftyEventSubscriber(pattern="disclaimer.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        // ignore input if the screen is animating in or out
        if(!m_active)
            return;
        
        // log a message
        s_logger.log(Level.INFO, "disclaimer button [" + id +"] pressed ");
        
        // if the user does not agree, we simply exit the game
        if( id.compareTo("disclaimer.btn.disagree")==0 )
            System.exit(0);
        
        // if the user does agree, we advance to the loading screen
        m_mgr.advance("nick");
    }

    /**
     *  \brief  the DisclaimerScreen does not have an entrance effect so 
     *          we need to activate it on the start screen
     */
    @Override
    public void onStartScreen()
    {
        super.onStartScreen();
        m_active = true;
    }
}
