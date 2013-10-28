/*
 * Copyright (c) 2013 Triforce
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

import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;

/**
 * Represents a camera in a 3D scene. Handles all the movement and rotation of
 * the camera, as well as calculating the view and projection matrices for
 * OpenGL.
 * 
 * @author Daniel
 */
public class PerspectiveCamera extends AbstractCamera
{

    /** Vertical field-of-view. */
    public float fovY;
    /** Near clipping plane. */
    public float near;
    /** Far clipping plane. */
    public float far;

    /**
     * Creates a new camera object with the given initial properties.
     * @param eye Camera location.
     * @param at Point that the camera is looking at.
     * @param up Up direction from the camera.
     * @param fovY Vertical field-of-view.
     * @param near Near clipping plane.
     * @param far Far clipping plane.
     */
    public PerspectiveCamera(Vec3 eye, Vec3 at, Vec3 up, float fovY, float near, float far)
    {
        super(eye, at, up);
        this.fovY = fovY;
        this.near = near;
        this.far = far;
    }
    
    @Override
    public void updateProjection(float w, float h)
    {
        float aspect = (float) w / (float) h;
        projection = Matrices.perspective(fovY, aspect, near, far);
    }
}
