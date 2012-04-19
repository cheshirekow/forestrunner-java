package edu.mit.lids.ares.forestrunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;

public class FloorPatch extends Node
{
    static ArrayList<ColorRGBA> s_colors;
    static ArrayList<Material>  s_litMaterials;
    static ArrayList<Material>  s_unlitMaterials;
    static Material             s_blackMaterial;
    static Material             s_wireMaterial;
    static float                m_pad;
    
    static Mesh     s_cylinderMesh;
    static Mesh     s_outlineMesh;
    static Mesh     s_wireframeMesh;
    static Mesh     s_gridMesh;
    
    static float    s_width;
    static float    s_height;
    
    static Boolean  s_useGrid;
    static Boolean  s_useOutline;
    static Boolean  s_useLighting;
   
    static int      s_maxTrees;
    static Matrix3f s_rotation;
    
    static
    {
        s_colors            = new ArrayList<ColorRGBA>();
        s_litMaterials      = new ArrayList<Material>();
        s_unlitMaterials    = new ArrayList<Material>();
        
        s_colors.add( new ColorRGBA(1.0f,0f,0f,1f) );   // red
        //s_colors.add( new ColorRGBA(0f,1.0f,0f,1f) );   // green
        //s_colors.add( new ColorRGBA(0f,0f,1.0f,1f) );   // blue
        s_colors.add( new ColorRGBA(1.0f,1.0f,0f,1f) ); // yellow;
        
        float cosx  = 0;
        float sinx  = 1;
        
        s_rotation = new Matrix3f(  1f,     0f,     0f,
                                    0f,     cosx,   sinx,
                                    0f,     -sinx,  cosx);
        
        s_useGrid       = false;
        s_useOutline    = true;
        s_useLighting   = true;
        s_maxTrees      = 100;
        
        m_pad = 0.03f;
    }
    
    static void setDim( float width, float height )
    {
        s_width     = width;
        s_height    = height;
        
        s_gridMesh = new Grid( (int)(s_height), (int)(s_width), 1f);
    }
    
    static void setMeshes(
            Mesh cylinder,
            Mesh outline,
            Mesh wireframe )
    {
        s_cylinderMesh  = cylinder;
        s_outlineMesh   = outline;
        s_wireframeMesh = wireframe;
    }
    
    static void buildMaterialList( AssetManager mgr )
    {
        Material unlitMat   = new Material(mgr, 
                                "Common/MatDefs/Misc/Unshaded.j3md");
        Material litMat     = new Material(mgr, 
                                "Common/MatDefs/Light/Lighting.j3md");
        
        s_blackMaterial = unlitMat.clone();
        s_blackMaterial.setColor("Color", ColorRGBA.Black);
        
        s_wireMaterial  = unlitMat.clone();
        s_wireMaterial.setColor("Color", ColorRGBA.Black);
        s_wireMaterial.getAdditionalRenderState().setWireframe(true);
        
        for(int i=0; i < s_colors.size(); i++)
        {
            Material um         = unlitMat.clone();
            Material lm         = litMat.clone();
            ColorRGBA color     = s_colors.get(i);
            
            um.setColor("Color",color);
            lm.setBoolean("UseMaterialColors",true);
            lm.setColor("Ambient", color);
            lm.setColor("Diffuse", color);
            lm.setColor("Specular",ColorRGBA.White);
            lm.setFloat("Shininess", 1.1f);
            
            s_unlitMaterials.add(um);
            s_litMaterials.add(lm);
        }
    }
    
    static void setUseGrid( Boolean use )
    {
        s_useGrid = use;
    }
    
    static void setUseOutline( Boolean use )
    {
        s_useOutline = use;
    }
    
    static void setUseLighting( Boolean use )
    {
        s_useLighting = use;
    }
    
    List<Geometry>      m_trees;
    List<Geometry>      m_outlines;
    List<Geometry>      m_wireframes;
    Geometry            m_grid;
    int                 m_numTrees;

    public static int getPoisson(double lambda) 
    {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do 
        {
            k++;
            p *= Math.random();
        } while (p > L);

        return k - 1;
    }
    
    public FloorPatch(String name )
    {
        this(name,100);
    }
    
    public FloorPatch(String name, int maxObstacles)
    {
        super(name);
        m_trees     = new LinkedList<Geometry>();
        m_outlines  = new LinkedList<Geometry>();
        m_wireframes= new LinkedList<Geometry>();
        
        m_grid = new Geometry("wireframe grid", s_gridMesh );
        m_grid.setMaterial(s_wireMaterial);
        
        for( int i=0; i < maxObstacles; i++)
        {
            int iColor = (int) (Math.random()*(double)s_colors.size() );
            Geometry geometry;
            
            geometry = new Geometry("cylinder",s_cylinderMesh);
            geometry.setMaterial(s_unlitMaterials.get(iColor));
            geometry.setLocalRotation(s_rotation);
            m_trees.add(geometry);
            
            geometry = new Geometry("cylinderOutline",s_outlineMesh);
            geometry.setMaterial(s_blackMaterial);
            geometry.setLocalRotation(s_rotation);
            m_outlines.add(geometry);
            
            geometry = new Geometry("cylinderWireframe",s_wireframeMesh);
            geometry.setMaterial(s_wireMaterial);
            geometry.setLocalRotation(s_rotation);
            m_wireframes.add(geometry);
        }
    }
    
    public void shuffle(float density)
    {
        shuffle(density,false);
    }
    
    public void shuffle(float density, Boolean override)
    {
        if( override )
        {
            System.out.println("override shuffle");
            m_numTrees = 0;
        }
        else
            m_numTrees = Math.min(getPoisson(density), s_maxTrees);
        
        // set position for as many children as we sampled
        for(int i=0; i < m_numTrees; i++)
        {
            // translate it to some point, uniformly distributed
            float x = (float)Math.random()*s_width;
            float y = (float)Math.random()*s_height;
            
            // random height eliminates jittering in image of overlapping
            // cylinders
            float z = 0.25f + (float)(Math.random()*0.001);
            
            m_trees.get(i).setLocalTranslation(x, z, y);
            m_outlines.get(i).setLocalTranslation(x,z, y);
            m_wireframes.get(i).setLocalTranslation(x,z, y);
        }
        
        reattach();
    }
    
    public void reattach()
    {
        // clear out children
        detachAllChildren();
        if(s_useGrid)
            attachChild(m_grid);
        
        // attach as many children as was sampled
        for(int i=0; i < m_numTrees; i++)
        {
            attachChild(m_trees.get(i));
            
            if(s_useOutline)
            {
                attachChild(m_wireframes.get(i));
                attachChild(m_outlines.get(i));
            }
        }
    }
    
    Boolean collisionCheck( float x, float y, float r )
    {
        float r2    = r*r;
        //float minD2 = (float)1e16;

        //System.out.println("trees:");
        for(int i=0; i < m_numTrees; i++)
        {
            Vector3f t = m_trees.get(i).getWorldTranslation();
            //System.out.println("   " + t);
            float d2  = t.x*t.x + t.z*t.z;
            
            if(d2 < r2)
            {
                //System.out.println("Collision in check");
                return true;
            }
            
            //minD2 = Math.min(d2, minD2);
        }
        //System.out.println("min dist: " + Math.sqrt(minD2) + ", radius: " + r );
        
        return false;
    }
}
