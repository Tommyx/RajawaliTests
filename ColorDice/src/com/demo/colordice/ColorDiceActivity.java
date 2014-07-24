package com.demo.colordice;

import android.graphics.Color;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ColorDiceActivity extends Starter {

	Renderer renderer;
	TextView score,sLabel;
	TextView hits, hLabel;
	TextView highscore;
	
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
		this.renderer.onTouch(event);
		return true;
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	switch (keyCode) {
	    		case KeyEvent.KEYCODE_MENU:
	    			if (renderer.mP != null)
	    				renderer.mP.stop();
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_POWER:
	    			if (renderer.mP != null)
	    				renderer.mP.stop();
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_SEARCH:
	    			return true;
	    		case KeyEvent.KEYCODE_BACK:
	    			if (renderer.mP != null)
	    				renderer.mP.stop();
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_HOME:
	    			if (renderer.mP != null)
	    				renderer.mP.stop();
	    			this.finish();
	    			return true;
	    		default:
	    			return true;
	    	  }
	}
}
