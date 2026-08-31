#version 150

#moj_import < fog.glsl >

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform int FogShape;
uniform sampler2D Sampler0;
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

void main() {
    float fogDistance = fog_distance(fogPosition, FogShape);
    vec3 seaColor = vertexColor.rgb;
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
        seaColor = mix(seaColor, screen(seaColor, textureColor * tint * flicker), layerAlpha);
    }
    fragColor = linear_fog(vec4(seaColor, vertexColor.a), fogDistance, FogStart, FogEnd, FogColor);
    fragColor.a *= 1.0 - smoothstep(FogStart, FogEnd, fogDistance);
}
