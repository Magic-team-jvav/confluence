#version 150

#moj_import <fog.glsl>
#moj_import <confluence:noise.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 LocalPosition;

out vec4 fragColor;

void main() {
    float Strength1 = 0.25;
    float Strength2 = 2.0;  
    vec4 diffuse = texture(Sampler0, texCoord0);
    vec4 pos = LocalPosition;
    pos.y = GameTime * 1200.0;
    pos = vec4(floor(pos * 16.0 + vec4(0.5)) / 16.0);

    vec4 rgbColor = vec4(
                sin(pos.x * Strength1 + GameTime * 1200.0 + perlin_noise3D(pos.xyz) * Strength2),
                sin(pos.z * Strength1 + GameTime * 1200.0 + perlin_noise3D(pos.xyz) * Strength2),
                sin(pos.y * Strength1 + GameTime * 1200.0 + perlin_noise3D(pos.xyz) * Strength2),
                1.0);
    rgbColor = floor(rgbColor * 128.0 + vec4(0.5)) / 128.0;
    if(perlin_noise3D(pos.xyz) > 0.0){
        diffuse=mix(
            diffuse,
            perlin_noise3D(pos.xyz) > 0.3?diffuse * abs(rgbColor):vec4(1.0, 1.0, 1.0, diffuse.a),
            perlin_noise3D(pos.xyz)
        );
    }

    if(diffuse.a < 0.1)discard;

    vec4 color = diffuse * vertexColor * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
