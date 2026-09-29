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
    private TemaFondo temaActual;

    public static class TemaFondo {
        public String nombre;
        public int efecto; // 0=Plasma, 1=Ondas, 2=Matrix
        public float[] c1, c2, c3;
        public TemaFondo(String nombre, int efecto, float[] c1, float[] c2, float[] c3) {
            this.nombre = nombre; this.efecto = efecto; this.c1 = c1; this.c2 = c2; this.c3 = c3;
        }
    }

    public static final TemaFondo[] TEMAS = {
        new TemaFondo("Truco Clásico", 0, new float[]{0.18f, 0.55f, 0.85f}, new float[]{0.95f, 0.96f, 1.0f}, new float[]{0.85f, 0.75f, 0.20f}),
        new TemaFondo("Magia Oscura", 0, new float[]{0.1f, 0.05f, 0.2f}, new float[]{0.4f, 0.1f, 0.5f}, new float[]{0.9f, 0.2f, 0.8f}),
        new TemaFondo("Ondas Retro", 1, new float[]{0.05f, 0.05f, 0.15f}, new float[]{0.1f, 0.8f, 0.9f}, new float[]{0.9f, 0.2f, 0.6f}),
        new TemaFondo("Matriz de Datos", 2, new float[]{0.0f, 0.1f, 0.0f}, new float[]{0.0f, 0.5f, 0.1f}, new float[]{0.4f, 1.0f, 0.4f}),
        new TemaFondo("Sangre y Fuego", 0, new float[]{0.4f, 0.05f, 0.05f}, new float[]{0.9f, 0.2f, 0.1f}, new float[]{1.0f, 0.8f, 0.2f})
    };

    public Background() {
        this.temaActual = TEMAS[0]; // Por defecto
        iniciarShader();
    }

    public void setTema(int indice) {
        if (indice >= 0 && indice < TEMAS.length) this.temaActual = TEMAS[indice];
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
                "uniform float u_brillo;\n" +
                "uniform int u_efecto;\n" +
                "uniform vec3 u_c1;\n" +
                "uniform vec3 u_c2;\n" +
                "uniform vec3 u_c3;\n" +
                "uniform sampler2D u_texture;\n" +
                "float random(vec2 st){ return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123); }\n" +
                "float noise(vec2 st){\n" +
                "    vec2 i = floor(st); vec2 f = fract(st);\n" +
                "    float a = random(i); float b = random(i + vec2(1.0,0.0));\n" +
                "    float c = random(i + vec2(0.0,1.0)); float d = random(i + vec2(1.0,1.0));\n" +
                "    vec2 u = f*f*(3.0-2.0*f);\n" +
                "    return mix(a,b,u.x) + (c-a)*u.y*(1.0-u.x) + (d-b)*u.x*u.y;\n" +
                "}\n" +
                "float fbm(vec2 st){\n" +
                "    float value = 0.0; float amp = 0.5;\n" +
                "    for(int i=0;i<5;i++){ value += amp*noise(st); st*=2.0; amp*=0.5; }\n" +
                "    return value;\n" +
                "}\n" +
                "float nubeOrganica(vec2 p, float escala, float velocidad, float semilla) {\n" +
                "    vec2 q = p * escala;\n" +
                "\n" +
                "    float t = u_time * velocidad;\n" +
                "\n" +
                "    q.x += sin(q.y * 2.1 + t + semilla) * 0.22;\n" +
                "    q.y += cos(q.x * 1.7 - t * 0.85 + semilla) * 0.18;\n" +
                "\n" +
                "    vec2 warp1 = vec2(\n" +
                "        fbm(q * 1.15 + vec2(t * 0.22 + semilla, -t * 0.16)),\n" +
                "        fbm(q * 1.15 + vec2(-t * 0.18, t * 0.24 + semilla))\n" +
                "    );\n" +
                "\n" +
                "    q += (warp1 - 0.5) * 1.15;\n" +
                "\n" +
                "    float n1 = fbm(q);\n" +
                "    float n2 = fbm(q * 2.1 + vec2(sin(t * 0.7 + semilla), cos(t * 0.55 + semilla)));\n" +
                "\n" +
                "    return mix(n1, n2, 0.28);\n" +
                "}"+
                "void main(){\n" +
                "    vec2 uv = gl_FragCoord.xy/u_resolution.xy;\n" +
                "    uv.x*=u_resolution.x/u_resolution.y;\n" +
                "    float t=u_time;\n" + // <--- ACÁ ESTÁ EL CAMBIO
                "    vec3 color = vec3(0.0);\n" +
                "   if(u_efecto == 0) {\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // CIELO\n" +
                "    // =========================================================\n" +
                "\n" +
                "    vec3 cieloArriba = mix(\n" +
                "        vec3(0.12, 0.32, 0.58),\n" +
                "        u_c1,\n" +
                "        0.35\n" +
                "    ) * u_brillo;\n" +
                "\n" +
                "    vec3 cieloAbajo = mix(\n" +
                "        vec3(0.58, 0.78, 0.93),\n" +
                "        u_c2,\n" +
                "        0.22\n" +
                "    ) * u_brillo;\n" +
                "\n" +
                "    float gradiente = smoothstep(0.0, 1.0, uv.y);\n" +
                "    color = mix(cieloAbajo, cieloArriba, gradiente);\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // NUBE LEJANA\n" +
                "    // =========================================================\n" +
                "\n" +
                "    float nube1 = nubeOrganica(uv + vec2(0.0, 0.20), 2.1, 0.22, 1.7);\n" +
                "    float mascara1 = smoothstep(0.58, 0.73, nube1);\n" +
                "\n" +
                "    color = mix(\n" +
                "        color,\n" +
                "        vec3(0.78, 0.86, 0.94),\n" +
                "        mascara1 * 0.26\n" +
                "    );\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // NUBE MEDIA\n" +
                "    // =========================================================\n" +
                "\n" +
                "    float nube2 = nubeOrganica(\n" +
                "        uv + vec2(3.7, -0.4),\n" +
                "        3.4,\n" +
                "        0.40,\n" +
                "        7.2\n" +
                "    );\n" +
                "\n" +
                "    float mascara2 = smoothstep(0.56, 0.72, nube2);\n" +
                "\n" +
                "    color = mix(\n" +
                "        color,\n" +
                "        vec3(0.88, 0.92, 0.96),\n" +
                "        mascara2 * 0.34\n" +
                "    );\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // NUBE CERCANA\n" +
                "    // =========================================================\n" +
                "\n" +
                "    float nube3 = nubeOrganica(\n" +
                "        uv + vec2(-2.5, 1.4),\n" +
                "        5.0,\n" +
                "        0.68,\n" +
                "        13.5\n" +
                "    );\n" +
                "\n" +
                "    float mascara3 = smoothstep(0.57, 0.75, nube3);\n" +
                "\n" +
                "    color = mix(\n" +
                "        color,\n" +
                "        vec3(0.94, 0.96, 0.98),\n" +
                "        mascara3 * 0.34\n" +
                "    );\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // SOMBRAS SUAVES\n" +
                "    // =========================================================\n" +
                "\n" +
                "    float sombra = smoothstep(0.45, 0.62, nube2);\n" +
                "\n" +
                "    color -= vec3(0.025, 0.035, 0.05) * sombra;\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // LUZ SOLAR SUAVE\n" +
                "    // =========================================================\n" +
                "\n" +
                "    vec2 posicionSol = vec2(0.72, 0.78);\n" +
                "    vec2 uvNormalizada = vec2(\n" +
                "        uv.x / (u_resolution.x / u_resolution.y),\n" +
                "        uv.y\n" +
                "    );\n" +
                "\n" +
                "    float distanciaSol = distance(uvNormalizada, posicionSol);\n" +
                "\n" +
                "    float halo = smoothstep(0.48, 0.0, distanciaSol);\n" +
                "\n" +
                "    color += vec3(1.0, 0.91, 0.72) * halo * 0.09;\n" +
                "\n" +
                "    // =========================================================\n" +
                "    // VARIACIÓN AMBIENTAL\n" +
                "    // =========================================================\n" +
                "\n" +
                "    color *= 0.985 + 0.015 * sin(u_time * 0.25);\n" +
                "}\n" +
                "else if(u_efecto == 1) {\n" +
                "        uv.y += sin(uv.x * 5.0 + u_time) * 0.1;\n" +
                "        float linea = abs(0.02 / sin(uv.y * 20.0 - u_time * 2.0));\n" +
                "        color = mix(u_c1, u_c2 * u_brillo, linea);\n" +
                "        color += u_c3 * (1.0 - uv.y) * 0.3;\n" +
                "    } else if(u_efecto == 2) {\n" +
                "        float n = fbm(uv*10.0 - vec2(0.0, u_time*0.5));\n" +
                "        color = mix(u_c1, u_c2 * u_brillo, smoothstep(0.4, 0.6, n));\n" +
                "        vec2 grid = fract(uv * 15.0);\n" +
                "        float border = step(0.95, grid.x) + step(0.95, grid.y);\n" +
                "        color = mix(color, u_c3, border * 0.5);\n" +
                "    }\n" +
                "    vec4 tex = texture2D(u_texture,v_texCoords)*0.00001;\n" +
                "    gl_FragColor = vec4(color,1.0)+tex;\n" +
                "}";
        shaderPlasma = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderPlasma.isCompiled()) System.err.println("¡ERROR SHADER!: " + shaderPlasma.getLog());
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        pixmap.fill();
        texturaVacia = new Texture(pixmap);
        pixmap.dispose();
    }

    public void setRivalesVencidos(int rivalesVencidos) {
        float reduccion = rivalesVencidos * 0.03f;
        this.nivelBrillo = Math.max(0.30f, 1.0f - reduccion);
    }

    public void render(SpriteBatch batch, float delta) {
        tiempo += delta;
        ShaderProgram shaderOriginal = batch.getShader();
        batch.setShader(shaderPlasma);
        shaderPlasma.setUniformf("u_time", tiempo);
        shaderPlasma.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shaderPlasma.setUniformf("u_brillo", nivelBrillo);
        shaderPlasma.setUniformi("u_efecto", temaActual.efecto);
        shaderPlasma.setUniformf("u_c1", temaActual.c1[0], temaActual.c1[1], temaActual.c1[2]);
        shaderPlasma.setUniformf("u_c2", temaActual.c2[0], temaActual.c2[1], temaActual.c2[2]);
        shaderPlasma.setUniformf("u_c3", temaActual.c3[0], temaActual.c3[1], temaActual.c3[2]);
        batch.draw(texturaVacia, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setShader(shaderOriginal);
    }

    public void dispose() {
        if (shaderPlasma != null) shaderPlasma.dispose();
        if (texturaVacia != null) texturaVacia.dispose();
    }
}
