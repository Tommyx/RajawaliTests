package com.demo.augreal;

import rajawali.materials.shaders.FragmentShader;
import rajawali.util.RawShaderLoader;
import android.opengl.GLES20;

public class CustomRawFragmentShader extends FragmentShader {
    private int handle_f;
    private int handle_f1;

    public CustomRawFragmentShader()
	{
		super();
		initialize();
	}
	
	@Override
	public void initialize()
	{
		mShaderString = RawShaderLoader.fetch(R.raw.custom_frag_shader);
	}
	
	@Override
	public void main() {

	}
	
	@Override
	public void setLocations(final int programHandle)
	{
		super.setLocations(programHandle);
        handle_f = getUniformLocation(programHandle, "f");
        handle_f1 = getUniformLocation(programHandle, "f1");
	}
	
	@Override
	public void applyParams() 
	{
		super.applyParams();
        GLES20.glUniform1f(handle_f,0.5f);
        GLES20.glUniform1f(handle_f1,0.5f);
	}
}
