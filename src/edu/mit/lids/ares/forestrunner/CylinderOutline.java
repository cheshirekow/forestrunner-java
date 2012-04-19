/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.mit.lids.ares.forestrunner;

import com.jme3.math.FastMath;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.scene.shape.Line;
import static com.jme3.util.BufferUtils.*;
import java.nio.FloatBuffer;

/**
 * A simple cylinder, defined by it's height and radius.
 * (Ported to jME3)
 *
 * @author Mark Powell
 * @version $Revision: 4131 $, $Date: 2009-03-19 16:15:28 -0400 (Thu, 19 Mar 2009) $
 */
public class CylinderOutline extends Line 
{
    private int radialSamples;

    private float radius;
    private float height;

    /**
     * Default constructor for serialization only. Do not use.
     */
    public CylinderOutline() {
    }

    /**
     * Creates a new Cylinder. By default its center is the origin. Usually, a
     * higher sample number creates a better looking cylinder, but at the cost
     * of more vertex information.
     *
     * @param radialSamples
     *            Number of triangle samples along the radial.
     * @param radius
     *            The radius of the cylinder.
     * @param height
     *            The cylinder's height.
     */
    public CylinderOutline(int radialSamples, float radius, float height) 
    {
        super();
        updateGeometry(radialSamples, radius, height);
    }

    /**
     * @return Returns the height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * @return number of samples around cylinder
     */
    public int getRadialSamples() {
        return radialSamples;
    }

    /**
     * @return Returns the radius.
     */
    public float getRadius() {
        return radius;
    }

    public void updateGeometry(float radius)
    {
        updateGeometry(radialSamples,radius,height);
    }
    
    /**
     * Rebuilds the cylinder based on a new set of parameters.
     *
     * @param axisSamples the number of samples along the axis.
     * @param radialSamples the number of samples around the radial.
     * @param radius the radius of the bottom of the cylinder.
     * @param radius2 the radius of the top of the cylinder.
     * @param height the cylinder's height.
     * @param closed should the cylinder have top and bottom surfaces.
     * @param inverted is the cylinder is meant to be viewed from the inside.
     */
    public void updateGeometry(int radialSamples, float radius, float height) 
    {
        setMode(Mode.Lines);
        
        this.radialSamples  = radialSamples;
        this.radius         = radius;
        this.height         = height;

//        VertexBuffer pvb = getBuffer(Type.Position);
//        VertexBuffer nvb = getBuffer(Type.Normal);
//        VertexBuffer tvb = getBuffer(Type.TexCoord);

        // Vertices
        int vertCount = 2 * (radialSamples ) ;
        int lineCount = 2 * (radialSamples );

        setBuffer(Type.Position, 3, createVector3Buffer(getFloatBuffer(Type.Position), vertCount));

        // Index buffer
        setBuffer(Type.Index, 2, createShortBuffer(getShortBuffer(Type.Index), 2 * lineCount));

        // generate geometry
        float inverseRadial = 1.0f / radialSamples;
        float halfHeight    = 0.5f * height;

        // Generate points on the unit circle to be used in computing the mesh
        // points on a cylinder slice.
        float[] sin = new float[radialSamples + 1];
        float[] cos = new float[radialSamples + 1];

        for (int radialCount = 0; radialCount < radialSamples; radialCount++) 
        {
            float angle = FastMath.TWO_PI * inverseRadial * radialCount;
            cos[radialCount] = FastMath.cos(angle);
            sin[radialCount] = FastMath.sin(angle);
        }
        
        sin[radialSamples] = sin[0];
        cos[radialSamples] = cos[0];

        FloatBuffer pb = getFloatBuffer(Type.Position);
        IndexBuffer ib = getIndexBuffer();
        int idx = 0;
        
        // bottom
        // -----------------------------------------------
        for (int radialCount = 0; radialCount < radialSamples; radialCount++) 
        {
            pb  .put( radius*cos[radialCount] )
                .put( radius*sin[radialCount] )
                .put( -halfHeight );
            ib  .put( idx++, radialCount );
            ib  .put( idx++, radialCount+1 );
        }
        ib.put(idx-1,0);
        
        int offset = radialSamples;
        // top
        // -----------------------------------------------
        for (int radialCount = 0; radialCount < radialSamples; radialCount++) 
        {
            pb  .put( radius*cos[radialCount] )
                .put( radius*sin[radialCount] )
                .put( halfHeight );
            ib  .put( idx++, offset + radialCount );
            ib  .put( idx++, offset + radialCount+1 );
        }
        ib.put(idx-1, offset);
        
        updateBound();
    }



}
