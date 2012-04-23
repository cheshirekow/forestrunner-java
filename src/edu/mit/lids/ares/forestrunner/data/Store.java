package edu.mit.lids.ares.forestrunner.data;

import edu.mit.lids.ares.forestrunner.SystemContext;

public abstract class Store
{
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
