#ifdef GL_ES
	precision mediump float;
#endif
precision mediump float;

uniform sampler2D uTexture;
uniform sampler2D uDiffuseTexture;
uniform float uTime;
varying vec2 vTextureCoord;
uniform vec2 touch;

void main(void)
{
    float frequence = 160.0;
    float damping = 1.0;          // smoothing value between 0.0 - 1.0
    float time = uTime*0.05;
    float radius = .5;
    float t = clamp(time / 6., 0., 1.);
    vec2 coords = vTextureCoord.st;
    vec2 dir = coords - vec2(.5) * touch;
    float dist = distance(coords, vec2(.5));

	vec2 offset = dir * (sin(dist * frequence - time*50.) + .5) / 60.;

	vec2 texCoord = coords + offset;
	vec4 diffuse = texture2D(uDiffuseTexture, texCoord);

	gl_FragColor = t + diffuse;

}





