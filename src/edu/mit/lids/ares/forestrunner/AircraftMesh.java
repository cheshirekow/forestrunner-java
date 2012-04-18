package edu.mit.lids.ares.forestrunner;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class AircraftMesh extends Mesh
{
    public AircraftMesh(float sideLen)
    {
        this(sideLen, false);
    }
    
    public AircraftMesh(float sideLen, Boolean inverted)
    {
        float y       = (float) Math.sqrt( sideLen*sideLen 
                                    - (sideLen/2f)*(sideLen/2f) );
        float x       = sideLen/2f;
        float height  = 0.1f;

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0f,0f,0f);
        vertices[1] = new Vector3f(-x,0,y);
        vertices[2] = new Vector3f(x,0,y);
        vertices[3] = new Vector3f(0,height,y);
        
        int[] indexes1  = { 0,2,1, 0,1,3, 1,2,3, 2,0,3 };
        int[] indexes2  = { 0,1,2, 0,3,1, 1,3,2, 2,3,0 };
        
        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices) );
        
        if(inverted)
            setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes2) );
        else
            setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes1) );
        
        updateBound();
    }
    
}
