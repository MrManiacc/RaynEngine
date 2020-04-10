#ifdef VERTEX_SHADER
    #version 400 core
    in vec3 vertex;
    in vec2 textureCoords;
    out vec2 pass_textureCoords;

    uniform mat4 projectionMatrix;
    uniform mat4 viewMatrix;
    uniform mat4 modelMatrix;

    void main(){
        gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertex, 1.0);
        pass_textureCoords = textureCoords;
}
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core
    in vec2 pass_textureCoords;
    out vec4 out_Color;

    uniform sampler2D diffuse;

    void main(){
        out_Color = texture(diffuse, pass_textureCoords);
    }
#endif