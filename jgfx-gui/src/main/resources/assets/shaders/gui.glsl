#ifdef VERTEX_SHADER
    #version 400 core
    in vec3 vertex;
    uniform mat4 viewProjMatrix;
    uniform mat4 modelMatrix;

    void main(){
        gl_Position = viewProjMatrix * modelMatrix * vec4(vertex, 1.0);
    }
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core
    out vec4 out_Color;

    void main(){
        out_Color = vec4(1, 0, 0, 1);
    }
#endif