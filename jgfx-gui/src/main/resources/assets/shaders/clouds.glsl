/*
#ifdef VERTEX_SHADER
    #version 400 core
    in vec3 vertex;
    in vec2 textureCoords;
    out vec2 pass_textureCoords;
    uniform mat4 viewProjMatrix;
    uniform mat4 modelMatrix;

    void main(){
        gl_Position = viewProjMatrix * modelMatrix * vec4(vertex,  1.0);
        pass_textureCoords = textureCoords;
    }
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core

    in vec2 pass_textureCoords;
    out vec4 out_Color;

    uniform sampler2D diffuse;
    uniform bool textured;
    uniform vec4 color;
    uniform vec2 resolution;
    uniform float time;

    const mat2 m = mat2( 0.80,  0.60, -0.60,  0.80 );

    float noise( in vec2 p )
    {
        return sin(p.x)*sin(p.y);
    }

    float fbm4( vec2 p )
    {
        float f = 0.0;
        f += 0.5000*noise( p ); p = m*p*2.02;
        f += 0.2500*noise( p ); p = m*p*2.03;
        f += 0.1250*noise( p ); p = m*p*2.01;
        f += 0.0625*noise( p );
        return f/0.9375;
    }

    float fbm6( vec2 p )
    {
        float f = 0.0;
        f += 0.500000*(0.5+0.5*noise( p )); p = m*p*2.02;
        f += 0.250000*(0.5+0.5*noise( p )); p = m*p*2.03;
        f += 0.125000*(0.5+0.5*noise( p )); p = m*p*2.01;
        f += 0.062500*(0.5+0.5*noise( p )); p = m*p*2.04;
        f += 0.031250*(0.5+0.5*noise( p )); p = m*p*2.01;
        f += 0.015625*(0.5+0.5*noise( p ));
        return f/0.96875;
    }

    vec2 fbm4_2( vec2 p )
    {
        return vec2(fbm4(p), fbm4(p+vec2(7.8)));
    }

    vec2 fbm6_2( vec2 p )
    {
        return vec2(fbm6(p+vec2(16.8)), fbm6(p+vec2(11.5)));
    }

    //====================================================================

    float func( vec2 q, out vec4 ron )
    {
        q += 0.03*sin( vec2(0.27,0.23)*time + length(q)*vec2(4.1,4.3));

        vec2 o = fbm4_2( 0.9*q );

        o += 0.04*sin( vec2(0.12,0.14)*time + length(o));

        vec2 n = fbm6_2( 3.0*o );

        ron = vec4( o, n );

        float f = 0.5 + 0.5*fbm4( 1.8*q + 6.0*n );

        return mix( f, f*f*f*3.5, f*abs(n.x) );
    }


    vec4 computeColor(){
        vec2 p = (2.0*fragCoord-resolution.xy)/resolution.y;
        float e = 2.0/resolution.y;

        vec4 on = vec4(0.0);
        float f = func(p, on);

        vec3 col = vec3(0.0);
        col = mix( vec3(0.2,0.1,0.4), vec3(0.3,0.05,0.05), f );
        col = mix( col, vec3(0.9,0.9,0.9), dot(on.zw,on.zw) );
        col = mix( col, vec3(0.4,0.3,0.3), 0.2 + 0.5*on.y*on.y );
        col = mix( col, vec3(0.0,0.2,0.4), 0.5*smoothstep(1.2,1.3,abs(on.z)+abs(on.w)) );
        col = clamp( col*f*2.0, 0.0, 1.0 );

        #if 0
        // gpu derivatives - bad quality, but fast
        vec3 nor = normalize( vec3( dFdx(f)*iResolution.x, 6.0, dFdy(f)*iResolution.y ) );
        #else
        // manual derivatives - better quality, but slower
        vec4 kk;
        vec3 nor = normalize( vec3( func(p+vec2(e,0.0),kk)-f,
        2.0*e,
        func(p+vec2(0.0,e),kk)-f ) );
        #endif

        vec3 lig = normalize( vec3( 0.9, 0.2, -0.4 ) );
        float dif = clamp( 0.3+0.7*dot( nor, lig ), 0.0, 1.0 );
        vec3 lin = vec3(0.70,0.90,0.95)*(nor.y*0.5+0.5) + vec3(0.15,0.10,0.05)*dif;
        col *= 1.2*lin;
        col = 1.0 - col;
        col = 1.1*col*col;

        return vec4(col, 1.0);
    }


    void main(){
        vec2 coords = pass_textureCoords * resolution.xy;
        out_Color = vec4(1,0,0,1);
    }
#endif*/


#ifdef VERTEX_SHADER
#version 400 core
in vec3 vertex;
in vec2 textureCoords;
out vec2 pass_textureCoords;
uniform mat4 viewProjMatrix;
uniform mat4 modelMatrix;

void main(){
    gl_Position = viewProjMatrix * modelMatrix * vec4(vertex,  1.0);
    pass_textureCoords = textureCoords;
}
#endif

#ifdef FRAGMENT_SHADER
    #version 400 core
    uniform sampler2D diffuse;
    uniform vec2 resolution;
    uniform float time;

    in vec2 pass_textureCoords;
    out vec4 out_Color;

    float N21 (vec2 p){
        float d = fract(sin(p.x*110.+(8.21-p.y)*331.)*1218.);
        return d;
    }

    float Noise2D(vec2 uv){
        vec2 st = fract(uv);
        vec2 id = floor(uv);
        st = st*st*(3.0-2.0*st);
        float c=mix(mix(N21(id),N21(id+vec2(1.0,0.0)),st.x),mix(N21(id+vec2(0.0,1.0)),N21(id+vec2(1.0,1.0)),st.x),st.y);
        return c;
    }

    float fbm (vec2 uv){

        float c=0.;
        c+=Noise2D(uv)/2.;
        c+=Noise2D(2.*uv)/4.;
        c+=Noise2D(4.*uv)/8.;
        c+=Noise2D(8.*uv)/16.;
        return c/(1.-1./16.);
    }

    vec3 fbm3(vec2 uv){
            vec3 color;
            float f1 = fbm(uv);
            color= mix(vec3(0.1,0.0,0.0),vec3(0.9,0.1,0.1),2.5*f1);

            float f2 = fbm(2.4*f1+uv+0.15*sin(time)*vec2(7.0,-8.0));
            color= mix(color,vec3(0.6,0.5,0.1),1.5*f2);
            float f3 = fbm(3.5*f2+uv-0.15*cos(1.5*time)*vec2(4.0,3.0));
            color= mix(color,vec3(0.1,0.35,0.45),f3);

            color= mix(color,vec3(0.45,0.35,0.25),smoothstep(0.7,0.75,f2));
            color= mix(color,vec3(0.2,0.4,0.2),smoothstep(0.75,0.8,f2));
            color= mix(color,vec3(0.55,0.55,0.35),smoothstep(0.88,0.99,f2));
            color= mix(color,vec3(0.55,0.55,0.35),smoothstep(0.88,0.99,f3));

        return color;

    }


    void main(){
        vec2 uv = (pass_textureCoords*resolution.xy)/resolution.xy;
        vec3 c = fbm3(vec2(5.0,5.0)*uv+sin(0.3*time)*0.5);
        vec3 col = c;

        col.r *= .725;
        col.g *= .725;
        out_Color = vec4(col * 2.5,1.0);
//        out_Color = texture(diffuse, pass_textureCoords);
    }
#endif