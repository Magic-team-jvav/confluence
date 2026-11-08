#version 150

// Bloom downsample (dual Kawase): centre x4 + four diagonals at half-texel offsets (bilinear)
uniform sampler2D Sampler0;
uniform vec2 TexelSize;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 h = TexelSize * 0.5;
    vec3 sum = texture(Sampler0, texCoord).rgb * 4.0;
    sum += texture(Sampler0, texCoord - h).rgb;
    sum += texture(Sampler0, texCoord + h).rgb;
    sum += texture(Sampler0, texCoord + vec2(h.x, -h.y)).rgb;
    sum += texture(Sampler0, texCoord - vec2(h.x, -h.y)).rgb;
    fragColor = vec4(sum / 8.0, 1.0);
}
