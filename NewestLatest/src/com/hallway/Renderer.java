package com.hallway;

import java.io.IOException;
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
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.opengl.GLES20;
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
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.renderer.plugins.Plugin;
import rajawali.util.MeshExporter;
import rajawali.util.exporter.SerializationExporter;

public class Renderer extends RajawaliRenderer {

//		Log.d("path", Environment.getExternalStorageDirectory().toString() );

	IBoundingVolume bbox2 ;
	boolean stop_all, level_loaded, unloaded = false;
	private float speed, speed2 = 0;
	int size = 4;
	int numclones = 1;
	int cubesize = 60*numclones;
	float score = -100;
	boolean rotate, rotatenow = false;
	private PointLight mLight; 
    float touchTurn;
	float touchTurnUp;
	
	Material levelMat;
	Material scoreMat;
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
	
	
	public boolean touchenabled = true;
	boolean scaling = false;
	
	Vector3 camrot, campos = new Vector3();
	
	MediaPlayer mP;
	
	Object3D tunnelcube;
	Object3D level;
	Object3D colliderObj;
	
	VertexAnimationObject3D levelPlane = new VertexAnimationObject3D();
	Plane scorePlane = new Plane(4,.5f,1,1);
	
	Bitmap mScoreBitmap;
	AlphaMapTexture mScoreTexture;
	Material stdMat,scoreMaterial, collidermat; 

	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	private String[] listFiles(String dirFrom) throws IOException {
        AssetManager am = getContext().getAssets();
        String fileList[];
        int numItems = am.list(dirFrom).length;
		fileList = new String[numItems];
		if (fileList != null)
            {   
                for ( int i = 0;i<fileList.length;i++)
                {
                    Log.d("",fileList[i]); 
                }
            }
		return fileList;
    }
	
