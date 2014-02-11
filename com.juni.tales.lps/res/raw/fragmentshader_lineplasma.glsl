#ifdef GL_ES
	precision highp float;
#endif

uniform float uTime;
varying vec2 vTextureCoord;

void main( void ) {
	
	float time = uTime;
	
	float x =  vTextureCoord.s;
	float y =  vTextureCoord.t;
	
	y *= 10.0+sin(time*0.005);
	y -= 5.0;
	
	float color1 = 0.1-abs(sin(time+1.0*3.1415*x)-y-1.0)*1.0;
	float color2 = 0.1-abs(sin(time+1.0*3.1415+x)-y-0.5)*1.0;
	float color3 = 0.1-abs(sin(time+1.0*3.1415*x)-y-0.0)*1.0;
	float color4 = 0.1-abs(sin(time+1.0*3.1415+x)-y+0.5)*1.0;
	float color5 = 0.1-abs(sin(time+1.0*3.1415*x)-y+1.0)*1.0;
	float color6 = 0.1-abs(sin(time+1.0*3.1415+x)-y+1.5)*1.0;
	
	gl_FragColor = vec4( (vec3( color1, color2, color3 ) * 
			            vec3( color4, color5, color6)), 1.0 );
}
