#version 420
in vec2 texCoord;

uniform sampler2D att;
uniform sampler2D ori;

out vec4 fragColor;

void main(){
//    if(texture(att, texCoord).a < 0.01) discard;
    fragColor = texture(ori, texCoord) + texture(att, texCoord);

/*
    const float gamma = 2.2;
    const float exposure = 1;

    vec3 hdrColor = texture(ori, texCoord).rgb;
    vec3 bloomColor = texture(att, texCoord).rgb;
    hdrColor += bloomColor; // additive blending
    // tone mapping
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // also gamma correct while we're at it
    result = pow(result, vec3(1.0 / gamma));
    fragColor = vec4(result, 1.0f);
*/
}
