/**
 * 噪声火焰效果
 */
#version 150
in vec2 texCoord0;
in vec4 vertexColor;

uniform sampler2D Sampler0;  // 遮罩纹理
uniform vec4 ColorModulator;
uniform float Time;

out vec4 fragColor;

vec4 lerp(vec4 a, vec4 b, float t) {
    return (1.0 - t) * a + t * b;
}

vec4 power(vec4 a, float t) {
    return vec4(pow(a.r, t), pow(a.g, t), pow(a.b, t), pow(a.a, t));
}

vec4 calColor(vec2 uv){
    // UV偏移
    float speed = 0.8;
//    vec2 uvOffset1 = vec2(0.0, Time * speed * 0.5);
//    vec2 uvOffset2 = vec2(Time * speed, Time * speed * 0.7);
    vec2 uvOffset1 = vec2(0.0, Time * speed * 0.5);
    vec2 uvOffset2 = vec2(Time * speed * 0.3, Time * speed * 1.2);


    float scale1 = 1.0;
    float scale2 = 1.0;

    // 增加采样权重
    vec4 color = texture(Sampler0, uv * scale1 + uvOffset1) * 1.2;
    vec4 color1 = texture(Sampler0, uv * scale2 + uvOffset2) * 0.8;

    if (color.a + color1.a < 0.01) {
        discard;
    }

    // 混合颜色
    vec4 blended = (color + color1) * 0.5 ;
    vec4 baseColor = blended * ColorModulator * vertexColor;

    // 增加对比
    vec4 powered = power(baseColor, 1.5);

    // 渐变
    float gradientFactor = clamp(pow(texCoord0.y, 1.2) , 0.0, 1.0);
    vec4 finalColor = lerp(powered, vec4(0.0), gradientFactor) * 2.0;

    if (finalColor.a < 0.01) {
        discard;
    }
    return finalColor;
}

void main() {
//    if(texCoord0.x > 0.1 && texCoord0.x < 0.9){
//        fragColor = calColor(texCoord0);
//    }else{
//        // 修复硬边
//        float size = 0.01;
//        vec4 from = calColor(vec2(1 - size, texCoord0.y));
//        vec4 to = calColor(vec2(size, texCoord0.y));
//        float lp = texCoord0.x > 0.5? texCoord0.x - 1.0 : texCoord0.x;
//        fragColor = lerp(from, to,(lp + size) / (2.0 * size));
//    }
    fragColor = calColor(texCoord0);
}