package com.juni.tales.lps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.animation.TranslateAnimation3D;
import rajawali.animation.Animation3D.RepeatMode;
import rajawali.math.vector.Vector3;
import rajawali.renderer.RajawaliRenderer;
import rajawali.scene.RajawaliScene2;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

public class IntroRenderer extends RajawaliRenderer {
	
	private int currentTrack = 0;
	RajawaliScene2[] scenes;  
	MediaPlayer mP;
	HashMap<Integer,Integer> hitlist = new HashMap<Integer, Integer>();
	List<Integer> positions = new ArrayList<Integer>();
	int currentScene = 0;
	boolean found = false;
	Tools tools = new Tools(); 
	
	public IntroRenderer(Context context) {
		super(context);
		setFrameRate(60);
		mP = MediaPlayer.create(getContext(), R.raw.intro);
		
		hitlist = tools.readRawTextFile(getContext(), R.raw.hitlist, hitlist);
		
		for(Integer key : hitlist.keySet())
		{
			  positions.add(key); 
			  Log.d("key", Integer.toString(key));
			  Log.d("value", Integer.toString(hitlist.get(key)));
		}
		scenes = new RajawaliScene2[5];
	}
	 
	protected void initScene() {
		
		scenes[0] = new Scene1(this);
		
		for (int i = 0; i<5; i++){
			addScene(scenes[i]);
		}
		mP.setOnPreparedListener(new OnPreparedListener() { 
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mP.start();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		((StoryBook_Activity) mContext).showLoader();
		
		super.onSurfaceCreated(gl, config);
		((StoryBook_Activity) mContext).hideLoader();
	}
	
	public void stopPlayer(){
		mP.stop();
	}
	
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
		int time = mP.getCurrentPosition();
		
		float sec = time / 100;
		
		int intsec = (int) sec; 
		
		if (positions.contains(intsec) && found!= true ){
			found = true;
			int scene = hitlist.get(intsec);
			Log.d("Hit",Float.toString(scene));
			startScene(scene);
		}
		else{
			found = false;
		}
	
		if (getCurrentScene() == scenes[currentScene]){
			scenes[currentScene].onDrawFrame(sec);
		}
		//Log.d("songpos", Float.toString(sec));


		if (intsec == 450 && currentTrack != 1) {
			currentTrack = 1;
			playTrack(R.raw.magican_intro);
			mP.setLooping(true);
		}		
		
		if (sec == 1000.0f){
			((StoryBook_Activity) mContext).finish();
		}
	}
		
	private void startScene(int sceneNumber){
		this.currentScene = sceneNumber;
		
		if (!scenes[sceneNumber].loaded){
			scenes[sceneNumber].InitScene();
			scenes[sceneNumber].loaded = true;
			Log.d("LOADED", Float.toString(sceneNumber) + " " + Boolean.toString(scenes[sceneNumber].loaded));
		}
		
		replaceAndSwitchScene(getCurrentScene(), scenes[sceneNumber]);
		switchScene(scenes[sceneNumber]);
	}
		
	private void playTrack(int resid)
	{
	    AssetFileDescriptor afd = getContext().getResources().openRawResourceFd(resid);

	    try
	    {   
	        mP.reset();
	        mP.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
	        mP.prepare();
	        afd.close();
	    }
	    catch (IllegalArgumentException e)
	    {
	        Log.e("IAE", "Unable to play audio queue do to exception: " + e.getMessage(), e);
	    }
	    catch (IllegalStateException e)
	    {
	        Log.e("ISE", "Unable to play audio queue do to exception: " + e.getMessage(), e);
	    }
	    catch (IOException e)
	    {
	        Log.e("IOE", "Unable to play audio queue do to exception: " + e.getMessage(), e);
	    }
	}
	
	public void onSurfaceDestroyed() {
		super.onSurfaceDestroyed();
	}

	public void onTouch(MotionEvent event)
	{
		if (getCurrentScene() == scenes[currentScene])
		{
			scenes[currentScene].onTouch(event);
		}
	}
	
	public void onMove(MotionEvent event)
	{
		if (getCurrentScene() == scenes[currentScene])
		{
			scenes[currentScene].onMove(event);
		}
	}
	
	public void onUp(MotionEvent event)
	{
		if (getCurrentScene() == scenes[currentScene])
		{
			scenes[currentScene].onUp(event);
		}
	}	
}