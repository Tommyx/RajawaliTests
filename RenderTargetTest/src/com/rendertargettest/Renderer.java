package com.rendertargettest;

import java.io.Console;
import java.util.Currency;

import javax.microedition.khronos.opengles.GL10;

import rajawali.parser.Loader3DSMax;
import rajawali.parser.ParsingException;
import rajawali.parser.fbx.LoaderFBX;

import rajawali.animation.Animation3D;
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
import rajawali.lights.DirectionalLight;
import rajawali.primitives.Plane;
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
	float oldposx = 0;
	float oldposy = 0;
	float fingerx=0; 
	boolean scaling = false;
	Vector3 camrot, campos = new Vector3();
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
		
	}
	
	private void createLandscape(){
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.terrain);

		try {
			SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
			terrainParams.setScale(1f,5f, 1f);
			terrainParams.setDivisions(128);
			terrainParams.setTextureMult(4);
			terrainParams.setColorMapBitmap(bmp);
			landscape = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams);
			landscape.setPosition(0,-5,0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Material Landmat = new Material();
		
		
		try{
			Landmat.enableLighting(true);
			Landmat.setDiffuseMethod(new DiffuseMethod.Lambert());
			Landmat.addTexture(new Texture("snow", R.drawable.meadow));
			Landmat.setColorInfluence(0);
			landscape.setMaterial(Landmat);
		}catch(TextureException t){
			t.printStackTrace();
		}
	}
	
	private void scene1(){
		
		getCurrentCamera().setPosition(0,5,20);
		getCurrentCamera().setLookAt(0,0,0);
		getCurrentCamera().setFogNear(-1);
		getCurrentCamera().setFogFar(10);
		getCurrentCamera().setFogColor(0xffffff);
		
		getCurrentScene().addLight(mLight);
		
		try {
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx,
					R.drawable.posy, R.drawable.negy, R.drawable.posz,
					R.drawable.negz);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		
		createLandscape();
		addChild(landscape);
		
		treeParser[0] = new Loader3DSMax(this, R.raw.tree);
		treeParser[1] = new Loader3DSMax(this, R.raw.tree2);
		
		Material[] treemat = new Material[2];
		treemat[0] = new Material();
		treemat[1] = new Material();
		
		try {
			treemat[0].addTexture(new Texture("treetex", R.drawable.tree));
			treemat[1].addTexture(new Texture("treetex2", R.drawable.tree2));
			
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
			t.setPosition(-80 + Math.random()*100,-2+Math.random()*2,-80 + Math.random()*100);
			addChild(t);
			t.setRotation(90,  0,  Math.random()*360);
			
		}
	}
	}

	protected void initScene() {
		setFogEnabled(true);
		mLight = new DirectionalLight(0, 0, 0f);
		mLight.setPosition(0,4,4);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(1);
		scene1();
		
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		time+=0.1;
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
    	getCurrentCamera().setPosition(campos.x, campos.y, i / 10);
	}
}
