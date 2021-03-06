package edu.mit.lids.ares.forestrunner.data;

public class GlobalHighScoreRow
    implements 
        Comparable<GlobalHighScoreRow>
{
    public boolean  isCurrent;
    public long     id;
    public String   nick;
    public long     date;
    public double   score;
    
    public GlobalHighScoreRow()
    {
        isCurrent = false;
    }
    
    public void copyFrom(GlobalHighScoreRow o)
    {
        isCurrent = o.isCurrent;
        id        = o.id;
        nick      = o.nick;
        date      = o.date;
        score     = o.score;
    }

    @Override
    public int compareTo(GlobalHighScoreRow o)
    {
        if( score < o.score )
            return -1;
        else if( score > o.score )
            return 1;
        else
        {
            if( date < o.date )
                return -1;
            else if( date > o.date )
                return 1;
            else
                return 0;
        }
    }
}
