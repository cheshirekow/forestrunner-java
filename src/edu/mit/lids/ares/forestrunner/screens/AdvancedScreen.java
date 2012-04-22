package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.Game;

public class AdvancedScreen implements ScreenController
{
    Game    m_game;
    Nifty   m_nifty;
    Screen  m_screen;
    
    public AdvancedScreen(Game game)
    {
        m_game              = game;
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
        
    }

    @Override
    public void onStartScreen()
    {
        AdvancedSettings settings = m_game.getAdvancedSettings();
        
        for( String param : AdvancedSettings.parameters )
        {
            String idName = "advanced.chk." + param;
            CheckBox check = m_screen.findNiftyControl(idName, CheckBox.class);
            check.setChecked(settings.get(param));
        }
    }
    
    @NiftyEventSubscriber(pattern="advanced.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("advanced button [" + id +"] pressed ");
        
        if( id.compareTo("advanced.btn.ok")==0 )
        {
            AdvancedSettings settings = new AdvancedSettings();
            
            for( String param : AdvancedSettings.parameters )
            {
                String idName = "advanced.chk." + param;
                CheckBox check = m_screen.findNiftyControl(idName, CheckBox.class);
                settings.put(param, check.isChecked());
            }
            
            m_game.changeAdvancedSettings(settings);
        }
        else
        {
            m_nifty.gotoScreen("game");
        }
    }
    

}
