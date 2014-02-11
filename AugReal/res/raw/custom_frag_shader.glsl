precision mediump float;

uniform float uColorInfluence;
uniform float uTime;

varying vec2 vTextureCoord;
varying vec4 vColor;

//vec2 surfacePosition = vec2 (1024.,128.);

float BRIGHTNESS = .8;
	void main() {
		vec2 uv = vTextureCoord.xy ;
		gl_FragColor = vec4(sin(uTime),cos(uTime),sin(uTime),0.3);

}
