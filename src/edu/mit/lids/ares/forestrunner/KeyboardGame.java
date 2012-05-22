package edu.mit.lids.ares.forestrunner;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public abstract class KeyboardGame extends Game
{
    protected Boolean m_leftDown;
    protected Boolean m_rightDown;
    
    protected ActionListener dodgeListener = new ActionListener() 
    {
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
            // the pause action is only meaning full if the game is active
            if (name.equals("Left") ) 
            {
                if(keyPressed)
                    m_leftDown = true;
                else
                    m_leftDown = false;
            }
                
            else // (name.equals("Right") )
            {
                if(keyPressed)
                    m_rightDown = true;
                else
                    m_rightDown = false;
            }
        }
    };
    
    protected ActionListener pauseListener = new ActionListener() 
    {
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
            // the pause action is only meaning full if the game is active
            if (name.equals("Pause") && !keyPressed) 
            {
                if( m_nifty.getCurrentScreen().getScreenId().compareTo("empty")==0 )
                {
                    m_state = State.PAUSED;
                    m_nifty.gotoScreen("game");
                }
            }
                
            if (name.equals("Crash") && !keyPressed )
            {
                m_nifty.gotoScreen("crash");
                m_state = State.CRASHED;
            }
        }
    };
    
    public KeyboardGame( SystemContext ctx )
    {
        super(ctx);
        
        m_leftDown  = false;
        m_rightDown = false;
    }
    
    /**
     * Map hotkeys.
     */
    protected void initKeys() 
    {
        //add pause keys which bring up the pause menu
        inputManager.addMapping("Pause",        new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("Pause",        new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Crash",        new KeyTrigger(KeyInput.KEY_Q));
        
        inputManager.addMapping("Left",         new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Left",         new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",        new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Right",        new KeyTrigger(KeyInput.KEY_RIGHT));
         
        //add the names to the action listener
        inputManager.addListener(pauseListener,new String[]{"Pause", "Crash"});
        inputManager.addListener(dodgeListener,new String[]{"Left", "Right"});
    }
    
    protected void updateSpeed(float tpf)
    {
        // update the xspeed if necessary
        if(m_leftDown || m_rightDown)
        {
            if(m_leftDown)
                m_xSpeed -= m_xAccel*tpf;
            if(m_rightDown)
                m_xSpeed += m_xAccel*tpf;
        }
        else
        {
            float sign = Math.signum(m_xSpeed); 
            m_xSpeed -= sign*m_xAccel*tpf;
            
            // avoid overshoot
            if( sign != Math.signum(m_xSpeed) )
                m_xSpeed = 0;
        }
        
        m_xSpeed = Math.min(m_xSpeed, m_xSpeedMax);
        m_xSpeed = Math.max(m_xSpeed, -m_xSpeedMax);
        
        // on a PC, we rotate the scene according to xspeed
        // on android, we do the opposite
        float angle = (float)(Math.PI / 12) * m_xSpeed / m_xSpeedMax;
        Quaternion q = new Quaternion();
        
        if(m_worldRotate)
        {
            q.fromAngleAxis(angle, new Vector3f(0f,0f,1f));
            m_patchRotate.setLocalRotation(q);
        }
        else
        {
            q.fromAngleAxis(-angle, new Vector3f(0f,0f,1f));
            m_acRotate.setLocalRotation(q);
        }
    }
    
    @Override
    public void initialize()
    {
        super.initialize();
        initKeys();
    }
    
    @Override
    protected void onCrash(float tpf)
    {
        pauseListener.onAction("Crash", false, tpf);
    }
    
}
