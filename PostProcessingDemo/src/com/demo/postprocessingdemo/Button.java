package com.demo.postprocessingdemo;

import rajawali.math.vector.Vector3;

public class Button {

	public String Name = "";
	public boolean isClicked = false;
	public int bitmap = 0;
	public boolean isClickable = false;
	public Vector3 position = new Vector3(0,0,0);
	public Vector3 rotation = new Vector3(0,0,180);
	public Vector3 scale = new Vector3(1,1,.1);
	
	public Button(){
		
	}
}
