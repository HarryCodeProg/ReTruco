package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Background {
    private ShaderProgram shaderPlasma;
    private Texture texturaVacia;
    private float tiempo;
    private float nivelBrillo = 1.0f;

    public Background() {
        iniciarShader();
    }

    private void iniciarShader() {
        String vertexShader = SpriteBatch.createDefaultShader().getVertexShaderSource();
        String fragmentShader =
            "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform float u_time;\n" +
                "uniform vec2 u_resolution;\n" +
                "uniform float u_brillo;\n" + // <-- NUEVO UNIFORM
                "uniform sampler2D u_texture;\n" +
                "float random(vec2 st){\n" +
                "    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);\n" +
                "}\n" +
                "float noise(vec2 st){\n" +
                "    vec2 i = floor(st);\n" +
                "    vec2 f = fract(st);\n" +
                "    float a = random(i);\n" +
                "    float b = random(i + vec2(1.0,0.0));\n" +
                "    float c = random(i + vec2(0.0,1.0));\n" +
                "    float d = random(i + vec2(1.0,1.0));\n" +
                "    vec2 u = f*f*(3.0-2.0*f);\n" +
                "    return mix(a,b,u.x) +\n" +
                "          (c-a)*u.y*(1.0-u.x) +\n" +
                "          (d-b)*u.x*u.y;\n" +
                "}\n" +
                "float fbm(vec2 st){\n" +
                "    float value = 0.0;\n" +
                "    float amp = 0.5;\n" +
                "    for(int i=0;i<5;i++){\n" +
                "        value += amp*noise(st);\n" +
                "        st*=2.0;\n" +
                "        amp*=0.5;\n" +
                "    }\n" +
                "    return value;\n" +
                "}\n" +
                "void main(){\n" +
                "    vec2 uv = gl_FragCoord.xy/u_resolution.xy;\n" +
                "    uv.x*=u_resolution.x/u_resolution.y;\n" +
                "    float t=u_time*0.10;\n" +
                "    float n  = fbm(uv*3.5 + vec2(t*1.5, t*0.5));\n" +
                "    float n2 = fbm(uv*6.0 - vec2(t*0.8, t*1.3));\n" +
                "    float f = mix(n,n2,0.35);\n" +
                "    vec3 celeste = vec3(0.18, 0.55, 0.85);\n" +
                "    // Aplicamos u_brillo directamente al color blanco para que se oscurezca por rival superado:\n" +
                "    vec3 blanco  = vec3(0.95, 0.96, 1.00) * u_brillo;\n" +
                "    vec3 oro     = vec3(0.85, 0.75, 0.20);\n" +
                "    vec3 color;\n" +
                "    if(f < 0.85){\n" +
                "        color = mix(celeste, blanco, smoothstep(0.0, 0.85, f));\n" +
                "    }else{\n" +
                "        color = mix(blanco, oro, smoothstep(0.85, 1.0, f) * 0.25);\n" +
                "    }\n" +
                "    color *= 0.90 + 0.05*sin(u_time*1.5);\n" +
                "    vec4 tex = texture2D(u_texture,v_texCoords)*0.00001;\n" +
                "    gl_FragColor = vec4(color,1.0)+tex;\n" +
                "}";

        shaderPlasma = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderPlasma.isCompiled()) {
            System.err.println("¡ERROR COMPILANDO SHADER EN BACKGROUND!: " + shaderPlasma.getLog());
        }
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        pixmap.fill();
        texturaVacia = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Setea el número de rivales vencidos hasta el momento para ajustar la oscuridad.
     * Ejemplo: con porcentajePorRival = 0.03f (3%), si venciste 5 rivales, resta 15% de blanco.
     */
    public void setRivalesVencidos(int rivalesVencidos) {
        float reduccion = rivalesVencidos * 0.03f; // 3% por cada rival
        // Usamos Math.max para asegurar que no baje más allá de un límite aceptable (ej: 30% del brillo)
        this.nivelBrillo = Math.max(0.30f, 1.0f - reduccion);
    }

    public void render(SpriteBatch batch, float delta) {
        tiempo += delta;
        ShaderProgram shaderOriginal = batch.getShader();
        batch.setShader(shaderPlasma);

        shaderPlasma.setUniformf("u_time", tiempo);
        shaderPlasma.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shaderPlasma.setUniformf("u_brillo", nivelBrillo); // <-- Pasamos la intensidad de blanco al GPU

        batch.draw(texturaVacia, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setShader(shaderOriginal);
    }

    public void dispose() {
        if (shaderPlasma != null) shaderPlasma.dispose();
        if (texturaVacia != null) texturaVacia.dispose();
    }
}
