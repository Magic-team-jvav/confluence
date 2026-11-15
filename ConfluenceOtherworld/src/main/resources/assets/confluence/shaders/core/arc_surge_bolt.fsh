#version 150
in vec4 vertexColor;
in vec2 uv;
flat in float roundCoverage;
uniform float RoundJoint;
uniform vec3 Tint;
uniform float Gain;
uniform float Profile;
uniform float Vivid;
uniform sampler2D DepthSnapshot;
uniform float LateDepthEnabled;
uniform vec2 OutputSize;
out vec4 fragColor;
void main(){
 if(LateDepthEnabled>.5 && gl_FragCoord.z>texture(DepthSnapshot,gl_FragCoord.xy/OutputSize).r+0.000002)discard;
 float d=RoundJoint>0.5?length(vec2(uv.x/max(roundCoverage,1e-20),uv.y)):abs(uv.y*2.0-1.0);
 if(d>=1.0)discard;
 float falloff=Profile>0.5 ? exp(-5.0*d*d)-exp(-5.0) : 1.0-smoothstep(0.30,0.95,d);
 vec3 source=Tint*vertexColor.rgb;
 float coverage=vertexColor.a*(RoundJoint>0.5?roundCoverage:uv.x)*max(0.0,falloff);
 if(Vivid>0.5){
  // Gain increases opacity rather than independently clipping RGB to white.
  // Below saturation this preserves the original contribution on black.
  float peak=max(source.r,max(source.g,source.b));
  fragColor=vec4(peak>0.0?source/peak:vec3(0.0),clamp(coverage*Gain*peak,0.0,1.0));
 }else{
  fragColor=vec4(source*Gain,coverage);
 }
}
