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

public class HighScoreScreen implements ScreenController
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
    
    public HighScoreScreen(Game game) 
    {
        m_game = game;
        
        String pkg          = "edu.mit.lids.ares.forestrunner.screens.";
        String className    ;
        
        switch(game.getSystem())
        {
            case ANDROID:
            {
                className = pkg + "AndroidCommProvider";
                break;
            }
                
            case DESKTOP:
            {
                className = pkg + "DesktopCommProvider";
                break;
            }
                
            case APPLET:
            {
                className = pkg + "AppletCommProvider";
                break;
            }
            
            default:
            {
                className = pkg + "DesktopCommProvider";
                break;
            } 
        }
        
        try
        {
            Class<?> providerClass = Class.forName(className);
            m_comm = (CommProvider) providerClass.newInstance();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
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
    
    @SuppressWarnings("unchecked")
    public void onStartScreenImpl(Boolean fake)
    {
        System.out.println("Building highscore tables");
        
        Properties props = new Properties();
        props.setProperty("user_hash", m_comm.getHash() );
        props.setProperty("velocity", m_game.getParam("velocity").toString() );
        props.setProperty("density", m_game.getParam("density").toString() );
        props.setProperty("radius", m_game.getParam("radius").toString() );
        props.setProperty("score", new Float(m_game.getScore()).toString() );
        
        if(!fake)
            m_comm.publishScore(props);
        
        HighScoreResult result = m_comm.getHighScores(props);
        System.out.println("Score request status: " + result.status );
        
        ListBox<HighScoreRow> listBox =(ListBox<HighScoreRow>) 
                m_screen.findNiftyControl("lb.personalHigh", ListBox.class);
        listBox.clear();
        System.out.println("personal scores:");
        for( HighScoreRow row : result.user_scores )
        {
            System.out.println("   " + row.user_nick + ", " + row.date + ", " + row.score);
            listBox.addItem(row);
        }
        listBox.refresh();
                
        listBox = (ListBox<HighScoreRow>) 
                m_screen.findNiftyControl("lb.globalHigh", ListBox.class);
        listBox.clear();
        System.out.println("global scores:");
        for( HighScoreRow row : result.global_scores)
        {
            System.out.println("   " + row.user_nick + ", " + row.date + ", " + row.score);
            listBox.addItem(row);        
        }
            
        listBox.refresh();        
        
        TextField textfield = 
                m_screen.findNiftyControl("txtfld.username", TextField.class);
        textfield.setText(m_comm.getNick());
        
    }

    @Override
    public void onStartScreen()
    {
        onStartScreenImpl(false);
    }
    
    @NiftyEventSubscriber(pattern="highscore.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("highscore button [" + id +"] pressed ");
        if( id.compareTo("highscore.btn.again")==0 )
        {
            m_game.initRun();
            m_nifty.gotoScreen("countdown3");
        }
        else if( id.compareTo("highscore.btn.settings")==0 )
        {
            m_nifty.gotoScreen("game");
        }
        else if( id.compareTo("highscore.btn.savename")==0 )
        {
            TextField textfield = 
                    m_screen.findNiftyControl("txtfld.username", TextField.class);
            NickChangeResult nickResult = 
                    m_comm.setUserNick(textfield.getText());
            
            if( nickResult.status.compareTo("OK") != 0 )
                System.out.println("Failed to set nickname: " + nickResult.message );

            onStartScreenImpl(true);
        }
    }
    
    @NiftyEventSubscriber(id="txtfld.username")
    public void onFieldChanged( String id, TextFieldChangedEvent event )
    {
        System.out.println("user name [" + id +"] changed ");
    }

}
