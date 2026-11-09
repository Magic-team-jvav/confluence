#version 150

// Light shafts: march from each pixel toward the sun/moon on screen and gather open sky
// near the light; trees and cliffs in between leave dark gaps, which become the shafts.
uniform sampler2D DepthSampler;  // world depth (1.0 = sky)
uniform vec2 SunPos;             // light position in screen uv
uniform float Aspect;

in vec2 texCoord;
out vec4 fragColor;

const int SAMPLES = 32;

void main() {
    vec2 toSun = SunPos - texCoord;
    vec2 stepUv = toSun / float(SAMPLES);
    vec2 uv = texCoord;
    float decay = 1.0;
    float light = 0.0;
    for (int i = 0; i < SAMPLES; i++) {
        uv += stepUv;
        if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) break;
        float sky = texture(DepthSampler, uv).r >= 0.99995 ? 1.0 : 0.0;
        vec2 d = (uv - SunPos) * vec2(Aspect, 1.0);
        float glow = exp(-dot(d, d) * 14.0);
        light += sky * glow * decay;
        decay *= 0.965;
    }
    light /= float(SAMPLES) * 0.35;
    fragColor = vec4(vec3(clamp(light, 0.0, 1.0)), 1.0);
}
