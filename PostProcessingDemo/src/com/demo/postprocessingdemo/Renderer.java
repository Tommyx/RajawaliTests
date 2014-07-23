package com.demo.postprocessingdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import rajawali.Object3D;
import rajawali.animation.Animation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.TranslateAnimation3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.plugins.FogMaterialPlugin.FogParams;
import rajawali.materials.plugins.FogMaterialPlugin.FogType;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.Matrix4;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.Loader3DSMax;
import rajawali.parser.LoaderAWD;
import rajawali.parser.ParsingException;
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

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener{
		
	private int hits = 0 ;
	private int highscore = 0;
	private int howoften = 29;
	private int score = 0;
	private int distance = -300;
	private int numParticles = 200;
	
	
	private int[] mViewport;
	
	public long  speed = 20000; 
	
	private float wall = 20;
	//private float deg = (float) Math.PI / 180;
	private float count, fcount =0;
	
	private double[] mNearPos4;
	private double[] mFarPos4;
	
	private boolean notHit = false;
	private boolean animrunning = false;
	private boolean mBoxIntersect = false;
	
	private Vector3 mNearPos;
	private Vector3 mFarPos;
	private Vector3 mNewObjPos;
	private Matrix4 mViewMatrix;
	private Matrix4 mProjectionMatrix;
	
	private Object3D mSelectedObject;
	
	private PostProcessingManager mEffects;
	private ObjectColorPicker mPicker;
	private Random random = new Random();
	
	private Canvas mScoreCanvas;
	private Paint mTextPaint;
	private Bitmap mScoreBitmap;
	private AlphaMapTexture mScoreTexture;
	
	private Material highscoreMat;
	
	private Cube filter, button_hit, button_highScore, button_delete, button_start;
	private boolean btn_hit_enabled, btn_delete_enabled, btn_start_enabled; 
	
	private Cube camerabox = new Cube(1000);
	
	private TranslateAnimation3D tanim;
	private RotateOnAxisAnimation ranim;
	private RotateOnAxisAnimation anim; 
	
	ArrayList<Object3D> colliders = new ArrayList<Object3D>();
	public MediaPlayer mP;
	public Object3D circle, particles;
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	public void initScene() {

		initPicking();
		
		DirectionalLight light = new DirectionalLight();
		
		light.setPower(20f);
		light.setPosition(0, 0, 0);
		
		getCurrentCamera().setFarPlane(10000);
		
		getCurrentScene().addLight(light);
    	getCurrentCamera().setZ(wall-1);
    	getCurrentScene().setBackgroundColor(0);
		
		createCamBox();
		createButtons();
		setButtonsInvisible();
		
		mEffects = new PostProcessingManager(this);
		RenderPass renderPass = new RenderPass(getCurrentScene(), getCurrentCamera(),0);
		mEffects.addPass(renderPass);
		
		BloomEffect bloomEffect = new BloomEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x111111, 0xffffff, BlendMode.ADD);
		
		mEffects.addEffect(bloomEffect);
		bloomEffect.setRenderToScreen(true);
		
		button_start.setVisible(true);
		btn_start_enabled = true;
		loadCircle();
		createParticle();
	}

	public void setButtonsInvisible()
	{
		button_start.setVisible(false);
		btn_start_enabled = false;
		button_hit.setVisible(false);
		btn_hit_enabled = false;
		button_delete.setVisible(false);
		btn_delete_enabled = false;
	}
	
	public void initPicking(){
		
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
	
	public void createCamBox(){
		
		Material m = new Material();
		//camerabox.setDrawingMode(GLES20.GL_LINES);
		camerabox.setScale(1,1,.0003);
		camerabox.setPosition(0,0,wall-3);
		camerabox.setMaterial(m);
		camerabox.setTransparent(true);
		camerabox.setColor(0x00000000);
		getCurrentScene().addChild(camerabox);
		
	}

	public void createFilter(){
		
		Material m = new Material();
		m.enableLighting(true);
		m.setColor(0x11111111);
		filter = new Cube(10);
		filter.setScale(10,10,1);
		filter.setPosition(0,0,-10);
		filter.setMaterial(m);
		filter.setTransparent(true);
		filter.setColor(0x11111111);
		getCurrentScene().addChild(filter);
		
	}
	
	public void resetState(){
		stopGame();

		
		setButtonsInvisible();
		button_start.setVisible(true);
		btn_start_enabled = true;
	}
	
	public void loadCircle()
	{
		Material m = new Material();		
		m.setColorInfluence(1);
		m.setDiffuseMethod(new DiffuseMethod.Lambert());
		m.enableLighting(true);
		Loader3DSMax l = new Loader3DSMax(this, R.raw.ring);
		try {
			l.parse();
			circle = l.getParsedObject();
			circle.setDoubleSided(true);
			circle.setScale(.8,.8,1);
			circle.setColor(0xffffffff);
			
			circle.setMaterial(m);
			getCurrentScene().addChild(circle);
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void createParticle()
	{
		Material m = new Material();		
		
		try{
			m.addTexture(new Texture("bla", R.drawable.light));
		}catch(TextureException e){
			
		}
		
		m.setDiffuseMethod(new DiffuseMethod.Lambert());
		m.enableLighting(true);
		m.setColorInfluence(0);
		
		Plane particle = new Plane(1,1,1,1);
		particle.setDoubleSided(true);
		particle.setTransparent(true);
		particle.setScale(.2,.2,.2);
		particle.setMaterial(m);
		
		for (int i = 0; i< numParticles; i++ ){
			
			Object3D p = particle.clone();
			p.setDoubleSided(true);
			p.setPosition(new Vector3(-7.5+random.nextFloat()*15,-7.5+random.nextFloat()*15,-20+random.nextFloat()*40));
			p.setTransparent(true);
			p.setMaterial(m);
			getCurrentScene().addChild(p);	
		} 
		
		getCurrentScene().addChild(particle);
		
		ranim = new RotateOnAxisAnimation(new Vector3(0,0,1),-360);
		ranim.setTransformable3D(particle);
		ranim.setDurationMilliseconds(1000000);
		ranim.setRepeatMode(RepeatMode.INFINITE);
		getCurrentScene().registerAnimation(ranim);
		ranim.play();	
		
	}
	
	public void createCircle()
	{
		Object3D c = circle.clone();
		c.setVisible(true);
		c.setPosition(0,0,-1000);
		c.setScale(5,5,5);
		c.setDoubleSided(true);
		c.setColor(0x000000 + random.nextInt(0xfffffff));
		getCurrentScene().addChild(c);
		
		tanim = new TranslateAnimation3D(c.getPosition(), new Vector3(c.getX(), c.getY(), wall)); 
		tanim.setTransformable3D(c);
		tanim.setDurationMilliseconds(speed-1000);
		tanim.setRepeatMode(RepeatMode.NONE);
		getCurrentScene().registerAnimation(tanim);
		tanim.play();
		
		ranim = new RotateOnAxisAnimation(new Vector3(0,0,1),360);
		ranim.setTransformable3D(c);
		ranim.setDurationMilliseconds(speed);
		ranim.setRepeatMode(RepeatMode.INFINITE);
		getCurrentScene().registerAnimation(ranim);
		ranim.play();
		
	}
	
	public void createCube()
	{
		Material m = new Material();		
		m.setColorInfluence(1);
		Cube c = new Cube(2 + random.nextFloat()*2);
		c.setName("ACube");
		c.setPosition(-10+random.nextFloat()*20,-10+random.nextFloat()*20,distance);
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
		c.setPosition(-8+random.nextFloat()*16,-8+random.nextFloat()*16,distance);
		c.setMaterial(m);
		c.setColor(0x1111111 + random.nextInt(0xeeeeee));
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
	
	private void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	public void onObjectPicked(Object3D object)
	{
		mSelectedObject = object;
		
		if (mSelectedObject.getName() == "hitButton" && btn_hit_enabled) 
		{
			Log.d("hitbtn", "hitid");
			button_highScore.setVisible(false);
			setButtonsInvisible();
			resetScore();
			button_start.setVisible(true);
			btn_start_enabled = true;
		}
		else if (mSelectedObject.getName() == "deleteButton" && btn_delete_enabled) 
		{
			deleteScore();
			Log.d("del", "del");
			button_delete.setVisible(false);
			btn_hit_enabled = true;
		}
		else if (mSelectedObject.getName() == "startButton" && btn_start_enabled) 
		{
			Log.d("startbtn", "startbtn");
			startGame();
		}
		
		else if (mSelectedObject.getName() == "ACube") {
			hits+=1;
			deleteCube(mSelectedObject);
		}
		
	}
	
	private void moveSelectedObject(float x, float y) {

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

//		double ox = mSelectedObject.getX();
//		double oy = mSelectedObject.getY();
//		
		mSelectedObject.setX(mNewObjPos.x);
		mSelectedObject.setY(mNewObjPos.y);
		
//		double nx = mSelectedObject.getX();
//		double ny = mSelectedObject.getY();
//		
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
	
	private void stopMovingSelectedObject() {
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
	
	private void startGame(){
		circle.setVisible(false);
		mP = MediaPlayer.create(getContext(), R.raw.cpu);
		mP.start();
		mP.setLooping(true);
		notHit = true;
		setButtonsInvisible();
	}
	
	private void stopGame(){
		speed = 20000;
		saveScore();
		resetCamera();
		button_hit.setVisible(true);
		btn_hit_enabled = true;
		button_delete.setVisible(true);
		btn_delete_enabled = true;
		showScoreBitmap();
		resetColliders();
		notHit = false;
		mP.stop();
		mP.reset();
	}
	
	private void createStartButton(){

		Material m = new Material();		
		m.setColorInfluence(0);
		button_start = new Cube(4);
		button_start.setName("startButton");
		button_start.setRotZ(180);
		button_start.setScale(1,.6,0.1f);
		button_start.setPosition(0,0,0);
		button_start.setMaterial(m);
		try {m.addTexture(new Texture("test", R.drawable.button_start));
			}catch(TextureException e){}
		button_start.setTransparent(true);
		mPicker.registerObject(button_start);
		btn_start_enabled = true;
		getCurrentScene().addChild(button_start);

	}
	
	private void createHitButton(){

		Material m = new Material();		
		m.setColorInfluence(0);
		button_hit = new Cube(4);
		button_hit.setName("hitButton");
		button_hit.setRotZ(180);
		button_hit.setScale(1.2,1,0.1f);
		button_hit.setPosition(0,0,1);
		button_hit.setMaterial(m);
		try {m.addTexture(new Texture("test", R.drawable.button_gothit));
			}catch(TextureException e){}
		button_hit.setTransparent(true);
		btn_hit_enabled = false;
		mPicker.registerObject(button_hit);
		getCurrentScene().addChild(button_hit);

	}
	
	private void createDeleteButton(){

		Material m = new Material();		
		m.setColorInfluence(0);
		button_delete = new Cube(4);
		button_delete.setName("deleteButton");
		button_delete.setRotZ(180);
		button_delete.setScale(1,.6,0.1f);
		button_delete.setPosition(0,2,0);
		button_delete.setMaterial(m);
		try {m.addTexture(new Texture("test", R.drawable.button_delete));
			}catch(TextureException e){}
		button_delete.setTransparent(true);
		btn_delete_enabled = false;
		mPicker.registerObject(button_delete);
		getCurrentScene().addChild(button_delete);

	}
	
	private void createButtons(){
		
		createStartButton();
		createHitButton();
		createDeleteButton();
				
	}
	
	private void resetCamera(){
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
            in.close();
        } catch (FileNotFoundException e) {
        	
            Log.d("FileError", filename + " not found");
        } catch (IOException e) {
        	Log.d("FileError", filename + " I/O Error");
        } 
        return stringBuilder.toString();
    }
	
	public void showScoreBitmap()
	{
		if ( button_highScore != null ){
			getCurrentScene().removeChild(button_highScore);
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
		
		mScoreTexture = new AlphaMapTexture("ScoreTexture", mScoreBitmap);
		
		try {
			highscoreMat.addTexture(mScoreTexture);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		highscoreMat.setColorInfluence(1);
		
		button_highScore = new Cube(6);
		button_highScore.setName("Panel2");
		button_highScore.setRotZ(180);
		button_highScore.setScale(1,1,0.1f);
		button_highScore.setPosition(0,-3,-1);
		button_highScore.setMaterial(highscoreMat);

		button_highScore.setTransparent(true);
		button_highScore.setVisible(true);
		getCurrentScene().addChild(button_highScore);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
	
		count +=.01;
		fcount+=1;
		
		((PostProcessingActivity) mContext).setUI(); 
		
		if (notHit){
			
			if (fcount % howoften == 0) createCube();
			if (fcount % howoften / 4  == 0) createCube2();
			if (score % howoften  == 0) createCircle();
		    
		    score +=1;
		    count = score / 10;
		    
		    if (count == 10) rotateCam(10000, 180); 
			if (count == 30) rotateCam(20000, -360);
			if (count == 50) speed -= 100;
			if (hits == 20) howoften = 17;
			if (hits == 40) howoften = 13;
			if (hits == 60) howoften = 11;
			if (hits == 80) howoften = 7;
			if (hits == 100) howoften = 3;
			if (count == 75) rotateCam(7500, 180);
			if (count % 10 == 0 && speed > 100) speed -= 100;
			if (count == 100) rotateCam(20000, -1080);
			if (count == 110) rotateCam(20000, 1080);
			
		    for (Object3D col : colliders){
				IBoundingVolume bbox = col.getGeometry().getBoundingBox();
				IBoundingVolume bbox2;
				bbox.transform(col.getModelMatrix());
				bbox2= camerabox.getGeometry().getBoundingBox();
				bbox2.transform(camerabox.getModelMatrix());
				
				mBoxIntersect = bbox.intersectsWith(bbox2);
				
				if (mBoxIntersect){
					stopGame();
				}
		    }
			}
			
		
	}
		
	@Override
	public void onRender(final double deltaTime) {
		mEffects.render(deltaTime);
	}
}

