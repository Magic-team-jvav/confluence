#version 150

// Underwater light shafts: march along the view ray inside the water, follow the (refracted) light
// back up to the surface and read a slowly moving shaft pattern there. Rendered at quarter resolution.
uniform sampler2D DepthSampler;
uniform mat4 InvViewProj;
uniform float SurfaceY;     // water surface height relative to the camera
uniform vec3 LightDir;      // direction light travels inside the water (pointing down)
uniform vec3 CameraWorld;   // camera world x/z (wrapped) so the pattern stays put in the world
uniform float Time;
uniform float Clarity;      // 1 = clear ocean, lower = murky (swamp, evil biomes, caves)

in vec2 texCoord;
out vec4 fragColor;

float shafts(vec2 q, float t) {
    float a = sin(q.x * 0.9 + t * 0.6) * sin(q.y * 0.7 - t * 0.45);
    float b = sin((q.x + q.y) * 1.7 - t * 0.9) * sin((q.x - q.y) * 1.3 + t * 0.7);
    return clamp(0.5 + 0.35 * a + 0.25 * b, 0.0, 1.0);
}

void main() {
    float depth = texture(DepthSampler, texCoord).r;
    vec4 far = InvViewProj * vec4(texCoord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec3 end = far.xyz / far.w;
    float dist = min(length(end), 40.0);
    vec3 dir = normalize(end);
    const int STEPS = 16;
    float stepLength = dist / float(STEPS);
    float jitter = fract(sin(dot(texCoord, vec2(12.9898, 78.233))) * 43758.5453);
    vec2 slope = LightDir.xz / max(-LightDir.y, 0.2);
    float light = 0.0;
    for (int i = 0; i < STEPS; i++) {
        float s = (float(i) + jitter) * stepLength;
        vec3 p = dir * s;
        float below = SurfaceY - p.y;
        if (below < 0.0) continue;
        vec2 q = p.xz + CameraWorld.xz - slope * below;
        float shaft = shafts(q * 0.35, Time);
        light += shaft * shaft * shaft * exp(-s * 0.06 / Clarity) * exp(-below * 0.05 / Clarity);
    }
    fragColor = vec4(vec3(light * stepLength * 0.12 * min(Clarity, 1.2)), 1.0);
}
