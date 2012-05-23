package edu.mit.lids.ares.forestrunner.gui.screens;

import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.TextField;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;
import edu.mit.lids.ares.forestrunner.screens.NickChangeResult;

public class HighScoreScreen 
    extends ScreenBase
{
    public HighScoreScreen( Game game )
    {
        super(game,true,true);
    }
    
    public void onStart_impl()
    {
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
    
    @NiftyEventSubscriber(pattern="highscore.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("highscore button [" + id +"] pressed ");
        if( id.compareTo("highscore.btn.again")==0 )
        {
            m_game.initRun();
            m_nifty.gotoScreen("countdown3");
        }
        else if( id.compareTo("highscore.btn.settings")==0 )
        {
            m_nifty.gotoScreen("game");
        }
    }
}
