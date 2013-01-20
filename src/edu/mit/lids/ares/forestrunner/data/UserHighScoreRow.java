package edu.mit.lids.ares.forestrunner.data;

public class UserHighScoreRow
    implements 
        Comparable<UserHighScoreRow>
{
    public boolean  isCurrent;
    public long     id;
    public long     date;
    public double   score;
    
    public UserHighScoreRow()
    {
        isCurrent = false;
    }
    
    public void copyFrom( UserHighScoreRow o )
    {
        isCurrent = o.isCurrent;
        id        = o.id;
        date      = o.date;
        score     = o.score;
    }

    @Override
    public int compareTo(UserHighScoreRow o)
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
