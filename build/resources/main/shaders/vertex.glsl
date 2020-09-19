#version 150 core

in vec2 position;
in vec4 color;
in vec2 texcoord;
in float textureIndex;

out vec4 vertexColor;
out vec2 textureCoord;
out float v_textureIndex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
    v_textureIndex = textureIndex;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 0.0, 1.0);
}