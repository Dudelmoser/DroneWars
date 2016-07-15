uniform sampler2D m_Texture1;
uniform sampler2D m_Texture2;
varying vec2 texCoord;

void main() {
	vec4 texVal1 = texture2D(m_Texture1, texCoord);
	vec4 texVal2 = texture2D(m_Texture2, texCoord);
	gl_FragColor =vec4(texVal1.r, texVal2.g, texVal2.b,1.0);
}
