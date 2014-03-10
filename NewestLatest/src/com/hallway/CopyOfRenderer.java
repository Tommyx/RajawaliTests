package com.hallway;

import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import rajawali.parser.Loader3DSMax;
import rajawali.parser.LoaderAWD;
import rajawali.parser.LoaderMD2;
import rajawali.parser.ParsingException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.animation.Animation3D;
import rajawali.animation.mesh.VertexAnimationObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.MeshExporter;
import rajawali.util.exporter.SerializationExporter;

public class CopyOfRenderer extends RajawaliRenderer {

//	//		Log.d("path", Environment.getExternalStorageDirectory().toString() );
//
//	private float time = 0;
//	private PointLight mLight; 
//	Object3D cube, sphere, plane, landscape, tree, tree2;
//    float touchTurn;
//	float touchTurnUp;
//    Object3D trees[] = new Object3D[2];
//    Loader3DSMax treeParser[] = new Loader3DSMax[3];
//	ArrayList<String> models = new ArrayList<String>();
//	ArrayList<String> materials = new ArrayList<String>();
//	ArrayList<String> textures = new ArrayList<String>();
//	ArrayList<Object3D> objects = new ArrayList<Object3D>();
//	float coordx = 0;
//	float coordy = 0;
//	float half_width  = 0; 
//	float half_height = 0; 
//	float oldi = 0; 
//	public boolean touchenabled = false;
//	Object3D[] clouds;
//	int numClouds = 50;
//	boolean scaling = false;
//	Vector3 camrot, campos = new Vector3();
//	Material streammat;
//	float mattime = 0.0f;
//	private boolean enableClouds = false;
//	MediaPlayer mP;
//	VertexAnimationObject3D animobj; 
//	
	public CopyOfRenderer(Context context) {
		super(context);
		setFrameRate(60);
	}
	
//	public void listRaw(){
//	    Field[] fields=R.raw.class.getFields();
//	    for(int count=0; count < fields.length; count++){
//	     models.add(fields[count].getName());
//	    }
//	}
//	
//	private void loadCube(int nr){
//		
//		
//		
//	}
//	
//	private void getModels(){
//		listRaw();
//		
//		for(String s : models){
//			if (s.endsWith("md")){
//				
//				Loader3DSMax parser = new Loader3DSMax(this, getContext().getResources().getIdentifier(s, "raw","com.hallway"));
//				Material mat = new Material();
//				mat.setColorInfluence(0);
//				
//				try {
//					mat.addTexture(new Texture(s, getContext().getResources().getIdentifier("stone_wall", "drawable","com.hallway")));
//				//	mat.addTexture(new NormalMapTexture("stone_wall_nm", getContext().getResources().getIdentifier("stone_wall", "drawable","com.hallway")));
//				}catch(TextureException t){
//					t.printStackTrace();
//				}
//				
//				try {
//					parser.parse();
//				} catch(ParsingException e) {
//					e.printStackTrace();
//				}
//				
//				Object3D obj = parser.getParsedObject();
//				obj.setScale(100);
//				obj.setMaterial(mat);
//			    Serialize(obj, s+"serial");
//
//				addChild(obj);
//			}
//		}
//	}
//	
//	public void Serialize(Object3D o, String name)
//	{
//	    MeshExporter exporter = new MeshExporter(o);
//	    SerializationExporter ser = new SerializationExporter();
//	    exporter.export(name, ser.getClass());
//
////		
//	}
//	
//	private void createClouds(int num){
//
//	    Plane cloud = new Plane(1,1,1,1);
//        cloud.setDoubleSided(true);
//        cloud.setTransparent(true);
//        cloud.setBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
//        
//        
//        Texture texture = new Texture("cloud", R.drawable.cloud);
//        Material cloudMat = new Material(); 
//        cloudMat.setColorInfluence(0);
//        
//        try{
//        	cloudMat.addTexture(texture);
//        }catch(TextureException t){
//        	t.printStackTrace();
//        }
//        
//        cloud.setMaterial(cloudMat);
//        float deg =  (float) Math.PI / 180;
//        
//        clouds = new Object3D[num];
//        
//        for ( int i = 0; i < num; i++ ) {
//
//        	clouds[i] = cloud.clone();
//        	
//        	float scale = 80+ (float) (Math.random() * 80.f);
//        	
//        	clouds[i].setPosition(-100 + Math.random()*300,50+Math.random()*50,-400 + Math.random()*800);
//        	clouds[i].setRotation(30*deg,30*deg, Math.random() * (float) Math.PI);
//        	clouds[i].setScale(scale,scale,0);
//
//        	addChild(clouds[i]);
//        }
//    }
//    
//	private void createSkyBox(){
//		try {
//			getCurrentScene().setSkybox(R.drawable.posx, R.drawable.negx,
//					R.drawable.posy, R.drawable.negy, R.drawable.posz,
//					R.drawable.negz, 1000);
//		}
//		catch(TextureException t) {
//			t.printStackTrace();
//		}
//	}
//	
//	private void createHallWay(){
//		
//		Loader3DSMax parser = new Loader3DSMax(this, R.raw.tunnel);
//		Material mat = new Material();
//		mat.setDiffuseMethod(new DiffuseMethod.Lambert());
//		mat.enableLighting(true);
//		mat.setColorInfluence(0);
//		
//		try {
//			Texture diffuseT = new Texture("stone_wall", R.drawable.stone_wall);
//			NormalMapTexture normalmapT = new NormalMapTexture("stone_wallnm", R.drawable.stone_wall_nm);
//			diffuseT.setRepeat(4, 4);
//			normalmapT.setRepeat(4, 4);
//			mat.addTexture(diffuseT);
//			mat.addTexture(normalmapT);
//		}catch(TextureException t){
//			t.printStackTrace();
//		}
//		
//		try {
//			parser.parse();
//		} catch(ParsingException e) {
//			e.printStackTrace();
//		}
//		
//		Object3D obj = parser.getParsedObject();
//		obj.setDoubleSided(true);
//		obj.setScale(5);
//		obj.setMaterial(mat);
//	    objects.add(obj);
//	}
//	
//	private void createMirror(){
//		
//		LoaderMD2 parser = new LoaderMD2(mContext.getResources(),
//				mTextureManager, R.raw.breaker);
//		try {
//			parser.parse();
//
//			animobj = (VertexAnimationObject3D) parser
//					.getParsedAnimationObject();
//			animobj.setScale(.07f);
//			animobj.setRotY(90);
//			animobj.setY(-1);
//
//				
//		
//		
//		Material mat = new Material();
//		mat.setColorInfluence(0);
//		mat.enableLighting(true);
//
//		addChild(animobj);
//		animobj.setMaterial(mat);
//		animobj.play();
//
//		try {
//			Texture t = new Texture("stone", R.drawable.stone_wall);
//			mat.addTexture(t);
//		}catch(TextureException t){
//			t.printStackTrace();
//		} 
//		} catch (ParsingException e) {
//		e.printStackTrace();
//	}
//	
//	}
//
//	public void stopPlayer(){
//		
//		if (mP != null){
//			mP.stop();
//		}
//	}
//
//	private void getModelsSer(){
//		listRaw();
//		ObjectInputStream ois;
//		Object3D obj;
//		
//		try {
//				ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.serial));
//				obj = new Object3D((SerializedObject3D) ois.readObject());
//				
//				Material mat = new Material();
//				mat.setColorInfluence(0);
//				mat.enableLighting(true);
//				
//				try {
//					Texture t = new Texture("stone", R.drawable.stone_wall);
//					mat.addTexture(t);
//				}catch(TextureException t){
//					t.printStackTrace();
//				}
//			
//				obj.setScale(30);
//				obj.setMaterial(mat);
//				objects.add(obj);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
//	
//	private void drawObjects(int count, Vector3 move){
//		
//		for (Object3D obj : objects){
//    		for (int i = 0; i<count;i++){
//		
//				Object3D mod = obj.clone();
//				Vector3 pos = mod.getPosition();
//				mod.setPosition(pos.x + move.x * i,pos.y + move.y * i, pos.z + move.z * i);
//				addChild(mod);
//    		}
//		}
//	}
//	
//	protected void initScene() {		
//		
//		//mP = MediaPlayer.create(getContext(), R.raw.loop2);
//		
//		mLight = new PointLight();
//		mLight.setPosition(0,100,0);
//		mLight.setColor(1.0f, 1.0f, 1.0f);
//		mLight.setPower(10f);
//		
//		getCurrentScene().addLight(mLight);
//		getCurrentScene().setBackgroundColor(0, 0, 0, 0);
//		getCurrentCamera().setPosition( 0,1,-10);
//		getCurrentCamera().setRotation( 0,0,0);
//		getCurrentCamera().setLookAt(   0,1,0);
//		
////		Object3D Plane = new Plane(200,200,1,1);
////		Plane.setDoubleSided(true);
////		Plane.setRotation(-90,0,0);
////		Plane.setPosition(0,-5,0);
////		Material PlaneMat = new Material();
////		
////		PlaneMat.enableLighting(true);
////		PlaneMat.setDiffuseMethod(new DiffuseMethod.Lambert());
////		try {
////			Texture diffuseT = new Texture("stone_wall", R.drawable.stone_wall);
////			NormalMapTexture normalmapT = new NormalMapTexture("stone_wallnm", R.drawable.stone_wall_nm);
////			diffuseT.setRepeat(1, 1);
////			normalmapT.setRepeat(1, 20);
////			PlaneMat.addTexture(diffuseT);
////			PlaneMat.addTexture(normalmapT);
////		}catch(TextureException t){
////			t.printStackTrace();
////		}
////		
////		Plane.setMaterial(PlaneMat);
//		//addChild(Plane);
//		
//		createSkyBox();
//		//createHallWay();
//		createMirror();
//		
//		drawObjects(1, new Vector3(0,0,0));
//		//getModelsSer();
//		
//		if (enableClouds) {	createClouds(20); }	
//	}
//
//	@Override
//	public void onDrawFrame(GL10 glUnused) {
//		super.onDrawFrame(glUnused);
//		time=0.2f;		
//		
//	//	mLight.setPosition(0, 1, mLight.getZ()+time);
//		
//		if(enableClouds ){
//			for (Object3D i : clouds){
//				if (i.getZ() < -300){ i.setZ(i.getZ() + 1000);}
//				float position = (float) i.getZ() - time;
//				i.setZ(position);
//				if (time > 2) time = 0;		
//			}
//		}
//	}
//
//	
//	public void onTouch(MotionEvent me){
//	
//		if (touchenabled){
//		
//			if (me.getAction() == MotionEvent.ACTION_DOWN) {
//				scaling = false;
//			}
//		
//			if (me.getAction() == MotionEvent.ACTION_UP) {
//			    
//			}
//			
//			if (me.getAction() == MotionEvent.ACTION_MOVE && !scaling) {
//	        	coordx = me.getX();
//	        	coordy = me.getY();
//	        	
//	        	float xd,yd = 0;
//	            
//	        	half_width = getCurrentViewportWidth() / 2;
//	    		half_height = getCurrentViewportHeight() / 2;
//	    		
//	    		float factor = 300;
//	    		
//	        	if (coordx < half_width) 
//	        	{
//	        		xd = (float) getCurrentCamera().getRotation().y + (-half_width + Math.abs(coordx)) / factor; 
//	        	}else{ 
//	        		xd = (float) getCurrentCamera().getRotation().y + (Math.abs(coordx)-half_width) / factor;
//	        	}
//	            if (coordy < half_height) 
//	        	{
//	        		yd = (float) getCurrentCamera().getRotation().z + (-half_height + Math.abs(coordy)) / factor; 
//	        	}else{ 
//	        		yd = (float) getCurrentCamera().getRotation().z + (Math.abs(coordy)-half_height) / factor;
//	        	}
//	            
//	        	getCurrentCamera().setRotation( 0,xd,yd);
//	        } 
//		}
//	}
//
//	public void onScale(float i) {
//		scaling = true;
//		campos = getCurrentCamera().getPosition(); 
//    	
//		//if (campos.z < 500 && campos.z > 0){
//		if (oldi > i)
//    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z -= 1);
//    	if (oldi < i)
//    			getCurrentCamera().setPosition(campos.x, campos.y, campos.z += 1);
//    	oldi = i;
//		
//    	/*}else{
//			if(campos.z > 500) campos.z = 499;
//			if(campos.z < 0) campos.z = 1;
//		}*/
//	}
}
