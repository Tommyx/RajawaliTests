package com.demo.water;

import java.util.Random;

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
import rajawali.materials.plugins.FogMaterialPlugin.FogParams;
import rajawali.materials.plugins.FogMaterialPlugin.FogType;
import rajawali.materials.shaders.FragmentShader;
import rajawali.materials.shaders.VertexShader;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;

import rajawali.parser.Loader3DSMax;
import rajawali.parser.LoaderAWD;
import rajawali.parser.LoaderMD2;
import rajawali.parser.ParsingException;
import rajawali.parser.md5.LoaderMD5Anim;
import rajawali.parser.md5.LoaderMD5Mesh;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.BloomEffect;
import rajawali.postprocessing.effects.ShadowEffect;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;

import rajawali.renderer.RajawaliRenderer;

public class Renderer extends RajawaliRenderer{
		
	private float uTime = 0;
	public SkeletalAnimationObject3D cube;
	Material mCube;		
	private PostProcessingManager mPostProcessingManager;
	private Random random = new Random();
	
	DirectionalLight mLight = new DirectionalLight();
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}


	public void initScene() {

		mLight = new DirectionalLight();
		mLight.setDirection(0, 0, 0);
		mLight.setPosition(0, 40, 0);
		mLight.setPower(.425f);
		
		getCurrentScene().addLight(mLight);
		
		getCurrentCamera().setPosition(0,10,26);
		getCurrentCamera().setLookAt(0, 0, 0);
		getCurrentCamera().setFarPlane(100);
		int fogColor = 0x999999;
		
		getCurrentScene().setBackgroundColor(fogColor);
		getCurrentScene().setFog(new FogParams(FogType.LINEAR, fogColor, 1,200));
		
		createSkyBox();
		water();
		

		mPostProcessingManager = new PostProcessingManager(this);

		ShadowEffect shadowEffect = new ShadowEffect(getCurrentScene(), getCurrentCamera(), mLight, 2048);
		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x000000, 0xffffff, BlendMode.ADD);
		shadowEffect.setShadowInfluence(.5f);
		mPostProcessingManager.addEffect(shadowEffect);
		mPostProcessingManager.addEffect(bloomEffect);
		
		bloomEffect.setRenderToScreen(true);
		shadowEffect.setRenderToScreen(true);
		
		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
										  new Vector3(0, 10, 0), 
										  new Vector3(0, 15, 30), 0, 359);
		
		
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
		Cube c = new Cube(100); 
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
		
		Plane street = new Plane (49,49,1,1);
		street.setDoubleSided(true);
		Material sMat= new Material();
		sMat.setColorInfluence(0.0f);
		//sMat.enableLighting(true);
		
		street.setRotX(90);
		street.setY(1);
		street.setZ(-4.5f);
		
		try{
			sMat.addTexture(new Texture("street", R.drawable.street));
		}
		catch(TextureException e){
			e.printStackTrace();
		}
		street.setMaterial(sMat);
		getCurrentScene().addChild(street);
		
		VertexShader v = new VertexShader(R.raw.minimal_vertex_shader);
		FragmentShader f = new FragmentShader(R.raw.fragmentshader_water);
		
		int[] cubemaps = new int[6];
		cubemaps[0] = R.drawable.posx;
		cubemaps[1] = R.drawable.negx;
		cubemaps[2] = R.drawable.posy;
		cubemaps[3] = R.drawable.negy;
		cubemaps[4] = R.drawable.posz;
		cubemaps[5] = R.drawable.negz;
		CubeMapTexture texture = new CubeMapTexture("cubemaps", cubemaps);
		texture.isEnvironmentTexture(true);
		
		mCube = new Material();
		mCube.setDiffuseMethod(new DiffuseMethod.Lambert());
		mCube.enableLighting(true);
//		mCube.enableTime(true);
		mCube.setColorInfluence(0);
		
		
		try {
			mCube.addTexture(new Texture("name",R.drawable.texture));
			mCube.addTexture(texture);
			
		} catch (TextureException e) {
			e.printStackTrace();
		}
		
		try {
			Loader3DSMax parser = new Loader3DSMax(this, R.raw.city);
			parser.parse();

			final Object3D obj = parser.getParsedObject();
		    obj.setScale(2.0f);
			obj.setRotation(90,0,0);
			obj.setMaterial(mCube);
			getCurrentScene().addChild(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Material trees = new Material();
		trees.setColorInfluence(0);
		
		try{
			trees.addTexture(new Texture("trees", R.drawable.tree));
		}catch(Exception e){}
		 
		try {
			Loader3DSMax parser2 = new Loader3DSMax(this, R.raw.trees);
			parser2.parse();

			Object3D obj2 = parser2.getParsedObject();
			obj2.setMaterial(trees);
			
			for (int i=0;i<100;i++){
				Object3D t = obj2.clone();
				t.setScale(1.0);
				t.setDoubleSided(true);
				t.setTransparent(true);
				t.setPosition(-20+random.nextFloat()*40, 2, -20+random.nextFloat()*40);
				getCurrentScene().addChild(t);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
	}
		 
	@Override
	public void onRender(final double deltaTime) {
		mPostProcessingManager.render(deltaTime);
		mLight.setPosition(getCurrentCamera().getPosition().x,
						   getCurrentCamera().getPosition().y,
						   getCurrentCamera().getPosition().z+20);
	}	
}	

