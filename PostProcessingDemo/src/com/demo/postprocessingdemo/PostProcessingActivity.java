package com.demo.postprocessingdemo;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class PostProcessingActivity extends Starter {

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
	
		RelativeLayout ll = new RelativeLayout(this);

		RelativeLayout.LayoutParams a = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams b = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	                ViewGroup.LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams c = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams d = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams e = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

	    a.addRule(RelativeLayout.BELOW, ll.getId());
	    
	    sLabel = new TextView(this);
		sLabel.setText("Time");
		sLabel.setTextSize(20);
		sLabel.setTextColor(Color.WHITE);
		sLabel.setId(1111);
		sLabel.setLayoutParams(a);
		ll.addView(sLabel);
		
		b.addRule(RelativeLayout.BELOW, sLabel.getId());
        
		score = new TextView(this);
		score.setText("");
		score.setTextSize(20);
		score.setTextColor(Color.WHITE);
		score.setLayoutParams(b);
		ll.addView(score);

		c.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, ll.getId());
        
		hLabel = new TextView(this);
		hLabel.setText("Score");
		hLabel.setTextSize(20);
		hLabel.setTextColor(Color.WHITE);
		hLabel.setId(1112);
		hLabel.setLayoutParams(c);
		ll.addView(hLabel);

		d.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, ll.getId());
        d.addRule(RelativeLayout.BELOW, hLabel.getId());
		
		hits = new TextView(this);
		hits.setText("");
		hits.setTextSize(20);
		hits.setTextColor(Color.WHITE);
		hits.setLayoutParams(d);
		ll.addView(hits);
		
		e.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, ll.getId());
        		
		mLayout.addView(ll);
		

	}

	public void setUI() {
	    this.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	            String msg = renderer.getScore();
	            score.setText(msg);
	            String msg2 = renderer.getHits();
	            hits.setText(msg2);
	        }
	    });
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
