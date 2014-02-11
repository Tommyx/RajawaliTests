package com.rendertargettest;

import java.io.Console;
import java.util.Currency;

import javax.microedition.khronos.opengles.GL10;


import rajawali.parser.fbx.LoaderFBX;

import rajawali.animation.Animation3D;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.util.Log;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture.FilterType;
import rajawali.materials.textures.ATexture.WrapType;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.RenderTarget;
import rajawali.scene.RajawaliScene;

public class Renderer extends RajawaliRenderer {

	private Material mMaterial, PlaneMat;
	private float time = 0;
	private DirectionalLight mLight; 
	Object3D cube, sphere, plane;
	RajawaliScene sc1, sc2;
	RenderTarget r1, r2, r3;
	private RajawaliScene mUserScene;
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
	private void scene1(){
		
		sc1 = new RajawaliScene(this);
		Camera cam = new Camera();
		cam.setPosition(0,2,5);
		cam.setLookAt(0,0,0);
		sc1.addCamera(cam);
		sc1.switchCamera(cam);
		sc1.addLight(mLight);
		
		try {
			cube = new Cube(1);
			
			Material cubeMat = new Material();
			cubeMat.enableLighting(true);
			cubeMat.setDiffuseMethod(new DiffuseMethod.Lambert());
			cubeMat.addTexture(new Texture("cubeMat", R.drawable.posy));
			cubeMat.setColorInfluence(0);
			cube.setMaterial(cubeMat);
			sc1.addChild(cube);
			
		}catch(TextureException e){
			e.printStackTrace();
		}
		
	}

	private void scene2(){
		
		sc2 = new RajawaliScene(this);
		Camera cam = new Camera();
		cam.setPosition(0,2,5);
		cam.setLookAt(0,0,0);
		sc2.switchCamera(cam);
		sc2.addCamera(cam);
		sc2.addLight(mLight);
		
		try {
			sphere = new Sphere(1,10,10);
			
			Material sphererMat = new Material();
			sphererMat.enableLighting(true);
			sphererMat.setDiffuseMethod(new DiffuseMethod.Lambert());
			sphererMat.addTexture(new Texture("sphereMat", R.drawable.negy));
			sphererMat.setColorInfluence(0);
			sphere.setMaterial(sphererMat);
			sc2.addChild(sphere);
			
		}catch(TextureException e){
			e.printStackTrace();
		}
	}
	
	protected void initScene() {
		
		mLight = new DirectionalLight(0, 0, 0f);
		mLight.setPosition(0,4,4);
		mLight.setColor(1.0f, 1.0f, 0.8f);
		mLight.setPower(1);
	
		Camera cam = getCurrentCamera();
		cam.setPosition(0,0,10);
		getCurrentScene().addLight(mLight);
		
		scene1();
		scene2();
		
		addScene(sc1);
		addScene(sc2);
		
		Log.d("width", Float.toString(mViewportWidth));
		Log.d("height", Float.toString(mViewportHeight));
		
		r1 = new RenderTarget(mViewportWidth, mViewportHeight,0,0,false,false,false,GL10.GL_TEXTURE_2D, Config.ARGB_8888,FilterType.LINEAR,WrapType.CLAMP);
		r2 = new RenderTarget(mViewportWidth, mViewportHeight,0,0,false,false,false,GL10.GL_TEXTURE_2D, Config.ARGB_8888,FilterType.LINEAR,WrapType.CLAMP);
		
		r1.create();
		r2.create();
		
		addRenderTarget(r1);
		addRenderTarget(r2);
		
		plane = new Plane(10,10,1,1);
		Material planeMat = new Material();
		plane.setRotX(180);
		plane.setRotZ(180);
		plane.setPosition(0,0,-5);
		
		try{
			planeMat.enableLighting(true);
			planeMat.setDiffuseMethod(new DiffuseMethod.Lambert());
			planeMat.addTexture(r1.getTexture());
			planeMat.setColorInfluence(0);
			plane.setMaterial(planeMat);
		}catch(TextureException t){
			t.printStackTrace();
		}
		
		addChild(plane);
		mUserScene = getCurrentScene();
	}

	@Override
	protected void onRender(double deltaTime) {
		sc1.render(deltaTime,r1);
		sc2.render(deltaTime,r2);
		render(deltaTime);
	};

	@Override
	public void onDrawFrame(GL10 glUnused) {
		// TODO Auto-generated method stub
		cube.setRotY(time);
		sphere.setRotY(time);
		super.onDrawFrame(glUnused);
		time+=0.1;
	}
}
