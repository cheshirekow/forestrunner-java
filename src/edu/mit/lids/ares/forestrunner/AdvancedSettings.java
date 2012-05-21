package edu.mit.lids.ares.forestrunner;

import java.util.HashMap;


public class AdvancedSettings 
    extends HashMap<String,Boolean>
{
    static final long serialVersionUID = 1L;

    public static final String[] parameters = 
    {
        "postProcessor",
        "fogFilter",
        "cartoon",
        "lighting",
        "debugGrids",
        "mainGrid",
        "gradientFloor",
        "verbose",
        "worldRotate"
    };
    
    public static AdvancedSettings s_default;
    
    static
    {
        s_default = new AdvancedSettings();
        s_default.put("cartoon", true);
        s_default.put("mainGrid", true);
        s_default.put("worldRotate", true);
    }
    
    public AdvancedSettings()
    {
        super();
        
        for( String param : parameters )
            put(param, false);
    }
}
