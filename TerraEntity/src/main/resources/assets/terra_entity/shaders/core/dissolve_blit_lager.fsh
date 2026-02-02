#version 150
in vec2 texCoord;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform vec4 ColorModulator;
uniform float Progress;
uniform float Distance;
out vec4 fragColor;

void main(){
    //if(texture(att, texCoord).a < 0.01) discard;
    // 主纹理
    vec4 color0 = texture(Sampler0, texCoord);
    // 待溶解纹理
    vec4 color1 = texture(Sampler1, texCoord);
    // 遮罩纹理 - 直接调大颗粒大小
    vec4 mask = texture(Sampler2, texCoord * max(1.0, Distance / 4) / 16.0);
    if(color1.a < 0.01){
        fragColor =  color0;
        return;
    }else{
        // 遮罩亮度
        float light = (mask.r+mask.g+mask.b) / 3.0;
        float up = pow(Progress, 0.9);

        if(light > Progress && light < up){
            fragColor =  vec4(color0.rgb, 1.0) * (1.0 - ColorModulator.a) + color1 * ColorModulator;
        }
        else if(light >= up){
            fragColor =  vec4(color0.rgb, 1.0) * (1.0 - ColorModulator.a) + color1 * ColorModulator.a;
        }else{
            fragColor =  color0;
        }

    }
}
