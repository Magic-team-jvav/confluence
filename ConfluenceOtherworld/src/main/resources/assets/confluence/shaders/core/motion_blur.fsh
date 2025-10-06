#version 420
in vec2 texCoord;

//layout(binding = 0)
//layout(binding = 1)
uniform sampler2D att;

uniform float dist;
uniform vec2 dir;

out vec4 fragColor;

void main(){

/*
    //卷积模糊
//    if( length(texture(att, texCoord ).rgb)>0){
//        fragColor =  texture(ori, texCoord);
////        fragColor =  texture(att, texCoord);
//    }else{
        float offset = offs/ 512.0;
        vec4 color1 = vec4(0.0);
        for(int i = -1; i <=1; i++){
            for(int j = -1; j <= 1; j++){
                color1 += texture(att, texCoord + vec2(float(i) * offset, float(j) * offset));
            }
        }
        color1 /= 9;
    fragColor = color1*1.1;
//        fragColor =  texture(ori, texCoord) + color1;
//    }
    */
    vec2 dir1 = vec2(dir.x, -dir.y);
    vec2 oneTexel = vec2(0.3/dist, 0.3/dist);
    vec3 color = vec3(0.0);
    for(int i = -6; i <= 2; i++){
        color += texture(att, texCoord + i*oneTexel*normalize(dir1)).rgb;
    }
    fragColor = vec4(color/10.0,texture(att,texCoord).a);



}
