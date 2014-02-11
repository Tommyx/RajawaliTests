 package com.demo.augreal;

import rajawali.util.RajLog;
import rajawali.vuforia.RajawaliVuforiaActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class RajawaliVuforiaExampleActivity extends RajawaliVuforiaActivity {
	private RajawaliVuforiaExampleRenderer mRenderer;
	private RajawaliVuforiaActivity mUILayout;
    private Button mStartScanButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		startVuforia();
	}

	@Override
	protected void setupTracker() {
		int result = initTracker(TRACKER_TYPE_MARKER);
		if (result == 1) {
			result = initTracker(TRACKER_TYPE_IMAGE);
			if (result == 1) {
				super.setupTracker();
			} else {
				RajLog.e("Couldn't initialize image tracker.");
			}
		} else {
			RajLog.e("Couldn't initialize marker tracker.");
		}
	}
	
	@Override
	protected void initApplicationAR()
	{
		super.initApplicationAR();
		createImageMarker("Mu5iC.xml");
	}
	
    @Override
	protected void initRajawali() {
		super.initRajawali();
		mRenderer = new RajawaliVuforiaExampleRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		super.setRenderer(mRenderer);
	}    
}
