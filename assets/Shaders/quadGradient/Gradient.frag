varying vec4 pos;
varying vec2 texCoord;
varying vec4 color1;
varying vec4 color2;
 
void main() {
    float factor = texCoord.y;
    gl_FragColor = mix(color1,color2,2*factor);
    //gl_FragColor = mix(color1,color2,texCoord.x + texCoord.y - 2*texCoord.x* texCoord.y);
}