	public void createStdMat(){
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
	
	public void createColliderMat(){
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
	
	private void createScoreMat(){
		scoreMat = new Material();
		scoreMat.enableLighting(true);
		scoreMat.setDiffuseMethod(new DiffuseMethod.Lambert());
		mScoreBitmap = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		mScoreTexture = new AlphaMapTexture("timeTexture", mScoreBitmap);
		try {
			scoreMat.addTexture(mScoreTexture);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		
		scoreMat.setColorInfluence(1);
		
		scorePlane.setMaterial(scoreMat);
		scorePlane.setDoubleSided(true);
		scorePlane.setColor(Color.WHITE);
		scorePlane.setPosition(-5,5,1);
		
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
		obj.setScale(1);
		obj.setDoubleSided(true);
		obj.setMaterial(stdMat);
		
		getCurrentScene().addChild(obj);
		return obj;
	}
	
	private Object3D loadCubeAWD(int nr){
		
		LoaderAWD parser = new LoaderAWD(getContext().getResources(), getTextureManager() , getContext().getResources().getIdentifier("cube_"+nr, "raw","com.hallway"));
		
		try {
			parser.parse();
		} catch(ParsingException e) {
			e.printStackTrace();
		}
		
		Object3D obj = parser.getParsedObject();
		getCurrentScene().addChild(obj);
		return obj;
	}
	
	private Object3D getColliders(int nr){
		
		String name = "cube_"+nr+"_colliders";
		
		LoaderAWD parser = new LoaderAWD(getContext().getResources(), getTextureManager() , getContext().getResources().getIdentifier(name, "raw","com.hallway"));
		
		try {
			parser.parse();
		} catch(ParsingException e) {
			e.printStackTrace();
		}
		
		Object3D obj = parser.getParsedObject();
		getCurrentScene().addChild(obj);
		return obj;
	}
	
	private void setColliders(Object3D level_object){ 
		
		for (int i=0; i<level_object.getNumChildren(); i++)
		{
			colliders.add(level_object.getChildAt(i));
		}
	}
	
	private void loadLevel(int levelnr){
		
		level = loadCubeAWD(levelnr); 
		colliderObj = getColliders(levelnr);
		setColliders(colliderObj);
		level.setZ(100);
		level.setMaterial(stdMat);
		level_loaded = true;
		level.setVisible(true);
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
	    colliders = new ArrayList<Object3D>();
	}
	
	protected void initScene() {
		//mP = MediaPlayer.create(getContext(), R.raw.loop2);
		
		mLight = new PointLight(); 
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(20f);
		
		createStdMat();
		createColliderMat();
		createScoreMat();
		
		camerabox = new Cube(.1f);
		camerabox.setMaterial(stdMat);
		getCurrentScene().addChild(camerabox);
		camerabox.setVisible(false);
		tunnelcube = loadCubeAWD(1);
		Log.d("size", Double.toString(tunnelcube.getScaleY()));
		
		//createSkyBox();
		
		getCurrentScene().addLight(mLight);
		getCurrentScene().setBackgroundColor(0, 0, 0, 0);
		getCurrentCamera().setPosition( 0,1,-20);
		getCurrentCamera().setRotation( 0,0,0);
		getCurrentCamera().setLookAt(   0,1,0);	
		getCurrentCamera().setFarPlane(1500);	
		
		create_level_Mat(1);
	//	load_plane_anim();
	//	levelPlane.setMaterial(stdMat);
	//	levelPlane.setVisible(true);
	//	levelPlane.setZ(-9);
	//	levelPlane.setY(1);
	//	addChild(levelPlane);
	//	levelPlane.play(true);
		loadLevel(1);
	}
	
	private void createSkyBox(){
		try {
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx,
					R.drawable.posy, R.drawable.negy, R.drawable.posz,
					R.drawable.negz);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
	}
	
	private void showScore(float score){
	
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		speed=0.3f;		
		score+=1;
		
		if (!stop_all)
		{
			if (score == -99){
				loadLevel(1);
				showLevelPlane(1);
			}
			
			if (score == 0){
				hideLevelPlane();
				scorePlane.setVisible(true);
				loadLevel(1);
			}
			
			showScore(score);
				
			camerabox.setPosition( getCurrentCamera().getPosition().x,
					getCurrentCamera().getPosition().y,
					getCurrentCamera().getPosition().z);
		
			mLight.setPosition( getCurrentCamera().getPosition().x,
					getCurrentCamera().getPosition().y-1,
					getCurrentCamera().getPosition().z + 40);
			
			
			if (score == 1000){ 
				rotationdegree = (float) getCurrentCamera().getRotY();
				rotatenow = true;
				speed = 1;
			}
		
			if (score == 2000) {
				unloadLevel();
				showLevelPlane(2);
			}
			
			if (score == 2100){
				hideLevelPlane();
				loadLevel(2);
			}
		
			if (rotatenow){
				if ( rotationdegree <= 180) 
					rotationdegree+=speed;  
				else { rotatenow = false; 
					rotationdegree = 0;
				 	 }
			}
			
			
			if (rotatenow){ 
				getCurrentCamera().setRotX(getCurrentCamera().getRotX() + speed);
				camerabox.setRotZ(camerabox.getRotZ()+speed);
			}
			
			
//			Repeat Tunnel		
			if (tunnelcube.getZ() < -60){ 
				tunnelcube.setZ(tunnelcube.getZ()+60);
			}

			checkBounds();
			
			float position = (float) tunnelcube.getZ() - speed;
			tunnelcube.setZ(position);
			
			if (level_loaded){
				float position2 = (float) level.getZ() - speed;
				level.setZ(position2);
				
				for (Object3D col : colliders){
					
					IBoundingVolume bbox = col.getGeometry().getBoundingBox();
					bbox.transform(col.getModelMatrix());
					bbox2= camerabox.getGeometry().getBoundingBox();
					bbox2.transform(camerabox.getModelMatrix());
					
					mBoxIntersect = bbox.intersectsWith(bbox2);
//						
//					if (mBoxIntersect){
//						Log.d("HIT!!!!!!!!", "HITTTTTT");
//						stop();
//					}
				
					if (col.getZ()< -40) {
						getCurrentScene().removeChild(col);
						colliders.remove(col);
					}
				}	
			}
		}else{
			double n = tunnelcube.getZ();
			getCurrentCamera().setPosition(0,1,-20);
			getCurrentCamera().setRotation(0,0,0);
			level.setVisible(false);	
			
			if(n> -1000){ 
				speed2+=.1;
				tunnelcube.setZ( n-speed2 );
				showLevelPlane(99);
			}
			
			if (!unloaded){
				hideLevelPlane();
				reset();
			}
		}
	}
	
	private void reset(){
		
		level.setVisible(false); 
		getCurrentScene().removeChild(level);
		tunnelcube.setZ(0);
		stop_all = false;
		score = -100;
		unloaded = false;

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
	
	private void showLevelPlane(int nr){
		
		
		levelPlane.setVisible(true);
	}
	
	private void hideLevelPlane(){
		levelPlane.setVisible(false);
		getCurrentScene().removeChild(levelPlane);
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
    	
		if (campos.z < 500 && campos.z > 0){
//		if (oldi > i)
//    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z -= 1);
//    	if (oldi < i)
//    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z += 1);
//    	oldi = i;
//		
    	}else{
			if(campos.z > 500) campos.z = 499;
			if(campos.z < 0) campos.z = 1;
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		((Starter) mContext).showLoader();
		
		super.onSurfaceCreated(gl, config);
		((Starter) mContext).hideLoader();
	}
	
	private void checkBounds()
	{
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
}
