package edu.mit.lids.ares.forestrunner.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.SystemContext;

/**
 *  @brief  Abstract data storage interface which provides a consistant API
 *          for storing data across all three version of the game
 *          (Android, Applet, Desktop)
 *  @author josh
 */
public abstract class Store
{
    protected Map<String,Integer>   m_intMap;
    protected Map<String,Boolean>   m_boolMap;
    protected Map<String,String>    m_stringMap;
    
    // advanced settings (i.e. prefs)
    protected AdvancedSettings  m_advancedSettings;
    
    public static final int s_numScoresToShow=20;
    
    /**
     *  @brief  just sets some defaults
     */
    public Store()
    {
        
    }
    
    /**
     *  @brief  make necessary database/network connections, build database
     *          files if they do not exist, fetch mem-stored values
     */
    public void init(Game game)
    {
        m_intMap    = new HashMap<String,Integer>();
        m_boolMap   = new HashMap<String,Boolean>();
        m_stringMap = new HashMap<String,String>();
        
        m_intMap.put("density",     (int)Math.random()*10);
        m_intMap.put("radius",      (int)Math.random()*10);
        m_intMap.put("velocity",    2);//(int)Math.random()*10);
        m_intMap.put("version",         Game.s_version);
        m_intMap.put("lastUserRowId",   0  );
        m_intMap.put("lastGlobalRowId", 0  );
        
        for( String key : AdvancedSettings.parameters)
            m_boolMap.put(key, AdvancedSettings.s_default.get(key));
        
        m_stringMap.put("nick", "Anon");
        m_stringMap.put("hash", "");
    }
    
    /// initialize a new log for a new run
    public void initRunLog( Long time )
    {
        
    }
    
    /// record the state in the current log if there is one
    public void logState( double time, double x, double y, double xspeed )
    {
        
    }
    
    public void sync()
    {
        
    }
    
    public void syncGlobalHigh()
    {
        
    }
    
    public Integer getInteger(String key)
    {
        return m_intMap.get(key);
    }
    
    public Boolean getBoolean(String key)
    {
        return m_boolMap.get(key);
    }
    
    public String getString(String key)
    {
        return m_stringMap.get(key);
    }
    
    public void setInteger(String key, Integer value)
    {
        m_intMap.put(key,value);
    }
    
    public void setBoolean(String key, Boolean value)
    {
        m_boolMap.put(key,value);
    }
    
    public void setString(String key, String value)
    {
        m_stringMap.put(key,value);
    }
    
    public void recordScore(float score)
    {
        
    }
    
    public void sendOneScore()
    {
        
    }
    
    public void sendNick()
    {
        
    }
    
    public List<UserHighScoreRow>   getUserScores()
    {
        return new ArrayList<UserHighScoreRow>();
    }
    
    public List<GlobalHighScoreRow>   getGlobalScores()
    {
        return new ArrayList<GlobalHighScoreRow>();
    }
    
    /**
     *  @brief  static method to create an appropriate data store for the 
     *          current system
     * @param   ctx system context (applet, desktop, android)
     * @return  an object implementing data.Store
     */
    public static Store createStore(SystemContext ctx)
    {
        String pkg          = "edu.mit.lids.ares.forestrunner.data.stores.";
        String className    ;
        
        switch(ctx)
        {
            case ANDROID:
            {
                className = pkg + "AndroidStore";
                break;
            }
                
            case APPLET:
            {
                className = pkg + "AppletStore";
                break;
            }
            
            case DESKTOP:
            default:
            {
                className = pkg + "DesktopStore";
                break;
            } 
        }
        
        System.err.println("Attempting to create data store: " + className );
        
        try
        {
            Class<?> providerClass = Class.forName(className);
            return (Store) providerClass.newInstance();
        } 
        
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } 
        
        catch (InstantiationException e)
        {
            e.printStackTrace();
        } 
        
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static String encode(String script, Map<String,String> params)
    {
        StringBuilder buf = new StringBuilder();
        buf.append( script ).append( '?' );
        
        try
        {
            for( String key : params.keySet() )
            {
                buf.append( URLEncoder.encode(key,"UTF-8") )
                    .append('=')
                    .append( URLEncoder.encode(params.get(key),"UTF-8"))
                    .append('&');
            } 
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace(System.err);
        }
        
        
        
        return buf.toString();
    }
}
