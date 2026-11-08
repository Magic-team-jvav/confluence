#version 150

// Bloom upsample (dual Kawase): 8-tap tent filter plus the same level of the downsample chain
uniform sampler2D Sampler0;     // smaller level
uniform sampler2D BaseSampler;  // this level of the downsample
uniform vec2 TexelSize;         // texel size of the smaller level

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 h = TexelSize * 0.5;
    vec3 sum = texture(Sampler0, texCoord + vec2(-h.x * 2.0, 0.0)).rgb;
    sum += texture(Sampler0, texCoord + vec2(-h.x, h.y)).rgb * 2.0;
    sum += texture(Sampler0, texCoord + vec2(0.0, h.y * 2.0)).rgb;
    sum += texture(Sampler0, texCoord + vec2(h.x, h.y)).rgb * 2.0;
    sum += texture(Sampler0, texCoord + vec2(h.x * 2.0, 0.0)).rgb;
    sum += texture(Sampler0, texCoord + vec2(h.x, -h.y)).rgb * 2.0;
    sum += texture(Sampler0, texCoord + vec2(0.0, -h.y * 2.0)).rgb;
    sum += texture(Sampler0, texCoord + vec2(-h.x, -h.y)).rgb * 2.0;
    fragColor = vec4(sum / 12.0 + texture(BaseSampler, texCoord).rgb, 1.0);
}
