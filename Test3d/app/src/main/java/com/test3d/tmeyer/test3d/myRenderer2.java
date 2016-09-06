package com.test3d.tmeyer.test3d;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import org.rajawali3d.renderer.Renderer;

import org.rajawali3d.Object3D;

import org.rajawali3d.animation.Animation.RepeatMode;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.mesh.SkeletalAnimationObject3D;
import org.rajawali3d.animation.mesh.SkeletalAnimationSequence;
import org.rajawali3d.animation.mesh.VertexAnimationObject3D;

import org.rajawali3d.lights.DirectionalLight;

import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.plugins.FogMaterialPlugin.FogParams;
import org.rajawali3d.materials.plugins.FogMaterialPlugin.FogType;
import org.rajawali3d.materials.shaders.FragmentShader;
import org.rajawali3d.materials.shaders.VertexShader;
import org.rajawali3d.materials.textures.CubeMapTexture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.ATexture.TextureException;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.math.vector.Vector3.Axis;

import org.rajawali3d.loader.Loader3DSMax;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.loader.LoaderMD2;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.loader.md5.LoaderMD5Anim;
import org.rajawali3d.loader.md5.LoaderMD5Mesh;
import org.rajawali3d.postprocessing.PostProcessingManager;
import org.rajawali3d.postprocessing.effects.BloomEffect;
import org.rajawali3d.postprocessing.effects.ShadowEffect;
import org.rajawali3d.postprocessing.passes.BlendPass.BlendMode;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Plane;

public class myRenderer2 extends Renderer {
		
	private float uTime = 0;
	public SkeletalAnimationObject3D cube;
	Material mCube;
	private PostProcessingManager mPostProcessingManager;
	private Random random = new Random();
	
	DirectionalLight mLight = new DirectionalLight();
	
	public myRenderer2(Context context) {
		super(context);
		setFrameRate(60);
	}


	public void initScene() {

		mLight = new DirectionalLight();
		mLight.setLookAt(0, 0, 0);
		mLight.setPosition(0, 40, 0);
		mLight.setPower(.425f);
		
		getCurrentScene().addLight(mLight);
		
		getCurrentCamera().setPosition(-20,10,40);
		getCurrentCamera().setLookAt(0, -4, -10);
		getCurrentCamera().setFarPlane(100);
		int fogColor = 0x999999;
		
		getCurrentScene().setBackgroundColor(fogColor);
		getCurrentScene().setFog(new FogParams(FogType.LINEAR, fogColor, 1,200));
		
		createSkyBox();
		water();
		mPostProcessingManager = new PostProcessingManager(this);


		ShadowEffect shadowEffect = new ShadowEffect(getCurrentScene(), getCurrentCamera(), mLight, 1024);
		//MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), getViewportWidth(), getViewportHeight(),0x100000, 0xffffff, BlendMode.ADD);
		shadowEffect.setShadowInfluence(.5f);
		mPostProcessingManager.addEffect(shadowEffect);
		//mPostProcessingManager.addEffect(bloomEffect);
		
		//bloomEffect.setRenderToScreen(true);
		shadowEffect.setRenderToScreen(true);
		
		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
										  new Vector3(0, 10, 0),
										  new Vector3(0, 10, 25), 0, 359);

		
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
		street.setRotation(90,0,0);
		street.setDoubleSided(true);
		Material sMat= new Material();
		sMat.setColorInfluence(0.0f);
		sMat.enableLighting(true);
		
		street.setRotZ(90);
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
			obj.setRotZ(90);
			obj.setY(1);
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
	protected void onRender(long ellapsedRealtime, double deltaTime) {
		mPostProcessingManager.render(ellapsedRealtime,deltaTime);
		super.onRender(ellapsedRealtime, deltaTime);

	}

	@Override
	public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

	}

	@Override
	public void onTouchEvent(MotionEvent event) {

	}
}

