package com.hallway;

import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.parser.Loader3DSMax;
import rajawali.parser.LoaderAWD;
import rajawali.parser.LoaderMD2;
import rajawali.parser.ParsingException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.GetChars;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.shaders.AShader.ShaderType;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.animation.Animation3D;
import rajawali.animation.mesh.VertexAnimationObject3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.postprocessing.passes.FogPass;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.plugins.Plugin;
import rajawali.util.MeshExporter;
import rajawali.util.exporter.SerializationExporter;

public class Renderer extends RajawaliRenderer {

	//		Log.d("path", Environment.getExternalStorageDirectory().toString() );

	IBoundingVolume bbox2 ;
	boolean stop_all, level_loaded = false;
	private float speed = 0;
	int size = 4;
	int numclones = 1;
	int cubesize = 60*numclones;
	float score = 0;
	boolean rotate, rotatenow = false;
	private PointLight mLight; 
    float touchTurn;
	float touchTurnUp;
	
	Material levelMat;
	
	float rotationdegree = 0;
	
	private boolean mBoxIntersect = false;
	Object3D camerabox;
    Loader3DSMax treeParser[] = new Loader3DSMax[3];
	ArrayList<String> models = new ArrayList<String>();
	ArrayList<String> materials = new ArrayList<String>();
	ArrayList<String> textures = new ArrayList<String>();
	ArrayList<Object3D> colliders = new ArrayList<Object3D>();
	ArrayList<Object3D> clones = new ArrayList<Object3D>();
	float coordx = 0;
	float coordy = 0;
	float half_width  = 0; 
	float half_height = 0; 
	float oldi = 0; 
	public boolean touchenabled = true;
	boolean scaling = false;
	Vector3 camrot, campos = new Vector3();
	MediaPlayer mP;
	Object3D tunnelcube;
	Object3D level;
	Plane levelPlane = new Plane(10,10,1,1);
	Plane scorePlane = new Plane(4,1,1,1);
	Bitmap mScoreBitmap;
	AlphaMapTexture mScoreTexture;
	Material stdMat,scoreMaterial, collidermat; 

	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
	public void loadStdMat(){
		stdMat = new Material();
		
		stdMat.setDiffuseMethod(new DiffuseMethod.Lambert(1.0f));
		stdMat.setColorInfluence(0);
		stdMat.enableLighting(true);
		
		try {
			Texture t = new Texture("cube1", getContext().getResources().getIdentifier("cube1", "drawable","com.hallway"));
			NormalMapTexture tn = new NormalMapTexture("cube1_nm", getContext().getResources().getIdentifier("cube1_nm", "drawable","com.hallway")); 
			t.setRepeat(3, 3);
			tn.setRepeat(3, 3);
			stdMat.addTexture(t);
			stdMat.addTexture(tn);
		}catch(TextureException t){
			t.printStackTrace();
		}
		
		
	}
	
	public void loadColiderMat(){
		collidermat = new Material();
		
		collidermat.setDiffuseMethod(new DiffuseMethod.Lambert(1.0f));
		collidermat.setColorInfluence(0);
		collidermat.enableLighting(true);
		
		try {
			Texture t = new Texture("cube1", getContext().getResources().getIdentifier("cube1", "drawable","com.hallway"));
			NormalMapTexture tn = new NormalMapTexture("cube1_nm", getContext().getResources().getIdentifier("cube1_nm", "drawable","com.hallway")); 
			tn.setRepeat(1, 1);
			collidermat.addTexture(t);
			collidermat.addTexture(tn);
		}catch(TextureException t){
			t.printStackTrace();
		}
		
		
	}
	
	private Object3D loadCube(int nr){
		String s = "cube"+Integer.toString(nr);
		Log.d("model", s);
		
		Loader3DSMax parser = new Loader3DSMax(this, getContext().getResources().getIdentifier(s, "raw","com.hallway"));
		
		try {
			parser.parse();
		} catch(ParsingException e) {
			e.printStackTrace();
		} 
		
		Object3D obj = parser.getParsedObject();
		obj.setScale(10);
		obj.setDoubleSided(true);
		obj.setTransparent(false);
		obj.setMaterial(stdMat);
		obj.setShowBoundingVolume(true);
		
		addChild(obj);
		return obj;
	}
	
