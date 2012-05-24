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
