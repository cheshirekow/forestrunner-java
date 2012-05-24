package edu.mit.lids.ares.forestrunner.gui.screens;

import java.util.List;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.data.GlobalHighScoreRow;
import edu.mit.lids.ares.forestrunner.data.UserHighScoreRow;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.nifty.ProgressbarControl;

public class HighScoreScreen 
    extends ScreenBase
{
    private int     m_step;
    private String  m_goto;
    
    private ProgressbarControl  m_pb; 
    private Element             m_commPopup;
    
    private List<UserHighScoreRow>      m_userList;
    private List<GlobalHighScoreRow>    m_globalList;
    
    private static final int s_scoresToSend = 10;
    private static final int s_numSteps     = 300;
    
    public HighScoreScreen( Game game )
    {
        super(game,true,true);
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        m_commPopup = m_nifty.createPopup("pop.comm");
        m_pb = m_commPopup.findControl("pb.comm", ProgressbarControl.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void update_impl(float tpf)
    {
        switch(m_step)
        {
            case 0:
                m_nifty.showPopup(m_screen, m_commPopup.getId(), null);
                break;
                
            case 1:
                m_dataStore.recordScore(m_game.getScore());
                break;
                
            case 3:
                m_userList    = m_dataStore.getUserScores();
                break;
                
            case 4:
                m_globalList  = m_dataStore.getGlobalScores();
                break;
                
            case 5:
            {
                ListBox<UserHighScoreRow> listBox =(ListBox<UserHighScoreRow>) 
                        m_screen.findNiftyControl("lb.personalHigh", ListBox.class);
                listBox.clear();
                System.out.println("personal scores:");
                for( UserHighScoreRow row : m_userList )
                {
                    System.out.println("   " + row.date + ", " + row.score);
                    listBox.addItem(row);
                }
                listBox.refresh();
                
                break;
            }
                
            case 6:
            {
                ListBox<GlobalHighScoreRow> listBox =(ListBox<GlobalHighScoreRow>) 
                        m_screen.findNiftyControl("lb.globalHigh", ListBox.class);
                listBox.clear();
                System.out.println("global scores:");
                for( GlobalHighScoreRow row : m_globalList )
                {
                    System.out.println("   " + row.date + ", " + row.score);
                    listBox.addItem(row);
                }
                listBox.refresh();
                
                break;
            }
                
            case 7: 
                // attempt to send scores to server
                break;
                
            case s_numSteps:
                m_nifty.closePopup(m_commPopup.getId());
                break;
                
            default:
            {
                if( m_goto.length() > 0 )
                    m_nifty.gotoScreen(m_goto);
                break;
            }
        }
        
        m_step++;
        m_pb.setProgress( m_step/(float)s_numSteps );
    }
    
    public void onStart_impl()
    {
        m_step = 0;
        m_goto = "";
        m_mgr.attach(this);
        m_pb.setProgress( 0.0f );
        
        /*
        Properties props = new Properties();
        props.setProperty("user_hash", m_comm.getHash() );
        props.setProperty("velocity", m_game.getParam("velocity").toString() );
        props.setProperty("density", m_game.getParam("density").toString() );
        props.setProperty("radius", m_game.getParam("radius").toString() );
        props.setProperty("score", new Float(m_game.getScore()).toString() );
        
        if(!fake)
            m_comm.publishScore(props);
        
        HighScoreResult result = m_comm.getHighScores(props);
        System.out.println("Score request status: " + result.status );
        
        ListBox<HighScoreRow> listBox =(ListBox<HighScoreRow>) 
                m_screen.findNiftyControl("lb.personalHigh", ListBox.class);
        listBox.clear();
        System.out.println("personal scores:");
        for( HighScoreRow row : result.user_scores )
        {
            System.out.println("   " + row.user_nick + ", " + row.date + ", " + row.score);
            listBox.addItem(row);
        }
        listBox.refresh();
                
        listBox = (ListBox<HighScoreRow>) 
                m_screen.findNiftyControl("lb.globalHigh", ListBox.class);
        listBox.clear();
        System.out.println("global scores:");
        for( HighScoreRow row : result.global_scores)
        {
            System.out.println("   " + row.user_nick + ", " + row.date + ", " + row.score);
            listBox.addItem(row);        
        }
            
        listBox.refresh();        
        
        TextField textfield = 
                m_screen.findNiftyControl("txtfld.username", TextField.class);
        textfield.setText(m_comm.getNick());
        */
    }
    
    @Override
    public void onEnd_impl()
    {
        m_mgr.detach(this);
    }
    
    @NiftyEventSubscriber(pattern="highscore.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("highscore button [" + id +"] pressed ");
        if( id.compareTo("highscore.btn.again")==0 )
        {
            m_game.initRun();
            m_goto = "countdown3";
        }
        else if( id.compareTo("highscore.btn.settings")==0 )
        {
            m_goto = "game";
        }
    }
}
