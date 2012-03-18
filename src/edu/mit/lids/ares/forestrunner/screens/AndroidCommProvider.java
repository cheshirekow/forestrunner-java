package edu.mit.lids.ares.forestrunner.screens;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;


public class AndroidCommProvider
    extends CommProvider
{
    public AndroidCommProvider()
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
                    throw new RuntimeException("Failed to create dir");
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
        
        // if the properties file exists, then load it
        String idFileName = dataDir + File.separator + "user.properties";
        File   idFile     = new File(idFileName);
        
        // if the file exists, then load it
        if(idFile.exists())
        {
            FileInputStream inStream;
            try
            {
                inStream = new FileInputStream(idFile);
                m_props.load(inStream);
            } 
            
            catch (FileNotFoundException e)
            {
                System.out.println("Supposidly " + idFileName 
                                    + " exists but exception thrown:");
                e.printStackTrace(System.out);
                m_dataOK = false;
                return;
            } 
            
            catch (IOException e)
            {
                System.out.println("Failed to read properties from" 
                                    + idFileName ); 
                e.printStackTrace(System.out);
                m_dataOK = false;
                return;
            }
            
        }
        
        // otherwise, try to get a hash from the server
        else
        {
            m_props.setProperty("user_hash", "0");
            
            // and try to write the properties to the file
            FileOutputStream outStream;
            try
            {
                outStream = new FileOutputStream(idFile);
                m_props.store(outStream, "");
            } 
            
            catch (IOException e)
            {
                System.out.println("Failed to write properties to" 
                                    + idFileName ); 
                e.printStackTrace(System.out);
                m_dataOK = false;
                return;
            }
        }
        
        
        // if the sqlite database doesn't exist, then create it
        String dbFileName = dataDir + File.separator + "scores.sqlite";
        File   dbFile     = new File(dbFileName);
        
        if(!dbFile.exists())
        {
            SQLiteConnection db = new SQLiteConnection(dbFile);
            
            try
            {
                db.open(true);
                SQLiteStatement st = db.prepare(
                    "CREATE  TABLE main.scores (" +
                        "'velocity' INTEGER NOT NULL , " +
                        "'density' INTEGER NOT NULL , " +
                        "'radius' INTEGER NOT NULL , " +
                        "'date' DATETIME NOT NULL DEFAULT timespec('now'), " +
                        "'score' DOUBLE NOT NULL )" );
                st.step();
                st.dispose();
                db.dispose();
            } 
            
            catch (SQLiteException e)
            {
                System.out.println("Failed to create sqlite database "
                		                + "for scores:");
                e.printStackTrace(System.out);
                m_dataOK = false;
                return;
            }
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
            result.message= "AndroidCommProvider is in a bad state";
            return result;
        }
        
        // first store new nick in properties file
        String userHome     = System.getProperty("user.home");
        String dataDir      = userHome + File.separator + ".forestrunner";
        String idFileName = dataDir + File.separator + "user.properties";
        File   idFile     = new File(idFileName);
        FileOutputStream outStream;
        try
        {
            outStream = new FileOutputStream(idFile);
            m_props.store(outStream, "");
        } 
        catch (IOException e)
        {
            System.out.println("Failed to write properties to" 
                                + idFileName ); 
            e.printStackTrace(System.out);
            m_dataOK = false;
        }
        
        return result;
    }

    @Override
    public HighScoreResult getHighScores(Properties props)
    {
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
        
        // if the file system is in a bad state don't bother trying to get
        // scores from the local database
        if(!m_dataOK)
        {
            row             = new HighScoreRow();
            row.user_nick   = "Database Error";
            row.date        = "";
            row.score       = "0";
            result.user_scores.add(row);
            return result;
        }
        
        // otherwise, retrieve scores from the sqlite database
        String userHome     = System.getProperty("user.home");
        String dataDir      = userHome + File.separator + ".forestrunner";
        String dbFileName   = dataDir + File.separator + "scores.sqlite";
        File   dbFile       = new File(dbFileName);
        SQLiteConnection db = new SQLiteConnection(dbFile);

        // build lists of properties and values
        String[] propNames2 = {"velocity","density","radius"};
        
        List<String>   propList     = Arrays.asList(propNames2); 
        List<String>   conditions   = new ArrayList<String>();
        
        for( String propName : propList )
            conditions.add(propName + "=" + props.getProperty(propName));
        
        String sqlQuery     = "SELECT date, score FROM main.scores WHERE " + 
                                StringUtils.join(conditions, " AND ") + 
                                " ORDER BY score DESC LIMIT 20 ";
        
        try
        {
            db.open();
            SQLiteStatement st = db.prepare( sqlQuery );
            while(st.step())
            {
                row             = new HighScoreRow();
                row.user_nick   = m_props.getProperty("user_nick");
                row.date        = st.columnString(0);
                row.score       = st.columnString(1);
                result.user_scores.add(row);
            }
            st.dispose();
            db.dispose();
        } 
        
        catch (SQLiteException e)
        {
            System.out.println("Failed to insert new score into " +
                                    "local databse:");
            e.printStackTrace(System.out);
        }
        
        
        return result;
    }


    @Override
    public GenericResult publishScore(Properties props)
    {
        GenericResult result = new GenericResult();
        result.status = "FAILED";
        result.message= "";
        
        // if the file system is in a bad state don't bother trying to write
        // scores to a local database
        if(!m_dataOK)
            return result;
        
        // otherwise, write the scores to a sqlite database
        String userHome     = System.getProperty("user.home");
        String dataDir      = userHome + File.separator + ".forestrunner";
        String dbFileName   = dataDir + File.separator + "scores.sqlite";
        File   dbFile       = new File(dbFileName);
        SQLiteConnection db = new SQLiteConnection(dbFile);

        // build lists of properties and values
        String[] propNames2 = {"velocity","density","radius","score"};
        
        List<String>   propList     = new ArrayList<String>(); 
        List<String>   valueList    = new ArrayList<String>();
        for( String propName : propNames2 )
        {
            propList.add(propName);
            valueList.add(  props.getProperty(propName) );
        }
                
        propList.add("date");
        valueList.add("datetime('now')");
        
        String sqlQuery     = "INSERT INTO main.scores (" +
                                StringUtils.join(propList,", ") 
                                + ") VALUES ( " +
                                StringUtils.join(valueList,", ")
                                + ")";
        
        try
        {
            db.open();
            SQLiteStatement st = db.prepare( sqlQuery );
            st.step();
            st.dispose();
            db.dispose();
        } 
        
        catch (SQLiteException e)
        {
            System.out.println("Failed to insert new score into " +
            		                "local databse:");
            e.printStackTrace(System.out);
        }
        
        return result;
    }

}
