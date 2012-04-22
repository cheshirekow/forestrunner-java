package edu.mit.lids.ares.forestrunner.data;

public class Triple
{
    private int m_a;
    private int m_b;
    private int m_c;
    
    public Triple (int a, int b, int c)
    {
        m_a = a;
        m_b = b;
        m_c = c;
    }
    
    /**
     *  we can override hashCode() because we know that each integer is
     *  between 0 and 10
     */
    public int hashCode()
    {
        return m_a + 10*( m_b + 10* m_c);
    }
}
