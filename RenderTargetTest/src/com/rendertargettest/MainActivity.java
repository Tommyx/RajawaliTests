package com.rendertargettest;

import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import rajawali.renderer.RajawaliRenderer;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class MainActivity extends Starter
{
    /** Called when the activity is first created. */
	Renderer renderer;
	ScaleGestureDetector scaleGestureDetector;
	private Button mTouchEnabled;
	
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
		mTouchEnabled.setText("Touch");
	         
		mTouchEnabled.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) 
	                {
	                	if (renderer.touchenabled) renderer.touchenabled=false;
	                	else renderer.touchenabled=true; 
	                }
		});
		
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
		 Log.d("scale", Float.toString(i));
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
}
