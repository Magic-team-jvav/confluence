#version 150

uniform sampler2D Sampler0;
uniform float Zoom;
uniform float Distortion;
uniform float CenterDistortion;
uniform float Time;
uniform float FilterProgress;
uniform float FilterFade;
uniform float BlackFilterRadius;
uniform float BlackFilterMaxRadius;
uniform float PurpleFilterRadius;
uniform float PurpleFilterMaxRadius;
uniform float FilterTransitionRatio;
uniform float FilterTransitionStrength;
uniform float FullScreenBlackFilterAlpha;
uniform vec3 BlackFilterColor;
uniform float BlackFilterAlpha;
uniform float BlackFilterMinAlpha;
uniform vec3 PurpleFilterColor;
uniform float PurpleFilterAlpha;

in vec2 texCoord;

out vec4 fragColor;

vec2 circleCoordinate;

vec4 applyFullScreenBlackFilter(vec4 color) {
    return vec4(mix(color.rgb, BlackFilterColor, FullScreenBlackFilterAlpha * FilterFade), color.a);
}

float drawCircle(float radius, float maxAlpha, float minAlpha, float transitionRatio, float transitionStrength) {
    vec2 centered = circleCoordinate - vec2(0.5);
    float currentRadius = length(centered);
    float transitionStart = radius * (1.0 - transitionRatio);
    float startAlpha = max(minAlpha, maxAlpha * (1.0 - transitionStrength));
    if (currentRadius < transitionStart) {
        return 0.0;
    }
    if (currentRadius < radius) {
        return startAlpha * smoothstep(transitionStart, radius, currentRadius);
    }
    float edgeRadius = currentRadius * 0.5 / max(max(abs(centered.x), abs(centered.y)), 0.000001);
    return mix(startAlpha, maxAlpha, smoothstep(radius, edgeRadius, currentRadius));
}

vec4 applyBlackFilter(vec4 color, vec2 coordinate) {
    circleCoordinate = coordinate;
    float radius = mix(BlackFilterRadius, BlackFilterMaxRadius, FilterProgress);
    float alpha = drawCircle(radius, BlackFilterAlpha * FilterFade, BlackFilterMinAlpha * FilterFade, FilterTransitionRatio, FilterTransitionStrength);
    return vec4(mix(color.rgb, BlackFilterColor, alpha), color.a);
}

vec4 applyPurpleFilter(vec4 color, vec2 coordinate) {
    circleCoordinate = coordinate;
    float radius = mix(PurpleFilterRadius, PurpleFilterMaxRadius, FilterProgress);
    float alpha = drawCircle(radius, PurpleFilterAlpha * FilterFade, 0.0, FilterTransitionRatio, FilterTransitionStrength);
    return vec4(mix(color.rgb, PurpleFilterColor, alpha), color.a);
}

void main() {
    vec2 centered = texCoord - 0.5;
    vec2 zoomedUv = 0.5 + centered * (1.0 - Zoom);
    float edgeMask = smoothstep(0.0, 1.0, length(centered) * 2.0);
    vec2 wave = vec2(sin(zoomedUv.y * 48.0 + Time * 1.3), cos(zoomedUv.x * 52.0 - Time)) * Distortion * mix(CenterDistortion, 1.0, edgeMask);
    vec2 distortedUv = clamp(zoomedUv + wave, 0.0, 1.0);
    vec4 result = texture(Sampler0, distortedUv);
    result = applyFullScreenBlackFilter(result);
    result = applyBlackFilter(result, distortedUv);
    result = applyPurpleFilter(result, distortedUv);
    fragColor = result;
}
