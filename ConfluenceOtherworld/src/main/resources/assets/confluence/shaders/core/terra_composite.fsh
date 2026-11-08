#version 150

// Final composite: scene + bloom -> soft highlight shoulder -> contrast, saturation -> tint (for biomes later) -> vignette
uniform sampler2D Sampler0;      // scene
uniform sampler2D BloomSampler;  // bloom (half resolution, bilinear upscale)
uniform float BloomIntensity;
uniform float Contrast;
uniform float Saturation;
uniform float Vignette;
uniform vec3 Tint;
uniform float TintStrength;
uniform float Aspect;

in vec2 texCoord;
out vec4 fragColor;

vec3 shoulder(vec3 c) {
    // below 0.8 unchanged, above it eases into 1 so bloom never clips to flat white
    vec3 over = max(c - 0.8, 0.0);
    return min(c, 0.8) + 0.2 * (1.0 - exp(-over / 0.2));
}

void main() {
    vec3 scene = texture(Sampler0, texCoord).rgb;
    vec3 bloom = texture(BloomSampler, texCoord).rgb;
    vec3 color = shoulder(scene + bloom * BloomIntensity);

    // contrast around mid grey
    color = clamp((color - 0.5) * Contrast + 0.5, 0.0, 1.0);
    // saturation
    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    color = clamp(mix(vec3(luma), color, Saturation), 0.0, 1.0);
    // tint (keeps luminance)
    vec3 tinted = color * Tint;
    float tintedLuma = dot(tinted, vec3(0.2126, 0.7152, 0.0722));
    tinted *= luma / max(tintedLuma, 1e-4);
    color = mix(color, clamp(tinted, 0.0, 1.0), TintStrength);
    // vignette (aspect-corrected to a circle)
    vec2 d = (texCoord - 0.5) * vec2(Aspect, 1.0);
    float vignette = smoothstep(0.95, 0.25, length(d));
    color *= mix(1.0, vignette, Vignette);

    fragColor = vec4(color, 1.0);
}
