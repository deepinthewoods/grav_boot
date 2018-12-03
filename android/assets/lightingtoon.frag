#ifdef GL_ES
precision mediump float;
#endif

//fixed number of lights
#define N_LIGHTS 4
#define N_LAYERS 7

//attributes from vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

//our texture samplers
uniform sampler2D u_texture;   //diffuse map
//uniform sampler2D u_normals;   //normal map
uniform sampler2D u_index_texture;
//values used for shading algorithm...
uniform vec2 Resolution;      //resolution of canvas
uniform float AmbientColor[N_LAYERS];    //ambient RGBA -- alpha is intensity

//uniform vec3 LightPos[N_LIGHTS];     //light position, normalized
//uniform vec3 Falloff[N_LIGHTS];      //attenuation coefficients
uniform vec4 LightColor[N_LIGHTS];   //light RGBA -- alpha is intensity
uniform float Zoom;
//Flat shading in four steps
#define STEP_A 0.2
#define STEP_B 0.55
#define STEP_C 1.0
#define STEP_D 1.4
const float INDEXPIXELHEIGHT = 1.1 / 66.0;
const float COEFFICIENTS_PIXEL_HEIGHT = 2.1 / 66.0;
const float POSITION_PIXEL_HEIGHT = 3.1 / 66.0;
const float RAMP_PIXEL_HEIGHT = 4.1 / 66.0;
const float ONE_PIXEL = 1.0 / 128.0;
const float LAYERS_SPACE = 4.0 / 128.0;

void main() {
	//RGBA of our diffuse color
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);
	#ifdef IMMEDIATE
	//DiffuseColor *= 0.0000001;
	//DiffuseColor += vec4(1., 1., 1., 1.);
	//DiffuseColor.r = 0.1f;
	#endif
	//RGB of our normal map
	//vec3 NormalMap = texture2D(u_index_texture, vTexCoord).rgb;
	vec4 IndexedColor = texture2D(u_index_texture, vec2(DiffuseColor.r , INDEXPIXELHEIGHT));
	vec3 NormalMap = texture2D(u_index_texture, vec2(DiffuseColor.g, 0.0)).rgb;
	float Sum = 0.0;
   // float layerIndex = float(int(DiffuseColor.b * 128.0));
    float layerIndex = 0.;;
    #ifdef IMMEDIATE
    layerIndex = float(int(vColor.r * 128.0));
    //layerIndex = 2.;
    #else
    layerIndex = float(int(DiffuseColor.b * 128.0));

    #endif
	for (int i=0; i<N_LIGHTS; i++) {
        vec3 Falloff = texture2D(u_index_texture, vec2(layerIndex * LAYERS_SPACE, COEFFICIENTS_PIXEL_HEIGHT)).rgb;
        Falloff.g *= 10.0;
        Falloff.b *= 50.0;
        
        //Falloff *= 0.0000000001;
        //Falloff.r += 0.4;
       // Falloff.g += 3.0;
        //Falloff.b += 20.0;

        vec3 LightPos = texture2D(u_index_texture, vec2(float(i) * ONE_PIXEL, POSITION_PIXEL_HEIGHT)).rgb ;

		//The delta position of light
		//vec3 LightDir = vec3(LightPos[i].xy - (gl_FragCoord.xy / Resolution.xy), LightPos[i].z);
		vec3 LightDir = vec3(vec2(LightPos.xy) - (gl_FragCoord.xy / Resolution.xy), LightPos.z);
		//Correct for aspect ratio
		LightDir.x *= Resolution.x / Resolution.y;

		//Determine distance (used for attenuation) BEFORE we normalize our LightDir
		float D = length(LightDir) * Zoom ;
		
		//normalize our vectors
		vec3 N = normalize(NormalMap * 2.0 - 1.0);
		vec3 L = normalize(LightDir);
		
		//Some normal maps may need to be inverted like so:
		
		//N.x = 1.0 - N.x;

		//pre-multiply ambient color with intensity
		//vec3 Ambient = AmbientColor.rgb * AmbientColor.a;
		
		//calculate attenuation
		float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );
		Attenuation = Attenuation *  max(dot(N, L), 0.0);
		
		
			
		
		//vec3 Diffuse = (LightColor[i].rgb * LightColor[i].a);
		//Diffuse = Diffuse ;
		
		//Diffuse = Diffuse * Attenuation;
		//the calculation which brings it all together
		
		//vec3 FinalColor = DiffuseColor.rgb * Diffuse;
		//Sum += Attenuation ;
		Sum = max(Sum, Attenuation);
	}
    //IndexedColor *= 0.000001;
    //IndexedColor += vec4(1., 1., 1., 1.);

	//Sum += AmbientColor;
	Sum = max(Sum, AmbientColor[int(layerIndex)]);
	//Sum = min(1.0, Sum);
    Sum = texture2D(u_index_texture, vec2(Sum , RAMP_PIXEL_HEIGHT)).r;

	//Here is where we apply some toon shading to the light
	//Sum = 1.0;

	gl_FragColor = vec4(IndexedColor.rgb * Sum, IndexedColor.a);
}