	private Object3D loadCubeAWD(int nr){
		
		LoaderAWD parser = new LoaderAWD(getContext().getResources(), getTextureManager() , getContext().getResources().getIdentifier("level_"+nr, "raw","com.hallway"));
		
		try {
			parser.parse();
		} catch(ParsingException e) {
			e.printStackTrace();
		}
		
		Object3D obj = parser.getParsedObject();
		addChild(obj);
		return obj;
	}
	
	private void setColliders(Object3D level_object){
		
		for (int i=0; i< level_object.getNumChildren(); i++)
		{
			colliders.add(level_object.getChildAt(i));
		}
	}
	
	private void loadLevel(int levelnr){
		
		level = loadCubeAWD(levelnr);
		level.setMaterial(collidermat);
		setColliders(level);
		level_loaded = true;
	}
	
	private void unloadLevel(){
		
		level = new Object3D();
		level.setMaterial(stdMat);
		resetColliders();
		level_loaded = false;
	}

	private void resetColliders(){
	
		for (int i=0; i<colliders.size();i++){
			colliders.remove(i); 
		}
	}
	
	
	protected void initScene() {		
		
		//mP = MediaPlayer.create(getContext(), R.raw.loop2);
		
		mLight = new PointLight(); 
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(20f);
		
		loadStdMat();
		loadColiderMat();
		
		camerabox = new Cube(.5f);
		camerabox.setMaterial(stdMat);
		addChild(camerabox);
		
		//createSkyBox();
		tunnelcube = loadCube(1);
		
		getCurrentScene().addLight(mLight);
		getCurrentScene().setBackgroundColor(0, 0, 0, 0);
		getCurrentCamera().setPosition( 0,1,-10);
		getCurrentCamera().setRotation( 0,0,0);
		getCurrentCamera().setLookAt(   0,1,0);	
		getCurrentCamera().setFarPlane(1500);	
		
	}
	
