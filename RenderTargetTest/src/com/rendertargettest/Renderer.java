package com.rendertargettest;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
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
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.SerializedObject3D;
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
import rajawali.util.MeshExporter;
import rajawali.util.exporter.SerializationExporter;

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
	ArrayList<Object3D> objects = new ArrayList<Object3D>();
	float coordx = 0;
	float coordy = 0;
	float half_width  = 0; 
	float half_height = 0; 
	float oldi = 0; 
	public boolean touchenabled = false;
	Object3D[] clouds;
	int numClouds = 50;
	boolean scaling = false;
	Vector3 camrot, campos = new Vector3();
	Material streammat;
	float mattime = 0.0f;
	private boolean enableClouds = true;
	MediaPlayer mP;
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
	private void createClouds(int num){

	    Plane cloud = new Plane(1,1,1,1);
        cloud.setDoubleSided(true);
        cloud.setTransparent(true);
        cloud.setBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        
        
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
			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx,
					R.drawable.posy, R.drawable.negy, R.drawable.posz,
					R.drawable.negz);
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
			    Serialize(obj, s+"serial");

				addChild(obj);
			}
		}
		
	}
	
	private void getModelsSer(){
		listRaw();
		ObjectInputStream ois;
		Object3D obj;
		
		for(String s : models)
		{
			if (s.endsWith("mdserial") )
			{
				if (s.startsWith("stream")){
					obj = flowstream();
					objects.add(obj);
				}
				else{
				try {
					ois = new ObjectInputStream(mContext.getResources().openRawResource(mContext.getResources().getIdentifier(s, "raw","com.rendertargettest")));
					obj = new Object3D((SerializedObject3D) ois.readObject());
					
					Material mat = new Material();
					mat.setColorInfluence(0);
									
					try {
						mat.addTexture(new Texture(s, getContext().getResources().getIdentifier(s, "drawable","com.rendertargettest")));
					}catch(TextureException t){
						t.printStackTrace();
					}
				
					obj.setScale(30);
					obj.setMaterial(mat);
					objects.add(obj);
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			}
		}
	}
	
	private void drawObjects(int count){
		
		for (Object3D obj : objects){
			addChild (obj);
		}
		
		for (int i = 0; i<count;i++){
		
			for (Object3D obj : objects){
				Object3D mod = obj.clone();
				if (i % 2 == 0){
					mod.setPosition(mod.getX()+125,mod.getY()+30,mod.getZ()+50);
					mod.setRotY(25);
				}else{
					mod.setPosition(mod.getX()-75,mod.getY()-50,mod.getZ()-50);
					mod.setRotY(-25);
				}
				addChild(mod);
			}
		}	
	}
	
	private Object3D flowstream(){

		ObjectInputStream ois;
		Object3D obj;
		
			try {
				ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.stream_mdserial));
		
			obj = new Object3D((SerializedObject3D) ois.readObject());
			obj.setDoubleSided(true);
			obj.setTransparent(true);
		
			streammat = new Material(new CustomRawVertexShader(), new CustomRawFragmentShader());
			streammat.enableTime(true);
			streammat.enableLighting(true);
			
			streammat.addTexture(new Texture("stream_mdserial", R.drawable.stream_mdserial));
			
			obj.setScale(30);
			obj.setMaterial(streammat);
			obj.setTransparent(true);
			ois.close();
			
			return obj;
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			return null;
	}
	
	
	private void scene1(){
		getCurrentScene().addLight(mLight);
		getCurrentScene().setBackgroundColor(0xeeeeee);
		
		createSkyBox();
		getModelsSer();
		drawObjects(2);
		if (enableClouds){ createClouds(numClouds); }
		
		
	}
	
	public void stopPlayer(){
		mP.stop();
	}

	protected void initScene() {		
		
		
		mP = MediaPlayer.create(getContext(), R.raw.loop2);
		mP.start();
		mP.setLooping(true);
		
		mLight = new DirectionalLight(0, 0, 0f);
		mLight.setPosition(0,50,0);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(10);
		
		getCurrentCamera().setPosition( -30,60,260);
		getCurrentCamera().setRotation( 0,175,0);
		getCurrentCamera().setLookAt(   -30,55,0);
		getCurrentCamera().setFogFar(1000);
		getCurrentCamera().setFarPlane(5000);
		
		scene1();
		
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		time=0.2f;		
		mattime+=0.01f;
		
		if(enableClouds ){
			for (Object3D i : clouds){
				if (i.getZ() < -300){ i.setZ(i.getZ() + 1000);}
				float position = (float) i.getZ() - time;
				i.setZ(position);
				if (time > 2) time = 0;		
			}
		}
     	streammat.setTime(mattime);
		
	}

	public void Serialize(Object3D o, String name)
	{
	    MeshExporter exporter = new MeshExporter(o);
	    SerializationExporter ser = new SerializationExporter();
	    exporter.export(name, ser.getClass());

//		
	}
	
	public void onTouch(MotionEvent me){
	
		if (touchenabled){
		
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				scaling = false;
			}
		
			if (me.getAction() == MotionEvent.ACTION_UP) {
			    
			}
			
//			if (me.getAction() == MotionEvent.ACTION_POINTER_DOWN){
//				coordx = me.getX();
//	        	coordy = me.getY();
//	        	half_width = getCurrentViewportWidth() / 2;
//	    		half_height = getCurrentViewportHeight() / 2;
//	    		
//	    		campos = getCurrentCamera().getPosition(); 
//	    		
//	    		if (coordx < half_width) 
//	        	{
//	    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z-=1.1f);
//	    			Log.d("za", Double.toString(campos.z));
//	    			
//	        	}else
//	        		getCurrentCamera().setPosition(campos.x, campos.y, campos.z+=1.1f);
//	    			Log.d("za", Double.toString(campos.z));
//			}
	    		
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
