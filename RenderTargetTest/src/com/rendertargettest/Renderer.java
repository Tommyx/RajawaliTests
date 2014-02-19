package com.rendertargettest;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Currency;

import javax.microedition.khronos.opengles.GL10;

import rajawali.parser.Loader3DSMax;
import rajawali.parser.LoaderAWD;
import rajawali.parser.ParsingException;
import rajawali.parser.fbx.LoaderFBX;

import rajawali.animation.Animation3D;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.Animation3D.RepeatMode;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.lights.DirectionalLight;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import rajawali.scene.RajawaliScene;
import rajawali.terrain.SquareTerrain;
import rajawali.terrain.TerrainGenerator;

public class Renderer extends RajawaliRenderer {

	//		Log.d("path", Environment.getExternalStorageDirectory().toString() );

	private Material mMaterial, PlaneMat;
	private float time = 0;
	private DirectionalLight mLight; 
	Object3D cube, sphere, plane, landscape, tree, tree2;
    float touchTurn;
	float touchTurnUp;
    Object3D trees[] = new Object3D[2];
	Loader3DSMax treeParser[] = new Loader3DSMax[3];
	ArrayList<String> models = new ArrayList<String>();
	ArrayList<String> materials = new ArrayList<String>();
	ArrayList<String> textures = new ArrayList<String>();
	float coordx = 0;
	float coordy = 0;
	float half_width  = 0; 
	float half_height = 0; 
	float oldi = 0; 
	public boolean touchenabled = false;
	Object3D[] clouds;
	int numClouds = 100;
	boolean scaling = false;
	Vector3 camrot, campos = new Vector3();
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
	private void createLandscape(){
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.sheep_md);
		try {
			SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
			terrainParams.setScale(5f,25f, 5f);
			terrainParams.setDivisions(128);
			terrainParams.setTextureMult(10);
			terrainParams.setColorMapBitmap(bmp);
			landscape = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams);
			landscape.setPosition(0,-15,0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Material Landmat = new Material();
			
		Landmat.enableLighting(true);
		Landmat.setDiffuseMethod(new DiffuseMethod.Lambert(0));
		Landmat.setColorInfluence(0);
		Landmat.setColor(0x003300);
		
		try {
			Texture t = new Texture("treetex", R.drawable.wiese3);
			Landmat.addTexture(t);
		}catch(TextureException t){
			t.printStackTrace();
		}
		landscape.setMaterial(Landmat);
		addChild(landscape);
	}
	
	private void createSky(){
	
		Sphere sky = new Sphere(1000, 30, 30);
		sky.setDoubleSided(true);
		
		Material skymat = new Material();
		skymat.setColorInfluence(0);
		try{
			skymat.addTexture(new Texture("sky", R.drawable.sky));
		}catch(TextureException t)
		{
			t.printStackTrace();
		}
		
		sky.setMaterial(skymat);
		addChild(sky);
	}
	
	private void createClouds(int num){

	    Plane cloud = new Plane(1,1,1,1);
        cloud.setDoubleSided(true);
        cloud.setTransparent(true);
        cloud.setBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
        
        
        Texture texture = new Texture("cloud", R.drawable.cloud);
        Material cloudMat = new Material(); 
        cloudMat.setColorInfluence(0);
        
        try{
        	cloudMat.addTexture(texture);
        }catch(TextureException t){
        	t.printStackTrace();
        }
        
        cloud.setMaterial(cloudMat);
        float deg =  (float) Math.PI / 180;
        
        clouds = new Object3D[num];
        
        for ( int i = 0; i < num; i++ ) {

        	clouds[i] = cloud.clone();
        	
        	float scale = 30+ (float) (Math.random() * 80.f);
        	
        	clouds[i].setPosition(-100 + Math.random()*300,50+Math.random()*50,-400 + Math.random()*800);
        	clouds[i].setRotation(30*deg,30*deg, Math.random() * (float) Math.PI);
        	clouds[i].setScale(scale,scale,0);

        	addChild(clouds[i]);
        }
    }
    
	private void createSkyBox(){
		try {
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx, R.drawable.posy, R.drawable.negy, R.drawable.posz, R.drawable.negz);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
	}
	
