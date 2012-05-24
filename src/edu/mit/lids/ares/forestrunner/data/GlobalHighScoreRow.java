package edu.mit.lids.ares.forestrunner.data;

import java.util.Date;

public class GlobalHighScoreRow
    implements 
        Comparable<GlobalHighScoreRow>
{
    public boolean  isCurrent;
    public String   nick;
    public Date     date;
    public float    score;
    
    public GlobalHighScoreRow()
    {
        
    }

    @Override
    public int compareTo(GlobalHighScoreRow o)
    {
        if( score < o.score )
            return -1;
        else if( score > o.score )
            return 1;
        else
            return date.compareTo(o.date);
    }
}
