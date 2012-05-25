package edu.mit.lids.ares.forestrunner.data.stores.android;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.mit.lids.ares.forestrunner.Game;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper
    extends SQLiteOpenHelper
{
    static final String s_dbName = "forestrunner";
    
    public DatabaseHelper(Context ctx)
    {
        super(ctx,s_dbName,null,Game.s_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        initDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        initDatabase(db);
    }
    
    
    private void initDatabase(SQLiteDatabase db)
    {
        System.out.println("It appears the database is old or does not exist, " +
                            "initializing now");
        try
        {
            InputStream fstream = this.getClass().getResourceAsStream("/SQL/Initialize.sql");
            DataInputStream in  = new DataInputStream(fstream);
            BufferedReader br   = new BufferedReader(new InputStreamReader(in));
            String strLine;
            StringBuilder buf   = new StringBuilder();

            while ((strLine = br.readLine()) != null)   
            {
                buf.append(strLine).append(" ");
                   
                if (strLine.endsWith(";"))
                {
                    db.execSQL(buf.toString());
                    buf = new StringBuilder();
                }
            }
        }
        
        catch (FileNotFoundException e)
        {
            e.printStackTrace(System.out);
            return;
        } 
        
        catch (IOException e)
        {
            e.printStackTrace(System.out);
            return;
        }
    }
}
