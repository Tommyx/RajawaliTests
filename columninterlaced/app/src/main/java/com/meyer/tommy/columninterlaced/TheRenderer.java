package com.meyer.tommy.columninterlaced;

import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;

/**
 * Created by tmeyer on 27.03.2017.
 */

public class TheRenderer extends SideBySideRenderer {

    public TheRenderer(Context context) {
        super(context);
    }

    Sphere s;

    @Override
    public void initScene(){

        DirectionalLight directionalLight = new DirectionalLight(1f, .2f, -1.0f);
        directionalLight.setColor(1.0f, 1.0f, 1.0f);
        directionalLight.setPower(2);
        getCurrentScene().addLight(directionalLight);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        material.setColor(0);

        Texture earthTexture = new Texture("Earth", R.drawable.map);
        try {
            material.addTexture(earthTexture);

        } catch (ATexture.TextureException error) {
            return;
        }

        s = new Sphere(1, 24, 24);
        s.setMaterial(material);
        getCurrentScene().addChild(s);

        getCurrentCamera().setZ(4.2f);

        super.initScene();
    }


    @Override
    protected void onRender(long ellapsedTime, double deltaTime) {
        super.onRender(ellapsedTime, deltaTime);
        s.rotate(Vector3.Axis.Y, 1.0);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }
}
