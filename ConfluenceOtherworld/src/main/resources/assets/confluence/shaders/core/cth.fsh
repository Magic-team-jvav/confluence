#version 150
#extension GL_ARB_explicit_attrib_location : enable
#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;



in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 frag3;


void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    float average = dot(fragColor.rgb,vec3(0.299, 0.587, 0.114));

//    frag3 = vec4(average, average, average, color.a);
    frag3 =fragColor;
}
