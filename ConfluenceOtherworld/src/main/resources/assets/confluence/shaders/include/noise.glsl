#version 150

vec3 hash33(vec3 p){
    p = vec3( dot(p,vec3(127.1,311.7,123.2)),
              dot(p,vec3(269.5,183.3,205.3)),
              dot(p,vec3(114.5,154.3,192.3)));
    return -1.0 + 2.0 * fract(sin(p)*43758.5453123);
}

vec2 hash22(vec2 p){
    p = vec2( dot(p,vec2(127.1,311.7)),
              dot(p,vec2(269.5,183.3)));
    return -1.0 + 2.0 * fract(sin(p)*43758.5453123);
}

vec2 hash(vec2 p)//不用sin的更好
{
    vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+19.19);
    return -1. + 2.*fract((p3.xx+p3.yz)*p3.zy);
}

float perlin_noise(vec2 p){
    vec2 pi = floor(p);
    vec2 pf = p - pi;
    
    //一个类似smoothstep的函数，叫Hermit插值函数，也叫S曲线：S(x) = -2 x^3 + 3 x^2
    //利用Hermit插值特性：可以在保证函数输出的基础上保证插值函数的导数在插值点上为0，这样就提供了平滑性
    vec2 w = pf * pf * (3.0 - 2.0 * pf);

    return mix(mix(dot(hash22(pi + vec2(0.0, 0.0)), pf - vec2(0.0, 0.0)), 
                   dot(hash22(pi + vec2(1.0, 0.0)), pf - vec2(1.0, 0.0)), w.x), 
               mix(dot(hash22(pi + vec2(0.0, 1.0)), pf - vec2(0.0, 1.0)), 
                   dot(hash22(pi + vec2(1.0, 1.0)), pf - vec2(1.0, 1.0)), w.x),
               w.y);
}

float perlin_noise3D(vec3 p){
    vec3 pi = floor(p);
    vec3 pf = p - pi;
    
    //一个类似smoothstep的函数，叫Hermit插值函数，也叫S曲线：S(x) = -2 x^3 + 3 x^2
    //利用Hermit插值特性：可以在保证函数输出的基础上保证插值函数的导数在插值点上为0，这样就提供了平滑性
    vec3 w = pf * pf * (3.0 - 2.0 * pf);

    return mix(
        mix(mix(dot(hash33(pi + vec3(0.0, 0.0, 0.0)), pf - vec3(0.0, 0.0, 0.0)), 
                dot(hash33(pi + vec3(1.0, 0.0, 0.0)), pf - vec3(1.0, 0.0, 0.0)), w.x), 
            mix(dot(hash33(pi + vec3(0.0, 1.0, 0.0)), pf - vec3(0.0, 1.0, 0.0)), 
                dot(hash33(pi + vec3(1.0, 1.0, 0.0)), pf - vec3(1.0, 1.0, 0.0)), w.x),w.y),
        mix(mix(dot(hash33(pi + vec3(0.0, 0.0, 1.0)), pf - vec3(0.0, 0.0, 1.0)), 
                dot(hash33(pi + vec3(1.0, 0.0, 1.0)), pf - vec3(1.0, 0.0, 1.0)), w.x), 
            mix(dot(hash33(pi + vec3(0.0, 1.0, 1.0)), pf - vec3(0.0, 1.0, 1.0)), 
                dot(hash33(pi + vec3(1.0, 1.0, 1.0)), pf - vec3(1.0, 1.0, 1.0)), w.x),w.y),w.z);
}