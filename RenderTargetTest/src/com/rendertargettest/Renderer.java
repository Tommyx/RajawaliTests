package com.rendertargettest;

import java.io.Console;
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

	private Material mMaterial, PlaneMat;
	private float time = 0;
	private DirectionalLight mLight; 
	Object3D cube, sphere, plane, landscape, tree, tree2;
    float touchTurn;
	float touchTurnUp;
    Object3D trees[] = new Object3D[2];
	Loader3DSMax treeParser[] = new Loader3DSMax[2];
	float coordx = 0;
	float coordy = 0;
	float half_width  = 0; 
	float half_height = 0; 
	float oldi = 0; 
	boolean scaling = false;
	Vector3 camrot, campos = new Vector3();
	private ExampleParticleSystem mParticleSystem;
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
	private void createLandscape(){
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.altar);
		try {
			SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
			terrainParams.setScale(5f,25f, 5f);
			terrainParams.setDivisions(128);
			terrainParams.setTextureMult(1);
			terrainParams.setColorMapBitmap(bmp);
			landscape = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams);
			landscape.setPosition(0,-15,0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Material Landmat = new Material();
			
		Landmat.enableLighting(true);
		Landmat.setDiffuseMethod(new DiffuseMethod.Lambert());
		Landmat.setColorInfluence(0);
		Landmat.setColor(0x003300);
		
		try {
			Texture t = new Texture("treetex", R.drawable.altar);
			Landmat.addTexture(t);
		}catch(TextureException t){
			t.printStackTrace();
		}
		landscape.setMaterial(Landmat);
		addChild(landscape);
	}
	
	private void loadClouds(int num){

        Object3D clouds = new Plane(1,1,1,1);
        clouds.setDoubleSided(true);
        clouds.setTransparent(true);
        
        
        Texture texture = new Texture("cloud", R.drawable.cloud);
        Material cloudMat = new Material(); 
        cloudMat.setColorInfluence(0);
        
        try{
        	cloudMat.addTexture(texture);
        }catch(TextureException t){
        	t.printStackTrace();
        }
        
        clouds.setMaterial(cloudMat);
        float deg =  (float) Math.PI / 180;
        
        
        for ( int i = 0; i < num; i++ ) {

        	Object3D planes = clouds.clone();
        	
        	float x = -100 + (float)Math.random() * 100;
        	float y = (float)Math.random() * 30;
        	float z = -100 + i*10;
        	float scale = (float) (Math.random() * 100.5f);
        	
        	planes.setPosition(-150 + Math.random()*300,100+Math.random()*20,-150 + Math.random()*300);
			
        	//planes.setPosition(x,y,z);
        	planes.setRotation(30*deg,0, Math.random() * (float) Math.PI);
        	
        	planes.setScale(scale,scale,scale);
        	addChild(planes);
        }
        
      
    }
    
	
	private void scene1(){
		getCurrentScene().addLight(mLight);
		getCurrentScene().setBackgroundColor(0xeeeeee);
		
		createLandscape();
		loadClouds(100);
		
		treeParser[0] = new Loader3DSMax(this, R.raw.tree);
		treeParser[1] = new Loader3DSMax(this, R.raw.tree2);
		
		Material[] treemat = new Material[2];
		treemat[0] = new Material();
		treemat[1] = new Material();
		
		try {
			treemat[0].addTexture(new Texture("treetex", R.drawable.tree2snow));
			treemat[1].addTexture(new Texture("treetex2", R.drawable.treesnow));
		}catch(TextureException t){
			t.printStackTrace();
		}
		
		for (int i = 0; i < 2; i++){
					
			try {
				treeParser[i].parse();
			} catch(ParsingException e) {
				e.printStackTrace();
			}
		
		treemat[i].setColorInfluence(0);
		
		trees[i] = treeParser[i].getParsedObject();
		trees[i].setMaterial(treemat[i]);
		
		for (float j = 0; j<=20; j++){
			Object3D t = trees[i].clone();
			t.setScale(8, 8, 8);
			t.setPosition(-150 + Math.random()*300,-2+Math.random()*2,-150 + Math.random()*300);
			addChild(t);
			t.setRotation(90,  0,  Math.random()*360);
			
		}
	}
	}

	protected void initScene() {
		
		
		mLight = new DirectionalLight(0, 0, 0f);
		mLight.setPosition(0,50,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(1);
		
		setFogEnabled(true);
		
		getCurrentCamera().setPosition(0,75,300);
		getCurrentCamera().setLookAt(0,0,0);
		getCurrentCamera().setFogFar(100);
		getCurrentCamera().setFarPlane(1000);
		
		scene1();
		
		
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		time+=0.1;
	//	getCurrentCamera().setRotY(time);
		
	}
	
	 	
	public void onTouch(MotionEvent me){
	
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
    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z+=0.5f);
        	}else
        		getCurrentCamera().setPosition(campos.x, campos.y, campos.z-=0.5f);
		}
    		
		if (me.getAction() == MotionEvent.ACTION_MOVE && !scaling) {
        	coordx = me.getX();
        	coordy = me.getY();
        	
        	float xd,yd = 0;
            
        	half_width = getCurrentViewportWidth() / 2;
    		half_height = getCurrentViewportHeight() / 2;
    		
        	if (coordx < half_width) 
        	{
        		xd = (float) getCurrentCamera().getRotation().y + (-half_width + Math.abs(coordx)) / 100; 
        	}else{ 
        		xd = (float) getCurrentCamera().getRotation().y + (Math.abs(coordx)-half_width) / 100;
        	}
            if (coordy < half_height) 
        	{
        		yd = (float) getCurrentCamera().getRotation().z + (-half_height + Math.abs(coordy)) / 100; 
        	}else{ 
        		yd = (float) getCurrentCamera().getRotation().z + (Math.abs(coordy)-half_height) / 100;
        	}
            
        	getCurrentCamera().setRotation( 0,xd,yd);
        } 

	}

	public void onScale(float i) {
		scaling = true;
		campos = getCurrentCamera().getPosition(); 
    	
		if (campos.z < 300 && campos.z > 0){
		if (oldi > i)
    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z -= 1);
    	if (oldi < i)
    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z += 1);
    	oldi = i;
		}else{
			if(campos.z > 300) campos.z = 299;
			if(campos.z < 0) campos.z = 1;
		}
	}
}
