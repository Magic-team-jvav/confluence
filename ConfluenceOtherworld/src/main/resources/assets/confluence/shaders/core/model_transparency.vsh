#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in ivec2 UV2;
out vec2 atlasPosition;
flat out float modelAlpha;
flat out vec3 modelTint;

void main() {
    gl_Position = vec4(Position, 1.0);
    atlasPosition = vec2(UV2);
    modelAlpha = UV0.x;
    modelTint = Color.rgb;
}
