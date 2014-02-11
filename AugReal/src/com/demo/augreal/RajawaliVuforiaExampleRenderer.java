package com.demo.augreal;

import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.animation.mesh.SkeletalAnimationSequence;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.math.Quaternion;
import rajawali.math.vector.Vector3;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.primitives.Cube;
import rajawali.primitives.Sphere;
import rajawali.util.RajLog;
import rajawali.vuforia.RajawaliVuforiaRenderer;
import android.content.Context;

public class RajawaliVuforiaExampleRenderer extends RajawaliVuforiaRenderer {
	private DirectionalLight mLight;

	private Object3D mAndroid;
	private RajawaliVuforiaExampleActivity activity;
	private float mTime=0;
	private Material androidMaterial;

	public RajawaliVuforiaExampleRenderer(Context context) {
		super(context);
		activity = (RajawaliVuforiaExampleActivity)context;
	}

	protected void initScene() {
		mLight = new DirectionalLight(.1f, 0, -1.0f);
		mLight.setColor(1.0f, 1.0f, 0.8f);
		mLight.setPower(1);
		
		getCurrentScene().addLight(mLight);
		
		try {
			
			mAndroid = new Cube(5);
			mAndroid.setScale(1);
			//mAndroid.setTransparent(false);
			//mAndroid.setBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			
			addChild(mAndroid);
			
			androidMaterial = new Material();
			
			int[] cubemaps = new int[6];
			cubemaps[0] = R.drawable.posx;
			cubemaps[1] = R.drawable.negx;
			cubemaps[2] = R.drawable.posy;
			cubemaps[3] = R.drawable.negy;
			cubemaps[4] = R.drawable.posz;
			cubemaps[5] = R.drawable.negz;
			CubeMapTexture texture = new CubeMapTexture("cubemaps", cubemaps);
			texture.isEnvironmentTexture(true);
			
			androidMaterial.addTexture(texture);
			androidMaterial.setSpecularMethod(new SpecularMethod.Phong());
			mAndroid.setMaterial(androidMaterial);
			androidMaterial.setColorInfluence(0.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void foundFrameMarker(int markerId, Vector3 position,
			Quaternion orientation) {
	}

	@Override
	protected void foundImageMarker(String trackableName, Vector3 position,
			Quaternion orientation) {
		
		if(trackableName.equals("Mu5ic_Code"))
		{
			mAndroid.setVisible(true);
			mAndroid.setPosition(position);
			mAndroid.setOrientation(orientation);
		}
		// -- also handle cylinder targets here
		// -- also handle multi-targets here
	}

	@Override
	public void noFrameMarkersFound() {
	}

	public void onDrawFrame(GL10 glUnused) {
		
		mAndroid.setVisible(false);
		mTime += .01f;
		//androidMaterial.setTime(mTime);
		mAndroid.setRotX(mAndroid.getRotX() + 1);
		mAndroid.setRotY(mAndroid.getRotY() + 1);
		super.onDrawFrame(glUnused);
	}
}
