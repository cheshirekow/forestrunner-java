package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.nifty.ProgressbarControl;

public class LoadingScreen 
    extends
        ScreenBase
    implements 
        ScreenController
{
    private ProgressbarControl  m_pb; 
    private int                 m_total;
    private int                 m_step;
    private static final int    s_totalSteps = 20;
    
    public LoadingScreen( Game game )
    {
        super(game,false,false);
    }
    
    @Override
    public void onStart_impl()
    {
        m_mgr.attach(this);
        m_step      = 0;
        m_total     = s_totalSteps;
    }
    
    @Override
    public void onEnd_impl()
    {
        m_mgr.detach(this);
    }
    
    @Override
    public void update_impl( float tpf )
    {
        switch(m_step)
        {
            case 0:
                m_game.initDataStore();
                break;
                
            case   1:
                m_game.initConstants();
                break;
                
            case   2:
                m_game.setupCamera();
                break;
                
            case   3:
                m_game.initSceneGraph();
                break;
                
            case   4:
                m_game.initStaticMeshes();
                break;
                
            case   5:
                m_game.initPatches();
                break;
                
            case   6:
                m_game.setupLights();
                break;
                
            case   7:
                break;
                
            case   8:
                m_game.setupProcessor();
                break;
                
            case   9:
                m_game.setupNifty();
                break;
                
            case  10:
                m_game.initRun();
                break;
                
            case  11:
                m_game.changeAdvancedSettings(AdvancedSettings.s_default);
                break;
                
            case  20:
                m_nifty.gotoScreen("disclaimer");
                break;
                
            default:
                break;
        }
        
        m_step++;
        m_pb.setProgress( (m_step) / (float)m_total );
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        m_pb = screen.findControl("pb_loading", ProgressbarControl.class);
    }
    
    
    

}
