package com.demo.postprocessingdemo;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import rajawali.Object3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.Animation.RepeatMode;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.BloomEffect;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.RajLog;

public class Renderer extends RajawaliRenderer{

	private PostProcessingManager mEffects;
	private float deg = (float) Math.PI / 180;
	private Material stdMat;
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	public void initScene() {
		DirectionalLight light = new DirectionalLight();
		light.setPower(1);
		getCurrentScene().addLight(light);

		createStdMat();
		getCurrentCamera().setZ(10);
		getCurrentScene().setBackgroundColor(1,1,1,1);
		
		Random random = new Random();
		int color = 0x0000000;
		
		for (int i=0;i<1; i++){

			Cube n = new Cube(2);
			n.setTransparent(true);
			//n.setRotation(0, 0, i*deg);
			//n.setDrawingMode(GLES20.GL_LINES);
			n.setPosition(0,0,0);
			n.setMaterial(stdMat);
			n.setDoubleSided(true);
			getCurrentScene().addChild(n);
			
			Vector3 randomAxis = new Vector3(random.nextFloat(),
					random.nextFloat(), random.nextFloat());
			randomAxis.normalize();

			//RotateOnAxisAnimation anim = new RotateOnAxisAnimation(randomAxis,360);
			RotateOnAxisAnimation anim = new RotateOnAxisAnimation(new Vector3(0,1,1),360);
			anim.setTransformable3D(n);
			anim.setDurationMilliseconds(20000 + (int) (i+1)*5000);
			anim.setRepeatMode(RepeatMode.INFINITE);
			getCurrentScene().registerAnimation(anim);
			anim.play();
		}	

		mEffects = new PostProcessingManager(this);
		RenderPass renderPass = new RenderPass(getCurrentScene(), getCurrentCamera(), 0);
		mEffects.addPass(renderPass);
		
		BloomEffect bloomEffect = new BloomEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight, 0x000000, 0xffffff, BlendMode.SCREEN);
		
		mEffects.addEffect(bloomEffect);

		bloomEffect.setRenderToScreen(true);
		
	}

	public void createStdMat(){
		stdMat = new Material();
		
		stdMat.setDiffuseMethod(new DiffuseMethod.Lambert());
		//stdMat.setColorInfluence(0);
		//stdMat.enableLighting(true);
		
		
		try {
			Texture t = new Texture("lines", R.drawable.lines);
			NormalMapTexture tn = new NormalMapTexture("lines_nm", R.drawable.lines_nm); 
//			t.setRepeat(3, 3);
//			tn.setRepeat(3, 3);
			stdMat.addTexture(t);
			stdMat.addTexture(tn);
		}catch(TextureException t){
			t.printStackTrace();
		}
		
		
	}
	
	@Override
	public void onRender(final double deltaTime) {
		mEffects.render(deltaTime);
	}
	
}

