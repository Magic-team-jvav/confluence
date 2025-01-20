#version 330 core
in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform float Time;
uniform float Radius;
out vec4 fragColor;

void main(){

    vec4 col = texture(Sampler0, texCoord0);
    if(col.a < 0.01) discard;

    vec4 col2 = texture(Sampler1, texCoord0 + vec2(0.0, Time * 0.01));
    fragColor = col * ColorModulator + col2 * Radius;
}
