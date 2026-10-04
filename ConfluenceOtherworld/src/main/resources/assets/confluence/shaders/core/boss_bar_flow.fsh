#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float Time;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.01) discard;
    float broadWave = 0.5 + 0.5 * sin(texCoord0.x * 38.0 - texCoord0.y * 12.0 - Time * 3.0);
    float fineWave = 0.5 + 0.5 * sin(texCoord0.x * 91.0 + texCoord0.y * 23.0 + Time * 4.5);
    float glow = pow(broadWave, 5.0) * 0.22 + pow(fineWave, 9.0) * 0.08;
    fragColor = vec4(color.rgb + vec3(glow) * color.a, color.a) * ColorModulator;
}
