#version 150 core

in vec4 vertexColor;
in vec2 textureCoord;
in float v_textureIndex;

out vec4 fragColor;

uniform sampler2D textures[32];

void main() {
    int index = int(v_textureIndex);
    vec4 textureColor = texture(textures[index], textureCoord);
    fragColor = vertexColor * textureColor;
}
