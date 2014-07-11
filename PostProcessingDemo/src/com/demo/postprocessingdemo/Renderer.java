package com.demo.postprocessingdemo;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import rajawali.Object3D;
import rajawali.animation.Animation;
import rajawali.animation.Animation3D;
import rajawali.animation.ColorAnimation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.TranslateAnimation3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.Matrix4;
import rajawali.math.vector.Vector3;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.BloomEffect;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.primitives.Cube;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.GLU;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import rajawali.util.RajLog;

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener{

	private PostProcessingManager mEffects;
	private float deg = (float) Math.PI / 180;
	private Material stdMat;
	float count=0;
	int numMotionObj = 5;
	private Object3D mObjects[] = new Object3D[numMotionObj];
	
	private ObjectColorPicker mPicker;
	private Object3D mSelectedObject;
	private int[] mViewport;
	private double[] mNearPos4;
	private double[] mFarPos4;
	private Vector3 mNearPos;
	private Vector3 mFarPos;
	private Vector3 mNewObjPos;
	private Matrix4 mViewMatrix;
	private Matrix4 mProjectionMatrix;
	public Random random = new Random();
	public long  speed = 10000; 
	int howoften = 17;
	int score = 0;
	public Cube camerabox = new Cube(1000);
	ArrayList<Object3D> colliders = new ArrayList<Object3D>();
	private boolean mBoxIntersect = false;
	private float wall = 20;
	private boolean gotHit = true;
	
	
	
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}


	public void initScene() {

		initPicking();
		DirectionalLight light = new DirectionalLight();
		light.setPower(1f);
		light.setPosition(0, 0, 15);
		getCurrentScene().addLight(light);
    	getCurrentCamera().setZ(wall);
		getCurrentScene().setBackgroundColor(0,0,0,0);
		
		Material m = new Material();
		camerabox.setDrawingMode(GLES20.GL_LINES);
		camerabox.setScale(1,1,0);
		camerabox.setPosition(0,0,wall-5);
		camerabox.setMaterial(m);
		camerabox.setVisible(true);
		camerabox.setTransparent(true);
		camerabox.setColor(0x00000000);
		getCurrentScene().addChild(camerabox);
		
		mEffects = new PostProcessingManager(this);
		RenderPass renderPass = new RenderPass(getCurrentScene(), getCurrentCamera(), 0);
		mEffects.addPass(renderPass);
		
		BloomEffect bloomEffect = new BloomEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight, 0x000000, 0xffffff, BlendMode.SCREEN);
		mEffects.addEffect(bloomEffect);
		bloomEffect.setRenderToScreen(true);
		
	}
	
	public void initPicking()
	{
		
		mViewport = new int[] { 0, 0, mViewportWidth, mViewportHeight };
		mNearPos4 = new double[4];
		mFarPos4 = new double[4];
		mNearPos = new Vector3();
		mFarPos = new Vector3();
		mNewObjPos = new Vector3();
		mViewMatrix = getCurrentCamera().getViewMatrix();
		mProjectionMatrix = getCurrentCamera().getProjectionMatrix();

		mPicker = new ObjectColorPicker(this);
		mPicker.setOnObjectPickedListener(this);

	}
	
	public void createCube()
	{
		Material m = new Material();		
		m.setColorInfluence(1);
		Cube c = new Cube(2 + random.nextFloat()*2);
		c.setPosition(-10+random.nextFloat()*20,-10+random.nextFloat()*20,-100);
		c.setMaterial(m);
		c.setColor(0x666666 + random.nextInt(0x999999));
		mPicker.registerObject(c);
		colliders.add(c);
		getCurrentScene().addChild(c);
		
		TranslateAnimation3D anim = new TranslateAnimation3D(c.getPosition(), new Vector3(c.getX(), c.getY(), wall)); 
		anim.setTransformable3D(c);
		anim.setDurationMilliseconds(speed);
		anim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(anim);
		
		anim.play();

	}
	
	public void createCube2()
	{
		Material m = new Material();		
		m.setColorInfluence(1);
		Cube c = new Cube(2 + random.nextFloat()*2);
		c.setPosition(-8+random.nextFloat()*16,-8+random.nextFloat()*16,-100);
		c.setMaterial(m);
		c.setColor(0x11111111 + random.nextInt(0xeeeeeee));
		mPicker.registerObject(c);
		colliders.add(c);
		getCurrentScene().addChild(c);
		
		TranslateAnimation3D anim = new TranslateAnimation3D(c.getPosition(), new Vector3(c.getX(), c.getY(), wall)); 
		anim.setTransformable3D(c);
		anim.setDurationMilliseconds(speed);
		anim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(anim);
				
		anim.play();
		
		Vector3 randomAxis = new Vector3(random.nextFloat(),random.nextFloat(), random.nextFloat());
				randomAxis.normalize();

		RotateOnAxisAnimation anim2 = new RotateOnAxisAnimation(randomAxis,360);
		anim2.setTransformable3D(c);
	    anim2.setDurationMilliseconds(speed);
		anim2.setRepeatMode(RepeatMode.INFINITE);
		getCurrentScene().registerAnimation(anim2);
		anim2.play();
	}

	public void deleteCube(Object3D cube){
	
		getCurrentScene().removeChild(cube);
		colliders.remove(cube);  
	
	}
	
	public void rotateCam(long camspeed, int winkel)
	{
	
		RotateOnAxisAnimation anim = new RotateOnAxisAnimation(new Vector3(0,0,1),winkel);
		anim.setTransformable3D(getCurrentCamera());
	    anim.setDurationMilliseconds(camspeed);
		anim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(anim);
		anim.play();
	}
	
	
		
	 	
	
	public boolean onTouch(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			getObjectAt(x,y);
			break;
		case MotionEvent.ACTION_MOVE:
		//	moveSelectedObject(x,y);
			break;
		case MotionEvent.ACTION_UP:
			stopMovingSelectedObject();
			break;
		}
		return true;
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		super.onSurfaceChanged(gl, width, height);
		mViewport[2] = mViewportWidth;
		mViewport[3] = mViewportHeight;
		mViewMatrix = getCurrentCamera().getViewMatrix();
		mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
	}
	
	public void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	public void onObjectPicked(Object3D object) {
		mSelectedObject = object;
		deleteCube(mSelectedObject);
	}

	public void moveSelectedObject(float x, float y) {

		if (mSelectedObject == null)
			return;
		
		//
		// -- unproject the screen coordinate (2D) to the camera's near plane
		//

		GLU.gluUnProject(x, mViewportHeight - y, 0, mViewMatrix.getDoubleValues(), 0,
				mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

		//
		// -- unproject the screen coordinate (2D) to the camera's far plane
		//

		GLU.gluUnProject(x, mViewportHeight - y, 1.f, mViewMatrix.getDoubleValues(), 0,
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

		double ox = mSelectedObject.getX();
		double oy = mSelectedObject.getY();
		
		mSelectedObject.setX(mNewObjPos.x);
		mSelectedObject.setY(mNewObjPos.y);
		
		double nx = mSelectedObject.getX();
		double ny = mSelectedObject.getY();
		
//		if (nx > 0){
//			for (int i=0;i<numMotionObj; i++){
//			    mObjects[i].setX(nx-i*.1f);
//			}
//		}else{
//			for (int i=0;i<numMotionObj; i++){
//				mObjects[i].setX(nx+i*.1f);
//			}
//		}
//		if (ny > 0){
//			for (int i=0;i<numMotionObj; i++){
//				mObjects[i].setY(ny-i*.1f);
//			}
//		}else{
//			for (int i=0;i<numMotionObj; i++){
//				mObjects[i].setY(ny+i*.1f);
//			}
//		}
	}
	
	public void stopMovingSelectedObject() {
//		for (int i=0;i<numMotionObj; i++){
//			mObjects[i].setPosition(mSelectedObject.getPosition());
//		}
		mSelectedObject = null;
	}

	@Override
	public void onRender(final double deltaTime) {
		mEffects.render(deltaTime);
	}

	private void resetColliders(){
	
		for (Object3D col : colliders){
			col.setVisible(false);
		}
	    colliders = new ArrayList<Object3D>();
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		// TODO Auto-generated method stub
		super.onDrawFrame(glUnused);
	
		score +=1;
		count +=1;
		if (gotHit){
			Log.d("score", Float.toString(score));
			if (count % howoften == 0) createCube();
		    if (count % howoften /2  == 0) createCube2();
		    
			for (Object3D col : colliders){
				IBoundingVolume bbox = col.getGeometry().getBoundingBox();
				IBoundingVolume bbox2;
				bbox.transform(col.getModelMatrix());
				bbox2= camerabox.getGeometry().getBoundingBox();
				bbox2.transform(camerabox.getModelMatrix());
				
				mBoxIntersect = bbox.intersectsWith(bbox2);
	
				if (score == 100) rotateCam(10000, 180); 
				if (score == 1000) rotateCam(4000, 12000);
				
				if (mBoxIntersect ){
					resetColliders();
					gotHit = false;
				}
			}
		}
	}
		
}

