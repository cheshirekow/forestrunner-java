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
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Cylinder;

public class FloorPatch extends Node
{
    static ArrayList<ColorRGBA>  s_colors;
    
    static
    {
        s_colors = new ArrayList<ColorRGBA>();
        s_colors.add( new ColorRGBA(1.0f,0f,0f,1f) );   // red
        //s_colors.add( new ColorRGBA(0f,1.0f,0f,1f) );   // green
        //s_colors.add( new ColorRGBA(0f,0f,1.0f,1f) );   // blue
        s_colors.add( new ColorRGBA(1.0f,1.0f,0f,1f) ); // yellow;
    }
    
    List<Geometry>      m_trees;
    float               m_width;
    float               m_height;
    Geometry            m_floor;
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
    
    public FloorPatch(String name, float width, float height, AssetManager assetManager)
    {
        super(name);
        m_trees     = new LinkedList<Geometry>();
        m_width     = width;
        m_height    = height;
        
        Grid        grid    = new Grid( (int)(height), (int)(width), 1f);
        Geometry    geometry= new Geometry("wireframe grid", grid );
        Material    material= new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", ColorRGBA.Black);
        geometry.setMaterial(material);
        m_floor = geometry;
        m_floor.setShadowMode(ShadowMode.Off);
    }
    
    public void fullRegenerate(AssetManager assetManager, float density, float radius)
    {
        m_trees.clear();
        regenerate(assetManager,density,radius);
    }
    
    public void regenerate(AssetManager assetManager, float density, float radius)
    {
        m_numTrees    = getPoisson(density);
        
        // if we don't have enough trees in the queue, then generate some more
        while(m_trees.size() < m_numTrees)
        {
            int iColor      = (int) (Math.random()*(double)s_colors.size() );
            
            Cylinder cylinder   = new Cylinder(25,25,radius,0.5f,true,false);
            Geometry geometry   = new Geometry("cylinder", cylinder);
            // does not work in mac osx lion
            /*
            Material material   = assetManager.loadMaterial("Materials/LightBlow/Toon_System/Toon_Base_Specular.j3m");
            material.setBoolean("UseMaterialColors",true);      // Set some parameters, e.g. blue.
            material.setColor("Ambient", s_colors.get(iColor));       // ... color of this object
            material.setColor("Diffuse", s_colors.get(iColor));
            */
            // works in mac but is ugly
            /*
            Material    material= new Material(assetManager,
                    "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", s_colors.get(iColor));
            */
            Material    material= new Material(assetManager,
                    "Common/MatDefs/Light/Lighting.j3md");
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Diffuse", s_colors.get(iColor));
            material.setColor("Ambient", s_colors.get(iColor));
            material.setColor("Specular", ColorRGBA.White);
            material.setFloat("Shininess", 1.1f);
            geometry.setMaterial(material);
            m_trees.add(geometry);
        }
        
        // clear out children
        detachAllChildren();
        //attachChild(m_floor);
        
        // add as many children as was sampled
        for(int i=0; i < m_numTrees; i++)
        {
            assert( i < m_trees.size() );
            attachChild(m_trees.get(i));
            
            // translate it to some point, uniformly distributed
            float x = (float)Math.random()*m_width;
            float y = (float)Math.random()*m_height;
            
            float cosx  = 0;
            float sinx  = 1;
            
            Matrix3f rotation = new Matrix3f(   1f,     0f,     0f,
                                                0f,     cosx,   sinx,
                                                0f,     -sinx,  cosx);
            
            // random height eliminates jittering in image of overlapping
            // cylinders
            m_trees.get(i).setLocalTranslation(x, 
                                0.25f + (float)(Math.random()*0.001), y);
            m_trees.get(i).setLocalRotation(rotation);
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
