package com.meyer.tommy.columninterlaced;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

public class MainActivity extends AppCompatActivity {

    MyRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_main);

            final RajawaliSurfaceView surface = new RajawaliSurfaceView(this);
            surface.setFrameRate(60.0);
            surface.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);

            // Add mSurface to your root view
            addContentView(surface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

            renderer = new MyRenderer(this);
            surface.setSurfaceRenderer(renderer);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
