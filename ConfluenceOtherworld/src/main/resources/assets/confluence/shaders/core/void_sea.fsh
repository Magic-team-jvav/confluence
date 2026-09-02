#version 150

// @formatter:off
#moj_import <fog.glsl>
// @formatter:on

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform int FogShape;
uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform sampler2D DepthSampler;
uniform vec2 ScreenSize;
uniform vec3 EdgeColor;
uniform float EdgeCoreWidth;
uniform float EdgeGlowWidth;
uniform float EdgeCoreStrength;
uniform float EdgeGlowStrength;
uniform float GameTime;
uniform int LayerCount;
uniform float DetailAlpha;
uniform float DetailBrightness;
uniform float FlowSpeed;
uniform float LayerScaleStep;
uniform float Hue;
uniform float HueStep;
uniform float Saturation;
uniform float FlickerIntensity;
uniform float FlickerSpeed;

in vec4 vertexColor;
in vec2 texCoord;
in vec2 lightMapCoord;
in vec3 fogPosition;

out vec4 fragColor;

vec2 rotate(vec2 value, float angle) {
    float sine = sin(angle);
    float cosine = cos(angle);
    return mat2(cosine, -sine, sine, cosine) * value;
}

vec3 hsvToRgb(vec3 hsv) {
    vec3 p = abs(fract(hsv.xxx + vec3(0.0, 0.6666667, 0.3333333)) * 6.0 - 3.0);
    return hsv.z * mix(vec3(1.0), clamp(p - 1.0, 0.0, 1.0), hsv.y);
}

vec3 screen(vec3 baseColor, vec3 detailColor) {
    return 1.0 - (1.0 - baseColor) * (1.0 - detailColor);
}

float getEdgeDistance() {
    vec2 pixelSize = 1.0 / ScreenSize;
    float depthScale = max(fwidth(gl_FragCoord.z), 0.0000001);
    float edgeDistance = EdgeGlowWidth + 1.0;
    for (int y = -1; y <= 1; ++y) {
        for (int x = -1; x <= 1; ++x) {
            float sceneDepth = texture(DepthSampler, gl_FragCoord.xy / ScreenSize + vec2(x, y) * pixelSize).r;
            if (sceneDepth < 0.999999) {
                edgeDistance = min(edgeDistance, abs(sceneDepth - gl_FragCoord.z) / depthScale);
            }
        }
    }
    return edgeDistance;
}

void main() {
    float fogDistance = fog_distance(fogPosition, FogShape);
    vec3 seaColor = vertexColor.rgb;
    vec3 emissiveColor = vec3(0.0);
    for (int layer = 0; layer < 32; ++layer) {
        if (layer >= LayerCount) {
            break;
        }
        float layerIndex = float(layer);
        float layerScale = pow(LayerScaleStep, layerIndex);
        vec2 layerUv = texCoord * layerScale;
        float layerTime = GameTime * (1.0 + layerIndex * 0.15);
        layerUv = rotate(layerUv, layerIndex * 0.7);
        layerUv += vec2(cos(layerIndex), sin(layerIndex)) * layerTime * FlowSpeed;
        vec3 textureColor = texture(Sampler0, fract(layerUv)).rgb;
        vec3 tint = hsvToRgb(vec3(fract(Hue + layerIndex * HueStep), Saturation, DetailBrightness));
        float layerAlpha = DetailAlpha * (1.0 - layerIndex / float(LayerCount));
        float flicker = 1.0 - FlickerIntensity + FlickerIntensity * (0.5 + 0.5 * sin(layerTime * FlickerSpeed + layerIndex * 2.3));
        emissiveColor = mix(emissiveColor, screen(emissiveColor, textureColor * tint * flicker), layerAlpha);
    }
    emissiveColor *= texture(Sampler2, lightMapCoord).rgb;
    fragColor = linear_fog(vec4(seaColor, vertexColor.a), fogDistance, FogStart, FogEnd, FogColor);
    fragColor.rgb = screen(fragColor.rgb, emissiveColor);
    float edgeDistance = getEdgeDistance();
    float edgeCore = 1.0 - smoothstep(0.0, EdgeCoreWidth, edgeDistance);
    float edgeGlow = 1.0 - smoothstep(EdgeCoreWidth, EdgeGlowWidth, edgeDistance);
    vec3 edgeColor = EdgeColor * (edgeCore * EdgeCoreStrength + edgeGlow * EdgeGlowStrength);
    fragColor.rgb = screen(fragColor.rgb, clamp(edgeColor, 0.0, 1.0));
    fragColor.a *= 1.0 - smoothstep(FogStart, FogEnd, fogDistance);
}
