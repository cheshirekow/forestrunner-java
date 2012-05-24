package edu.mit.lids.ares.forestrunner.data.stores;

import java.applet.Applet;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import com.jme3.app.AppletHarness;

import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.data.Store;


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
}
