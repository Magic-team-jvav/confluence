#version 150
out vec4 fragColor;

in vec2 texCoord;

uniform sampler2D DiffuseSampler;

vec3 sepiaTone(vec3 color) {
    vec3 outputColor = color;
    outputColor.r = dot(color, vec3(0.433, 0.769, 0.329));
    outputColor.g = dot(color, vec3(0.399, 0.686, 0.168));
    outputColor.b = dot(color, vec3(0.312, 0.534, 0.131));
    return mix(color, outputColor, 0.9);
}

void main()
{
    vec4 color = texture(DiffuseSampler, texCoord);

    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = mix(color.rgb, vec3(gray), 0.7);

    color.rgb = sepiaTone(color.rgb);

    color.rgb = pow(color.rgb, vec3(2.2));

    color.rgb = mix(color.rgb, vec3(0.95, 0.85, 0.75), 0.1);

    vec2 center = texCoord - 0.5;
    float vignette = 1.0 - dot(center, center) * 2.0;
    color.rgb *= pow(vignette, 0.9);

    fragColor = mix(color,texture(DiffuseSampler, texCoord),0.2);
}