package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.CheckBox;

import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class AdvancedScreen
    extends ScreenBase
{
    boolean             m_shouldSave;
    boolean             m_shouldExit;
    
    public AdvancedScreen( Game game )
    {
        super(game,true,true);
        m_shouldSave   = false;
        m_shouldExit     = false;
    }
    
    @Override
    public void onStart_impl()
    {   
        AdvancedSettings settings = m_game.getAdvancedSettings();
        
        for( String param : AdvancedSettings.parameters )
        {
            String idName = "advanced.chk." + param;
            CheckBox check = m_screen.findNiftyControl(idName, CheckBox.class);
            check.setChecked(settings.get(param));
        }
        
        m_mgr.attach(this);
        m_shouldSave   = false;
        m_shouldExit   = false;
     
        for( String param : AdvancedSettings.parameters )
        {
            String idName = "advanced.chk." + param;
            CheckBox check = m_screen.findNiftyControl(idName, CheckBox.class);
            check.setChecked( m_dataStore.getBoolean(param) );
        }
    }
    
    public void onEnd_impl()
    {
        m_mgr.detach(this);
    }
    
    @Override 
    public void update(float tpf)
    {
        if(m_shouldExit)
        {
            if(m_shouldSave)
            {
                AdvancedSettings settings = new AdvancedSettings();
                
                for( String param : AdvancedSettings.parameters )
                {
                    String idName = "advanced.chk." + param;
                    CheckBox check = m_screen.findNiftyControl(idName, CheckBox.class);
                    settings.put(param, check.isChecked());
                    m_dataStore.setBoolean(param, check.isChecked());
                }
                
                m_game.changeAdvancedSettings(settings);
                m_shouldSave = false;
                
                // do nothing else this frame, let the timer advance 
                // one more frame before we start the transition animation
                return;
            }
            else
                m_nifty.gotoScreen("game");
        }
    }
    
    @NiftyEventSubscriber(pattern="advanced.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("advanced button [" + id +"] pressed ");
        
        if( id.compareTo("advanced.btn.ok")==0 )
            m_shouldSave = true;
        else
            m_shouldExit = true;
    }
}
