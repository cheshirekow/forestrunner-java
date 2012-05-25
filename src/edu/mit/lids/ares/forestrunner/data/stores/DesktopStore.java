package edu.mit.lids.ares.forestrunner.data.stores;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteConstants;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.data.GlobalHighScoreRow;
import edu.mit.lids.ares.forestrunner.data.Store;
import edu.mit.lids.ares.forestrunner.data.UserHighScoreRow;

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
    public void init(Game game)
    {
        super.init(game);
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
            // the program, and if it's not, run the init script to
            // rebuild tables
            if( m_intMap.containsKey("version") )
            {
                if( m_intMap.get("version") < Game.s_version )
                    initDatabase();
            }
            else
                initDatabase();
            
            // check that the user has a hash stored
            if( !m_stringMap.containsKey("hash") ||
                    m_stringMap.get("hash").length() < 16 )
            {
                System.out.println("Database seems to have invalid hash, " +
                		            "requesting one from server");
                
                String urlString = 
                        "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
                        + "create_user.php";

                // try to open a stream to the create user page
                InputStream source;
                try
                {
                    source = new URL(urlString).openStream();
                } 
                
                catch (MalformedURLException e)
                {
                    System.out.println("Failed to connect to server to get a new " +
                                        "hash, continuing without data");
                    e.printStackTrace(System.out);
                    m_dataOK = false;
                    return;
                } 
                
                catch (IOException e)
                {
                    System.out.println("Failed to connect to server to get a new " +
                            "hash, continuing without data");
                    e.printStackTrace(System.out);
                    m_dataOK = false;
                    return;
                }  

                // read the entire stream into a single string
                String jsonString = 
                        new Scanner( source, "UTF-8" )
                                .useDelimiter("\\A").next();

                JSONObject obj = (JSONObject) JSONValue.parse(jsonString);
                
                // check the result message
                if( obj.containsKey("status") )
                {
                    String status = (String) obj.get("status");
                    if( status.compareTo("OK") == 0 )
                    {
                        m_stringMap.put("hash", (String)obj.get("hash") );
                        sync();
                    }
                }
            }
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
    
    @Override
    public void recordScore(float score)
    {
        if(!m_dataOK)
            return;
        
        long unixTime = System.currentTimeMillis() / 1000L;
        String fmt = "INSERT INTO %s " +
        		     "    (date, velocity, density, radius, score) " +
        		     "VALUES" +
        		     "    (%d, %d, %d, %d, %.30f)";
        
        String globalFmt = "INSERT INTO %s " +
                "    (date, velocity, density, radius, score, global_id) " +
                "VALUES" +
                "    (%d, %d, %d, %d, %.30f, %d)";
        
        String delFmt = 
                "DELETE FROM %s WHERE " +
                        "velocity=%d " +
                        "AND density=%d " +
                        "AND radius=%d " +
                        "AND " +
                        "score <= " + 
                "(" +
                    "SELECT score FROM user_data " +
                        "ORDER BY score  DESC LIMIT 20, 1 " +
                ")";
        try
        {
            m_sqlite.exec(String.format(fmt,
                        "user_data",
                        (int) unixTime,
                        getInteger("velocity"),
                        getInteger("density"),
                        getInteger("radius"),
                        score
                    ));
            
            m_intMap.put("lastUserRowId", (int)m_sqlite.getLastInsertId());

            m_sqlite.exec(String.format(fmt,
                    "unsent_score",
                    (int) unixTime,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius"),
                    score
                ));
            
            SQLiteStatement st = 
                    m_sqlite.prepare("SELECT MIN(global_id) FROM global_data");
            st.step();
            int newId = Math.min(0, st.columnInt(0))-1;
            
            m_sqlite.exec(String.format(globalFmt,
                    "global_data",
                    (int) unixTime,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius"),
                    score,
                    newId
                ));
            
            m_intMap.put("lastGlobalRowId", (int)m_sqlite.getLastInsertId());
            
            m_sqlite.exec(String.format(delFmt,
                    "user_data",
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius")
                    ));
            
            m_sqlite.exec(String.format(delFmt,
                    "global_data",
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius")
                    ));
        } 
        
        catch (SQLiteException e)
        {
            e.printStackTrace(System.err);
        }
        
        catch (RuntimeException e)
        {
            e.printStackTrace(System.err);
        }
    }
    
    @Override
    public List<UserHighScoreRow>   getUserScores()
    {
        List<UserHighScoreRow> scores = new ArrayList<UserHighScoreRow>();
        
        String fmt = 
            "SELECT * FROM user_data WHERE" +
		    "     velocity=%d  " +
		    "     AND density=%d   " +
		    "     AND radius=%d    " +
		    " ORDER BY score DESC";
        
        try
        {
            SQLiteStatement st = m_sqlite.prepare(String.format(
                    fmt,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius")
                    ));
            
            while(st.step())
            {
                UserHighScoreRow row= new UserHighScoreRow();
                row.id      = st.columnLong(0);
                row.date    = st.columnLong(1);
                row.score   = st.columnDouble(5);
                if(row.id == getInteger("lastUserRowId"))
                    row.isCurrent = true;
                scores.add(row);
            }
            
            st.dispose();
        }
        catch(SQLiteException e)
        {
            e.printStackTrace(System.err);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace(System.err);
        }
        
        return scores;
    }
    
    @Override 
    public void syncGlobalHigh()
    {
        Map<String,String> paramMap = new HashMap<String,String>();
        
        paramMap.put("version", String.format("%d",getInteger("version")));
        paramMap.put("velocity", String.format("%d",getInteger("velocity")));
        paramMap.put("density", String.format("%d",getInteger("density")));
        paramMap.put("radius", String.format("%d",getInteger("radius")));
        paramMap.put("hash", getString("hash"));
        
        String urlString = 
                "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
                + Store.encode("get_global_scores.php", paramMap);

        // try to open a stream to the create user page
        InputStream source;
        try
        {
            source = new URL(urlString).openStream();
        } 
        catch (MalformedURLException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.out);
            return;
        } 
        catch (IOException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.out);
            return;
        }  

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
            System.err.println("Failed to get global scores from server ");
            if(obj.containsKey("message"))
                System.err.println("Message: " + obj.get("message"));
            return;
        }
        
        String updateFmt = 
            "INSERT OR IGNORE INTO global_data " +
                    "(nick,date,velocity,density,radius,score,global_id)" +
                "VALUES" + 
                    "('%s',%d,%d,%d,%d,%.30f,%d)";
        
        String delFmt = 
                "DELETE FROM %s WHERE " +
                        "velocity=%d " +
                        "AND density=%d " +
                        "AND radius=%d " +
                        "AND " +
                        "score <= " + 
                "(" +
                    "SELECT score FROM user_data " +
                        "ORDER BY score  DESC LIMIT 20, 1 " +
                ")";
        
        // otherwise, things look good
        JSONArray scoreArray = (JSONArray)obj.get("scores");
        for( Object scoreObj : scoreArray )
        {
            JSONObject scoreMap = (JSONObject)scoreObj;
            
            try
            {
                m_sqlite.exec(String.format(updateFmt, 
                    (String) scoreMap.get("nick"),
                    Integer.parseInt( (String) scoreMap.get("date") ),
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius"),
                    Double.parseDouble( (String) scoreMap.get("score") ),
                    Integer.parseInt(   (String) scoreMap.get("global_id") )
                ));
                
                m_sqlite.exec(String.format(delFmt,
                    "global_data",
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius")
                ));
            }
            catch(SQLiteException e)
            {
                e.printStackTrace(System.err);
            }
        }
    }
    
    @Override
    public void sendOneScore()
    {
        if(!m_dataOK)
            return;
        
        try
        {
            SQLiteStatement st = 
                    m_sqlite.prepare("SELECT * FROM unsent_score ORDER BY score_id ASC LIMIT 1");
            if(st.step())
            {
                Map<String,String> paramMap = new HashMap<String,String>();
                paramMap.put("data_id", String.format("%d",st.columnInt(0)));
                paramMap.put("date",    String.format("%d",st.columnInt(1)));
                paramMap.put("velocity",String.format("%d",st.columnInt(2)));
                paramMap.put("density", String.format("%d",st.columnInt(3)));
                paramMap.put("radius",  String.format("%d",st.columnInt(4)));
                paramMap.put("score",   String.format("%.30f",st.columnDouble(5)));
                paramMap.put("hash",    getString("hash"));
                paramMap.put("version", String.format("%d",getInteger("version")));
                
                String urlString = 
                        "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
                        + Store.encode("insert_score.php", paramMap);

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
                
                System.out.println("Sent score to server: " + jsonString);
                
                m_sqlite.exec(String.format(
                        "DELETE FROM unsent_score WHERE score_id=%d", 
                        st.columnInt(0)
                ));
            }
            
            st.dispose();
        }
        catch(SQLiteException e)
        {
            e.printStackTrace(System.err);
        }
        catch (MalformedURLException e)
        {
            System.err.println("Failed to send score to server");
            e.printStackTrace(System.out);
            return;
        } 
        catch (IOException e)
        {
            System.err.println("Failed to send score to  server");
            e.printStackTrace(System.out);
            return;
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
                    "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
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
    public List<GlobalHighScoreRow>   getGlobalScores()
    {
        List<GlobalHighScoreRow> scores = new ArrayList<GlobalHighScoreRow>();
        
        String fmt = 
            "SELECT * FROM global_data WHERE" +
            "     velocity=%d  " +
            "     AND density=%d   " +
            "     AND radius=%d    " +
            " ORDER BY score DESC";
        
        try
        {
            SQLiteStatement st = m_sqlite.prepare(String.format(
                    fmt,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius")
                    ));
            
            while(st.step())
            {
                GlobalHighScoreRow row= new GlobalHighScoreRow();
                row.id      = st.columnLong(0);
                row.nick    = st.columnString(1);
                if(row.nick == null)
                    row.nick = getString("nick");
                row.date    = st.columnLong(2);
                row.score   = st.columnDouble(6);
                if(row.id == getInteger("lastGlobalRowId"))
                    row.isCurrent = true;
                scores.add(row);
            }
            
            st.dispose();
        }
        catch(SQLiteException e)
        {
            e.printStackTrace(System.err);
        }
        catch (RuntimeException e)
        {
            e.printStackTrace(System.err);
        }
        
        return scores;
    }
    
    
}
