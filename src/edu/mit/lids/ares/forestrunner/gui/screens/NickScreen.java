package edu.mit.lids.ares.forestrunner.gui.screens;

import java.util.logging.Level;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.screen.Screen;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.SystemContext;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class NickScreen
    extends
        ScreenBase
{
    private Boolean     m_nickChanged;
    private Boolean     m_buttonPressed;
    private Button      m_btn;
    private TextField   m_textField;
    
    public NickScreen(Game game) 
    {
        super(game,true,true);
        m_nickChanged       = false;
        m_buttonPressed     = false;
    }
    
    public void fetchNick()
    {
        m_textField.setText(m_dataStore.getString("nick"));
    }
    
    /**
     *  \brief  when nifty binds this screen, we grab a pointer to the button
     *          so we can disable it after it's pressed
     */
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        m_btn       = screen.findNiftyControl("nick.btn.finished", Button.class);
        m_textField = screen.findNiftyControl("txtfld.username", TextField.class);
    }
       
    public void onStart_impl()
    {
        m_mgr.attach(this);
        m_textField.setText(m_dataStore.getString("nick"));
        m_btn.setFocus();
    }
    
    public void onEnd_impl()
    {
        m_mgr.detach(this);
        m_btn.setFocus();
    }
    
    /**
     *  \brief  if the button has been pressed then it writes any necessary
     *          updates to the data store, and if updates have been written 
     *          then it advances to the next screen
     */
    @Override
    public void update_impl(float tpf)
    {
        if(m_buttonPressed)
        {
            // this may actually give it focus
            // m_textField.disable();
            
            // this will allow us to do all the fun work in one frame, and then
            // transition screens in the next frame
            if(m_nickChanged)
            {
                s_logger.log(Level.INFO, "writing updated nick to data store");
                
                m_dataStore.setString("nick", m_textField.getText());
                m_dataStore.sync();
                m_nickChanged = false;
                m_btn.setFocus();
                
                // dont do anything more until we render again
                return;
            }

            else
                m_nifty.gotoScreen("game");
        }
            
    }
    
    /**
     *  \brief  when the user clicks the button to continue, then we
     *          can move on to the next screen
     *  @param id
     *  @param event
     */
    @NiftyEventSubscriber(pattern="nick.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        m_buttonPressed = true;
    }
    
    /**
     *  \brief  when the user changes the username field, we need to flag
     *          the field as changed
     *  @param id
     *  @param event
     */
    @NiftyEventSubscriber(id="txtfld.username")
    public void onFieldChanged( String id, TextFieldChangedEvent event )
    {
        if(m_game.getSystem() == SystemContext.ANDROID )
            m_btn.setFocus();
        s_logger.log(Level.INFO, "user name [" + id +"] changed ");
        m_nickChanged = true;
    }

}
