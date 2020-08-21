#version 150 core

#ifndef MAX_LIGHTS
#define MAX_LIGHTS 10
#endif

in vec2 position;
in vec4 color;
in vec2 texcoord;
in float textureIndex;
in vec3 normal;

out vec4 vertexColor;
out vec2 textureCoord;
out float v_textureIndex;
out vec3 surfaceNormal;
out vec3 toLightVector[MAX_LIGHTS];

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 lightPosition[MAX_LIGHTS];

void main() {
    vec4 worldPosition = model * vec4(position, 0.0, 1.0);
    vertexColor = color;
    textureCoord = texcoord;
    v_textureIndex = textureIndex;
    gl_Position = projection * view * worldPosition;

    surfaceNormal = normalize((model * vec4(normal, 1.0f)).xyz);
    for(int i = 0;i<MAX_LIGHTS;i++){
        toLightVector[i] = normalize(lightPosition[i] - worldPosition.xyz);
    }
}