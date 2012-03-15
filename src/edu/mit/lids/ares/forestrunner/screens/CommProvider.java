package edu.mit.lids.ares.forestrunner.screens;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;


public abstract class CommProvider
{
    protected Boolean     m_firstParamEncoded;
    protected Boolean     m_dataOK; 
    protected Properties  m_props;
    
    public CommProvider()
    {
        m_dataOK            = true;
        m_firstParamEncoded = false;
        
        // set default properties
        m_props.setProperty("user_hash", "0");
        m_props.setProperty("user_nick", "Anonymous");
    }
    
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
    
    public String getUserNick()
    {
        return m_props.getProperty("user_nick");
    }
    
    public abstract NickChangeResult    setUserNick(String nick);
    public abstract HighScoreResult     getHighScores(Properties props);
    public abstract GenericResult       publishScore(Properties props);
}
