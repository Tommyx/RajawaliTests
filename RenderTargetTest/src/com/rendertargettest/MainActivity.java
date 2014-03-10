package com.rendertargettest;

import android.os.*;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import rajawali.renderer.RajawaliRenderer;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Starter
{
    /** Called when the activity is first created. */
	Renderer renderer;
	ScaleGestureDetector scaleGestureDetector;
	private Button mTouchEnabled;
	private TextView text, text2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.renderer = new Renderer(this);
		this.renderer.setSurfaceView(mSurfaceView);
		super.setRenderer(this.renderer);
		initLoader();
		scaleGestureDetector = new ScaleGestureDetector(this, new simpleOnScaleGestureListener());
		mTouchEnabled = new Button(this);
		text = new TextView(this);
		mTouchEnabled.setText("Touch");
		text.setText("Model by PigArt@Blendswap.com");
		text2 = new TextView(this);
		text2.setText("Music by Helios - Hatsu Yume");
		
		mTouchEnabled.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) 
	                {
	                	if (renderer.touchenabled) renderer.touchenabled=false;
	                	else renderer.touchenabled=true; 
	                }
		});
		
		text.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		this.addContentView(text, 
	            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		text2.setGravity(Gravity.TOP | Gravity.CENTER);
		this.addContentView(text2, 
	            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		showTouchButton();
		
	}
	
	public void showTouchButton()
    {
        this.runOnUiThread(new Runnable() {
                public void run() {
                    if  (mTouchEnabled != null)
                    	mTouchEnabled.setVisibility(View.VISIBLE);
                 }
         });
        
        this.addContentView(mTouchEnabled, 
            new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));		
    }
	
	@Override
    public boolean onTouchEvent(MotionEvent event) 
    {
		this.renderer.onTouch(event);
     	scaleGestureDetector.onTouchEvent(event);
		return true;
    }
	
	public class simpleOnScaleGestureListener extends
	  SimpleOnScaleGestureListener {

	 @Override
	 public boolean onScale(ScaleGestureDetector detector) {
		 float i = detector.getCurrentSpan();
		 renderer.onScale(i);
		 return true;
	 }

	 @Override
	 public boolean onScaleBegin(ScaleGestureDetector detector) {
	  // TODO Auto-generated method stub
	  return true;
	 }

	 @Override
	 public void onScaleEnd(ScaleGestureDetector detector) {
	  // TODO Auto-generated method stub
	 }
	}
	
	
	 @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	switch (keyCode) {
	    		case KeyEvent.KEYCODE_MENU:
	    			renderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_POWER:
	    			renderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_SEARCH:
	    			renderer.stopPlayer();
	    			return true;
	    		case KeyEvent.KEYCODE_BACK:
	    			renderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		case KeyEvent.KEYCODE_HOME:
	    			renderer.stopPlayer();
	    			this.finish();
	    			return true;
	    		default:
	    			renderer.stopPlayer();
	    			return true;
	    	  }
	    	}

	
}
