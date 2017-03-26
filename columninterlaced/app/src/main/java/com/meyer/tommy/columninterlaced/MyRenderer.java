package com.meyer.tommy.columninterlaced;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by tommy on 21.03.2017.
 */
import org.rajawali3d.Camera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Plane;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.renderer.RenderTarget;
import org.rajawali3d.scene.RajawaliScene;

import java.util.Collections;

public class MyRenderer extends RajawaliRenderer
{
    public Context context;
    private DirectionalLight directionalLight;
    public Sphere sphere;
    RenderTarget mRenderTarget1;
    RenderTarget mRenderTarget2;
    Camera mCamera1;
    Camera mCamera2;

    RajawaliScene sc1, sc2;

    int width, height;

    public MyRenderer(Context context){
        super(context);
        this.context = context;
        setFrameRate(60);
    }

        private void scene1(){
            sc1 = new RajawaliScene(this);

            replaceAndSwitchScene(getCurrentScene(),sc1);

            mCamera1 = getCurrentCamera(); //We will utilize the initial camera

            mCamera1.setPosition(-10, 0, 20);
            mCamera1.setFieldOfView(60);
            mCamera1.setFarPlane(50);

            sc1.replaceAndSwitchCamera(mCamera1, 0);
            sc1.addCamera(mCamera1);

            sceneContent();
        }

        private void scene2(){
            sc2 = new RajawaliScene(this);

            replaceAndSwitchScene(getCurrentScene(),sc2);

            mCamera2 = getCurrentCamera(); //We will utilize the initial camera
            mCamera2.setPosition(-10, 0, 20);
            mCamera2.setFieldOfView(60);
            mCamera2.setFarPlane(50);

            sc2.replaceAndSwitchCamera(mCamera2, 0);
            sc2.addCamera(mCamera2); //Add our second camera to the scene
            sceneContent();
        }

        public void scene3(){

            Plane planeLeft = new Plane();
            Plane planeRight = new Plane();
        }

        public void sceneContent(){
            directionalLight = new DirectionalLight(1f, .2f, -1.0f);
            directionalLight.setColor(1.0f, 1.0f, 1.0f);
            directionalLight.setPower(2);
            getCurrentScene().addLight(directionalLight);

            Material material = new Material();
            material.enableLighting(true);
            material.setDiffuseMethod(new DiffuseMethod.Lambert());
            material.setColor(0);

            Texture earthTexture = new Texture("Earth", R.drawable.map);
            try{
                material.addTexture(earthTexture);

            } catch (ATexture.TextureException error){
                Log.d("DEBUG", "TEXTURE ERROR");
            }

            sphere = new Sphere(1, 24, 24);
            sphere.setMaterial(material);
            getCurrentScene().addChild(sphere);
        }

        @Override
        protected void initScene() {
            scene1();
            scene2();

            addScene(sc1);
            addScene(sc2);

            if(width == -1 && height == -1) {
                width = getViewportWidth();
                height = getViewportHeight();
            }

            mRenderTarget1 = new RenderTarget("rt1" + hashCode(), width, height, 0, 0,
                    false, false, GLES20.GL_TEXTURE_2D, Bitmap.Config.ARGB_8888,
                    ATexture.FilterType.LINEAR, ATexture.WrapType.CLAMP);
            mRenderTarget2 = new RenderTarget("rt2" + hashCode(), width, height, 0, 0,
                    false, false, GLES20.GL_TEXTURE_2D, Bitmap.Config.ARGB_8888,
                    ATexture.FilterType.LINEAR, ATexture.WrapType.CLAMP);

            addRenderTarget(mRenderTarget1);
            addRenderTarget(mRenderTarget2);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        }

        @Override
        public void onRender(final long elapsedTime, final double deltaTime) {
            super.onRender(elapsedTime, deltaTime);
            switchScene(sc1);
            setRenderTarget(r1);
            render(deltaTime);

            switchScene(sc2);
            setRenderTarget(mRenderTarget1);
            render(deltaTime);

            sphere.rotate(Vector3.Axis.Y, 1.0);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {

        }
}
