#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec2 OutlineStep;
in vec2 texCoord;
in vec4 vertexColor;
out vec4 fragColor;

void main() {
    float center = texture(Sampler0, texCoord).a;
    if (center > 0.1) discard;
    float edge = 0.0;
    edge = max(edge, texture(Sampler0, texCoord + vec2(OutlineStep.x, 0.0)).a);
    edge = max(edge, texture(Sampler0, texCoord - vec2(OutlineStep.x, 0.0)).a);
    edge = max(edge, texture(Sampler0, texCoord + vec2(0.0, OutlineStep.y)).a);
    edge = max(edge, texture(Sampler0, texCoord - vec2(0.0, OutlineStep.y)).a);
    if (edge <= 0.1) discard;
    fragColor = vec4(vertexColor.rgb, edge * vertexColor.a) * ColorModulator;
}
