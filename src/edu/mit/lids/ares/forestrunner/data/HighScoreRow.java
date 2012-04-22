package edu.mit.lids.ares.forestrunner.data;

import java.util.Date;

public class HighScoreRow
    implements 
        Comparable<HighScoreRow>
{
    public String user_nick;
    public Date   date;
    public float  score;
    
    public HighScoreRow()
    {
        
    }

    @Override
    public int compareTo(HighScoreRow o)
    {
        if( score < o.score )
            return -1;
        else if( score > o.score )
            return 1;
        else
            return date.compareTo(o.date);
    }
}
