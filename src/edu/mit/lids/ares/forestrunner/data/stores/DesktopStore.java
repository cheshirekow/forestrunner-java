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
    protected Boolean                   m_dataOK;
    protected SQLiteConnection          m_sqlite;
    protected HashMap<String,String>    m_configMap;
    
    public DesktopStore()
    {
        m_configMap = new HashMap<String,String>();
    }
    
    @Override
    public void init()
    {
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
            
            SQLiteStatement st = m_sqlite.prepare(
                    "SELECT name FROM sqlite_master " +
                            "WHERE type='table' AND name='config'"
                    );
            
            Boolean configExists = st.step();
            st.dispose();
            
            if(configExists)
            {
                st = m_sqlite.prepare("SELECT * FROM config");
                while(st.step())
                    m_configMap.put(st.columnString(0),st.columnString(1));
                st.dispose();
                
                // check that the version of the database is the same as that of
                // the program
                if( m_configMap.containsKey("version") )
                {
                    if( Integer.parseInt(m_configMap.get("version"))
                            < Game.s_version )
                    {
                        initDatabase();
                    }
                }
                else
                    initDatabase();
            }
            else 
                initDatabase();
            
            
            
        }
        catch (SQLiteException e)
        {
            System.out.println("Failed to open sqlite database "
                                    + "for scores and data:");
            e.printStackTrace(System.out);
            m_dataOK = false;
            return;
        }
    }
    
    private void initDatabase()
    {
        System.out.println("It appears the database is old or does not exist, initializing now");
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
            
            m_sqlite.exec("INSERT INTO config (config_key, config_value) "
            		        +"VALUES ('version', '"
        		            +Game.s_version
        		            +"')");
        }
        
        catch( SQLiteException e )
        {
            e.printStackTrace();
            return;
        } 
        
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return;
        } 
        
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
    }
}
