#version 420
in vec2 texCoord;

uniform sampler2D att;
uniform sampler2D ori;

out vec4 fragColor;

void main(){
    //混合模式
    //fragColor = texture(ori, texCoord) * texture(att, texCoord);


   if(texture(ori, texCoord).a < 0.01) {

   }else{

   }
    fragColor = texture(ori, texCoord) + texture(att, texCoord);
}
