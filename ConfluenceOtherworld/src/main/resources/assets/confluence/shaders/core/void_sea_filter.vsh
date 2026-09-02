#version 150
in vec3 Position;
uniform float TextureScale;
uniform vec2 ViewOffset;
out vec2 texCoord;
out vec2 screenCoord;
void main() {
    gl_Position = vec4(Position.xy * 2.0 - 1.0, 0.0, 1.0);
    texCoord = Position.xy * TextureScale + ViewOffset;
    screenCoord = Position.xy;
}
