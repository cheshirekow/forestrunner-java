package edu.mit.lids.ares.forestrunner;

import java.util.HashMap;


public class AdvancedSettings 
    extends HashMap<String,Boolean>
{
    public static final String[] parameters = 
        {
        "postProcessor",
        "fogFilter",
        "cartoonFilter",
        "toonBlow",
        "lighting",
        "debugGrids",
        "mainGrid",
        "verbose"
        };
    
    public AdvancedSettings()
    {
        super();
        
        for( String param : parameters )
            put(param, false);
    }
}
