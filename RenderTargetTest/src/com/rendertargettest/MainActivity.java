package com.rendertargettest;

import android.os.*;
import android.view.Window;
import rajawali.renderer.RajawaliRenderer;

public class MainActivity extends Starter
{
    /** Called when the activity is first created. */
    RajawaliRenderer renderer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		this.renderer = new Renderer(this);
		this.renderer.setSurfaceView(mSurfaceView);
		super.setRenderer(this.renderer);
		initLoader();
		}
}
