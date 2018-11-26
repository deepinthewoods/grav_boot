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

// uniform float Test[2];
float modd();
float modd(float x, float y){
return (x - y*floor(x/y));
}

void main() {
	//RGBA of our diffuse color
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);

	int index = int(modd(vTexCoord.x * 128.0 + (DiffuseColor.r * 0.0000000001), float(N_LIGHTS)));

	int layerIndex = int((vTexCoord.x * 128.0 + DiffuseColor.r * 0.0000000001) / float(N_LIGHTS));

    vec3 v = Falloff[index + layerIndex * N_LIGHTS];
    v.g /= 10.0;
    v.b /= 50.0;


	gl_FragColor = vec4(v, 1.0 );

	//1.0, 1.0);

}