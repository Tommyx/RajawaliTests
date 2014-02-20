precision mediump float;

uniform float uColorInfluence;
uniform float uTime;
uniform float uInfluencemyTex;

varying vec2 vTextureCoord;
varying vec4 vColor;

//vec2 surfacePosition = vec2 (1024.,128.);
#define MAX_ITER 8

float BRIGHTNESS = .8;

float field(in vec3 p) {
	float strength = 8.;
	float accum = 0.;
	float prev = 0.;
	float tw = 0.;
	for (int i = 0; i < 10; ++i) {
		float mag = dot(p, p);
		p = abs(p) / mag + vec3(sin(mag), -.5-abs(sin(uTime/5.)/2.), -cos(prev));
		float w = exp(-float(i) / 5.);
		accum += w * exp(-strength * pow(abs(mag - prev), 10.2));
		tw += w;
		prev = mag;
	}

	return max(0., 3. * accum / tw - BRIGHTNESS);
}

	void main() {
		vec2 uv = vTextureCoord.xy ;
		vec2 uvs = uv * vTextureCoord.xy / max(vTextureCoord.x, vTextureCoord.y);


		float r = field(vec3(uvs,0.2));
		float g = field(vec3(uvs,0.3));
		float b = field(vec3(uvs,0.4));
		gl_FragColor = vec4(r,g,b,1.);

}
