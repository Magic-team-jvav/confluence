#version 150
in vec2 texCoord;

uniform sampler2D ori;
uniform sampler2D att;

out vec4 fragColor;

void main(){

    fragColor = texture(att, texCoord)+texture(ori, texCoord)*2;
    /*
    if(texture(att, texCoord).a > 0) {
        fragColor = vec4(texture(att, texCoord).rgb,1.0);
    }else{
        fragColor =vec4(texture(ori, texCoord).rgb,1.0);
//        fragColor = vec4(1,0,0, 1.0);
        */
    /*
        const float gamma = 2.2;
        const float exposure = 1;
        vec3 hdrColor = texture(ori, texCoord).rgb;
        vec3 bloomColor = texture(att, texCoord).rgb;
        hdrColor += bloomColor; // additive blending
        fragColor = vec4(hdrColor,1.0);
*/
    /*
    // tone mapping
    fragColor = vec4(hdrColor, 1.0f);
    vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // also gamma correct while we're at it
    result = pow(result, vec3(1.0 / gamma));
    fragColor = vec4(result, 1.0f);
*/
//    }

}
