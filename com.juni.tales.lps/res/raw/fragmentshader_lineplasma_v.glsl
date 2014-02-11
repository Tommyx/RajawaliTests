#ifdef GL_ES
precision mediump float;
#endif

uniform float uTime;
varying vec2 vTextureCoord;
const float PI = 3.141592;

float makePoint(float x,float y,float fx,float fy,float sx,float sy,float t){
   float xx=x+sin(t*fx)*sx;
   float yy=y+cos(t*fy)*sy;
   return 1.0/sqrt(xx*xx+yy*yy);
}

void main( void ) {

   vec2 p= vTextureCoord.st;

   p=p*2.0;
   
   float x=p.x;
   float y=p.y;

   float a= makePoint(x,y,3.3,2.9,0.3,0.3,uTime);
   a=a+makePoint(x,y,1.9,2.0,0.4,0.4,uTime);
   a=a+makePoint(x,y,0.8,0.7,0.4,0.5,uTime);
   a=a+makePoint(x,y,2.3,0.1,0.6,0.3,uTime);
   a=a+makePoint(x,y,0.8,1.7,0.5,3.4,uTime);
   a=a+makePoint(x,y,0.3,1.0,0.4,0.4,uTime);
   a=a+makePoint(x,y,1.4,1.7,0.4,0.5,uTime);
   a=a+makePoint(x,y,1.3,2.1,0.6,0.3,uTime);
   a=a+makePoint(x,y,1.8,1.7,0.5,0.4,uTime);   
   
   float b=
       makePoint(x,y,1.2,1.9,0.3,0.3,uTime);
   b=b+makePoint(x,y,3.7,2.7,0.4,0.4,uTime);
   b=b+makePoint(x,y,1.4,0.6,0.4,0.5,uTime);
   b=b+makePoint(x,y,2.6,0.4,0.6,0.3,uTime);
   b=b+makePoint(x,y,3.7,1.4,0.5,0.4,uTime);
   b=b+makePoint(x,y,3.7,1.7,0.4,0.4,uTime);
   b=b+makePoint(x,y,3.8,0.5,0.4,0.5,uTime);
   b=b+makePoint(x,y,1.4,0.9,0.6,0.3,uTime);
   b=b+makePoint(x,y,3.7,1.3,0.5,0.4,uTime);

   float c=
       makePoint(x,y,3.7,0.3,0.3,0.3,uTime);
   c=c+makePoint(x,y,2.9,1.3,0.4,0.4,uTime);
   c=c+makePoint(x,y,2.8,0.9,0.4,0.5,uTime);
   c=c+makePoint(x,y,1.2,1.7,0.6,0.3,uTime);
   c=c+makePoint(x,y,2.3,0.6,0.5,0.4,uTime);
   c=c+makePoint(x,y,2.3,0.3,0.4,0.4,uTime);
   c=c+makePoint(x,y,1.4,0.8,0.4,0.5,uTime);
   c=c+makePoint(x,y,2.2,0.6,0.6,0.3,uTime);
   c=c+makePoint(x,y,1.3,0.5,0.5,0.4,uTime);
   
   
   vec3 d=vec3(a,b,c)/32.0;
   
   gl_FragColor = vec4(vec3(2.0*d.x,2.0*d.y,2.0*d.z)*vec3(d.x*2.0,d.y*2.0,d.z*2.0),1.0);
}