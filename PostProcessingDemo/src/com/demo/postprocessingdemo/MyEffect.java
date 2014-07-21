package com.demo.postprocessingdemo;

import rajawali.Camera;
import rajawali.postprocessing.APostProcessingEffect;
import rajawali.postprocessing.passes.BlendPass;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.postprocessing.passes.BlurPass.Direction;
import rajawali.postprocessing.passes.BlurPass;
import rajawali.postprocessing.passes.ColorThresholdPass;
import rajawali.postprocessing.passes.CopyToNewRenderTargetPass;
import rajawali.postprocessing.passes.DepthPass;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.renderer.RajawaliRenderer;
import rajawali.scene.RajawaliScene;

public class MyEffect extends APostProcessingEffect {
	private RajawaliScene mScene;
	private Camera mCamera;
	private int mWidth;
	private int mHeight;
	private int mLowerThreshold;
	private int mUpperThreshold;
	private float mIntensity;
	private BlendMode mBlendMode;
	
	/**
	 * Bloom or glow is used to amplify light in a scene. It produces light bleeding. Bright light will extend
	 * to other parts of the scene. The colors that will bleed can be controlled by specifying the lower
	 * and upper threshold colors.
	 * 
	 * @param scene
	 * @param camera
	 * @param width
	 * @param height
	 * @param lowerThreshold
	 * @param upperThreshold
	 * @param blendMode
	 */
	public MyEffect(RajawaliScene scene, Camera camera, int width, int height, int lowerThreshold, int upperThreshold, BlendMode blendMode, float intensity) {
		super();
		mScene = scene;
		mCamera = camera;
		mWidth = width;
		mHeight = height;
		mLowerThreshold = lowerThreshold;
		mUpperThreshold = upperThreshold;
		mBlendMode = blendMode;
		mIntensity = intensity;
	}
	
	public void initialize(RajawaliRenderer renderer)
	{
		addPass(new DepthPass(mScene, mCamera));
		CopyToNewRenderTargetPass copyPass = new CopyToNewRenderTargetPass("bloomPassTarget", renderer, mWidth, mHeight);
		addPass(copyPass);
		addPass(new RenderPass(mScene, mCamera, mScene.getBackgroundColor()));
		addPass(new BlendPass(mBlendMode, copyPass.getRenderTarget().getTexture()));
	}
}
