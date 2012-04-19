package edu.mit.lids.ares.forestrunner.data;

import java.util.Collection;

import edu.mit.lids.ares.forestrunner.AdvancedSettings;
import edu.mit.lids.ares.forestrunner.screens.HighScoreRow;

public interface Store
{
    /**
     * Performs different initialization stuff depending on which game is
     * being run:
     *      AndroidGame, DesktopGame:
     *      <ul>
     *          <li> create database connection </li>
     *          <li> create database if it doesn't exist </li>
     *          <li> get settings from database </li>
     *          <ul>
     *              <li> advanced render settings </li>
     *              <li> last difficulty settings </li>
     *              <li> user hash </li>
     *              <li> user name </li>
     *          </ul>
     *          <li> attempt to get userhash from server if none in database </li>
     *      </ul>
     *          
     *      Applet:
     *      <ul>
     *          <li> get settings from applet parameters 
     *                  (delivered by php, stored as a cookie) </li>
     *          <ul>
     *              <li> advanced render settings </li>
     *              <li> last difficulty settings </li>
     *              <li> user hash </li>
     *              <li> user name </li>
     *          </ul>
     *      </ul>
     */
    abstract void init();

    /**
     *  @return the advanced settings that were stored or communicated or 
     *          the defaults if none
     */
    abstract AdvancedSettings getAdvancedSettings();
    
    /**
     * 
     *  @return return the current value of the user's nickname
     */
    String getNick();
    
    /**
     *  @return return the current list of the user's high scores for the
     *          game just played
     */
    Collection<HighScoreRow>    getUserHighScores();
    
    /**
     *  @return return the current list of the global high scores for the
     *          game just played
     */
    Collection<HighScoreRow>    getGlobalHighScores();

    /**
     *  update the user's nickname
     */
    void  setNick(String nick);
    
    /**
     *  record a new score for the user, also updates internal caches of
     *  user high scores (Android and Desktop games)
     *  
     *  @param speed
     *  @param density
     *  @param radius
     *  @param score
     */
    void  writeScore( int speed, int density, int radius, float score );  
    
    /**
     *  attempts to write local updates to the server (note that on desktop
     *  and android games these may be quite large).
     */
    void flush();
}
