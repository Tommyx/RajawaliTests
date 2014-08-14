package com.demo.colordice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.MotionEvent;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.Animation;
import rajawali.animation.Animation3D;
import rajawali.animation.EllipticalOrbitAnimation3D;
import rajawali.animation.IAnimationListener;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.methods.SpecularMethod;
import rajawali.materials.shaders.FragmentShader;
import rajawali.materials.shaders.VertexShader;
import rajawali.materials.shaders.fragments.texture.NormalMapFragmentShaderFragment;
import rajawali.materials.textures.ATexture;
import rajawali.materials.textures.CubeMapTexture;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.Matrix4;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.Loader3DSMax;
import rajawali.parser.ParsingException;
import rajawali.postprocessing.PostProcessingManager;
import rajawali.postprocessing.effects.BloomEffect;
import rajawali.postprocessing.effects.ShadowEffect;
import rajawali.postprocessing.passes.RenderPass;
import rajawali.postprocessing.passes.BlendPass.BlendMode;
import rajawali.postprocessing.passes.ShadowPass;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.terrain.SquareTerrain;
import rajawali.terrain.Terrain;
import rajawali.terrain.TerrainGenerator;
import rajawali.util.GLU;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener{
		
	private int[] mViewport;
	private int numParticles = 100;
	
	private double[] mNearPos4;
	private double[] mFarPos4;
	
	private float yrot = 45;
	private float time, uTime = 0;
	private float[] touch = new float[2];
	
	private float deg = (float) (Math.PI / 180);
	
	private boolean animrunning = false;
	private boolean loaded = false;
	private boolean enableclouds = true;

	private Vector3 mNearPos;
	private Vector3 mFarPos;
	private Vector3 mNewObjPos;
	private Matrix4 mViewMatrix;
	private Matrix4 mProjectionMatrix;

	private SquareTerrain mTerrain;
	
	private Object3D mSelectedObject;
	
	private PostProcessingManager mPostProcessingManager;
	private ObjectColorPicker mPicker;
	private Random random = new Random();
	
	private TranslateAnimation3D tanim;
	private RotateOnAxisAnimation ranim;
	
	public MediaPlayer mP;
	public Object3D wuerfel, particle ,mEmpty;
	Object3D[] clouds;
	public Camera c, temp;
	int numClouds = 10;
	
	Material mCube;		
	VertexShader v = new VertexShader(R.raw.vertex_shader_water);
	FragmentShader f = new FragmentShader(R.raw.fragmentshader_water2);
	DirectionalLight mLight = new DirectionalLight();
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}


	public void initScene() {

		initPicking();
		
		mLight = new DirectionalLight();
		mLight.setDirection(0, 0, 0);
		mLight.setPosition(0, 40, 20);
		mLight.setPower(1.0f);
		
		getCurrentScene().addLight(mLight);
		
		getCurrentCamera().setPosition(5,5,30);
		getCurrentCamera().setLookAt(0, -1, 0);
		getCurrentCamera().setFarPlane(150);
	
		createSkyBox();
		water();
		
		mPostProcessingManager = new PostProcessingManager(this);

		ShadowEffect shadowEffect = new ShadowEffect(getCurrentScene(), getCurrentCamera(), mLight, 1024);
		MyEffect bloomEffect = new MyEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0x4444444, 0x999999, BlendMode.ADD);
		shadowEffect.setShadowInfluence(.5f);
		mPostProcessingManager.addEffect(shadowEffect);
		mPostProcessingManager.addEffect(bloomEffect);
		
		bloomEffect.setRenderToScreen(true);
		shadowEffect.setRenderToScreen(true);
		
		loaded = true;
		
		createTerrain();
		createClouds(4);
		getCurrentScene().setBackgroundColor(0x557d9f);
		
		EllipticalOrbitAnimation3D anim = new EllipticalOrbitAnimation3D(
				new Vector3(0, 30, 0), new Vector3(0, 30, 3), 0, 359);
		anim.setRepeatMode(RepeatMode.INFINITE);
		anim.setDurationMilliseconds(100000);
		anim.setTransformable3D(mLight);
		getCurrentScene().registerAnimation(anim);
		anim.play();

