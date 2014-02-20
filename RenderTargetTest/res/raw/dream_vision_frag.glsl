#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D uTexture; // 0
varying vec2 vTextureCoord;
uniform float uTime;


void main ()
{
  vec2 uv = vTextureCoord.xy;
  vec4 c = texture2D(uTexture, uv);

  float time = sin(uTime)*3.141592;

  uv = vec2(uv.x, uv.y-uTime);
  c += texture2D(uTexture, uv+0.3);
  c += texture2D(uTexture, -uv+0.3);

  //  	c += texture2D(uTexture, uv+0.001*time);
//    c += texture2D(uTexture, uv+0.003*time);
//    c += texture2D(uTexture, uv+0.005*time);
//    c += texture2D(uTexture, uv+0.007*time);
//    c += texture2D(uTexture, uv+0.009*time);
//    c += texture2D(uTexture, uv+0.011*time);
//
//    c += texture2D(uTexture, uv-0.001*time);
//    c += texture2D(uTexture, uv-0.003*time);
//    c += texture2D(uTexture, uv-0.005*time);
//    c += texture2D(uTexture, uv-0.007*time);
//    c += texture2D(uTexture, uv-0.009*time);
//    c += texture2D(uTexture, uv-0.011*time);


  //c.rgb = vec3((c.r+c.g+c.b)/3.0);
  c = c / 4.0;
  gl_FragColor = c ;
  gl_FragColor.a = 0.75;

}
