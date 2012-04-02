package edu.mit.lids.ares.forestrunner.android;


import android.hardware.SensorManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.jme3.system.android.JmeAndroidSystem;
import edu.mit.lids.ares.forestrunner.Game;
 
public class AccelerometerReader 
    implements SensorEventListener
{
    private SensorManager   m_SensorManager;
    private Sensor          m_Accelerometer;
    private Game            m_Game;
    
     /**
     * Sets up an AccelerometerReader. Checks if Accelerometer is available on
     * this device and throws UnsupportedOperationException if not .
     *
     * @throws UnsupportedOperationException
     *             if Accelerometer is not available on this device.
     */
    public AccelerometerReader(Game game)
                    throws UnsupportedOperationException 
    {
        m_Game          = game;
      
        
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
        double  a = (3*Math.PI/2) - Math.atan2(y, x);
        double  s = clamp(a / (Math.PI/4), -1,1);
        
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


