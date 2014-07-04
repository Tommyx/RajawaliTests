package com.demo.postprocessingdemo;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

public class PostProcessingActivity extends Starter {

	Renderer renderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.renderer = new Renderer(this);
		this.renderer.setSurfaceView(mSurfaceView);
	//	setGLBackgroundTransparent(true);
		super.setRenderer(this.renderer);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) 
    {
	//	this.renderer.onTouch(event);
     	return true;
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	switch (keyCode) {
	    		case KeyEvent.KEYCODE_MENU:
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_POWER:
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_SEARCH:
	    			return true;
	    		case KeyEvent.KEYCODE_BACK:
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_HOME:
	    			this.finish();
	    			return true;
	    		default:
	    			return true;
	    	  }
	}
}
