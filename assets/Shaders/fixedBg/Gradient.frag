varying vec4 pos;
varying vec4 color1;
varying vec4 color2;
 
void main() {
    float factor = pos.y * 0.5 + 0.5;
    //gl_FragColor = vec4(pos);
    gl_FragColor = mix(color1,color2,2*factor);
    //gl_FragColor = vec4(0,0,factor,1);
}