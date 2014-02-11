package com.demo.augreal;

import rajawali.materials.shaders.VertexShader;
import rajawali.util.RawShaderLoader;

public class CustomRawVertexShader extends VertexShader {
	public CustomRawVertexShader()
	{
		super();
		initialize();
	}
	
	@Override
	public void initialize()
	{
		mShaderString = RawShaderLoader.fetch(R.raw.custom_vert_shader);
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
