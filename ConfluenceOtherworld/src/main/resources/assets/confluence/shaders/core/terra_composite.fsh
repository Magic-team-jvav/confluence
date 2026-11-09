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
// underwater: caustics, absorption, refraction wobble, light shafts
uniform float Underwater;          // 0 = camera not in water
uniform float SurfaceY;            // water surface relative to the camera
uniform vec3 UnderwaterLight;      // light colour * sun/moon * open sky
uniform vec3 LightDir;             // direction light travels in the water
uniform vec3 CameraWorld;          // camera world x/z (wrapped)
uniform sampler2D UnderwaterSampler;
uniform vec3 UnderwaterTint;       // colour of light in this biome's water (halo, local lights)
uniform vec3 UnderwaterAbsorb;     // per-block absorption of red, green, blue
uniform float UnderwaterClarity;   // 1 = clear ocean, lower = murky
uniform vec3 UnderwaterGlow;       // self-lit caustics (glowing mushroom water)

vec2 cfHash2(vec2 p) {
    return fract(sin(vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)))) * 43758.5453);
}

// caustic network: bright where two moving cell centres are equally close (cell borders)
float cfCaustic(vec2 p, float t) {
    vec2 cell = floor(p);
    vec2 f = fract(p);
    float d1 = 8.0;
    float d2 = 8.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 o = vec2(float(x), float(y));
            vec2 h = cfHash2(cell + o);
            vec2 pt = o + 0.5 + 0.38 * sin(t * 0.8 + 6.2831 * h);
            float d = length(pt - f);
            if (d < d1) {
                d2 = d1;
                d1 = d;
            } else if (d < d2) {
                d2 = d;
            }
        }
    }
    return pow(1.0 - smoothstep(0.0, 0.22, d2 - d1), 2.5);
}

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
    vec3 rel = vec3(0.0);
    bool needWorld = AerialStrength > 0.0 || GroundFog > 0.0 || Haze > 0.0 || Distortion > 0.0 || Underwater > 0.0;
    if (!sky && needWorld) {
        vec4 p = InvViewProj * vec4(texCoord * 2.0 - 1.0, worldDepth * 2.0 - 1.0, 1.0);
        rel = p.xyz / p.w;
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
    // underwater: everything wobbles a little, as seen through moving water
    if (Underwater > 0.0) {
        vec2 wobble = vec2(sin(texCoord.y * 38.0 + Time * 2.1), cos(texCoord.x * 33.0 + Time * 1.7)) * 0.0012 * Underwater;
        uv = clamp(uv + wobble, vec2(0.0005), vec2(0.9995));
    }
    vec3 scene = texture(Sampler0, uv).rgb;

    // atmosphere: only on world pixels (not sky, not the hand)
    if (!hand && !sky && (AerialStrength > 0.0 || GroundFog > 0.0)) {
        float aerial = (1.0 - exp(-dist / AerialDistance)) * AerialStrength;
        float ground = GroundFog * (1.0 - exp(-dist / 40.0)) * exp(-max(y - FogBaseY, 0.0) / 9.0);
        scene = mix(scene, FogColor, clamp(aerial + ground, 0.0, 0.85));
    }
    // underwater light: caustics on surfaces below the water line, red light absorbed first, shafts in the water
    if (Underwater > 0.0) {
        // local lights (torches, glowing blocks, a torch in hand) light the water too; they matter most
        // where little sky light reaches, so in sunny shallows the caustics are not doubled
        float localWeight = 1.0 - clamp(max(UnderwaterLight.r, max(UnderwaterLight.g, UnderwaterLight.b)) * 1.5, 0.0, 1.0);
        if (!sky && !hand) {
            float below = SurfaceY - rel.y;
            if (below > 0.0) {
                vec2 q = (rel.xz + CameraWorld.xz - LightDir.xz / max(-LightDir.y, 0.2) * below) * 0.55;
                float c = cfCaustic(q, Time) * 0.7 + cfCaustic(q * 1.9 + 3.1, Time * 1.3) * 0.3;
                float fade = exp(-dist * 0.05 / UnderwaterClarity) * exp(-below * 0.04 / UnderwaterClarity);
                float sharp = clamp(UnderwaterClarity, 0.3, 1.3);
                float lum = dot(scene, vec3(0.2126, 0.7152, 0.0722));
                vec3 local = UnderwaterTint * smoothstep(0.08, 0.5, lum) * 0.9 * localWeight;
                scene += scene * c * (UnderwaterLight * 1.8 + local) * fade * sharp * Underwater;
                scene += c * UnderwaterGlow * exp(-dist * 0.04) * Underwater;
            }
            vec3 transmit = exp(-min(dist, 64.0) * UnderwaterAbsorb * Underwater);
            scene *= mix(vec3(1.0), transmit, 0.6);
        }
        scene += texture(UnderwaterSampler, texCoord).r * UnderwaterLight * Underwater;
        // light scatters in water: bright sources get a wide, water-tinted halo (uses the bloom chain)
        scene += texture(BloomSampler, uv).rgb * UnderwaterTint * 0.9 * Underwater;
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
