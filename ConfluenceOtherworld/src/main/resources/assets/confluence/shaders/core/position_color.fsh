#version 150
#extension GL_ARB_explicit_attrib_location : enable

in vec4 vertexColor;

uniform vec4 colorMask;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 frag3;

void main() {
    vec4 color = vertexColor;
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color * colorMask;

    frag3 = color * colorMask;
}
