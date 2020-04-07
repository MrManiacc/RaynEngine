#ifdef VERTEX_SHADER
    #define engine:shaders:utils-> VERSION
    #define engine:shaders:utils-> BINDS
    #define engine:shaders:utils-> UNIFORMS
    out vec2 pass_textureCoords;
//    out vec3 pass_normal;
    void main(){
        gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertex, 1.0);
//        pass_normal = normal;
        pass_textureCoords = textureCoords;
//        pass_textureCoords.y = 1.0 - pass_textureCoords.y;
}
#endif

#ifdef FRAGMENT_SHADER
    #define engine:shaders:utils-> VERSION
//    in vec3 pass_normal;
    in vec2 pass_textureCoords;
    out vec4 out_Color;

    void main(){
//        out_Color = vec4(normalize(pass_normal),1);
        out_Color = texture(diffuse, pass_textureCoords);
    }
#endif