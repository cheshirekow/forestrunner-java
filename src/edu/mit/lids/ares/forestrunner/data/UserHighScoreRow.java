package edu.mit.lids.ares.forestrunner.data;

import java.util.Date;

public class UserHighScoreRow
    implements 
        Comparable<UserHighScoreRow>
{
    public boolean  isCurrent;
    public int      id;
    public Date     date;
    public float    score;
    
    public UserHighScoreRow()
    {
        
    }

    @Override
    public int compareTo(UserHighScoreRow o)
    {
        if( score < o.score )
            return -1;
        else if( score > o.score )
            return 1;
        else
            return date.compareTo(o.date);
    }
}
