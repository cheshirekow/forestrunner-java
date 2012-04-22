package edu.mit.lids.ares.forestrunner;

import static com.jme3.util.BufferUtils.*;

import java.nio.FloatBuffer;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

public class GradientQuad extends Mesh
{
    
    public GradientQuad()
    {
        this(1f,1f,
                new ColorRGBA(1f,1f,1f,1f),
                new ColorRGBA(.2f,.2f,.2f,1f)  );
    }
    
    public GradientQuad(float width, float height)
    {
        this(width,height,
                new ColorRGBA(1f,1f,1f,1f),
                new ColorRGBA(.1f,.1f,.1f,1f)  );
    }
    
    public GradientQuad( float width, float height, 
                            ColorRGBA color1, ColorRGBA color2 )
    {
        setBuffer(Type.Position, 3, createFloatBuffer(3*4));
        setBuffer(Type.TexCoord, 2, createFloatBuffer(2*4));
        setBuffer(Type.Color,    4, createFloatBuffer(4*4));
        setBuffer(Type.Normal,   3, createFloatBuffer(3*4));
        
        int[] idx = { 1,0,2, 2,3,1 };
        setBuffer(Type.Index, 3, createIntBuffer(idx) );
        
        updateGeometry(width,height,color1,color2);
    }
    
    
    public void updateGeometry(float width, float height,
                                ColorRGBA color1, ColorRGBA color2)
    {
        ColorRGBA[] colors = new ColorRGBA[2];
        colors[0] = color1;
        colors[1] = color2;
        
        FloatBuffer pb = getFloatBuffer(Type.Position);
        FloatBuffer tb = getFloatBuffer(Type.TexCoord);
        FloatBuffer cb = getFloatBuffer(Type.Color);
        FloatBuffer nb = getFloatBuffer(Type.Normal);
        
        Vector3f    normal     = new Vector3f(0,1f,0);
        Vector3f    position   = new Vector3f(0,0,0);
        Vector2f    uv         = new Vector2f(0,0);
        ColorRGBA   color      = new ColorRGBA(0, 0, 0, 0);
        
        for(int i=0; i < 2; i++)
        {
            for(int j=0; j < 2; j++)
            {
                position.set(i*width,0,-j*height);
                uv.set(i,j);
                color = colors[j];
                
                pb.put(position.x).put(position.y).put(position.z);
                tb.put(uv.x).put(uv.y);
                cb.put(color.r).put(color.g).put(color.b).put(color.a);
                nb.put(normal.x).put(normal.y).put(normal.z);
            }
        }
    }
    
    
}
