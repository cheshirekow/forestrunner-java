package edu.mit.lids.ares.forestrunner.data.stores;



import java.applet.Applet;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import com.jme3.app.AppletHarness;

import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.data.GlobalHighScoreRow;
import edu.mit.lids.ares.forestrunner.data.Store;
import edu.mit.lids.ares.forestrunner.data.UserHighScoreRow;


/**
 *  @brief  data storage backend for the applet, uses cookies to store 
 *          preferences and php scripts to store scores
 *  @author josh
 *
 */
public class AppletStore 
    extends Store
{
    Applet              m_applet;
    Map<String,String>  m_cookieData;
    
    private void extractString(String key)
    {
        if(m_cookieData.containsKey(key))
            setString(key, m_cookieData.get(key));
    }
    
    private void extractInt(String key)
    {
        if(m_cookieData.containsKey(key))
            setInteger(key, Integer.parseInt(m_cookieData.get(key)));
    }
    
    private void extractBool(String key)
    {
        if(m_cookieData.containsKey(key))
            setBoolean(key, Integer.parseInt(m_cookieData.get(key)) != 0);
    }
    
    private void insertString(String key)
    {
        m_cookieData.put(key, getString(key));
    }
    
    private void insertInt(String key)
    {
        m_cookieData.put(key, String.format("%d",getInteger(key)));
    }
    
    private void insertBool(String key)
    {
        m_cookieData.put(key, getBoolean(key) ? "1" : "0" );
    }
    
    @Override
    public void init(Game game)
    {
        super.init(game);
        m_applet        = AppletHarness.getApplet(game);
        m_cookieData    = new HashMap<String,String>();
        
        try
        {
            setString("hash", m_applet.getParameter("forestrunner_hash"));
        }
        catch(Exception ex)
        {
            System.err.println("Failed to get hash from applet parameter");
            System.err.println("   " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
        
        // try to get data from the cookie
        String data         = "";
        String cookiename   = "forestrunner";
        
        try
        {
            JSObject myBrowser  = JSObject.getWindow(m_applet);
            JSObject myDocument = (JSObject) myBrowser.getMember("document");
    
            String myCookie = (String) myDocument.getMember("cookie");
            if (myCookie.length() > 0) 
            {
                String[] cookies = myCookie.split(";");
                for (String cookie : cookies) 
                {
                    int pos = cookie.indexOf("=");
                    if (cookie.substring(0, pos).trim().equals(cookiename)) 
                    {
                            data = cookie.substring(pos + 1);
                            break;
                    }
                }
            }
        }
        catch( JSException e )
        {
            e.printStackTrace(System.err);
        }
        
        // if data was found, then parse it
        if(data.length() > 0)
        {
            try
            {
                String decodedData = URLDecoder.decode(data,"UTF-8");
                String[] pairs = decodedData.split("&");
                for( String pair : pairs )
                {
                    if(pair.length() < 3)
                        continue;
                    int pos      = pair.indexOf("=");
                    String key   = pair.substring(0,pos).trim();
                    String value = pair.substring(pos+1).trim();
                    m_cookieData.put(
                            URLDecoder.decode(key,"UTF-8"),
                            URLDecoder.decode(value,"UTF-8"));
                }
            } 
            
            catch (UnsupportedEncodingException e)
            {
                System.err.println("Failed to retrieve cookie data");
                e.printStackTrace(System.err);
            }
        }
        
        System.out.println("Cookie Data:");
        for( String key : m_cookieData.keySet() )
        {
            System.out.println( key + " : " + m_cookieData.get(key) );
        }
        
        for( String key : m_stringMap.keySet() )
            extractString(key);
        
        for( String key : m_intMap.keySet() )
            extractInt(key);
        
        for( String key : AdvancedSettings.parameters )
            extractBool(key);
    }
    
    @Override
    public void sync()
    {
        for( String key : m_stringMap.keySet() )
            insertString(key);
        
        for( String key : m_intMap.keySet() )
            insertInt(key);
        
        for( String key : AdvancedSettings.parameters )
            insertBool(key);
        
        StringBuilder buf       = new StringBuilder();
        String        encData   = "";
        try
        {
           for(String key : m_cookieData.keySet())
           {
               if(m_cookieData.get(key) == null)
                   continue;
               
               buf .append(URLEncoder.encode(key,"UTF-8"))
                   .append('=')
                   .append(URLEncoder.encode(m_cookieData.get(key),"UTF-8"))
                   .append('&');
           }
           
           encData = URLEncoder.encode(buf.toString(),"UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
            System.err.println("Failed to encode cookie data");
            e.printStackTrace(System.err);
            return;
        }
        
        String cookiename   = "forestrunner";
        try
        {
            JSObject win    = JSObject.getWindow(m_applet);
            JSObject doc    = (JSObject) win.getMember("document");
            String data     = cookiename + "=" + encData + 
                                "; path=/; expires=Thu, 31-Dec-2020 12:00:00 GMT";
            doc.setMember("cookie", data);
        }
        catch( JSException e )
        {
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public void sendNick()
    {
        try
        {
            Map<String,String> paramMap = new HashMap<String,String>();
            paramMap.put("hash", getString("hash"));
            paramMap.put("nick", getString("nick"));
            
            String urlString = 
                    "http://ares.lids.mit.edu/forestrunner/comm/" 
                    + Store.encode("set_nick.php", paramMap);
    
            // try to open a stream to the create user page
            InputStream source = new URL(urlString).openStream();
        
            // read the entire stream into a single string
            String jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
    
            JSONObject obj = (JSONObject) JSONValue.parse(jsonString);
            
            // check the result message
            if( !obj.containsKey("status") )
            {
                System.err.println("Returned JSON message is malformed, " +
                                    "no status: " + jsonString);
                return;
            }
            
            String status = (String) obj.get("status");
            if( status.compareTo("OK") != 0 )
            {
                System.err.println("Failed to update nickname with server ");
                if(obj.containsKey("message"))
                    System.err.println("Message: " + obj.get("message"));
                return;
            }
            
            System.out.println("Updated nickname at server: " + jsonString);
        }
        
        catch (MalformedURLException e)
        {
            e.printStackTrace(System.err);
        } 
        
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public void recordScore(float score)
    {
        try
        {
            Map<String,String> paramMap = new HashMap<String,String>();
            paramMap.put("date",    String.format("%d",System.currentTimeMillis()/1000));
            paramMap.put("velocity",String.format("%d",getInteger("velocity")));
            paramMap.put("density", String.format("%d",getInteger("density")));
            paramMap.put("radius",  String.format("%d",getInteger("radius")));
            paramMap.put("score",   String.format("%.30f",score));
            paramMap.put("hash",    getString("hash"));
            paramMap.put("version", String.format("%d",getInteger("version")));
                
            String urlString = 
                    "http://ares.lids.mit.edu/forestrunner/comm/" 
                    + Store.encode("insert_score_applet.php", paramMap);

            // try to open a stream to the create user page
            InputStream source = new URL(urlString).openStream();
        
            // read the entire stream into a single string
            String jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();

            JSONObject obj = (JSONObject) JSONValue.parse(jsonString);
            
            // check the result message
            if( !obj.containsKey("status") )
            {
                System.err.println("Returned JSON message is malformed, " +
                                    "no status: " + jsonString);
                return;
            }
            
            String status = (String) obj.get("status");
            if( status.compareTo("OK") != 0 )
            {
                System.err.println("Failed to send scores to server ");
                if(obj.containsKey("message"))
                    System.err.println("Message: " + obj.get("message"));
                return;
            }
            
            setInteger("lastRowId", ( (Long)obj.get("row_id") ).intValue());
            
            System.out.println("Sent score to server: " + jsonString);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace(System.err);
        } 
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public List<UserHighScoreRow>   getUserScores()
    {
        List<UserHighScoreRow> scores = new ArrayList<UserHighScoreRow>();
        
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("version", String.format("%d",getInteger("version")));
        paramMap.put("velocity", String.format("%d",getInteger("velocity")));
        paramMap.put("density", String.format("%d",getInteger("density")));
        paramMap.put("radius", String.format("%d",getInteger("radius")));
        paramMap.put("hash",   getString("hash"));
        
        try
        {
            String urlString = 
                    "http://ares.lids.mit.edu/forestrunner/comm/" 
                    + Store.encode("get_user_scores_applet.php", paramMap);
    
            // try to open a stream to the create user page
            InputStream source = new URL(urlString).openStream();
    
            // read the entire stream into a single string
            String jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
    
            JSONObject obj = (JSONObject) JSONValue.parse(jsonString);
            
            // check the result message
            if( !obj.containsKey("status") )
            {
                System.err.println("Returned JSON message is malformed, " +
                                    "no status: " + jsonString);
                throw new RuntimeException("");
            }
            
            String status = (String) obj.get("status");
            if( status.compareTo("OK") != 0 )
            {
                System.err.println("Failed to get global scores from server ");
                if(obj.containsKey("message"))
                    System.err.println("Message: " + obj.get("message"));
                throw new RuntimeException("");
            }
            
            // otherwise, things look good
            JSONArray scoreArray = (JSONArray)obj.get("scores");
            
            System.out.println(
                    "Received user scores: (" + scoreArray.size() + ")");
            
            for( Object scoreObj : scoreArray )
            {
                JSONObject scoreMap = (JSONObject)scoreObj;
                
                System.out.println(scoreMap.toJSONString());
                UserHighScoreRow row = new UserHighScoreRow();
                row.id      = Integer.parseInt(     (String) scoreMap.get("row_id") );
                row.date    = Integer.parseInt(     (String) scoreMap.get("date") );
                row.score   = Double.parseDouble(   (String)  scoreMap.get("score") );
                
                if( row.id == getInteger("lastRowId"))
                    row.isCurrent = true;
                
                scores.add(row);
            }
        
        }
        catch (MalformedURLException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.out);
        } 
        catch (IOException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.out);
        }  
        catch( RuntimeException e)
        {
            System.err.println("Failed to process row");
            e.printStackTrace(System.err);
        }

        
        return scores;
    }
    
    
    @Override
    public List<GlobalHighScoreRow>   getGlobalScores()
    {
        List<GlobalHighScoreRow> scores = new ArrayList<GlobalHighScoreRow>();
        
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("version", String.format("%d",getInteger("version")));
        paramMap.put("velocity", String.format("%d",getInteger("velocity")));
        paramMap.put("density", String.format("%d",getInteger("density")));
        paramMap.put("radius", String.format("%d",getInteger("radius")));
        
        try
        {
            String urlString = 
                    "http://ares.lids.mit.edu/forestrunner/comm/" 
                    + Store.encode("get_global_scores_applet.php", paramMap);
    
            // try to open a stream to the create user page
            InputStream source = new URL(urlString).openStream();
    
            // read the entire stream into a single string
            String jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
    
            JSONObject obj = (JSONObject) JSONValue.parse(jsonString);
            
            // check the result message
            if( !obj.containsKey("status") )
            {
                System.err.println("Returned JSON message is malformed, " +
                                    "no status: " + jsonString);
                throw new RuntimeException("");
            }
            
            String status = (String) obj.get("status");
            if( status.compareTo("OK") != 0 )
            {
                System.err.println("Failed to get global scores from server ");
                if(obj.containsKey("message"))
                    System.err.println("Message: " + obj.get("message"));
                throw new RuntimeException("");
            }
            
            // otherwise, things look good
            JSONArray scoreArray = (JSONArray)obj.get("scores");
            for( Object scoreObj : scoreArray )
            {
                JSONObject scoreMap = (JSONObject)scoreObj;
                
                GlobalHighScoreRow row = new GlobalHighScoreRow();
                row.id      = Integer.parseInt(     (String) scoreMap.get("row_id") );
                row.nick    =                       (String) scoreMap.get("nick");
                row.date    = Integer.parseInt(     (String) scoreMap.get("date")   );
                row.score   = Double.parseDouble(   (String) scoreMap.get("score")  );
                if( row.id == getInteger("lastRowId"))
                    row.isCurrent = true;
                scores.add(row);
            }
        
        }
        catch (MalformedURLException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.out);
        } 
        catch (IOException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.out);
        }  
        catch( RuntimeException e)
        {
            
        }

        
        return scores;
    }
}
