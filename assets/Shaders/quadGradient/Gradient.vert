uniform mat4 g_WorldViewProjectionMatrix;
uniform vec4 m_FirstColor;
uniform vec4 m_SecondColor;
 

attribute vec4 inPosition;
attribute vec2 inTexCoord;

varying vec2 texCoord;
varying vec4 pos;
varying vec4 color1;
varying vec4 color2;
 
void main() {
    color1=m_FirstColor;
    color2=m_SecondColor;
    pos=inPosition;
    texCoord=inTexCoord;
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
}



