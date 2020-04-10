#ifdef VERTEX_SHADER
    #version 400 core
    in vec3 vertex;
    in vec4 color;

    out vec4 pass_color;
    uniform mat4 projectionMatrix;
    uniform mat4 viewMatrix;
    uniform mat4 modelMatrix;

    void main(){
        gl_Position = projectionMatrix * viewMatrix * modelMatrix* vec4(vertex, 1.0);
        pass_color = color;
    }
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core
    in vec4 pass_color;
    out vec4 out_Color;

    void main(){
        out_Color = pass_color;
    }
#endif