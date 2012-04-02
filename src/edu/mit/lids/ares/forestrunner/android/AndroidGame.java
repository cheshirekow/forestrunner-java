package edu.mit.lids.ares.forestrunner.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.android.JmeAndroidSystem;

import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.SystemContext;
import edu.mit.lids.ares.forestrunner.Game.State;

public class AndroidGame 
    extends Game
    implements SensorEventListener
{
    private SensorManager   m_SensorManager;
    private Sensor          m_Accelerometer;
    
    protected   float       m_rotate;
    
    public AndroidGame()
        throws UnsupportedOperationException 
    {
        super(SystemContext.ANDROID);
        
        Context ctx = JmeAndroidSystem.getActivity();
        m_SensorManager =  
                (SensorManager) ctx.getSystemService( Context.SENSOR_SERVICE );
        m_Accelerometer = 
                m_SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (m_Accelerometer == null)
                throw new UnsupportedOperationException(
                                "Accelerometer is not available.");
        
        m_SensorManager.registerListener(this, m_Accelerometer, 
                SensorManager.SENSOR_DELAY_NORMAL);
        
        m_rotate = 0f;
        
        m_advancedSettings.put("mainGrid", true);
    }

    @Override
    protected void updateSpeed(float tpf)
    {
        m_xSpeed = m_rotate*m_xSpeedMax;
        
     // on a PC, we rotate the scene according to xspeed
        // on android, we do the opposite
        float angle = (float)(Math.PI / 9) * m_rotate;
        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle, new Vector3f(0f,0f,1f));
        m_patchRotate.setLocalRotation(q);
    }

    @Override
    protected void onCrash(float tpf)
    {
        m_nifty.gotoScreen("crash");
        m_state = State.CRASHED;
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float[] acc = event.values;
        // from a normal view of the phone, y axis goes out the top of the 
        // phone, x axis goes out the right, and z goes out towards the 
        // user
        // acc[0]   x acceleration 
        // acc[1]   y acceleration
        // acc[2]   z acceleration
        
        // I guess as a simple estimate of orientation we can look only at the
        // magnitude of the x,y 
        
        float   x = acc[0];
        float   y = acc[1];
        // atan2 returns -PI to PI so a is -1 to 1
        double  a = Math.atan2(y, x)/Math.PI;
                a*=4;
        m_rotate = (float)clamp(a,-1,1);
    }
    
    
    public static float clamp( float f, float low, float high )
    {
        return Math.max( Math.min(f, high), low);
    }
    
    public static double clamp( double f, double low, double high )
    {
        return Math.max( Math.min(f, high), low);
    }
    

    
    

}

