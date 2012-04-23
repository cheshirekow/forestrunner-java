package edu.mit.lids.ares.forestrunner.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *  \brief common interface for all of the gui screens
 */
public class ScreenBase implements ScreenController
{
    public static final Logger s_logger = 
            Logger.getLogger(ScreenBase.class.getName());
    
    protected Boolean         m_active;
    protected ScreenManager   m_mgr;
    protected Nifty           m_nifty;
    protected Screen          m_screen;
    
    /**
     *  \brief constructor
     *  @param mgr  screen manager for this process
     */
    public ScreenBase( ScreenManager mgr )
    {
        m_mgr       = mgr;
        m_active    = false;
    }
    
    /**
     *  \brief  sets the active flag for this screen, if the screen is
     *          inactive user input will be ignored (though animations and
     *          other effects will still be processed by nifty)
     *  @param active
     */
    public void setActive( Boolean active )
    {
        m_active = active;
    }
    
    /**
     *  \brief  if the screen is active, perform one frame-worth of actions for 
     *          the current screen (default does nothing)
     */
    public void update( float tpf )
    {
        if(m_active)
            update_impl(tpf);
    }
    
    /**
     *  \brief  default per-frame action (does nothing)
     */
    public void update_impl( float tpf )
    {
        
    }
    
    /**
     *  \brief  called when nifty creates the screen object associated with
     *          this controller, simply stores pointers to nifty and the
     *          screen
     */
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        s_logger.log(Level.INFO, "Nifty is binding " + screen.getScreenId() );
        m_nifty     = nifty;
        m_screen    = screen;
    }

    /**
     *  \brief  the nifty start/end screen callbacks are unfortunately
     *          placed before/after animations... which are a bad place
     *          to put lengthy code, so we do nothing here
     */
    @Override
    public void onEndScreen()
    {
        m_active = false;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onEndScreenEvent");
    }

    /**
     *  \brief  the nifty start/end screen callbacks are unfortunately
     *          placed before/after animations... which are a bad place
     *          to put lengthy code, so we do nothing here
     */
    @Override
    public void onStartScreen()
    {
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onStartScreenEvent");
    }
    
    /**
     *  \brief  called after the exit animation of the screen, is used to
     *          notify the manager that this screen has successfully exited
     */
    public void onExitStarted()
    {
        m_active = false;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onExitStartedEvent");
    }
    
    /**
     *  \brief  called after the entrance animation of the screen, is used to
     *          notify the manager that this screen has successfully exited
     */
    public void onEntranceFinished()
    {
        m_active = true;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onEntranceFinishedEvent");
    }
}
