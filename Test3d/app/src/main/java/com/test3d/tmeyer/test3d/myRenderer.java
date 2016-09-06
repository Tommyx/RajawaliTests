package com.test3d.tmeyer.test3d;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.ISpecularMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.methods.SpecularMethod.SpecularShaderVar;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.SpecularMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.RajLog;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tmeyer on 06.09.2016.
 */

public class myRenderer extends Renderer implements View.OnTouchListener, OnObjectPickedListener, ScaleGestureDetector.OnScaleGestureListener {

    private Context context;
    private PointLight dl;
    private Sphere s;
    private Sphere c;
    private Cube lightCube;
    private ObjectColorPicker mPicker;
    private Object3D mSelectedObject;
    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    ScaleGestureDetector mScaleDetector =
            new ScaleGestureDetector(getContext(), this);

    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    }

    @Override
    public void onTouchEvent(MotionEvent m){

    }

    @Override
    public boolean onTouch(View v,  MotionEvent event) {
        if (event.getPointerCount()> 1) {
            if (mScaleDetector.onTouchEvent(event))
                return true;
        }else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getObjectAt(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveSelectedObject(event.getX(),
                            event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    stopMovingSelectedObject();
                    break;
            }
        }
        return true;
    }

    public myRenderer(Context context){
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene(){

        dl = new PointLight();
        dl.setPosition(-4,4,3);
        dl.setColor(1.0f, 1.0f, 1.0f);
        dl.setPower(2);
        getCurrentScene().addLight(dl);



        mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();

        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setSpecularMethod(new SpecularMethod.Phong(0xeeeeee, 200));
        material.setColor(0);

        Material material2 = new Material();
        material2.enableLighting(true);
        material2.setDiffuseMethod(new DiffuseMethod.Lambert());
        material2.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.earthtruecolor_nasa_big);
        Texture cloudTexture = new Texture("Cloud", R.drawable.earth_clouds);
        Texture starsTexture = new Texture("Stars", R.drawable.stars);
        SpecularMapTexture specuTexture = new SpecularMapTexture("Specu", R.drawable.earth_clouds);
        NormalMapTexture normalTexture = new NormalMapTexture("Specu", R.drawable.earth_bump);

        Material pm = new Material();
        pm.setColor(0);
        starsTexture.setRepeat(7,7);

        try{
            material.addTexture(specuTexture);
            material.addTexture(earthTexture);
            //material.addTexture(normalTexture);

            pm.addTexture(starsTexture);
            material2.addTexture(cloudTexture);

        } catch (ATexture.TextureException error){
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        s = new Sphere(1, 24, 24);
        s.setMaterial(material);

        Sphere p = new Sphere(10,100,100);
        p.setPosition(0,0,0);
        p.setMaterial(pm);
        p.setDoubleSided(true);
        getCurrentScene().addChild(p);

        getCurrentScene().addChild(s);

        Material emptyMat = new Material();
        lightCube = new Cube(2.1f);
        lightCube.setPosition(0,0,0);
        lightCube.setMaterial(emptyMat);
        lightCube.setTransparent(true);
        emptyMat.setColor(1);
        mPicker.registerObject(lightCube);
        getCurrentScene().addChild(lightCube);

        c = new Sphere(1.01f, 24, 24);
        c.setMaterial(material2);
        c.setTransparent(true);
        getCurrentScene().addChild(c);

        getCurrentCamera().setZ(3.2f);

     }

    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
    }

    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }

    public void onObjectPicked(@NonNull Object3D object) {
        mSelectedObject = object;
    }

    @Override
    public void onNoObjectPicked() {

    }

    public void moveSelectedObject(float x, float y) {
        if (mSelectedObject == null)
            return;

        //
        // -- unproject the screen coordinate (2D) to the camera's near plane
        //

        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

        //
        // -- unproject the screen coordinate (2D) to the camera's far plane
        //

        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

        //
        // -- transform 4D coordinates (x, y, z, w) to 3D (x, y, z) by dividing
        // each coordinate (x, y, z) by w.
        //

        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

        //
        // -- now get the coordinates for the selected object
        //

        double factor = (Math.abs(mSelectedObject.getZ()) + mNearPos.z)
                / (getCurrentCamera().getFarPlane() - getCurrentCamera()
                .getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.add(mNearPos);

        mSelectedObject.setX(mNewObjPos.x);
        mSelectedObject.setY(mNewObjPos.y);
    }

    public void stopMovingSelectedObject() {
        mSelectedObject = null;
    }


    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);
        s.rotate(Vector3.Axis.Y, .040);
        c.rotate(Vector3.Axis.Y, .030);
        dl.setPosition(lightCube.getPosition().x, lightCube.getPosition().y, lightCube.getPosition().z+2);

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float cZoom = detector.getCurrentSpan();
        float pZoom = detector.getPreviousSpan();
        Vector3 pos = getCurrentCamera().getPosition();
        Log.d("cZoom", Float.toString(cZoom));
        Log.d("pZoom", Float.toString(pZoom));

        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        float cZoom = detector.getCurrentSpan();
        float pZoom = detector.getPreviousSpan();
        Vector3 pos = getCurrentCamera().getPosition();
        Log.d("cZoom", Float.toString(cZoom));
        Log.d("pZoom", Float.toString(pZoom));
        getCurrentCamera().setPosition(pos.x, pos.y, (cZoom / 300));

        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.d("Scale", detector.toString());
    }
}
