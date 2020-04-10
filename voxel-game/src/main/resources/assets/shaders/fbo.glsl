#ifdef VERTEX_SHADER
    #version 400 core
    in vec3 vertex;
    in vec2 textureCoords;
    out vec2 pass_textureCoords;
    uniform mat4 viewProjMatrix;
    uniform mat4 modelMatrix;

    void main(){
        gl_Position = viewProjMatrix * modelMatrix * vec4(vertex,  1.0);
//        gl_Position =  modelMatrix * vec4(vertex,  1.0);
//        gl_Position =  vec4(vertex, 1.0);
        pass_textureCoords = textureCoords;
    }
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core

    in vec2 pass_textureCoords;
    out vec4 out_Color;

    uniform sampler2D diffuse;
    uniform vec4 color;
    uniform vec2 resolution;
    uniform float time;

float d;

    float lookup(vec2 p, float dx, float dy)
    {
        vec2 uv = (p.xy + vec2(dx * d, dy * d)) / resolution.xy;
        vec4 c = texture(diffuse, uv.xy);

        // return as luma
        return 0.2126*c.r + 0.7152*c.g + 0.0722*c.b;
    }

    vec4 render(vec2 coords){
        d = sin(time * 5.0)*0.5 + 1.5; // kernel offset
        vec2 p = coords.xy;

        // simple sobel edge detection
        float gx = 0.0;
        gx += -1.0 * lookup(p, -1.0, -1.0);
        gx += -2.0 * lookup(p, -1.0,  0.0);
        gx += -1.0 * lookup(p, -1.0,  1.0);
        gx +=  1.0 * lookup(p,  1.0, -1.0);
        gx +=  2.0 * lookup(p,  1.0,  0.0);
        gx +=  1.0 * lookup(p,  1.0,  1.0);

        float gy = 0.0;
        gy += -1.0 * lookup(p, -1.0, -1.0);
        gy += -2.0 * lookup(p,  0.0, -1.0);
        gy += -1.0 * lookup(p,  1.0, -1.0);
        gy +=  1.0 * lookup(p, -1.0,  1.0);
        gy +=  2.0 * lookup(p,  0.0,  1.0);
        gy +=  1.0 * lookup(p,  1.0,  1.0);

        // hack: use g^2 to conceal noise in the video
        float g = gx*gx + gy*gy;
        float g2 = g * (sin(time) / 2.0 + 0.5);

        vec4 col = texture(diffuse, p / resolution.xy);
        col += vec4(0.0, g, g2, 1.0);

        return col;
    }

    void main(){
        float x = pass_textureCoords.x;
        float y = pass_textureCoords.y;
        vec2 coords = pass_textureCoords * resolution.xy;
        vec4 color = render(coords);
        out_Color = color;


//        out_Color = texture(diffuse, pass_textureCoords);

    }
#endif