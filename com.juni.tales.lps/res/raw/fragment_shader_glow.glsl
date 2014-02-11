#ifdef GL_ES
	precision highp float;
#endif

uniform sampler2D uDiffuseTexture;
uniform sampler2D uAlphaTexture;
uniform float time;
varying vec2 vTextureCoord;

void main( void )
{
	float halfPixel = 0.5 / vTextureCoord.s;
	vec4 col1  = texture2D( uDiffuseTexture, vTextureCoord + vec2( 0.0, halfPixel) );
	vec4 col2  = texture2D( uAlphaTexture, vTextureCoord + vec2( 0.0, halfPixel) );

	gl_FragColor = col1*col2;
}
