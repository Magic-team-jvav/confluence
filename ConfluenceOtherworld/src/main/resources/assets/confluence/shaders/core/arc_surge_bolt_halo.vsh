#version 150
in vec3 Position;
in vec4 Color;
in vec2 UV0;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float LateDepthEnabled;
uniform mat4 WorldProjection,WorldModelView;
out vec4 vertexColor;
out vec2 uv;
flat out float roundCoverage;
void main(){
 gl_Position=(LateDepthEnabled>.5?WorldProjection*WorldModelView:ProjMat*ModelViewMat)*vec4(Position,1.0);
 vertexColor=Color;uv=UV0;roundCoverage=abs(UV0.x);
}
