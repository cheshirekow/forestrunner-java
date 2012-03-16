package edu.mit.lids.ares.forestrunner.screens;

import java.applet.Applet;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jme3.app.AppletHarness;

import edu.mit.lids.ares.forestrunner.Game;

public class AppletCommProvider
    extends CommProvider
{
    public AppletCommProvider(Game game)
    {
        Applet applet = AppletHarness.getApplet(game);
        
        try
        {
            m_props.setProperty("user_hash", applet.getParameter("forestrunner_hash") );
            System.out.println("user hash: " + applet.getParameter("forestrunner_hash"));
            m_props.setProperty("user_nick", applet.getParameter("forestrunner_nick") );
            System.out.println("user nick: " + applet.getParameter("forestrunner_nick"));
        }
        catch(Exception ex)
        {
            m_dataOK = false;
            System.out.println("Failed to get hash from applet parameter");
            System.out.println("   " + ex.getMessage());
            ex.printStackTrace(System.out);
            return;
        }
    }
    
    
    @Override
    public NickChangeResult setUserNick(String nick)
    {
        m_props.setProperty("user_nick", nick);
        
        NickChangeResult result = new NickChangeResult();
        result.status = "ERROR";
        result.nick   = nick;
        
        // if we can't read/write from properties file then we need to 
        // just bail here
        if(!m_dataOK)
        {
            result.message= "AppletCommProvider is in a bad state";
            return result;
        }
        
        // now attempt to write the new value to the server as well
        
        String getString    = "";
        m_firstParamEncoded =false;
        getString =  urlAppend(getString,"user_hash",
                                    m_props.getProperty("user_hash"));
        getString =  urlAppend(getString,"user_nick",
                                    m_props.getProperty("user_nick"));
        
        String urlString    = "http://ares.lids.mit.edu/~jbialk/"
        		                    + "forest_runner/src/set_nick.php?" 
    		                        + getString;

        InputStream source;
        try
        {
            source = new URL(urlString).openStream();
        } 
        
        catch (MalformedURLException e)
        {
            System.out.println("Failed to write new nick to server:");
            e.printStackTrace(System.out);
            result.message= "Can't connect to ares server";
            return result;
        } 
        
        catch (IOException e)
        {
            System.out.println("Failed to write new nick to server:");
            e.printStackTrace(System.out);
            result.message= "Can't connect to ares server";
            return result;
        }  
        
        String jsonString = 
                new Scanner( source, "UTF-8" )
                        .useDelimiter("\\A").next();
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(NickChangeResult.class, 
                                    new JsonNickChangeResultDeserializer());
        Gson gson = gsonBuilder.create();
        
        NickChangeResult nickResult = 
                gson.fromJson(jsonString, NickChangeResult.class);
        
        if( nickResult.status.compareTo("OK") != 0 )
        {
            System.out.println("Failed to write new nick to server:");
            System.out.println(result.message);
            return result;
        }
        
        return nickResult;
    }

    @Override
    public HighScoreResult getHighScores(Properties props)
    {
        String[]    propNames       = {"user_hash","velocity","density","radius"};
        String      getString       = "";
        Boolean     serverSuccess   = true;
        m_firstParamEncoded         = false;
        
        for( String propName : propNames )
            getString = urlAppend(getString,propName,
                                    props.getProperty(propName));
                
        String urlString    = "http://ares.lids.mit.edu/~jbialk/" +
        		                "forest_runner/src/get_scores.php?" + 
    		                    getString;
        
        InputStream source  = null;
        try
        {
            source = new URL(urlString).openStream();
        } 
        catch (MalformedURLException e)
        {
            System.out.println("Failed to get high scores from server:");
            System.out.println("   " + e.getMessage() );
            e.printStackTrace(System.out);
            serverSuccess = false;
        } 
        
        catch (IOException e)
        {
            System.out.println("Failed to get high scores from server:");
            System.out.println("   " + e.getMessage() );
            e.printStackTrace(System.out);
            serverSuccess = false;
        }  
        
        if(serverSuccess)
        {
            String jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            
            gsonBuilder.registerTypeAdapter(HighScoreRow.class, 
                                        new JsonHighScoreRowDeserializer());
            gsonBuilder.registerTypeAdapter(HighScoreResult.class, 
                                        new JsonHighScoreDeserializer());
            Gson gson = gsonBuilder.create();
        
            HighScoreResult result = 
                    gson.fromJson(jsonString, HighScoreResult.class);
            
            return result;
        }
        
        // if we can't connect to the server we can at least get data from
        // the local database
        HighScoreResult result  = new HighScoreResult();
        HighScoreRow    row;
        result.status       = "OK";
        result.message      = "Locally Generated";
        result.user_scores  = new ArrayList<HighScoreRow>();
        result.global_scores= new ArrayList<HighScoreRow>();
        
        row             = new HighScoreRow();
        row.user_nick   = "Server Error";
        row.date        = "";
        row.score       = "0";
        result.global_scores.add(row);
        
        row.user_nick   = "Database Error";
        row.date        = "";
        row.score       = "0";
        result.user_scores.add(row);
        return result;
    }


    @Override
    public GenericResult publishScore(Properties props)
    {
        String[] propNames = {"user_hash","velocity","density","radius","score"};
        
        GenericResult result = new GenericResult();
        result.status = "FAILED";
        result.message= "";
        
        // first attempt to publish data to the server
        String getString    = "";
        m_firstParamEncoded =false;
        
        for( String propName : propNames )
        {
            getString =  urlAppend(getString,propName,
                                    props.getProperty(propName));
        }
        
        String urlString    = "http://ares.lids.mit.edu/~jbialk/"
                                    + "forest_runner/src/insert_score.php?" 
                                    + getString;

        InputStream source;
        try
        {
            source = new URL(urlString).openStream();
        } 
        
        catch (MalformedURLException e)
        {
            System.out.println("Failed to write new score to server:");
            e.printStackTrace(System.out);
            result.message= "Can't connect to ares server";
            return result;
        } 
        
        catch (IOException e)
        {
            System.out.println("Failed to write new score to server:");
            e.printStackTrace(System.out);
            result.message= "Can't connect to ares server";
            return result;
        }  
        
        String jsonString = 
                new Scanner( source, "UTF-8" )
                        .useDelimiter("\\A").next();
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(GenericResult.class, 
                                    new JsonGenericResultDeserializer());
        Gson gson = gsonBuilder.create();
        
        GenericResult insertResult = 
                gson.fromJson(jsonString, GenericResult.class);
        
        if( insertResult.status.compareTo("OK") != 0 )
        {
            System.out.println("Failed to write scores to server:");
            System.out.println(result.message);
        }
        
        return result;
    }

}
