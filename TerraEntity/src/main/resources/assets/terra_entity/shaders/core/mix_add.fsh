#version 150
in vec2 texCoord;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;
out vec4 fragColor;

void main(){
    //if(texture(att, texCoord).a < 0.01) discard;
    vec4 color0 = texture(Sampler0, texCoord);
    vec4 color1 = texture(Sampler1, texCoord);
    if(color1.a < 0.01 && color0.a > 0.01){
        discard;
        return;
    }

    fragColor =  (color0 + color1) * ColorModulator;

}
