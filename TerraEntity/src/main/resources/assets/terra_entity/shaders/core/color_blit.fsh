#version 150
in vec2 texCoord;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;
uniform float Time;
out vec4 fragColor;

void main(){
    //if(texture(att, texCoord).a < 0.01) discard;
    vec4 color0 = texture(Sampler0, texCoord);

    vec4 color1 = texture(Sampler1, texCoord);
    if(color1.a < 0.01){
        fragColor =  color0;
        return;
    }else{
//        vec2 uv = texCoord;
//
//        // 添加一些随机偏移
//        float glitchFactor = 0.1;
//        float glitchSpeed = 0.5;
//        float glitchAmount = sin(Time * glitchSpeed) * glitchFactor;
//
//        // 随机选择一个方向进行偏移
//        float randomDir = texture(Sampler1, vec2(uv.x * 2.0, uv.y * 2.0)).r;
//        if (randomDir > 0.5) {
//            uv.x += glitchAmount;
//        } else {
//            uv.x -= glitchAmount;
//        }
//
//        // 随机偏移颜色通道
//        vec4 color = texture(Sampler1, uv);
//        float randomChannel = texture(Sampler1, vec2(uv.x * 4.0, uv.y * 4.0)).g;
//        if (randomChannel > 0.5) {
//            color.r = texture(Sampler1, vec2(uv.x + glitchAmount, uv.y)).r;
//        } else {
//            color.b = texture(Sampler1, vec2(uv.x - glitchAmount, uv.y)).b;
//        }
//
//        // 添加一些随机噪声
//        float noiseFactor = 0.05;
//        float noise = texture(Sampler1, vec2(uv.x * 8.0, uv.y * 8.0)).a * noiseFactor;
//        color += noise;
//        fragColor =  vec4(color0.rgb, 1.0) * (1.0 - ColorModulator.a) + vec4(ColorModulator.rgb, 1.0) * color * ColorModulator.a;


        fragColor =  vec4(color0.rgb, 1.0) * (1.0 - ColorModulator.a) + vec4(ColorModulator.rgb, 1.0) * color1 * ColorModulator.a;

    }
}