//		
		
	}
	
	public void createTerrain(){
		
		
		
		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.map);

		try {
			
			SquareTerrain.Parameters terrainParams = SquareTerrain.createParameters(bmp);
		
			terrainParams.setScale(1f, -10f, 1f);
		
			terrainParams.setDivisions(128);
		
			terrainParams.setTextureMult(4);
		
			terrainParams.setColorMapBitmap(bmp);
		
			mTerrain = TerrainGenerator.createSquareTerrainFromBitmap(terrainParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mTerrain.setPosition(0,-5,-10);
		Material material = new Material(); 
		material.enableLighting(true);
		material.setDiffuseMethod(new DiffuseMethod.Lambert());
		
		try {
			Texture groundTexture = new Texture("ground", R.drawable.terrain_texture);
			material.addTexture(groundTexture);
			material.addTexture(new NormalMapTexture("groundNormalMap", R.drawable.terrain_texture_nm));
			material.setColorInfluence(0);
		} catch (TextureException e) {
			e.printStackTrace();
		}
		mTerrain.setMaterial(material);
		getCurrentScene().addChild(mTerrain);
		
		
	}
	
	
	private void water(){
		
		mCube = new Material(v,f);
		
		try{
			mCube.addTexture(new Texture("diffuse", R.drawable.water));
		}catch(TextureException e){
		}
		
		wuerfel = new Plane(100,100,1,1);
		wuerfel.setDoubleSided(true);
		wuerfel.setPosition(0,-10,0);
		wuerfel.setRotation(90,0,0);
		wuerfel.setMaterial(mCube);
		getCurrentScene().addChild(wuerfel);
		
	}
	
	private void createSkyBox(){
		
		int [] resourceIds = { R.drawable.posx, 
							   R.drawable.negx, 
							   R.drawable.posy, 
							   R.drawable.negy, 
							   R.drawable.posz, 
							   R.drawable.negz};
		
		CubeMapTexture m = new CubeMapTexture("sky", resourceIds);
		
		Material qm = new Material();
		Cube c = new Cube(140); 
		c.setDoubleSided(true);
		m.isSkyTexture(true);
		qm.setColorInfluence(0);
		
		try {
			qm.addTexture(m);
		}
		catch(TextureException t) {
			t.printStackTrace();
		}
		
		c.setMaterial(qm);
		getCurrentScene().addChild(c);
	}
	
	private void createClouds(int num){

	    Plane cloud = new Plane(1,1,1,1);
        cloud.setDoubleSided(true);
        cloud.setTransparent(true);
        //cloud.setBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        cloud.setRotation(90,90,0);
        Texture texture = new Texture("cloud", R.drawable.cloud);
        Material cloudMat = new Material(); 
        cloudMat.setColorInfluence(.0f);
        
        try{
        	cloudMat.addTexture(texture);
        }catch(TextureException t){
        	t.printStackTrace();
        }
        
        cloud.setMaterial(cloudMat);
        clouds = new Object3D[num];
        
        for ( int i = 0; i < num; i++ ) {

        	clouds[i] = cloud.clone();
        	clouds[i].setDoubleSided(true);
        	//clouds[i].setColor(0x000000 + random.nextInt(0xfffffff));
        	float scale = 15 + (float) (Math.random() * 30.f);
        	
        	clouds[i].setPosition(-80 + Math.random()*160,-10+Math.random()*20,-40 + Math.random()*40);
        	clouds[i].setRotation(30*deg, 30*deg, Math.random() * (float) Math.PI);
        	clouds[i].setScale(scale*2,scale,0);

        	getCurrentScene().addChild(clouds[i]);
        }
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
	
	public void loadCube()
	{
	
		mCube = new Material(v,f);
		mCube.setDiffuseMethod(new DiffuseMethod.Lambert());
		mCube.enableLighting(true);
		
		try{
			mCube.addTexture(new Texture("bla", R.drawable.water));
		//	mCube.addTexture(new Texture("bl2", R.drawable.water_nm));
		}catch(TextureException e){
		}
		
		mCube.setColorInfluence(0);
		mCube.setDiffuseMethod(new DiffuseMethod.Lambert());
		mCube.enableLighting(true);
		Loader3DSMax l = new Loader3DSMax(this, R.raw.dice);
		
		try {
			l.parse();
			wuerfel = l.getParsedObject();
			wuerfel = new Plane(20,20,3,3);
			wuerfel.setDoubleSided(true);
			wuerfel.setPosition(0,-3,0);
			wuerfel.setRotation(90,0,0);
			wuerfel.setMaterial(mCube);
			getCurrentScene().addChild(wuerfel);
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean onTouch(MotionEvent event) {
		touch[0] = event.getX();
		touch[1] = event.getY();
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			touch[0] = event.getX();
			touch[1] = event.getY();
			getObjectAt(touch[0],touch[1]);
			
			break;
		case MotionEvent.ACTION_MOVE:
			moveSelectedObject(touch[0],touch[1]);
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
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
		
		if (mSelectedObject.getName() == "wuerfel" ) 
		{
			Log.d("hitbtn", "hitid");
			
		}
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
//		if (loaded) {
//			Log.d("yrot", Float.toString(yrot));
//			waende[0].setRotation(0,-yrot,0);
//			waende[1].setRotation(0,+yrot,0);
//		}
	}
		
	@Override
	public void onRender(final double deltaTime) {
	//	mLight.setLookAt(mEmpty.getPosition());
		mPostProcessingManager.render(deltaTime);
		time=0.1f;
		uTime++;
		mCube.setTime(uTime);
		if(enableclouds ){
			for (Object3D i : clouds){
				if (i.getX() > 40){ i.setX(i.getX() - 80);}
				float position = (float) i.getX() + time;
				i.setX(position);
			}
		}
		
	}
}

