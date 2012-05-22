package edu.mit.lids.ares.forestrunner.gui.screens;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.gui.ScreenManager;
import edu.mit.lids.ares.forestrunner.nifty.ProgressbarControl;

public class LoadingScreen 
    extends
        ScreenBase
    implements 
        ScreenController
{
    private ProgressbarControl  m_pb; 
    private Queue<String>       m_screens;
    private int                 m_processed;
    private int                 m_total;
    private int                 m_phase;
    private static final int    s_totalPhases = 500;
    
    public LoadingScreen( ScreenManager mgr )
    {
        super(mgr);
    }
    
    @Override
    public void update_impl( float tpf )
    {
        switch(m_phase)
        {
            // in phase 0 we manually bind all of the screens
            case 0:
            {
                /*
                if( m_screens.size() > 0 )
                {
                    String screenId = m_screens.remove();
                    m_processed++;
                    
                    s_logger.log( Level.INFO, "processing screen " + screenId);
                    Screen screen = m_nifty.getScreen(screenId);
                    ScreenController sc = screen.getScreenController();
                    sc.bind(m_nifty, screen);
                }
                else
                */
                    m_phase++;
                
                break;
            }
            
            // in phase 1 we update the text of the nick screen to match the
            // users saved nickname
            case 1:
            {
                m_phase++;
                break;
            }
            
            case s_totalPhases:
                m_mgr.advance("disclaimer");
                break;
                
            default:
                m_phase++;
                break;
        }
        
        m_pb.setProgress( (m_processed + m_phase) / (float)m_total );
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        m_pb = screen.findControl("pb_loading", ProgressbarControl.class);
    }
    
    /**
     *  \brief  this screen does not have an entrance effect so 
     *          we need to activate it on the start screen
     */
    @Override
    public void onStartScreen()
    {
        super.onStartScreen();
        m_active = true;
        
        m_screens   = new LinkedList<String>(m_nifty.getAllScreensName());
        m_processed = 0;
        m_phase     = 0;
        m_total     = s_totalPhases + m_screens.size();
    }
    
    
    

}
