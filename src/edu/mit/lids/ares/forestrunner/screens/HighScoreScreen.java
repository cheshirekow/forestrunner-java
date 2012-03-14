package edu.mit.lids.ares.forestrunner.screens;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    Game    m_game;
    Nifty   m_nifty;
    Screen  m_screen;
    Boolean m_firstParamEncoded;
    String  m_userName;
    
    
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
        
        String getString = "";
        m_firstParamEncoded=false;
        getString =  urlAppend(getString,"user_hash",m_game.getUserHash());
        
        
        String urlString    = "http://ares.lids.mit.edu/~jbialk/forest_runner/src/get_nick.php?" + getString;
        String jsonResult   = "";
        try {  
            InputStream source = new URL(urlString).openStream();  
            
            jsonResult = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
        }  
        catch( Exception e ) {  
            e.printStackTrace();  
        }
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(NickChangeResult.class, new JsonNickChangeResultDeserializer());
        Gson gson = gsonBuilder.create();
        NickChangeResult nickResult = 
                gson.fromJson(jsonResult, NickChangeResult.class);
        m_userName = nickResult.nick;
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
    @Override
    public void onStartScreen()
    {
        System.out.println("Building highscore tables");
        
        String[] paramNames = {"velocity","density","radius"};
        
        String getString = "";
        m_firstParamEncoded=false;
        getString =  urlAppend(getString,"user_hash",m_game.getUserHash());
        
        for( String paramName : paramNames )
            getString = urlAppend(getString,paramName,
                                    m_game.getParam(paramName).toString());
                
        String urlString    = "http://ares.lids.mit.edu/~jbialk/forest_runner/src/get_scores.php?" + getString;
        String jsonResult   = "";
        try {  
            InputStream source = new URL(urlString).openStream();  
            
            jsonResult = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
        }  
        catch( Exception e ) {  
            e.printStackTrace();  
        }
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(HighScoreRow.class, new JsonHighScoreRowDeserializer());
        gsonBuilder.registerTypeAdapter(HighScoreResult.class, new JsonHighScoreDeserializer());
        Gson gson = gsonBuilder.create();
        
        HighScoreResult result= gson.fromJson(jsonResult, HighScoreResult.class);
        
        System.out.println("Score request status: " + result.status );
        
        ListBox<HighScoreRow> listBox =(ListBox<HighScoreRow>) 
                m_screen.findNiftyControl("lb.personalHigh", ListBox.class);
        listBox.clear();
        System.out.println("personal scores:");
        for( HighScoreRow row : result.user_scores )
        {
            System.out.println("   " + row.user_nick + ", " + row.date + ", " + row.score);
            row.date = row.date.substring(0,5);
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
            row.date = row.date.substring(0,5);
            listBox.addItem(row);        
        }
            
        listBox.refresh();        
        
        TextField textfield = 
                m_screen.findNiftyControl("txtfld.username", TextField.class);
        textfield.setText(m_userName);
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
            m_userName = textfield.getText();
            
            String getString = "";
            m_firstParamEncoded=false;
            getString =  urlAppend(getString,"user_hash",m_game.getUserHash());
            getString =  urlAppend(getString,"user_nick",m_userName);
            
            String urlString    = "http://ares.lids.mit.edu/~jbialk/forest_runner/src/set_nick.php?" + getString;
            String jsonResult   = "";
            try {  
                InputStream source = new URL(urlString).openStream();  
                
                jsonResult = 
                        new Scanner( source, "UTF-8" )
                                .useDelimiter("\\A").next();
            }  
            catch( Exception e ) {  
                e.printStackTrace();  
            }
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(NickChangeResult.class, new JsonNickChangeResultDeserializer());
            Gson gson = gsonBuilder.create();
            
            NickChangeResult nickResult = 
                    gson.fromJson(jsonResult, NickChangeResult.class);
            if( nickResult.status.compareTo("OK") != 0 )
                System.out.println("Failed to set nickname: " + nickResult.message );

            onStartScreen();
        }
    }
    
    @NiftyEventSubscriber(id="txtfld.username")
    public void onFieldChanged( String id, TextFieldChangedEvent event )
    {
        System.out.println("user name [" + id +"] changed ");
    }

}
