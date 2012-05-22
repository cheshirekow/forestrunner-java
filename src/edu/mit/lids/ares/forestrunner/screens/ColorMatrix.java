package edu.mit.lids.ares.forestrunner.screens;

import edu.mit.lids.ares.forestrunner.Game;
import java.util.Collection;
import java.util.Properties;

/**
 *
 * @author Elizabeth
 */
public class ColorMatrix {
    String[] colors = {"#f60f","#ff2f","#fe0f","#fc0f","#fa0f","#f80f"};
    CommProvider    m_comm;
    Game            m_game;
    Properties props = new Properties();
    
    public ColorMatrix(Game game){
        switch(game.getSystem())
        {
            case ANDROID:
                m_comm = new AndroidCommProvider();
                break;
                
            case DESKTOP:
                m_comm = new DesktopCommProvider();
                break;
                
            case APPLET:
                m_comm = new AppletCommProvider(game);
                break;
        }
        
        m_game = game;
        
        props.setProperty("user_hash", m_comm.getHash() );
        props.setProperty("radius", m_game.getParam("radius").toString() );
        props.setProperty("score", new Float(m_game.getScore()).toString() );
    }
    
    public String[][] getColors(String type){
        String[][] key = new String[10][10];
        double[][] mean = new double[10][10];
        double max = 0;
        double min = 100;
        Collection<HighScoreRow> scores;
        
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                props.setProperty("velocity", Integer.toString(i+1));
                props.setProperty("density", Integer.toString(j+1));
                
                HighScoreResult result = m_comm.getHighScores(props);
                if(type.equals("global"))
                    scores = result.global_scores;
                else // if(type.equals("personal"))
                    scores = result.user_scores;
                mean[i][j] = 0;
                for( HighScoreRow row : scores )
                {
                    if(row.score.length() > 0)
                        mean[i][j] += Double.parseDouble(row.score);
                }
                if(scores.size()!=0)
                    mean[i][j] = mean[i][j] / scores.size();
                if(mean[i][j] > max)
                    max = mean[i][j];
                if(mean[i][j] < min)
                    min = mean[i][j];
                //System.out.println("MEAN"+i+","+j+": "+mean[i][j]);
            }
        }
        
        double fraction = (max-min)/colors.length;
        System.out.println("FRACTION: "+fraction);
        
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if(mean[i][j] < 1*fraction)
                    key[i][j] = colors[0];
                else if(mean[i][j] < 2*fraction)
                    key[i][j] = colors[1];
                else if(mean[i][j] < 3*fraction)
                    key[i][j] = colors[2];
                else if(mean[i][j] < 4*fraction)
                    key[i][j] = colors[3];
                else if(mean[i][j] < 5*fraction)
                    key[i][j] = colors[4];
                else
                    key[i][j] = colors[5];
            }
        }
        
        return key;
    }
}
