#version 330 core

layout (location = 0) in vec3 aPos; 
layout (location = 1) in vec4 vertexColors; 

uniform mat4 matrix; 

out vec4 myColors; 

void main() {
    gl_Position = matrix *  vec4(aPos.x, aPos.y, aPos.z, 1.0);
    myColors = vertexColors;
}