	public void listRaw(){
	    Field[] fields=R.raw.class.getFields();
	    for(int count=0; count < fields.length; count++){
	     models.add(fields[count].getName());
	    }
	}
	
	private void getModels(){
		listRaw();
		
		for(String s : models){
			if (s.endsWith("md")){
				
				Loader3DSMax parser = new Loader3DSMax(this, getContext().getResources().getIdentifier(s, "raw","com.rendertargettest"));
				Material mat = new Material();
				mat.setColorInfluence(0);
				
				try {
					mat.addTexture(new Texture(s, getContext().getResources().getIdentifier(s, "drawable","com.rendertargettest")));
				}catch(TextureException t){
					t.printStackTrace();
				}
				
				try {
					parser.parse();
				} catch(ParsingException e) {
					e.printStackTrace();
				}
				
				Object3D obj = parser.getParsedObject();
				obj.setScale(30);
				obj.setMaterial(mat);
				
				addChild(obj);
			}
		}
		
	}
	
	private void scene1(){
		getCurrentScene().addLight(mLight);
		getCurrentScene().setBackgroundColor(0xeeeeee);
		
		createSky();
		//createSkyBox();
		//createLandscape();
		getModels();
		createClouds(numClouds);
		
}

	protected void initScene() {		
		
		mLight = new DirectionalLight(0, 0, 0f);
		mLight.setPosition(0,50,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(10);
		
		setFogEnabled(true);
		
		getCurrentCamera().setPosition(120,50,190);
		getCurrentCamera().setRotation(0,180,0);
		getCurrentCamera().setLookAt(0,50,0);
		getCurrentCamera().setFogFar(100);
		getCurrentCamera().setFarPlane(2000);
		
		scene1();
		
		
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		time=0.1f;		
		
		for (Object3D i : clouds){
			if (i.getZ() < -300){ i.setZ(i.getZ() + 1000);}
			float position = (float) i.getZ() - time;
			i.setZ(position);
			if (time > 2) time = 0;		}
	//	getCurrentCamera().setRotY(time);
		
	}
	 	
	public void onTouch(MotionEvent me){
	
		if (touchenabled){
		
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				scaling = false;
			}
		
			if (me.getAction() == MotionEvent.ACTION_UP) {
			    
			}
			
			if (me.getAction() == MotionEvent.ACTION_POINTER_DOWN){
				coordx = me.getX();
	        	coordy = me.getY();
	        	half_width = getCurrentViewportWidth() / 2;
	    		half_height = getCurrentViewportHeight() / 2;
	    		
	    		campos = getCurrentCamera().getPosition(); 
	    		
	    		if (coordx < half_width) 
	        	{
	    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z+=0.1f);
	        	}else
	        		getCurrentCamera().setPosition(campos.x, campos.y, campos.z-=0.1f);
			}
	    		
			if (me.getAction() == MotionEvent.ACTION_MOVE && !scaling) {
	        	coordx = me.getX();
	        	coordy = me.getY();
	        	
	        	float xd,yd = 0;
	            
	        	half_width = getCurrentViewportWidth() / 2;
	    		half_height = getCurrentViewportHeight() / 2;
	    		
	    		float factor = 300;
	    		
	        	if (coordx < half_width) 
	        	{
	        		xd = (float) getCurrentCamera().getRotation().y + (-half_width + Math.abs(coordx)) / factor; 
	        	}else{ 
	        		xd = (float) getCurrentCamera().getRotation().y + (Math.abs(coordx)-half_width) / factor;
	        	}
	            if (coordy < half_height) 
	        	{
	        		yd = (float) getCurrentCamera().getRotation().z + (-half_height + Math.abs(coordy)) / factor; 
	        	}else{ 
	        		yd = (float) getCurrentCamera().getRotation().z + (Math.abs(coordy)-half_height) / factor;
	        	}
	            
	        	getCurrentCamera().setRotation( 0,xd,yd);
	        } 
		}
	}

	public void onScale(float i) {
		scaling = true;
		campos = getCurrentCamera().getPosition(); 
    	
		if (campos.z < 500 && campos.z > 0){
		if (oldi > i)
    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z -= 1);
    	if (oldi < i)
    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z += 1);
    	oldi = i;
		}else{
			if(campos.z > 500) campos.z = 499;
			if(campos.z < 0) campos.z = 1;
		}
	}
}
