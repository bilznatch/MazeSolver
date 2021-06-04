#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

uniform sampler2D u_texture;
uniform float u_pause;

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

void main() {
  if(u_pause==1){
  }
  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
}