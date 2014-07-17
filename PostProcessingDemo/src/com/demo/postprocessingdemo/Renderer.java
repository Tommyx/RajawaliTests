package com.demo.postprocessingdemo;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView; 
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import rajawali.materials.textures.AlphaMapTexture;
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
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.GLU;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import rajawali.util.RajLog;

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener{

	private PostProcessingManager mEffects;
	private float deg = (float) Math.PI / 180;
	private Material stdMat;
	float count, fcount =0;
	int numMotionObj = 5;
	private Object3D mObjects[] = new Object3D[numMotionObj];
	private Material highscoreMat;
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
	public long  speed = 20000; 
	int howoften = 27;
	int score = 0;
	public Cube camerabox = new Cube(1000);
	ArrayList<Object3D> colliders = new ArrayList<Object3D>();
	private boolean mBoxIntersect = false;
	private float wall = 20;
	private boolean notHit = true;
	private RotateOnAxisAnimation anim; 
	private boolean animrunning = false;
	private int hits = 0 ;
	private Canvas mScoreCanvas;
	private Paint mTextPaint;
	private int highscore = 0;
	private Bitmap mScoreBitmap;
	private boolean mShouldUpdateTexture;
	private AlphaMapTexture mScoreTexture;
	private Cube highScorePanel,delPanel;
	private TranslateAnimation3D tanim;
	private RotateOnAxisAnimation ranim;
	
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
    	getCurrentCamera().setZ(wall-1);
		getCurrentScene().setBackgroundColor(.0f,.0f,.0f,0);
		
		Material m = new Material();
		//camerabox.setDrawingMode(GLES20.GL_LINES);
		camerabox.setScale(10,10,0);
		camerabox.setPosition(0,0,wall);
		camerabox.setMaterial(m);
		camerabox.setTransparent(true);
		camerabox.setColor(0x00000000);
		getCurrentScene().addChild(camerabox);
		
		mEffects = new PostProcessingManager(this);
		RenderPass renderPass = new RenderPass(getCurrentScene(), getCurrentCamera(),0);
		mEffects.addPass(renderPass);
		
		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x111111, 0xffffff, BlendMode.ADD, 0.1f);
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
		c.setName("ACube");
		c.setPosition(-10+random.nextFloat()*20,-10+random.nextFloat()*20,-100);
		c.setMaterial(m);
		c.setColor(0x666666 + random.nextInt(0x999999));
		mPicker.registerObject(c);
		colliders.add(c);
		getCurrentScene().addChild(c);
		
		tanim = new TranslateAnimation3D(c.getPosition(), new Vector3(c.getX(), c.getY(), wall)); 
		tanim.setTransformable3D(c);
		tanim.setDurationMilliseconds(speed);
		tanim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(tanim);
		
		tanim.play();

	}
	
	public void createCube2()
	{
		Material m = new Material();		
		m.setColorInfluence(1);
		Cube c = new Cube(2 + random.nextFloat()*2);
		c.setName("ACube");
		c.setPosition(-8+random.nextFloat()*16,-8+random.nextFloat()*16,-100);
		c.setMaterial(m);
		c.setColor(0x11111111 + random.nextInt(0xeeeeeee));
		mPicker.registerObject(c);
		colliders.add(c);
		getCurrentScene().addChild(c);
		
		tanim = new TranslateAnimation3D(c.getPosition(), new Vector3(c.getX(), c.getY(), wall)); 
		tanim.setTransformable3D(c);
		tanim.setDurationMilliseconds(speed);
		tanim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(tanim);
				
		tanim.play();
		
		Vector3 randomAxis = new Vector3(random.nextFloat(),random.nextFloat(), random.nextFloat());
				randomAxis.normalize();

		ranim = new RotateOnAxisAnimation(randomAxis,360);
		ranim.setTransformable3D(c);
		ranim.setDurationMilliseconds(speed);
		ranim.setRepeatMode(RepeatMode.INFINITE);
		getCurrentScene().registerAnimation(ranim);
		ranim.play();
	}

	public void deleteCube(Object3D cube){
		getCurrentScene().removeChild(cube);
		colliders.remove(cube);  
		mSelectedObject = null;
	}
	
	public void rotateCam(long camspeed, int winkel)
	{
		anim = new RotateOnAxisAnimation(new Vector3(0,0,1),winkel);
		anim.setTransformable3D(getCurrentCamera());
	    anim.setDurationMilliseconds(camspeed);
		anim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(anim);
		anim.play();
		animrunning = true;
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

	public void onObjectPicked(Object3D object)
	{
		mSelectedObject = object;
		
		if (mSelectedObject.getName() == "Panel") 
		{
			this.notHit = true;
			getCurrentScene().removeChild(mSelectedObject);
			getCurrentScene().removeChild(delPanel);
			getCurrentScene().removeChild(highScorePanel);
			resetScore();
		}
		else if (mSelectedObject.getName() == "Panel3") 
		{
			getCurrentScene().removeChild(delPanel);
			deleteScore();
		}
		
		else if (mSelectedObject.getName() == "ACube") {
			hits+=1;
		}else
		{
			hits-=1;
		}
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

	private void resetColliders(){
	
		for (Object3D col : colliders){
			col.setVisible(false);
		}
	    colliders = new ArrayList<Object3D>();
	}
	
	public void showHitPanel(){
		
		Material m = new Material();		
		m.setColorInfluence(0);
		Cube c = new Cube(4);
		c.setName("Panel");
		c.setRotZ(180);
		c.setScale(1,1,0.1f);
		c.setPosition(0,0,0);
		c.setMaterial(m);
		try {m.addTexture(new Texture("test", R.drawable.panel));
			}catch(TextureException e){}
		c.setTransparent(true);
		mPicker.registerObject(c);
		getCurrentScene().addChild(c);
		
	}
	
	public void showDELPanel(){
		
		Material m = new Material();		
		m.setColorInfluence(0);
		delPanel = new Cube(4);
		delPanel.setName("Panel3");
		delPanel.setRotZ(180);
		delPanel.setScale(1,.7,0.1f);
		delPanel.setPosition(0,3,0);
		delPanel.setMaterial(m);
		try {m.addTexture(new Texture("test", R.drawable.panel2));
			}catch(TextureException e){}
		delPanel.setTransparent(true);
		mPicker.registerObject(delPanel);
		getCurrentScene().addChild(delPanel);
		
	}
	
	public void resetCamera(){
		if (animrunning) anim.reset();
		getCurrentCamera().setRotation(0,0,0);
		
	}
	
	public String getHits(){
		return Integer.toString(hits);
	}
	
	public String getScore(){
		return Integer.toString(score / 10);
	}
	
	public void resetScore(){
		score = 0;
		hits = 0;
		count = 0;
		howoften = 17;
	}
	
	public void saveScore(){
		String highestscore = readScoreString();
		Log.d("Highscore", highestscore);
		if (highestscore == "") highestscore = "0";
		highscore = Integer.parseInt(highestscore);
		
		if (highscore<hits){ 
			highscore = hits;
			String filename = "score.txt";
			String Hits  = Integer.toString(hits);
		
			File file = new File(getContext().getFilesDir(), filename);
			try {
				FileWriter out = new FileWriter(file);
				out.write(Hits);
				out.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteScore(){
			String filename = "score.txt";
			String Hits  = Integer.toString(hits);
		
			File file = new File(getContext().getFilesDir(), filename);
			file.delete();
	}
	
	public String readScoreString() {
		String filename = "score.txt";
		StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
        	File file = new File(getContext().getFilesDir(), filename);
            in = new BufferedReader(new FileReader(file));
            line = in.readLine();
            stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            Log.d("FileError", filename + " not found");
        } catch (IOException e) {
        	Log.d("FileError", filename + " I/O Error");
        } 

        return stringBuilder.toString();
    }
	
	public void showScoreBitmap()
	{
		if ( highScorePanel != null ){
			getCurrentScene().removeChild(highScorePanel);
		}
		
		highscoreMat = new Material();
		
		mScoreBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		
		mScoreCanvas = new Canvas(mScoreBitmap);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(Color.WHITE);
		mTextPaint.setTextSize(25);

		mScoreCanvas.drawColor(0, Mode.CLEAR);
		
		mScoreCanvas.drawText("Highest Score: " + Integer.toString(highscore), 30,
				100, mTextPaint);
		mShouldUpdateTexture = true;

		mScoreTexture = new AlphaMapTexture("ScoreTexture", mScoreBitmap);
		
		try {
			highscoreMat.addTexture(mScoreTexture);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		highscoreMat.setColorInfluence(1);
		
		highScorePanel = new Cube(6);
		highScorePanel.setName("Panel2");
		highScorePanel.setRotZ(180);
		highScorePanel.setScale(1,1,0.1f);
		highScorePanel.setPosition(0,-2,0);
		highScorePanel.setMaterial(highscoreMat);

		highScorePanel.setTransparent(true);
		highScorePanel.setVisible(true);
		getCurrentScene().addChild(highScorePanel);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		// TODO Auto-generated method stub
		super.onDrawFrame(glUnused);
	
		
		count +=.01;
		fcount+=1;
		
		((PostProcessingActivity) mContext).setUI(); 
		
		if (notHit){
			Log.d("score", Float.toString(score));
			
			
			if (fcount % howoften == 0) createCube();
		    if (fcount % howoften /2  == 0) createCube2();
		    
		    score +=1;
		    count = score / 10;
		    
		    if (count == 10) rotateCam(10000, 180); 
			if (count == 30  && anim.isEnded()) rotateCam(20000, -360);
			if (count % 100 == 0 && speed > 100) speed -= 100;
			
		    for (Object3D col : colliders){
				IBoundingVolume bbox = col.getGeometry().getBoundingBox();
				IBoundingVolume bbox2;
				bbox.transform(col.getModelMatrix());
				bbox2= camerabox.getGeometry().getBoundingBox();
				bbox2.transform(camerabox.getModelMatrix());
				
				mBoxIntersect = bbox.intersectsWith(bbox2);
				
				if (mBoxIntersect){
					saveScore();
					showHitPanel();
					showDELPanel();
					resetCamera();
					showScoreBitmap();
					resetColliders();
					notHit = false;
				}
		    }
			}
			
		
	}
		
	@Override
	public void onRender(final double deltaTime) {
		mEffects.render(deltaTime);
	}
}

