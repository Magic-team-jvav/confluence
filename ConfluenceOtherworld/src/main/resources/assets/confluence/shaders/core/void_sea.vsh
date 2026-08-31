#version 150

#moj_import < fog.glsl >

in vec3 Position;
in vec2 UV0;
in vec4 Color;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 vertexColor;
out vec2 texCoord;
out vec3 fogPosition;

void main() {
    vec4 viewPosition4 = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewPosition4;
    vertexColor = Color;
    texCoord = UV0;
    fogPosition = Position;
}
