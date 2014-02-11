package com.juni.tales.lps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.Animation3D.RepeatMode;
import rajawali.lights.PointLight;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.math.vector.Vector3;
import rajawali.parser.Loader3DSMax;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import rajawali.scene.RajawaliScene;
import rajawali.scenegraph.IGraphNode.GRAPH_TYPE;

public class Scene1 extends RajawaliScene {

	RajawaliRenderer renderer; 
	Cube  cube;
	float count 						= 10;
	public boolean loaded 				= false;
	Tools tools 						= new Tools();
	List<Integer> positions 			= new ArrayList<Integer>();
	HashMap<Integer,Integer> hitlist 	= new HashMap<Integer, Integer>();
	Plane clouds[] 						= new Plane[10]; 
	Loader3DSMax objParser;
	Object3D cloud3d;
	TranslateAnimation3D cloudAnim[];
	PointLight cLight1 					= new PointLight();
	float timer =0;
	
	
	public Scene1(RajawaliRenderer r) {
		super(r, GRAPH_TYPE.NONE);
		this.renderer = r;
		objParser = new Loader3DSMax(this.renderer, R.raw.cloud);
		try {
			objParser.parse();
		} catch(rajawali.parser.ALoader.ParsingException e) {
			e.printStackTrace();
		}

		hitlist = tools.readRawTextFile(r.getContext(), R.raw.hitlist_scene1, hitlist);
		
		for(Integer key : hitlist.keySet())
		{
			  positions.add(key); 
		}
	}
	
	public void InitScene()
	{	
		Camera mCamera1 = this.renderer.getCurrentCamera();
		
		PointLight mLight1 = new PointLight();
		mLight1.setPosition(5, 5, 12);
		mLight1.setPower(10f);
		mCamera1.setPosition(0,0,10);
		replaceAndSwitchCamera(mCamera1, 0); 
		addCamera(mCamera1); 
		cloudAnim = new TranslateAnimation3D[11];
		
		try {
			Plane plane = new Plane(20, 12, 2, 2);
			Material material1 = new Material();
			material1.setDiffuseMethod(new DiffuseMethod.Lambert());
			material1.addTexture(new NormalMapTexture("back", R.drawable.background1nm));
			material1.addTexture(new Texture("front", R.drawable.background1));
			plane.setMaterial(material1);
			plane.setZ(-1);
			addChild(plane);
			
			Material cloudmat = new Material();
			cloudmat.addTexture(new Texture("cloud",R.drawable.cloud2d));
			Material cloudmat2 = new Material();
			cloudmat2.addTexture(new Texture("cloud3d",R.drawable.cloud3d));
						
			cloud3d = objParser.getParsedObject();
			cloud3d.setScale(0.5f,0.5f,0.5f);
			cloud3d.setRotation(90, 10, 0);
			cloud3d.setPosition(0,-3,1);
			cloud3d.setMaterial(cloudmat2);
			addChild(cloud3d);
			
			cloudAnim[10] = new TranslateAnimation3D(new Vector3(-12, 2, 0), new Vector3(12, 2, 0));
			cloudAnim[10].setDuration(20000);
			cloudAnim[10].setRepeatMode(RepeatMode.REVERSE_INFINITE);
			cloudAnim[10].setTransformable3D(cloud3d);
			registerAnimation(cloudAnim[10]);
			cloudAnim[10].play();
			
		} catch(TextureException e) {
			e.printStackTrace();
		}
		loaded = true;
	} 		

	public void onDrawFrame(float time){
		timer+=.10f;
		
		if (loaded)
		{
		}
	}
	
	public void move_cam(float posz)
	{
		
	}

	
}

