/*
 * Copyright (c) 2013 Triforce - in association with the University of Pretoria and Epi-Use <Advance/>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
 package afk.gfx;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a graphical resource that can be loaded, unloaded, and attached to
 * a graphics entity.
 * @author Daniel
 */
public abstract class Resource
{
    // TODO: types to add: imposters, sounds (although not necessarily part of the gfx engine)
    /**
     * Resource types.
     */
    public static final int WAVEFRONT_MESH = 0, PRIMITIVE_MESH = 1,
            HEIGHTMAP_MESH = 2, TEXTURE_2D = 3, TEXTURE_CUBE = 4, MATERIAL = 5,
            SHADER = 6, PARTICLE_PARAMETERS = 7;
    
    /** The number of resource types currently accounted for. */
    public static final int NUM_RESOURCE_TYPES = 8;
    
    /** The type of this resource. */
    protected int type;
    /** The name of this resource. */
    protected String name;
    
    /** True if the resource is loaded, false otherwise. */
    protected AtomicBoolean loaded = new AtomicBoolean(false);

    /**
     * Creates a new resource with the specified type and name.
     * @param type the resource type.
     * @param name the resource name.
     */
    public Resource(int type, String name)
    {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets the resource name.
     * @return the resource name.
     */
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return getName();
    }

    /**
     * Gets the resource type.
     * @return the resource type.
     */
    public int getType()
    {
        return type;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Resource)
        {
            Resource other = (Resource) obj;
            if (this.name == null)
            {
                if (other.name != null) return false;
            }
            else if (!this.name.equalsIgnoreCase(other.name)) return false;
            
            return other.type == this.type;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 37 * hash + this.type;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Checks the load status of the resource.
     * @return true if the resource is currently loaded in memory, false
     * otherwise.
     */
    public boolean isLoaded()
    {
        return loaded.get();
    }
    
}
