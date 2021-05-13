#version 330 core

out vec4 FragColor;
in vec4 myColors;

void main()
{
   FragColor = myColors;
}
