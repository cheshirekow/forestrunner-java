package edu.mit.lids.ares.forestrunner.screens;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import edu.mit.lids.ares.forestrunner.Game;

public class NickScreen implements ScreenController
{
    Game            m_game;
    Nifty           m_nifty;
    Screen          m_screen;
    Boolean         m_firstParamEncoded;
    CommProvider    m_comm;
    
    public String urlAppend( String string, String key, String value )
    {
        if(m_firstParamEncoded)
            string += "&";
        else
            m_firstParamEncoded = true;
        
        try
        {
            string += URLEncoder.encode( key, "UTF-8") 
                    + "=" 
                    + URLEncoder.encode( value, "UTF-8") ;
        }
        catch( UnsupportedEncodingException e ){}
        
        return string;
    }
    
    public NickScreen(Game game) 
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
        
    }
    
    @NiftyEventSubscriber(pattern="nick.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        m_nifty.gotoScreen("game");
    }
    
    @NiftyEventSubscriber(id="txtfld.username")
    public void onFieldChanged( String id, TextFieldChangedEvent event )
    {
        System.out.println("user name [" + id +"] changed ");
    }

}
