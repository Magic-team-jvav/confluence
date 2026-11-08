#version 150

// Bloom step 1: keep the bright parts of the image (soft threshold), only a little sky; rendered at half resolution
uniform sampler2D Sampler0;      // scene colour
uniform sampler2D DepthSampler;  // scene depth (1.0 = sky)
uniform float Threshold;
uniform float SkyBloom;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 color = texture(Sampler0, texCoord).rgb;
    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    float knee = 0.18;
    float soft = clamp(luma - Threshold + knee, 0.0, 2.0 * knee);
    soft = soft * soft / (4.0 * knee + 1e-4);
    float contribution = max(soft, luma - Threshold) / max(luma, 1e-4);
    // saturated, light-like colours glow more; flat white snow and sand a bit less
    float chroma = max(color.r, max(color.g, color.b)) - min(color.r, min(color.g, color.b));
    contribution *= mix(0.55, 1.0, clamp(chroma * 2.5, 0.0, 1.0));
    float depth = texture(DepthSampler, texCoord).r;
    if (depth >= 0.99995) contribution *= SkyBloom;
    fragColor = vec4(color * clamp(contribution, 0.0, 1.0), 1.0);
}
