package edu.mit.lids.ares.forestrunner.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *  \brief common interface for all of the gui screens
 */
public class ScreenBase 
    implements 
        ScreenController,
        AppState
{
    public static final Logger s_logger = 
            Logger.getLogger(ScreenBase.class.getName());

    protected Boolean         m_hasEntranceAnim  = false;
    protected Boolean         m_hasExitAnim      = false;
    protected Boolean         m_active           = false;

    protected ScreenManager   m_mgr;
    protected Nifty           m_nifty;
    protected Screen          m_screen;
    
    // app state stuff
    protected boolean   m_initialized = false;
    
    /**
     *  \brief constructor
     *  @param mgr  screen manager for this process
     */
    public ScreenBase( ScreenManager mgr )
    {
        m_mgr           = mgr;
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
     *  \brief  default per-frame action (does nothing), implemented in
     *          screens which do something on a per-frame basis
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
     *  @brief  the nifty start/end screen callbacks are unfortunately
     *          placed before/after animations... which are a bad place
     *          to put lengthy code, so we do nothing here
     *          
     *  If the screen does not have an entrance animation, then this will 
     *  activate the screen and attach it to the app state manager. If it does
     *  have an entrance animation it will do nothing.
     */
    @Override
    public void onStartScreen()
    {
        if(!m_hasEntranceAnim)
            m_active = true;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onStartScreenEvent");
    }
    
    /**
     *  @brief  called after the entrance animation of the screen, is used to
     *          notify the manager that this screen has successfully exited
     *          
     *  Enables input and attaches this screen to the app state manager
     */
    public void onEntranceFinished()
    {
        m_active = true;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onEntranceFinishedEvent");
    }
    
    /**
     *  @brief  called after the exit animation of the screen, is used to
     *          notify the manager that this screen has successfully exited
     *          
     *  Disables input and detaches this screen from the app state manager
     */
    public void onExitStarted()
    {
        m_active = false;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onExitStartedEvent");
    }
    
    /**
     *  @brief  the nifty start/end screen callbacks are unfortunately
     *          placed before/after animations... which are a bad place
     *          to put lengthy code, so we do nothing here
     *          
     *  If the screen has an exit animation, this does nothing, otherwise it
     *  disables input and detaches this screen from the app state manager
     */
    @Override
    public void onEndScreen()
    {
        if(!m_hasExitAnim)
            m_active = false;
        s_logger.log(Level.INFO, m_screen.getScreenId() + ": onEndScreenEvent");
    }
    
    

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        m_initialized = true;
        
    }

    @Override
    public boolean isInitialized()
    {
        return m_initialized;
    }

    @Override
    public void setEnabled(boolean active)
    {
        m_active = active;
        
    }

    @Override
    public boolean isEnabled()
    {
        return m_active;
    }

    @Override
    public void stateAttached(AppStateManager stateManager)
    {
        
    }

    @Override
    public void stateDetached(AppStateManager stateManager)
    {
        
    }

    @Override
    public void render(RenderManager rm)
    {
        
    }

    @Override
    public void postRender()
    {
        
    }

    @Override
    public void cleanup()
    {
        m_initialized = false;
    }
}
