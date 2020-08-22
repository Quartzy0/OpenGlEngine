#version 150 core

#ifndef MAX_LIGHTS
#define MAX_LIGHTS 10
#endif

in vec2 position;
in vec4 color;
in vec2 texcoord;
in float textureIndex;
#ifdef LIGHTING_ENABLED
in vec3 normal;
#endif

out vec4 vertexColor;
out vec2 textureCoord;
out float v_textureIndex;
#ifdef LIGHTING_ENABLED
out vec3 surfaceNormal;
out vec3 toLightVector[MAX_LIGHTS];
#endif

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
#ifdef LIGHTING_ENABLED
uniform vec3 lightPosition[MAX_LIGHTS];
#endif

void main() {
    vec4 worldPosition = model * vec4(position, 0.0, 1.0);
    vertexColor = color;
    textureCoord = texcoord;
    v_textureIndex = textureIndex;
    gl_Position = projection * view * worldPosition;

    #ifdef LIGHTING_ENABLED
    surfaceNormal = normalize((model * vec4(normal, 1.0f)).xyz);
    for(int i = 0;i<MAX_LIGHTS;i++){
        toLightVector[i] = normalize(lightPosition[i] - worldPosition.xyz);
    }
    #endif
}