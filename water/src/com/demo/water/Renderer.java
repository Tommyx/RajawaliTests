package com.demo.water;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import rajawali.Object3D;

import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.Animation3D;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.mesh.SkeletalAnimationObject3D;
import rajawali.animation.mesh.SkeletalAnimationSequence;
import rajawali.animation.mesh.VertexAnimationObject3D;

import rajawali.lights.DirectionalLight;

import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.shaders.FragmentShader;
import rajawali.materials.shaders.VertexShader;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;

import rajawali.parser.LoaderAWD;
import rajawali.parser.LoaderMD2;
import rajawali.parser.ParsingException;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;

import rajawali.renderer.RajawaliRenderer;

public class Renderer extends RajawaliRenderer{
		
	private float uTime = 0;
	public SkeletalAnimationObject3D cube;
	Material mCube;		
	
	DirectionalLight mLight = new DirectionalLight();
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}


	public void initScene() {

		mLight = new DirectionalLight();
		mLight.setDirection(0, 0, 0);
		mLight.setPosition(0, 10, 0);
		mLight.setPower(5.0f);
		
		getCurrentScene().addLight(mLight);
		
		getCurrentCamera().setPosition(0,1,6);
		getCurrentCamera().setLookAt(0, 0, 0);
		getCurrentCamera().setFarPlane(1000);
		getCurrentScene().setBackgroundColor(0);
		
		water();
		createSkyBox();

		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
										  new Vector3(0, 4, 0), 
										  new Vector3(0, 1, 10), 0, 359);
		
		
		anim.setRepeatMode(RepeatMode.INFINITE);
		anim.setDurationMilliseconds(100000);
		anim.setTransformable3D(getCurrentCamera());
		getCurrentScene().registerAnimation(anim);
		anim.play();
		
	}
	
	
	private void createSkyBox(){
		
		int [] resourceIds = { R.drawable.posx, 
							   R.drawable.negx, 
							   R.drawable.posy, 
							   R.drawable.negy, 
							   R.drawable.posz, 
							   R.drawable.negz};
		
		CubeMapTexture m = new CubeMapTexture("sky", resourceIds);
		
		Material qm = new Material();
		Cube c = new Cube(1000); 
		c.setDoubleSided(true);
		m.isSkyTexture(true);
		qm.setColorInfluence(0);
		
		try {
			qm.addTexture(m);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
		
		c.setMaterial(qm);
		getCurrentScene().addChild(c);
	}
		
	private void water(){
		
		VertexShader v = new VertexShader(R.raw.minimal_vertex_shader);
		FragmentShader f = new FragmentShader(R.raw.fragmentshader_water);
		
//		mCube = new Material();
//		mCube.setDiffuseMethod(new DiffuseMethod.Lambert());
//		mCube.enableLighting(true);
//		mCube.enableTime(true);
//		mCube.setColorInfluence(0);
//		
//		try {
//			mCube.addTexture(new Texture("name",R.drawable.untitled));
//		} catch (TextureException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			LoaderMD5Mesh meshParser = new LoaderMD5Mesh(this,
//					R.raw.setzer_md5mesh);
//			meshParser.parse();
//
//			LoaderMD5Anim animParser = new LoaderMD5Anim("wave", this,
//					R.raw.rigged_md5anim);
//			animParser.parse();
//
//			SkeletalAnimationSequence sequence = (SkeletalAnimationSequence) animParser
//					.getParsedAnimationSequence();
//
//			cube = (SkeletalAnimationObject3D) meshParser
//					.getParsedAnimationObject();
//			//cube.setAnimationSequence(sequence);
//			cube.setScale(1f,-1,1);
//			
//			//cube.play();
//			getCurrentScene().addChild(cube);
//		
//		} catch (ParsingException e) {
//			e.printStackTrace();
//		}
		
		try {
			LoaderAWD parser = new LoaderAWD(mContext.getResources(), mTextureManager, R.raw.city);
			parser.parse();

			final Object3D obj = parser.getParsedObject();
			obj.setScale(0.25f);
			getCurrentScene().addChild(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
	}
}	

