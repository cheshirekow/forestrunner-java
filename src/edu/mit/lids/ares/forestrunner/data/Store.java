package edu.mit.lids.ares.forestrunner.data;

import java.util.HashMap;

import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.SystemContext;

/**
 *  @brief  Abstract data storage interface which provides a consistant API
 *          for storing data across all three version of the game
 *          (Android, Applet, Desktop)
 *  @author josh
 */
public abstract class Store
{
    // advanced settings (i.e. prefs)
    protected AdvancedSettings  m_advancedSettings;
    
    // config stuff
    protected String            m_nick;
    protected String            m_userHash;
    
    // game paramters
    protected int               m_radius;
    protected int               m_density;
    protected int               m_speed;
    
    /**
     *  @brief  just sets some defaults
     */
    public Store()
    {
        m_advancedSettings  = new AdvancedSettings();
        m_nick      = "Anon";
        m_userHash  = "";
    }
    
    /**
     *  @brief  make necessary database/network connections, build database
     *          files if they do not exist, fetch mem-stored values
     */
    public void init(){}
    
    /**
     *  @return user's saved nickname, or the default if there is not one
     *          saved
     */
    public String getNick()
    {
        //TODO: override in derived classes to retrieve nickname from
        // backend
        return m_nick;
    }

    /**
     *  @brief  stores a new nickname for the user
     *  @param  nick the new nickname
     */
    public void setNick(String nick)
    {
        m_nick = nick;
        //TODO: override in base classes to store new nickname in backend
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
        
        System.out.println("Attempting to create data store: " + className );
        
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
}
