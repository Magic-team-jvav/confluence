#version 150

// @formatter:off
#moj_import <fog.glsl>
// @formatter:on

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 vertexColor;
out vec2 texCoord;
out vec2 lightMapCoord;
out vec3 fogPosition;

void main() {
    vec4 viewPosition4 = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * viewPosition4;
    vertexColor = Color;
    texCoord = UV0;
    lightMapCoord = UV2 / 256.0;
    fogPosition = Position;
}
