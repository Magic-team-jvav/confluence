#version 150

uniform sampler2D DepthSampler;
uniform mat4 InverseProjMat;
uniform mat4 InverseViewMat;
uniform float SeaRelativeY;
uniform vec3 SubmergedColor;
uniform float SubmergedStrength;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float sceneDepth = texture(DepthSampler, texCoord).r;
    if (sceneDepth >= 0.999999) {
        discard;
    }

    vec4 clipPosition = vec4(texCoord * 2.0 - 1.0, sceneDepth * 2.0 - 1.0, 1.0);
    vec4 viewPosition = InverseProjMat * clipPosition;
    viewPosition /= viewPosition.w;
    vec4 relativePosition = InverseViewMat * vec4(viewPosition.xyz, 1.0);
    if (relativePosition.y < SeaRelativeY) {
        fragColor = vec4(SubmergedColor, SubmergedStrength);
        return;
    }
    discard;
}
