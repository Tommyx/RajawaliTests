package com.juni.tales.lps;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StoryBook_Activity extends RajawaliStarterActivity {
	private IntroRenderer mRenderer;
	
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
        mRenderer = new IntroRenderer(this);
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
        initLoader();
      
    }
	
    
    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
    	switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			mRenderer.onTouch(event);
			break;
		case MotionEvent.ACTION_MOVE:
			mRenderer.onMove(event);
			break;
		case MotionEvent.ACTION_UP:
			mRenderer.onUp(event);
			break;
		}
		return true;
	}
    
    
    @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	switch (keyCode) {
	    		case KeyEvent.KEYCODE_MENU:
	    			mRenderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		
	    		case KeyEvent.KEYCODE_SEARCH:
	    			mRenderer.stopPlayer();
	    			return true;
	    	
	    		case KeyEvent.KEYCODE_BACK:
	    			mRenderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		
	    		case KeyEvent.KEYCODE_HOME:
	    			mRenderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		default:
	    			mRenderer.stopPlayer();
	    			this.finish();
	    			return super.onKeyDown(keyCode, event);
	    	  }
	    	}


	protected void exit(){
		this.finish();
	}
	
    
    
}
