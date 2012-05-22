package edu.mit.lids.ares.forestrunner;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 *  @brief  manages the initialization of the game within the gui thread 
 *          so that the frame loop gets a couple of frames out while the
 *          game is doing it's init work
 *  @author josh
 *
 *  I still don't really understand the purpose of the app state system, but
 *  my suspicion is that it's intention is to implement what I call a 
 *  "state graph" paradigm, by attaching callbacks to the currently active
 *  "states", which can instigate "state transitions" by detaching and 
 *  attaching different states to the state manager
 */
public class InitAppState 
    extends AbstractAppState
{
    protected Game              m_game;     // the game we are initializing
    protected AppStateManager   m_mgr;      // the manager who manages us
    protected int               m_step;     // where we are in the init cycle
    
    public void initialize(AppStateManager mgr, Application app) 
    {
        super.initialize(mgr,app);
        m_mgr   = mgr;
        m_game  = (Game) app;
        m_step  = 1;
    }
    
    public void update(float tpf) 
    {
        switch(m_step)
        {
            case 10:
                m_game.initViews();
                break;
                
            case 20:
                m_game.setupNifty();
                break;
                
            case 30:
                m_game.initConstants();
                break;
                
            case 40:
                m_game.initSceneGraph();
                break;
                
            case 50:
                m_game.initStaticMeshes();
                break;
                
            case 60:
                m_game.initPatches();
                break;
                
            case 70:
                m_game.setupLights();
                break;
                
            case 80:
                m_game.setupCamera();
                break;
                
            case 90:
                m_game.setupProcessor();
                break;
                
            case 100:
                m_game.initRun();
                break;
                
            case 110:
                m_game.changeAdvancedSettings(AdvancedSettings.s_default);
                break;
                
            case 120:
                m_mgr.detach(this);
                break;
                
            default:
                break;
        }
        
        m_step++;
    }
    
    
}
