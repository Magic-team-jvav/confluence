#version 150

// Light rays from bright point lights (Lantern Night lanterns) in the dark: march from each pixel toward
// the light on screen and gather the light's glow wherever the view is open up to the light's depth;
// leaves, walls and edges in front of it cut the glow into rays. Up to 4 lights, quarter resolution.
uniform sampler2D DepthSampler;
uniform vec4 Light0;   // u, v, depth, strength
uniform vec4 Light1;
uniform vec4 Light2;
uniform vec4 Light3;
uniform vec4 Radius;   // glow radius on screen (uv units) per light
uniform vec3 Color;
uniform float Aspect;

in vec2 texCoord;
out vec4 fragColor;

const int SAMPLES = 20;

float rays(vec4 light, float radius) {
    if (light.w <= 0.0) return 0.0;
    vec2 stepUv = (light.xy - texCoord) / float(SAMPLES);
    vec2 uv = texCoord;
    float decay = 1.0;
    float sum = 0.0;
    float inv = 1.0 / max(radius * radius, 1e-6);
    for (int i = 0; i < SAMPLES; i++) {
        uv += stepUv;
        if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) break;
        float open = texture(DepthSampler, uv).r > light.z ? 1.0 : 0.0;
        vec2 d = (uv - light.xy) * vec2(Aspect, 1.0);
        sum += open * exp(-dot(d, d) * inv) * decay;
        decay *= 0.93;
    }
    return sum / float(SAMPLES) * light.w;
}

void main() {
    float light = rays(Light0, Radius.x) + rays(Light1, Radius.y) + rays(Light2, Radius.z) + rays(Light3, Radius.w);
    fragColor = vec4(Color * light * 3.0, 1.0);
}
