#version 150 core

#ifndef MAX_TEXTURES
#define MAX_TEXTURES 8
#endif

#ifndef MAX_LIGHTS
#define MAX_LIGHTS 10
#endif

in vec4 vertexColor;
in vec2 textureCoord;
in float v_textureIndex;
in vec3 surfaceNormal;
in vec3 toLightVector[MAX_LIGHTS];

out vec4 fragColor;

uniform sampler2D textures[MAX_TEXTURES];
uniform vec3 lightColors[MAX_LIGHTS];

void main() {
    vec3 unitNormal = surfaceNormal;

    vec3 diffuse = vec3(0.0);

    for(int i = 0;i<MAX_LIGHTS;i++){
        vec3 unitLightVector = toLightVector[i];
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        diffuse = diffuse + brightness * lightColors[i];
    }

    diffuse = max(diffuse, 0.2);

    int index = int(v_textureIndex);
    vec4 textureColor = texture(textures[index], textureCoord);
    fragColor = (textureColor * vertexColor) * vec4(diffuse, 1.0);
}
