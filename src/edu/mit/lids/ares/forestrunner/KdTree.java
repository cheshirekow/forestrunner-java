package edu.mit.lids.ares.forestrunner;

import com.jme3.math.Vector2f;

/**
 *  A KD-Tree for efficient nearest neighbor searching, allows us to find the
 *  nearest cylinder for collision checking
 *  
 *  @author Josh Bialkowski <jbialk@mit.edu>
 *
 */
public class KdTree
{
    public class Rect
    {
        Vector2f    minExt;
        Vector2f    maxExt;
        
        Rect()
        {
            minExt = new Vector2f();
            maxExt = new Vector2f();
        }
        
        void makeInfinite()
        {
            minExt.set(-1000000f,-1000000f);
            maxExt.set(1000000f,1000000f);
        }
        
        float dist2( Vector2f v )
        {
            float dist2  = 0;
            float dist2i = 0;
            
            if( v.x < minExt.x )
                dist2i = minExt.x - v.x;
            else if( v.x > maxExt.x )
                dist2i = maxExt.x - v.x;
            else
                dist2i = 0;
            
            dist2  += dist2i*dist2i;
            
            if( v.y < minExt.y )
                dist2i = minExt.y - v.y;
            else if( v.y > maxExt.y )
                dist2i = maxExt.y - v.y;
            else
                dist2i = 0;
            
            dist2  += dist2i*dist2i;
            
            return dist2;
        }
    }
    
    public class NearestSearch
    {
        public Vector2f    x0;
        public Vector2f    x1;
        public float       dBest;
        public Node        nearest;
        public Rect        rect;
        
        NearestSearch()
        {
            rect = new Rect();
            x0   = null;
            x1   = null;
        }
        
        void reset( Vector2f x0_in, Vector2f x1_in )
        {
            x0      = x0_in;
            x1      = x1_in;
            dBest   = 1000000f;
            nearest = null;
        }
    }
    
    public class Node
    {
        Vector2f m_x;
        Node     m_left;
        Node     m_right;
        
        public Node(Vector2f x)
        {
            m_x     = x;
            m_left  = null;
            m_right = null;
        }
        
        public void findNearest( NearestSearch search )
        {
            float diff = 
        }
    }
}
