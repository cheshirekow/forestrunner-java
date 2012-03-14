/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mit.lids.ares.forestrunner.screens;

import java.util.Collection;

/**
 *
 * @author josh
 */
public class HighScoreResult 
{
    public String status;
    public String message;
    public Collection<HighScoreRow> global_scores;
    public Collection<HighScoreRow> user_scores;
}
