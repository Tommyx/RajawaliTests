package com.hallway;

import rajawali.materials.shaders.FragmentShader;
import rajawali.util.RawShaderLoader;
import android.opengl.GLES20;

public class CustomRawFragmentShader extends FragmentShader {
    
    public CustomRawFragmentShader()
	{
		super();
		initialize();
	}
	
	@Override
	public void initialize()
	{
		mShaderString = RawShaderLoader.fetch(R.raw.waterfall_frag);
	}
	
	@Override
	public void main() {

	}
	
	@Override
	public void setLocations(final int programHandle)
	{
		super.setLocations(programHandle);
	}
	
	@Override
	public void applyParams() 
	{
		super.applyParams();
    }
}
