#ifdef GL_ES
	precision mediump float;
#endif

uniform float uTime;
uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec2 aTextureCoord;

varying vec2 vTextureCoord;
varying vec4 vColor;

void main(void)
{
	gl_Position = uMVPMatrix * aPosition;
	vTextureCoord = aTextureCoord;

}
