#version 150
in vec4 vertexColor;
in vec2 uv;
flat in float roundCoverage;
uniform float Gain;
uniform float RoundJoint;
uniform float Core;
uniform float Vivid;
uniform sampler2D DepthSnapshot;
uniform float LateDepthEnabled;
uniform vec2 OutputSize;
out vec4 fragColor;
void main(){
 if(LateDepthEnabled>.5 && gl_FragCoord.z>texture(DepthSnapshot,gl_FragCoord.xy/OutputSize).r+0.000002)discard;
 float coverage=RoundJoint>0.5?roundCoverage:uv.x;
 float d=RoundJoint>0.5?length(vec2(uv.x/max(roundCoverage,1e-20),uv.y)):abs(uv.y*2.0-1.0);
 if(d>=1.0)discard;
 float falloff=Core>.5?1.0-smoothstep(0.15,0.75,d):max(0.0,exp(-8.0*d*d)-exp(-8.0));
 if(Core>.5 && Vivid>.5){
  float peak=max(vertexColor.r,max(vertexColor.g,vertexColor.b));
  float a=clamp(vertexColor.a*coverage*falloff*Gain*peak,0.0,1.0);
  fragColor=vec4(peak>0.0?vertexColor.rgb/peak*a:vec3(0.0),a);
  return;
 }
 // MAX ignores blend factors. Store the actual fixed-point additive contribution.
 // Preserve recipe gain; MAX already prevents joint accumulation.
 vec3 radiance=vertexColor.rgb*Gain;
 vec3 emission=(Core>.5?radiance:clamp(radiance,0.0,1.0))*vertexColor.a*coverage*falloff;
 fragColor=vec4(emission,0.0);
}
