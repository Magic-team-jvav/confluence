#version 150

in vec4 Position;
in vec2 UV0;

uniform mat4 ProjMat;
uniform vec2 OutSize;

out vec2 texCoord;

void main(){
    vec4 outPos = ProjMat * vec4(Position.xy, 0.0, 1.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);

    texCoord = UV0;;
}
