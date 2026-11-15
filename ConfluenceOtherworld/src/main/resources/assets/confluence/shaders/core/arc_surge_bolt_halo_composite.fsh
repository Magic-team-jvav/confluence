#version 150
uniform sampler2D Sampler0;
in vec2 uv;
out vec4 fragColor;
void main(){fragColor=vec4(texture(Sampler0,uv).rgb,0.0);}
