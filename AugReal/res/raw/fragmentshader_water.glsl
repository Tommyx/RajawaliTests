#ifdef GL_ES
	precision mediump float;
#endif
precision mediump float;

uniform float uTime;
varying vec2 vTextureCoord;
uniform sampler2D uDiffuseTexture;
uniform sampler2D uAlphaTexture;

void main(void)
{
	vec2 touched = vec2(1.0,1.0);

	float frequence = 60.0;
    float damping = 1.0;          // smoothing value between 0.0 - 1.0
    float time = uTime*0.05;
    float radius = .5;
    float t = clamp(time / 6., 0., 1.);
    vec2 coords = vTextureCoord.st;
    vec2 dir = coords - vec2(.5) * touched;
    float dist = distance(coords, vec2(.5));

	vec2 offset = dir * (sin(dist * frequence - time*25.) + .5) / 60.;

	vec2 texCoord = coords + offset;
	vec4 diffuse = texture2D(uDiffuseTexture, texCoord);
	vec4 mixin = texture2D(uAlphaTexture, texCoord);
	
	gl_FragColor = mixin * t + diffuse * (1. - t);

}





