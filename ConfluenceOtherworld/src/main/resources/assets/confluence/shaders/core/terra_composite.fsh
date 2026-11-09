#version 150

// Final composite: (heat haze) scene + atmosphere + light shafts + bloom -> exposure -> soft highlight shoulder
// -> contrast, saturation -> Terraria biome/event tint -> vignette
uniform sampler2D Sampler0;      // scene
uniform sampler2D BloomSampler;  // bloom (half resolution, bilinear upscale)
uniform float BloomIntensity;
uniform float Contrast;
uniform float Saturation;
uniform float Vignette;
uniform vec3 Tint;
uniform float TintStrength;
uniform float Aspect;
// atmosphere (distance haze + morning ground fog) and light shafts
uniform sampler2D DepthSampler;      // world depth, captured before the hand is drawn
uniform sampler2D HandDepthSampler;  // depth after the hand pass (hand pixels differ from world depth)
uniform sampler2D RaysSampler;       // light shafts (half resolution)
uniform sampler2D PointRaysSampler;  // rays from bright point lights (lanterns), already coloured
uniform mat4 InvViewProj;
uniform float CameraY;
uniform vec3 FogColor;
uniform float AerialStrength;
uniform float AerialDistance;
uniform float GroundFog;
uniform float FogBaseY;
uniform float RaysStrength;
uniform vec3 RaysColor;
// Terraria effects: exposure, close storm/biome haze, heat distortion
uniform float Exposure;
uniform vec3 HazeColor;
uniform float Haze;
uniform float HazeDistance;
uniform float Distortion;
uniform float Time;
// split toning (sickly Corruption / Crimson look), coloured and pulsing vignette edges
uniform vec3 ShadowColor;      // luminance-normalised
uniform float ShadowStrength;
uniform vec3 HighlightColor;   // luminance-normalised
uniform float HighlightStrength;
uniform vec3 EdgeColor;
uniform float Pulse;

in vec2 texCoord;
out vec4 fragColor;

vec3 shoulder(vec3 c) {
    // below 0.8 unchanged, above it eases into 1 so bloom never clips to flat white
    vec3 over = max(c - 0.8, 0.0);
    return min(c, 0.8) + 0.2 * (1.0 - exp(-over / 0.2));
}

void main() {
    float worldDepth = texture(DepthSampler, texCoord).r;
    float handDepth = texture(HandDepthSampler, texCoord).r;
    bool hand = handDepth < 1.0 && abs(handDepth - worldDepth) > 1e-5;
    bool sky = worldDepth >= 0.99995;

    // world distance of this pixel (sky = far away)
    float dist = 100000.0;
    float y = CameraY + 1000.0;
    bool needWorld = AerialStrength > 0.0 || GroundFog > 0.0 || Haze > 0.0 || Distortion > 0.0;
    if (!sky && needWorld) {
        vec4 p = InvViewProj * vec4(texCoord * 2.0 - 1.0, worldDepth * 2.0 - 1.0, 1.0);
        vec3 rel = p.xyz / p.w;
        dist = length(rel);
        y = CameraY + rel.y;
    }

    // heat haze: rising shimmer, stronger with distance, never on the hand
    vec2 uv = texCoord;
    if (Distortion > 0.0 && !hand) {
        float far = smoothstep(6.0, 40.0, dist);
        float wave = sin(texCoord.y * 95.0 + Time * 4.2) + 0.5 * sin(texCoord.y * 210.0 - Time * 6.1 + texCoord.x * 20.0);
        vec2 offset = vec2(wave * 0.0016, sin(texCoord.x * 60.0 + Time * 3.0) * 0.0007) * Distortion * far;
        uv = clamp(texCoord + offset, vec2(0.0005), vec2(0.9995));
    }
    vec3 scene = texture(Sampler0, uv).rgb;

    // atmosphere: only on world pixels (not sky, not the hand)
    if (!hand && !sky && (AerialStrength > 0.0 || GroundFog > 0.0)) {
        float aerial = (1.0 - exp(-dist / AerialDistance)) * AerialStrength;
        float ground = GroundFog * (1.0 - exp(-dist / 40.0)) * exp(-max(y - FogBaseY, 0.0) / 9.0);
        scene = mix(scene, FogColor, clamp(aerial + ground, 0.0, 0.85));
    }
    // close haze of storms and evil biomes (also hides the sky)
    if (!hand && Haze > 0.0) {
        float haze = sky ? Haze : Haze * (1.0 - exp(-dist / HazeDistance));
        scene = mix(scene, HazeColor, clamp(haze, 0.0, 0.9));
    }
    // light shafts (also over the hand: light in the air in front of it)
    if (RaysStrength > 0.0) scene += texture(RaysSampler, texCoord).r * RaysStrength * RaysColor;
    scene += texture(PointRaysSampler, texCoord).rgb;
    if (BloomIntensity > 0.0) scene += texture(BloomSampler, uv).rgb * BloomIntensity;
    vec3 color = shoulder(scene * Exposure);

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
    // split toning: shadows and highlights each pushed toward their own hue, brightness kept
    luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    float shadowMask = 1.0 - smoothstep(0.0, 0.5, luma);
    float highlightMask = smoothstep(0.45, 1.0, luma);
    color = mix(color, clamp(ShadowColor * luma, 0.0, 1.0), ShadowStrength * shadowMask);
    color = mix(color, clamp(HighlightColor * luma, 0.0, 1.0), HighlightStrength * highlightMask);
    // vignette (aspect-corrected to a circle)
    vec2 d = (texCoord - 0.5) * vec2(Aspect, 1.0);
    float beat = Pulse * (0.5 + 0.5 * sin(Time * 2.4)) * (0.5 + 0.5 * sin(Time * 4.8 + 1.3));
    float vignette = smoothstep(0.95 - beat * 0.25, 0.25, length(d));
    float edge = mix(1.0, vignette, clamp(Vignette * (1.0 + beat), 0.0, 1.0));
    color = color * edge + EdgeColor * (1.0 - edge) * 0.35;

    fragColor = vec4(color, 1.0);
}
