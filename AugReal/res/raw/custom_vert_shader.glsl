precision mediump float;

uniform mat4 uMVPMatrix;
uniform float uTime;

attribute vec4 aPosition;
attribute vec2 aTextureCoord;

varying vec4 vColor;
varying vec2 vTextureCoord;

void main() {
	vTextureCoord = aTextureCoord;



	vec4 position;
    float time = sin(uTime*0.15);
		position.x = aPosition.x;
		position.y = aPosition.y;
		position.z = aPosition.z;
		position.w = 1.0;

	gl_Position = uMVPMatrix * position;
}
