#ifdef GL_ES
precision mediump float;
#endif

//fixed number of lights
#define N_LIGHTS 4

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

uniform vec3 LightPos[N_LIGHTS];     //light position, normalized
uniform vec3 Falloff[N_LIGHTS];      //attenuation coefficients
uniform vec4 LightColor[N_LIGHTS];   //light RGBA -- alpha is intensity
uniform float Zoom;
//Flat shading in four steps
#define STEP_A 0.2
#define STEP_B 0.55
#define STEP_C 1.0
#define STEP_D 1.4
const float INDEXPIXELHEIGHT = 1.1 / 66.0; // 1/66

// uniform float Test[2];

void main() {
	//RGBA of our diffuse color
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);
	
	//RGB of our normal map
	//vec3 NormalMap = texture2D(u_index_texture, vTexCoord).rgb;
	vec4 IndexedColor = texture2D(u_index_texture, vec2(DiffuseColor.r, INDEXPIXELHEIGHT));
	vec3 NormalMap = texture2D(u_index_texture, vec2(DiffuseColor.r, 0.0)).rgb;

	float Sum = 0.0;
    int cIndex = int(DiffuseColor.r * 128.0);
    int nIndex = int(DiffuseColor.g * 128.0);

	for (int i=0; i<N_LIGHTS; i++) {
		//The delta position of light
		vec3 LightDir = vec3(LightPos[i].xy - (gl_FragCoord.xy / Resolution.xy), LightPos[i].z);
		
		//Correct for aspect ratio
		LightDir.x *= Resolution.x / Resolution.y;
		
		//Determine distance (used for attenuation) BEFORE we normalize our LightDir
		float D = length(LightDir) * Zoom;
		
		//normalize our vectors
		vec3 N = normalize(NormalMap * 2.0 - 1.0);
		vec3 L = normalize(LightDir);
		
		//Some normal maps may need to be inverted like so:
		
		//N.x = 1.0 - N.x;

		//pre-multiply ambient color with intensity
		//vec3 Ambient = AmbientColor.rgb * AmbientColor.a;
		
		//calculate attenuation
		float Attenuation = 1.0 / ( Falloff[i].x + (Falloff[i].y*D) + (Falloff[i].z*D*D) );
		Attenuation = Attenuation *  max(dot(N, L), 0.0);
		
		
			
		
		//vec3 Diffuse = (LightColor[i].rgb * LightColor[i].a);
		//Diffuse = Diffuse ;
		
		//Diffuse = Diffuse * Attenuation;
		//the calculation which brings it all together
		
		//vec3 FinalColor = DiffuseColor.rgb * Diffuse;

		//Sum += Attenuation ;
		Sum = max(Sum, Attenuation);
	}
	
	//Sum += AmbientColor;
	Sum = max(Sum, AmbientColor);
	//Here is where we apply some toon shading to the light
	//Sum = 1.0;

	gl_FragColor = vec4(IndexedColor.rgb * Sum, IndexedColor.a);
}