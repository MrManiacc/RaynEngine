#ifdef VERTEX_SHADER
    #define engine:shaders:utils-> VERSION
    #define engine:shaders:utils-> BINDS
    #define engine:shaders:utils-> UNIFORMS
    out vec2 pass_textureCoords;
    void main(){
//        gl_Position = vec4(vertex, 0, 1.0);
        gl_Position = projectionMatrix * modelMatrix * vec4(vertex, 0, 1.0);
        pass_textureCoords = textureCoords;
    }
#endif

#ifdef FRAGMENT_SHADER
    #define engine:shaders:utils-> VERSION
    in vec2 pass_textureCoords;
    out vec4 out_Color;

    void main(){
        out_Color = texture(diffuse, pass_textureCoords);
    }
#endif