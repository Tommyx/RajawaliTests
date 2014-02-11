precision mediump float;

uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec2 aTextureCoord;

varying vec2 vTextureCoord;
varying vec2 v_blurTexCoords[8];

void main()
{
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = aTextureCoord;
	v_blurTexCoords[ 0] = vTextureCoord + vec2(0.0, -0.020);
	v_blurTexCoords[ 1] = vTextureCoord + vec2(0.0, -0.012);
	v_blurTexCoords[ 2] = vTextureCoord + vec2(0.0, -0.008);
	v_blurTexCoords[ 3] = vTextureCoord + vec2(0.0, -0.004);
	v_blurTexCoords[ 4] = vTextureCoord + vec2(0.0,  0.004);
	v_blurTexCoords[ 5] = vTextureCoord + vec2(0.0,  0.008);
	v_blurTexCoords[ 6] = vTextureCoord + vec2(0.0,  0.012);
	v_blurTexCoords[ 7] = vTextureCoord + vec2(0.0,  0.020);
}
