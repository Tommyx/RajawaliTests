#ifdef GL_ES
precision mediump float;
#endif

uniform float uTime;
uniform vec2 vTextureCoord;
//normalized sin
uniform vec2 mT;


float sinn(float x)
{
	return sin(x)/2.+.5;
}

float CausticPatternFn(vec2 pos)
{
	return (sin(pos.x*40.+uTime)
		+pow(sin(-pos.x*130.+uTime),1.)
		+pow(sin(pos.x*30.+uTime),2.)
		+pow(sin(pos.x*50.+uTime),2.)
		+pow(sin(pos.x*80.+uTime),2.)
		+pow(sin(pos.x*90.+uTime),2.)
		+pow(sin(pos.x*12.+uTime),2.)
		+pow(sin(pos.x*6.+uTime),2.)
		+pow(sin(-pos.x*13.+uTime),5.))/2.;
}

vec2 CausticDistortDomainFn(vec2 pos)
{
	pos.x*=(pos.y*0.60+1.);
	pos.x*=1.+sin(uTime/2.)/10.;
	return pos;
}

void main( void )
{
	vec2 pos = gl_FragCoord.xy / vec2(800.0,480.0);
	pos-=.5;
	vec2  CausticDistortedDomain = CausticDistortDomainFn(pos);
	float CausticShape = clamp(7.-length(CausticDistortedDomain.x*20.),0.,1.);
	float CausticPattern = CausticPatternFn(CausticDistortedDomain);
	float Caustic = CausticShape*CausticPattern;
	float f = length(pos+vec2(-.5,.5))*length(pos+vec2(.5,.5))*(1.+(pos.y+.5)/4.*Caustic)/1.;

	gl_FragColor = vec4(1.0*mT.x, 1.0*mT.z,1.0*mT.y,1)*(1.0/f);

}
