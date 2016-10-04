package com.test3d.tmeyer.test3d;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;

import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;

import org.rajawali3d.materials.textures.CubeMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;

import javax.microedition.khronos.opengles.GL10;

public class defaultRenderer extends Renderer implements View.OnTouchListener, OnObjectPickedListener, ScaleGestureDetector.OnScaleGestureListener {

    private Context context;
    private PointLight pl;
    private DirectionalLight dl;
    private ObjectColorPicker mPicker;
    private Object3D mSelectedObject;
    private Object3D nullObject;
    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;
    private RotateOnAxisAnimation mCameraAnim;


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

    public defaultRenderer(Context context){
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    public void initScene(){

        getCurrentCamera().setY(1.3);
        getCurrentCamera().setX(-1.5);
        getCurrentCamera().setLookAt(0,0,0);
        getCurrentCamera().setZ(3.3);

        pl = new PointLight();
        pl.setPosition(-4,4,3);
        pl.setColor(1.0f, 1.0f, 1.0f);
        pl.setPower(2);
        getCurrentScene().addLight(pl);

        dl = new DirectionalLight();
        dl.setPosition(4,5,-3);
        dl.setColor(1.0f, 1.0f, 1.0f);
        dl.setPower(3);
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

        LoaderOBJ wallsLoader = new LoaderOBJ(this, R.raw.walls_obj);
        LoaderOBJ thomasLoader = new LoaderOBJ(this, R.raw.thomas_obj);

        try{

            Material m1 = new Material();
            Material m2 = new Material();

            Texture t1 = new Texture("walls", R.drawable.walls);

            int[] skytextures = new int[]{R.drawable.negx,R.drawable.posx,
                                          R.drawable.negy,R.drawable.posy,
                                          R.drawable.negz,R.drawable.posz};

            CubeMapTexture t3 = new CubeMapTexture("sky", skytextures);
            t3.isEnvironmentTexture(true);

            Texture t2 = new Texture("tmx", R.drawable.thomas);

            m1.addTexture(t1);
            //m2.addTexture(t2);
            m2.addTexture(t3);

            m1.setColorInfluence(0);
            m2.setColorInfluence(0);

            wallsLoader.parse();
            thomasLoader.parse();

            Object3D cube = wallsLoader.getParsedObject();
            Object3D tmx  = thomasLoader.getParsedObject();

            cube.setMaterial(m1);
            tmx.setMaterial(m2);

            cube.setScale(1);
            cube.setTransparent(false);
            cube.setDoubleSided(true);

            tmx.setScale(1);
            tmx.setTransparent(false);
            tmx.setDoubleSided(true);

            //mPicker.registerObject(tmx);

            nullObject = new Object3D();
            Material m = new Material();
            nullObject.setMaterial(m);
            nullObject.setX(nullObject.getX()-.3);

            nullObject.addChild(cube);
            nullObject.addChild(tmx);

            getCurrentScene().addChild(nullObject);

            mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 40);
            mCameraAnim.setDurationMilliseconds(14500);
            mCameraAnim.setRepeatMode(Animation.RepeatMode.REVERSE_INFINITE);
            mCameraAnim.setTransformable3D(nullObject);

            getCurrentScene().registerAnimation(mCameraAnim);

            mCameraAnim.play();

        }catch(Exception e){
            e.printStackTrace();
        }
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

    public void onObjectPicked(Object3D object) {
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

        mSelectedObject.setX(mNewObjPos.x*.5);
        mSelectedObject.setY(mNewObjPos.y*.5);

        getCurrentCamera().setX(mNewObjPos.x*.25);
        getCurrentCamera().setY(-mNewObjPos.y*.25);
    }

    public void stopMovingSelectedObject() {
        mSelectedObject = null;
    }

    @Override
    public void onRender(final long elapsedTime, final double deltaTime) {
        super.onRender(elapsedTime, deltaTime);

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
