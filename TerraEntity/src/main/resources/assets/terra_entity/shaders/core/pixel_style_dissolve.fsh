/**
* 像素化溶解效果，用于3D坐标的像素化过度效果
*/

#version 150
in vec2 texCoord0;

uniform sampler2D Sampler0;  // 主纹理
uniform sampler2D Sampler1;  // 遮罩纹理
uniform vec4 ColorModulator;
uniform float Progress;      // 溶解进度 [0, 1]
uniform float PixelSize = 8.0; // 像素化大小，可通过uniform传入或设为固定值
out vec4 fragColor;

void main() {
    // 主纹理
    vec4 color0 = texture(Sampler0, texCoord0);
    if(color0.a == 0.0) {
        discard;
        return;
    }

    // 计算当前纹理坐标对应的像素块坐标
    //    vec2 uv = floor(texCoord0 * PixelSize) / PixelSize;

    vec2 uv = texCoord0 - mod(texCoord0, 1.0/PixelSize);

    // 遮罩纹理
    vec4 mask = texture(Sampler1, uv);

    // 溶解因子
    float dissolveFactor = mask.r;

    if(dissolveFactor < Progress) {
        discard;
    }

    // 溶解边缘过渡效果
    float edgeWidth = 0.05;
    if(dissolveFactor < Progress + edgeWidth) {
        // 计算过渡因子
        float transitionFactor = 1.0 - (Progress + edgeWidth - dissolveFactor) / edgeWidth;
        // 颜色变换
        color0.rgb *= transitionFactor * 0.5 + 0.5;
    }

    fragColor = color0 * ColorModulator;
}