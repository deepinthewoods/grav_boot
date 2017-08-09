#ifdef GL_ES
precision mediump float;
#endif

//fixed number of lights
#define N_LIGHTS 4
#define N_LAYERS 6

//attributes from vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
//uniform sampler2D u_normals;   //normal map
uniform sampler2D u_index_texture;
//values used for shading algorithm...
uniform vec2 Resolution;      //resolution of canvas
uniform float AmbientColor;    //ambient RGBA -- alpha is intensity 

uniform vec3 LightPos[N_LIGHTS * N_LAYERS];     //light position, normalized
uniform vec3 Falloff[N_LIGHTS * N_LAYERS];      //attenuation coefficients
uniform vec4 LightColor[N_LIGHTS];   //light RGBA -- alpha is intensity
uniform float Zoom;
//Flat shading in four steps
#define STEP_A 0.2
#define STEP_B 0.55
#define STEP_C 1.0
#define STEP_D 1.4

// uniform float Test[2];

float quadraticOut(float t) {
  return -t * (t - 2.0);
}

float circularOut(float t) {
  return sqrt((2.0 - t) * t);
}

void main() {
	//RGBA of our diffuse color
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);

	int index = int(mod(vTexCoord.x * 128.0 + (DiffuseColor.r * 0.0000000001), N_LIGHTS));

	int layerIndex = int((vTexCoord.x * 128.0 ) / float(N_LIGHTS));

    vec3 v = vec3(float(int((vTexCoord.x) * 4.0)) / 4.0, 0., 0.);
    //v.xy += 1.0;
    //v.xy *= 0.5;

	gl_FragColor = vec4(v, 1.0 );

	//1.0, 1.0);

}