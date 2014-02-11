// water turbulence effect by joltz0r 2013-07-04, improved 2013-07-07
#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 vTextureCoords;
varying vec2 surfacePosition;


#define MAX_ITER 8
void main( void ) {

	vec2 p = surfacePosition*4.0;
	vec2 i = p;
	float c = 0.0;
	float inten = 1.0;

	for (int n = 0; n < MAX_ITER; n++) {
		float t = time * (1.0 - (1.0 / float(n+1)));
		i = p + vec2(
			cos(t - i.x) + cos(t + i.y),
			sin(t - i.y) + cos(t + i.x)
		);
		c += 1.0/length(vec2(
			p.x / (sin(i.x+t)/inten),
			p.y / (cos(i.y+t)/inten)
			)
		);
	}
	c /= float(MAX_ITER);

	gl_FragColor = vec4(vec3(pow(c,1.5))*vec3(0.95, 0.97, 1.8), 1.0);
}
