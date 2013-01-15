package edu.mit.lids.ares.forestrunner.data.stores;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jme3.system.android.JmeAndroidSystem;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.data.GlobalHighScoreRow;
import edu.mit.lids.ares.forestrunner.data.Store;
import edu.mit.lids.ares.forestrunner.data.UserHighScoreRow;
import edu.mit.lids.ares.forestrunner.data.stores.android.DatabaseHelper;


/**
 *  @brief  data storage backend for the desktop, uses android native
 *          sqlite database API
 *  @author josh
 *
 */
public class AndroidStore 
    extends Store
{
    protected boolean           m_dataOK;
    protected DatabaseHelper    m_helper;
    protected boolean           m_hasHash;
    
    @Override
    public void init(Game game)
    {
        super.init(game);
        m_dataOK  = true;
        m_hasHash = false;
        
        Context ctx = JmeAndroidSystem.getActivity();
        m_helper = new DatabaseHelper(ctx);
        
        readConfig();
        
        // check that the user has a hash stored
        if( !m_stringMap.containsKey("hash") ||
                m_stringMap.get("hash").length() < 16 )
        {
            requestHash();
        }
        
    }
    
    @Override
    public void sync()
    {
        SQLiteDatabase db = m_helper.getWritableDatabase();
        
        for (String key : m_stringMap.keySet())
        {
            db.execSQL(String.format(
                    "INSERT OR IGNORE INTO strings " +
                    "(string_key, string_value) " +
                    "   VALUES" +
                    "('%s','%s')",
                    
                    key,
                    m_stringMap.get(key)
                ));
            
            db.execSQL(String.format(
                    "UPDATE strings SET " +
                    "   string_value='%s'" +
                    "   WHERE string_key='%s'",
                    
                    m_stringMap.get(key),
                    key
                ));
        }
        
        for (String key : m_intMap.keySet())
        {
            db.execSQL(String.format(
                    "INSERT OR IGNORE INTO integers " +
                    "(int_key, int_value) " +
                    "   VALUES" +
                    "('%s',%d)",
                    
                    key,
                    m_intMap.get(key)
                ));
            
            db.execSQL(String.format(
                    "UPDATE integers SET " +
                    "   int_value=%d" +
                    "   WHERE int_key='%s'",
                    
                    m_intMap.get(key),
                    key
                ));
        }
        
        for (String key : m_boolMap.keySet())
        {
            db.execSQL(String.format(
                    "INSERT OR IGNORE INTO booleans " +
                    "(bool_key, bool_value) " +
                    "   VALUES" +
                    "('%s',%d)",
                    
                    key,
                    m_boolMap.get(key) ? 1 : 0
                ));
            
            db.execSQL(String.format(
                    "UPDATE booleans SET " +
                    "   bool_value=%d" +
                    "   WHERE bool_key='%s'",
                    
                    m_boolMap.get(key) ? 1 : 0,
                    key
                ));
        }
    }
    
    @Override
    public void recordScore(float score)
    {
        SQLiteDatabase db       = m_helper.getWritableDatabase();
        SQLiteDatabase readDb   = m_helper.getReadableDatabase();
        
        long unixTime = System.currentTimeMillis() / 1000L;
        
        ContentValues cv;
        cv = new ContentValues();
        
        cv.put("date", (int) unixTime);
        cv.put("velocity",  getInteger("velocity"));
        cv.put("density",   getInteger("density"));
        cv.put("radius",    getInteger("radius"));
        cv.put("score",     score);
        
        m_intMap.put("lastUserRowId", 
                (int) db.insert("user_data", "data_id", cv) );

        db.insert("unsent_score", "score_id", cv);
        
        Cursor cur = db.rawQuery("SELECT MIN(global_id) FROM global_data", 
                                new String[]{});
        cur.moveToFirst();
        int newId = Math.min(0, cur.getInt(0))-1;
        cur.close();
        
        cv.put("global_id",newId);
        
        m_intMap.put("lastGlobalRowId", 
                (int)db.insert("global_data", "data_id", cv) );
    }
    
    
    
    
    @Override
    public List<UserHighScoreRow>  getUserScores()
    {
        List<UserHighScoreRow> scores = new ArrayList<UserHighScoreRow>();
        SQLiteDatabase db = m_helper.getReadableDatabase();
        SQLiteDatabase writeDb = m_helper.getWritableDatabase();
        
        String fmt = 
            "SELECT * FROM user_data WHERE" +
            "     velocity=%d  " +
            "     AND density=%d   " +
            "     AND radius=%d    " +
            " ORDER BY score DESC";
        
        String delFmt = 
                "DELETE FROM user_data WHERE" +
                "     velocity=%d  " +
                "     AND density=%d   " +
                "     AND radius=%d" +
                "     AND score<%f";
        
        Cursor cur = db.rawQuery(String.format(
                    fmt,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius")
                    ), new String[]{} );
            
        int iRow = 0;
        if(cur.moveToFirst())
        {
            do
            {
                UserHighScoreRow row= new UserHighScoreRow();
                row.id      = cur.getLong(0);
                row.date    = cur.getLong(1);
                row.score   = cur.getDouble(5);
                if(row.id == getInteger("lastUserRowId"))
                    row.isCurrent = true;
                scores.add(row);
                
                iRow++;
            }while(cur.moveToNext() && iRow < s_numScoresToShow);
        }
        
        if(iRow >= s_numScoresToShow)
        {
            double maxScore = scores.get( scores.size()-1 ).score;
            writeDb.execSQL(String.format(
                    delFmt,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius"),
                    maxScore
                    ));
        }
            
        cur.close();
        
        return scores;
    }
    
    
    
    
    
    
    @Override
    public List<GlobalHighScoreRow>   getGlobalScores()
    {
        List<GlobalHighScoreRow> scores = new ArrayList<GlobalHighScoreRow>();
        SQLiteDatabase db = m_helper.getReadableDatabase();
        SQLiteDatabase writeDb = m_helper.getWritableDatabase();
        
        String fmt = 
            "SELECT * FROM global_data WHERE" +
            "     velocity=%d  " +
            "     AND density=%d   " +
            "     AND radius=%d    " +
            " ORDER BY score DESC";
        
        String delFmt = 
            "DELETE FROM global_data WHERE " +
                    "velocity=%d " +
                    "AND density=%d " +
                    "AND radius=%d " +
                    "AND score<%f ";
    
        Cursor cur = db.rawQuery(String.format(
                fmt,
                getInteger("velocity"),
                getInteger("density"),
                getInteger("radius")
                ), new String[]{} );
        
        int iRow = 0;
        if(cur.moveToFirst())
        {
            do
            {
                GlobalHighScoreRow row= new GlobalHighScoreRow();
                row.id      = cur.getLong(0);
                row.nick    = cur.getString(1);
                if(row.nick == null)
                    row.nick = getString("nick");
                if(row.nick.length() > 14)
                    row.nick = row.nick.substring(0,14);
                row.date    = cur.getLong(2);
                row.score   = cur.getDouble(6);
                if(row.id == getInteger("lastGlobalRowId"))
                    row.isCurrent = true;
                scores.add(row);
                
                iRow++;
            }while(cur.moveToNext() && iRow < s_numScoresToShow);
        }
        
        if(iRow >= s_numScoresToShow)
        {
            double maxScore = scores.get( scores.size()-1 ).score;
            writeDb.execSQL(String.format(
                    delFmt,
                    getInteger("velocity"),
                    getInteger("density"),
                    getInteger("radius"),
                    maxScore
                    ));
        }
        
        cur.close();
        
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
        String jsonString = "";
        try
        {
            // try to open a stream to the create user page
            URL           url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(4000);
            InputStream source = con.getInputStream();
            
            // read the entire stream into a single string
            jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
        } 
        catch (MalformedURLException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.err);
            return;
        } 
        catch (SocketTimeoutException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.err);
            return;
        }
        catch (IOException e)
        {
            System.err.println("Failed to get global scores form server");
            e.printStackTrace(System.err);
            return;
        }  

        

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
        
        SQLiteDatabase db= m_helper.getWritableDatabase();
        
        // otherwise, things look good
        JSONArray scoreArray = (JSONArray)obj.get("scores");
        for( Object scoreObj : scoreArray )
        {
            JSONObject scoreMap = (JSONObject)scoreObj;
            
            db.execSQL(String.format(updateFmt, 
                (String) scoreMap.get("nick"),
                Integer.parseInt( (String) scoreMap.get("date") ),
                getInteger("velocity"),
                getInteger("density"),
                getInteger("radius"),
                Double.parseDouble( (String) scoreMap.get("score") ),
                Integer.parseInt(   (String) scoreMap.get("global_id") )
            ));
        }
    }
    
    
    
    
    
    @Override
    public void sendOneScore()
    {
        if(!m_hasHash)
            return;
        
        //System.out.println("Sending one score row");
        
        SQLiteDatabase db = m_helper.getReadableDatabase();
        SQLiteDatabase writeDb = m_helper.getWritableDatabase();
        Cursor cur = db.rawQuery("" +
        		"SELECT * FROM unsent_score ORDER BY score_id ASC LIMIT 1",
        		new String[]{});
        
        if(cur.moveToFirst())
        {
            try
            {
                Map<String,String> paramMap = new HashMap<String,String>();
                paramMap.put("data_id", String.format("%d",cur.getInt(0)));
                paramMap.put("date",    String.format("%d",cur.getInt(1)));
                paramMap.put("velocity",String.format("%d",cur.getInt(2)));
                paramMap.put("density", String.format("%d",cur.getInt(3)));
                paramMap.put("radius",  String.format("%d",cur.getInt(4)));
                paramMap.put("score",   String.format("%.30f",cur.getDouble(5)));
                paramMap.put("hash",    getString("hash"));
                paramMap.put("version", String.format("%d",Game.s_version));
                
                String urlString = 
                        "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
                        + Store.encode("insert_score.php", paramMap);
    
                // try to open a stream to the create user page
                URL           url = new URL(urlString);
                URLConnection con = url.openConnection();
                con.setConnectTimeout(2000);
                con.setReadTimeout(4000);
                InputStream source = con.getInputStream();
            
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
                    throw new RuntimeException("Bad JSON return");
                }
                
                String status = (String) obj.get("status");
                if( status.compareTo("OK") != 0 )
                {
                    System.err.println("Failed to send scores to server ");
                    if(obj.containsKey("message"))
                        System.err.println("Message: " + obj.get("message"));
                    throw new RuntimeException("Bad JSON return");
                }
                
                System.out.println("Sent score to server: " + jsonString);
                
                writeDb.execSQL(String.format(
                        "DELETE FROM unsent_score WHERE score_id=%d", 
                        cur.getInt(0)
                ));
            } 
            catch (MalformedURLException e)
            {
                e.printStackTrace(System.err);
            } 
            catch (SocketTimeoutException e)
            {
                e.printStackTrace(System.err);
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
            }
            catch (RuntimeException e)
            {
                e.printStackTrace(System.err);
            }
        }
        
        cur.close();
    }
    
    
    
    
    
    
    @Override
    public void sendNick()
    {
        if(!m_hasHash)
            return;
        
        //System.out.println("Sending nickname");
        
        try
        {
            Map<String,String> paramMap = new HashMap<String,String>();
            paramMap.put("hash", getString("hash"));
            paramMap.put("nick", getString("nick"));
            
            String urlString = 
                    "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
                    + Store.encode("set_nick.php", paramMap);
    
            // try to open a stream to the create user page
            URL           url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(4000);
            InputStream source = con.getInputStream();

        
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
            
            //System.out.println("Updated nickname at server: " + jsonString);
        }
        
        catch (MalformedURLException e)
        {
            e.printStackTrace(System.err);
        } 
        
        catch (SocketTimeoutException e)
        {
            e.printStackTrace(System.err);
        }
        
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
        
        
    }
    
    
    
    
    
    
    private void readConfig()
    {
        // read in config strings from the config table
        SQLiteDatabase db = m_helper.getReadableDatabase();
        Cursor cur;
        
        cur = db.rawQuery("SELECT * FROM strings", new String []{} );
        if( cur.moveToFirst() )
        {
            do
            {
                m_stringMap.put(cur.getString(0), cur.getString(1));
            }while( cur.moveToNext() );
        }
        cur.close();
        
        cur = db.rawQuery("SELECT * FROM integers", new String []{} );
        if( cur.moveToFirst() )
        {
            do
            {
                m_intMap.put(cur.getString(0), cur.getInt(1) );
            }while( cur.moveToNext() );
        }
        cur.close();

        cur = db.rawQuery("SELECT * FROM booleans", new String[]{} );
        if( cur.moveToFirst() )
        {
            do
            {
                m_boolMap.put(cur.getString(0), cur.getInt(1)>0 );
            }while( cur.moveToNext() );
        }
        cur.close();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private void requestHash()
    {
        String urlString = 
                "http://ares.lids.mit.edu/~jbialk/forest_runner/src/" 
                + "create_user.php";

        // try to open a stream to the create user page
        String jsonString = "";
        try
        {
            // try to open a stream to the create user page
            URL           url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(4000);
            InputStream source = con.getInputStream();
            
            // read the entire stream into a single string
            jsonString = 
                    new Scanner( source, "UTF-8" )
                            .useDelimiter("\\A").next();
        } 
        
        catch (MalformedURLException e)
        {
            System.err.println("Failed to connect to server to get a new " +
                                "hash, continuing without data");
            e.printStackTrace(System.err);
            m_dataOK = false;
            return;
        } 
        
        catch (SocketTimeoutException e)
        {
            System.err.println("Failed to connect to server to get a new " +
                    "hash, continuing without data");
            e.printStackTrace(System.err);
            m_dataOK = false;
            return;
        }
        
        catch (IOException e)
        {
            System.err.println("Failed to connect to server to get a new " +
                    "hash, continuing without data");
            e.printStackTrace(System.err);
            m_dataOK = false;
            return;
        }  


        JSONObject obj = (JSONObject) JSONValue.parse(jsonString);
        
        // check the result message
        if( obj.containsKey("status") )
        {
            String status = (String) obj.get("status");
            if( status.compareTo("OK") == 0 )
            {
                m_stringMap.put("hash", (String)obj.get("hash") );
                m_hasHash = true;
                sync();
            }
        }
    }
}
