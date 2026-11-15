#version 150

uniform sampler2D ModelColor;
uniform sampler2D ModelDepth;
in vec2 atlasPosition;
flat in float modelAlpha;
flat in vec3 modelTint;
out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(atlasPosition);
    vec4 color = texelFetch(ModelColor, pixel, 0);
    float depth = texelFetch(ModelDepth, pixel, 0).r;
    if (color.a <= 0.0 || depth >= 1.0) {
        discard;
    }
    color.rgb /= color.a;
    color.rgb *= modelTint;
    gl_FragDepth = max(0.0, depth - 1e-7);
    fragColor = vec4(color.rgb, color.a * modelAlpha);
}