	private void createSkyBox(){
		try {
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx,
					R.drawable.posy, R.drawable.negy, R.drawable.posz,
					R.drawable.negz, 1000);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
	}
	
	
	private void creatScoreMat(){
		Material timeSphereMaterial = new Material();
		timeSphereMaterial.enableLighting(true);
		timeSphereMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
		mScoreBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		mScoreTexture = new AlphaMapTexture("timeTexture", mScoreBitmap);
		try {
			timeSphereMaterial.addTexture(mScoreTexture);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		timeSphereMaterial.setColorInfluence(1);

	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		speed=0.2f;		
		score++;
		
		if (!stop_all){
			if (score == 10){
				showLevel(1);
			}
			
			
			if (score == 100){
				hideLevel();
				loadLevel(1);
			}
				
		camerabox.setPosition( getCurrentCamera().getPosition().x,
				getCurrentCamera().getPosition().y,
				getCurrentCamera().getPosition().z + 0);
	
		
		mLight.setPosition( getCurrentCamera().getPosition().x,
				getCurrentCamera().getPosition().y-1,
				getCurrentCamera().getPosition().z + 30);
		
		for (Object3D col : colliders){
			col.setShowBoundingVolume(true);

			IBoundingVolume bbox = col.getGeometry().getBoundingBox();
			bbox.transform(col.getModelMatrix());
			bbox.setBoundingColor(Color.WHITE);
			bbox2= camerabox.getGeometry().getBoundingBox();
			bbox2.transform(camerabox.getModelMatrix());
			mBoxIntersect = bbox.intersectsWith(bbox2);
				
			if (mBoxIntersect){
				Log.d("HIT!!!!!!!!", "HITTTTTT");
				stop();
			}
			
			if (col.getZ()< 10) {
				removeChild(col);
				colliders.remove(col);
			}
		}	
		
		//if (score % 4 == 0) time+=0.2f;
		
		if (score == 1000){ 
			rotationdegree = (float) getCurrentCamera().getRotY();
			rotatenow = true;
			speed = 1;
		}
		
		if (score == 2000) {
			unloadLevel();
			showLevel(2);
		}
		if (score == 2100){
			hideLevel();
			loadLevel(2);
		}
		
		if (rotatenow){
			if ( rotationdegree <= 180) 
				rotationdegree+=speed;  
			else { rotatenow = false; 
			rotationdegree = 0;
			}
		}
		
		if (tunnelcube.getZ() < -60){ 
			tunnelcube.setZ(tunnelcube.getZ()+60);
		}
		
		if (rotatenow){ 
			getCurrentCamera().setRotX(getCurrentCamera().getRotX() + speed);
			camerabox.setRotZ(camerabox.getRotZ()+speed);
		}
		
		checkBounds();
		
		float position = (float) tunnelcube.getZ() - speed;
			tunnelcube.setZ(position);
		if (level_loaded){
			float position2 = (float) level.getZ() - speed;
			level.setZ(position2);
		}
		
		if (speed > 100) speed = 0;		
			
		}
		else{
			double n = getCurrentCamera().getZ();
			getCurrentCamera().setZ( n++ );
		}
	}

	public void stop(){
		stop_all = true;
		touchenabled = false;
	}
	
	private void create_level_Mat(int nr){
		
		levelMat = new Material();
		levelMat.setDiffuseMethod(new DiffuseMethod.Lambert(1.0f));
		levelMat.setColorInfluence(0);
		levelMat.enableLighting(true);
		
		try {
			Texture t = new Texture("level", getContext().getResources().getIdentifier("level_"+nr, "drawable","com.hallway"));
			levelMat.addTexture(t);
		}catch(TextureException t){
			t.printStackTrace();
		}
	}
	
	private void showLevel(int nr){
		
		create_level_Mat(nr);
		
		levelPlane.setName("levelplane");
		levelPlane.setTransparent(true);
		levelPlane.setMaterial(levelMat);
		levelPlane.setVisible(true);
		addChild(levelPlane);
	}

	private void hideLevel(){
		levelPlane.setVisible(false);
		removeChild(levelPlane);
		
	}
	
	public void onTouch(MotionEvent me){
	
		if (touchenabled){
		
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				scaling = false;
			}
		
			if (me.getAction() == MotionEvent.ACTION_UP) {
			    
			}
			
			if (me.getAction() == MotionEvent.ACTION_MOVE && !scaling) {
	        	coordx = me.getX();
	        	coordy = me.getY();
	        	
	        	float xd,yd = 0;
	            
	        	half_width = getCurrentViewportWidth() / 2;
	    		half_height = getCurrentViewportHeight() / 2;
	    		
	    		float factor = .3f;
	    		
	        	if (coordx < half_width) 
	        	{
	        		xd = (float) getCurrentCamera().getPosition().x + factor;
	        	}else{ 
	        		xd = (float) getCurrentCamera().getPosition().x - factor;
	        	}
	            if (coordy < half_height) 
	        	{
	        		yd = (float) getCurrentCamera().getPosition().y + factor;
	        	}else{ 
	        		yd = (float) getCurrentCamera().getPosition().y - factor;
	        	}
	            
	        	getCurrentCamera().setX(xd);
	        	getCurrentCamera().setY(yd);
	        	
	        	
	        	getCurrentCamera().setLookAt(xd,yd,0);
	        } 
		}
	}

	public void onScale(float i) {
		scaling = true;
		campos = getCurrentCamera().getPosition(); 
    	
		//if (campos.z < 500 && campos.z > 0){
//		if (oldi > i)
//    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z -= 1);
//    	if (oldi < i)
//    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z += 1);
//    	oldi = i;
//		
    	/*}else{
			if(campos.z > 500) campos.z = 499;
			if(campos.z < 0) campos.z = 1;
		}*/
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		((Starter) mContext).showLoader();
		
		super.onSurfaceCreated(gl, config);
		((Starter) mContext).hideLoader();
	}
	
	private void checkBounds(){
		if (camerabox.getX() >=size)
		{ 
			camerabox.setX(size);
			getCurrentCamera().setPosition(camerabox.getPosition());
		}
		if( camerabox.getX() < -size)
		{
			camerabox.setX(-size);
			getCurrentCamera().setPosition(camerabox.getPosition());
		}
		
		if (camerabox.getY() >=size)
		{
			camerabox.setY(size);
			getCurrentCamera().setPosition(camerabox.getPosition());
		}
		
		if (camerabox.getY() <=-size){
			camerabox.setY(-size);
			getCurrentCamera().setPosition(camerabox.getPosition());
		}
		
		
	}

	public void stopPlayer(){
		
}
