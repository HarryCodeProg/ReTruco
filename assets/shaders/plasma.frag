#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform float u_time; // El tiempo que pasa, maneja la velocidad
uniform vec2 u_resolution; // El tamaño de la pantalla

void main() {
    // Normalizamos las coordenadas de la pantalla (0.0 a 1.0)
    vec2 uv = gl_FragCoord.xy / u_resolution.xy;
    
    // Multiplicadores para cambiar la escala de las ondas (estilo Balatro)
    float tiempo = u_time * 0.4; 
    
    // --- MATEMÁTICA PSICODÉLICA (Mezcla de ondas Seno y Coseno) ---
    float onda1 = sin(uv.x * 4.0 + tiempo) + sin(uv.y * 4.0 + tiempo);
    float onda2 = sin(3.0 * (uv.x * sin(tiempo / 2.0) + uv.y * cos(tiempo / 3.0)) + tiempo);
    
    vec2 centro = uv - vec2(0.5, 0.5);
    float onda3 = sin(sqrt(centro.x * centro.x + centro.y * centro.y) * 10.0 - tiempo);
    
    // Sumamos todas las distorsiones
    float plasma = onda1 + onda2 + onda3;
    
    // --- ASIGNACIÓN DE COLORES ---
    // Modificando estos factores cambias la paleta (Verde mesa de truco, violeta, etc.)
    float r = sin(plasma * 3.1415) * 0.3 + 0.2; // Rojo bajo para mantenerlo verdoso/oscuro
    float g = cos(plasma * 3.1415) * 0.4 + 0.3; // Verde predominante
    float b = sin(plasma * 1.0) * 0.2 + 0.2;        // Toque de azul para variantes
    
    // Retornamos el color final con la opacidad al mango
    gl_FragColor = vec4(r, g, b, 1.0);
}