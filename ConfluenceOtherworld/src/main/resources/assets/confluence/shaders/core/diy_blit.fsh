#version 150
in vec2 texCoord;

uniform sampler2D att;
uniform vec4 colorMask;
out vec4 fragColor;

void main(){
    //if(texture(att, texCoord).a < 0.01) discard;
    fragColor = texture(att, texCoord) * colorMask;
}
