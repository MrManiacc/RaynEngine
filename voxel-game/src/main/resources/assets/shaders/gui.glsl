#ifdef VERTEX_SHADER
#version 400 core
    in vec2 vertex;
    in vec2 textureCoords;
    out vec2 pass_textureCoords;
    uniform mat4 modelMatrix;

    void main(){
        gl_Position = modelMatrix * vec4(vertex, 0, 1.0);
        pass_textureCoords = textureCoords;
    }
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core
    out vec4 out_Color;
    in vec2 pass_textureCoords;

    uniform sampler2D diffuse;
    uniform bool isColored;
    uniform bool isTextured;
    uniform vec4 color;

    void main(){
        if(isColored && !isTextured){
            out_Color = color;
        }else if(isColored && isTextured){
            vec4 tex = texture(diffuse, pass_textureCoords);
            //Overlaysss
            out_Color = tex + color;
        }else{
            out_Color = texture(diffuse, pass_textureCoords);
        }
    }
#endif