#ifdef GL_ES
	precision mediump float;
#endif
#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float uTime;
varying vec2 vTextureCoord;
uniform samplerExternalOES uDiffuseTexture;

void main(void)
{
	float frequence = 160.0;
    float damping = 1.0;          // smoothing value between 0.0 - 1.0
    float time = uTime*0.05;
    float radius = .5;
    float t = clamp(time / 6., 0., 1.);
    vec2 coords = vTextureCoord.st;
    vec2 dir = coords - vec2(1.0,1.0);
    float dist = distance(coords, vec2(.5));

	vec2 offset = dir * (sin(dist * frequence - time*50.) + .5) / 60.;

	vec2 texCoord = coords + offset;
	vec4 diffuse = texture2D(uDiffuseTexture, texCoord);

	gl_FragColor = t + diffuse * (1. - t);

}





