package com.demo.colordice;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.Animation3D;
import rajawali.animation.RotateOnAxisAnimation;
import rajawali.animation.Animation.RepeatMode;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.Matrix4;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.Loader3DSMax;
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
		
	private int[] mViewport;
	private int numParticles = 100;
	
	private double[] mNearPos4;
	private double[] mFarPos4;
	
	private float yrot = 45;
	
	private float deg = (float) (Math.PI / 180);
	
	private boolean animrunning = false;
	private boolean loaded = false;

	private Vector3 mNearPos;
	private Vector3 mFarPos;
	private Vector3 mNewObjPos;
	private Matrix4 mViewMatrix;
	private Matrix4 mProjectionMatrix;
	
	private Object3D mSelectedObject;
	
	private PostProcessingManager mEffects;
	private ObjectColorPicker mPicker;
	private Random random = new Random();
	
	private TranslateAnimation3D tanim;
	private RotateOnAxisAnimation ranim;
	
	public MediaPlayer mP;
	public Object3D wuerfel, particle;
	public Object3D[] waende = new Object3D[3];
	public Camera c, temp;
	
	
	public Renderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	public void initScene() {

		initPicking();
		c  = new Camera();
		DirectionalLight light = new DirectionalLight();
		
		light.setPower(1f);
		light.setPosition(0, 5, 10);
		light.setDirection(0,0,0);
		
		getCurrentScene().addLight(light);
    	
    	getCurrentCamera().setPosition(0,3,.001);
    	getCurrentCamera().setLookAt(new Vector3(0,0,0));
    	getCurrentScene().setBackgroundColor(0);
    	
    	c.setPosition(0,1,5);
    	c.setLookAt(new Vector3(0,0,0));
    	getCurrentScene().addCamera(c);

    	mEffects = new PostProcessingManager(this);
		RenderPass renderPass = new RenderPass(getCurrentScene(), getCurrentCamera(),0);
		mEffects.addPass(renderPass);
		
		BloomEffect bloomEffect = new BloomEffect(getCurrentScene(), getCurrentCamera(), mViewportWidth, mViewportHeight,0xeeeeee, 0xffffff, BlendMode.ADD);
		
		mEffects.addEffect(bloomEffect);
		bloomEffect.setRenderToScreen(true);

		//loadCube();
		createWalls();
		loaded = true;
		
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

	public void createWalls(){
		
		Material[] m = new Material[3];		
		
		for(int i = 0; i<waende.length; i++){
			m[i] = new Material();
			m[i].setColorInfluence(0);
			m[i].setDiffuseMethod(new DiffuseMethod.Lambert());
			m[i].enableLighting(true);
		}
		
		try {
			m[0].addTexture(new Texture("left2", R.drawable.left));
			m[1].addTexture(new Texture("right2", R.drawable.right));
			m[2].addTexture(new Texture("floor2", R.drawable.floor));
		} catch (TextureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		waende[0] = new Plane(1,1,1,1);
		waende[1] = new Plane(1,1,1,1);
		waende[2] = new Plane(1,1,1,1);
		
		waende[0].setPosition( -.25,0,0);
		waende[1].setPosition( .25,0,0);
		waende[2].setPosition( 0,-.5,0);
		
		waende[0].setRotation(0,45,0);
		waende[1].setRotation(0,-45,0);
		waende[2].setRotation(90, 0,45);
		
		for(int i = 0; i<waende.length; i++){
			
			waende[i].setMaterial(m[i]);
			waende[i].setDoubleSided(true);
			getCurrentScene().addChild(waende[i]);
		
		}
	}
	
	public void loadCube()
	{
		Material m = new Material();		
		
		try{
			m.addTexture(new Texture("bla", R.drawable.texture));
		}catch(TextureException e){
		}
		
		m.setColorInfluence(0);
		m.setDiffuseMethod(new DiffuseMethod.Lambert());
		m.enableLighting(true);
		Loader3DSMax l = new Loader3DSMax(this, R.raw.dice);
		
		try {
			l.parse();
			wuerfel = l.getParsedObject();
			wuerfel.setDoubleSided(true);
			wuerfel.setPosition(0,1,0);
			wuerfel.setMaterial(m);
			getCurrentScene().addChild(wuerfel);
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
			p.setPosition(new Vector3(-5+random.nextFloat()*10,-5+random.nextFloat()*10,-10+random.nextFloat()*20));
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
	
	public boolean onTouch(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		y+= 5;
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			x = event.getX();
			y = event.getY();
			getObjectAt(x,y);
			
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			temp = getCurrentCamera();
			if (getCurrentScene().getCamera() != temp ){
				getCurrentScene().replaceAndSwitchCamera(temp,0);
			}else{
				getCurrentScene().replaceAndSwitchCamera(c,0);
			}
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
		
		if (mSelectedObject.getName() == "wuerfel" ) 
		{
			Log.d("hitbtn", "hitid");
			
		}
	}
	
	
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
		if (loaded) {
			Log.d("yrot", Float.toString(yrot));
			waende[0].setRotation(0,-yrot,0);
			waende[1].setRotation(0,+yrot,0);
		}
	}
		
	@Override
	public void onRender(final double deltaTime) {
		mEffects.render(deltaTime);
	}
}

