package edu.mit.lids.ares.forestrunner.data.stores;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteConstants;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.data.Store;

/**
 *  @brief  data storage backend for the desktop, uses sqlite database stored
 *          somewhere in the user's home directory
 *  @author josh
 *
 */
public class DesktopStore 
    extends Store
{
    protected boolean           m_dataOK;
    protected SQLiteConnection  m_sqlite;
    
    public DesktopStore()
    {
        super();
    }
    
    @Override
    public void init()
    {
        super.init();
        m_dataOK = true;

        // get the path to the user's home directory
        String userHome     = System.getProperty("user.home");
        String dataDir      = userHome + File.separator + ".forestrunner";
        File dataDirFile    = new File(dataDir);
        
        // if the forestrunner directory doesn't exist yet, then create it
        if(!dataDirFile.exists())
        {
            try
            {
                Boolean result = dataDirFile.mkdirs();
                if(result)
                    throw new RuntimeException("Failed to create data dir");
            }
            catch(Exception e)
            {
                System.out.println("Failed to create data dir: " 
                                    + dataDir + ", will continue without data");
                e.printStackTrace(System.out);
                m_dataOK    = false;
                return;
            }
        }
        

        String  dbFileName = dataDir + File.separator + "data.sqlite";
        File    dbFile     = new File(dbFileName);

        try
        {
            m_sqlite = new SQLiteConnection(dbFile);
            m_sqlite.open(true);
            
            readConfig();
            
            // check that the version of the database is the same as that of
            // the program
            if( m_intMap.containsKey("version") )
            {
                if( m_intMap.get("version") < Game.s_version )
                    initDatabase();
            }
            else
                initDatabase();
            
        }
        catch (SQLiteException e)
        {
            switch( e.getErrorCode() )
            {
                case SQLiteConstants.SQLITE_CANTOPEN:
                {
                    System.out.println("Can't open database:");
                    e.printStackTrace(System.out);
                    m_dataOK = false;
                    break;
                }
            
                // by default we just assume corrupt, out of date database
                // so we run the init script, it will catch further exceptions
                // if there is a real problem
                default:
                {
                    initDatabase();
                    break;
                }
            }
        }
    }
    
    @Override
    public void sync()
    {
        if(!m_dataOK)
        {
            System.out.println("Cant sync Desktop store, data is not OK");
            return;
        }
        
        try
        {
            for (String key : m_stringMap.keySet())
            {
                m_sqlite.exec(String.format(
                        "INSERT OR IGNORE INTO strings " +
                        "(string_key, string_value) " +
                        "   VALUES" +
                        "('%s','%s')",
                        
                        key,
                        m_stringMap.get(key)
                    ));
                
                m_sqlite.exec(String.format(
                        "UPDATE strings SET " +
                        "   string_value='%s'" +
                        "   WHERE string_key='%s'",
                        
                        m_stringMap.get(key),
                        key
                    ));
            }
            
            for (String key : m_intMap.keySet())
            {
                m_sqlite.exec(String.format(
                        "INSERT OR IGNORE INTO integers " +
                        "(int_key, int_value) " +
                        "   VALUES" +
                        "('%s',%d)",
                        
                        key,
                        m_intMap.get(key)
                    ));
                
                m_sqlite.exec(String.format(
                        "UPDATE integers SET " +
                        "   int_value=%d" +
                        "   WHERE int_key='%s'",
                        
                        m_intMap.get(key),
                        key
                    ));
            }
            
            for (String key : m_boolMap.keySet())
            {
                m_sqlite.exec(String.format(
                        "INSERT OR IGNORE INTO booleans " +
                        "(bool_key, bool_value) " +
                        "   VALUES" +
                        "('%s',%d)",
                        
                        key,
                        m_boolMap.get(key) ? 1 : 0
                    ));
                
                m_sqlite.exec(String.format(
                        "UPDATE booleans SET " +
                        "   bool_value=%d" +
                        "   WHERE bool_key='%s'",
                        
                        m_boolMap.get(key) ? 1 : 0,
                        key
                    ));
            }
        } 
        
        catch (SQLiteException e)
        {
            e.printStackTrace(System.out);
        }
        
        
        
    }
    
    private void readConfig() throws SQLiteException
    {
        // read in config strings from the config table
        SQLiteStatement st;
        st = m_sqlite.prepare("SELECT * FROM strings");
        while(st.step())
            m_stringMap.put(st.columnString(0),st.columnString(1));
        st.dispose();
       
        // read in advanced settings from the advanced table
        st = m_sqlite.prepare("SELECT * FROM integers");
        while(st.step())
            m_intMap.put(st.columnString(0),st.columnInt(1));
        st.dispose();
        
        // read in recent game settings from the game table
        st = m_sqlite.prepare("SELECT * FROM booleans");
        while(st.step())
            m_boolMap.put(st.columnString(0),st.columnInt(1)>0);
        st.dispose();
    }
    
    private void initDatabase()
    {
        System.out.println("It appears the database is old or does not exist, " +
        		            "initializing now");
        try
        {
            InputStream fstream = this.getClass().getResourceAsStream("/SQL/Initialize.sql");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            StringBuilder buf = new StringBuilder();

            while ((strLine = br.readLine()) != null)   
            {
                buf.append(strLine).append(" ");
                   
                if (strLine.endsWith(";"))
                {
                    m_sqlite.exec(buf.toString());
                    buf = new StringBuilder();
                }
            }
            
            readConfig();
        }
        
        catch( SQLiteException e )
        {
            m_dataOK = false;
            e.printStackTrace(System.out);
            return;
        } 
        
        catch (FileNotFoundException e)
        {
            m_dataOK = false;
            e.printStackTrace(System.out);
            return;
        } 
        
        catch (IOException e)
        {
            m_dataOK = false;
            e.printStackTrace(System.out);
            return;
        }
    }
}
