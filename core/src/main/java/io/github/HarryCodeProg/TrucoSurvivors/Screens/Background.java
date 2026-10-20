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
        public int efecto; // 0=Cielo, 1=Ondas, 2=Matrix, 3=Plasma, 4=Aurora, 5=Estrellas, 6=Agua, 7=Brasas, 8=Topo, 9=Portal, 10=Glitch, 11=Luciernagas
        public float[] c1, c2, c3;
        public float spinAmount = 0.5f;  // solo para efecto 3
        public float contraste  = 1.0f;  // solo para efecto 3

        public TemaFondo(String nombre, int efecto, float[] c1, float[] c2, float[] c3) {
            this.nombre = nombre; this.efecto = efecto; this.c1 = c1; this.c2 = c2; this.c3 = c3;
        }
        public TemaFondo(String nombre, int efecto, float[] c1, float[] c2, float[] c3,
                         float spinAmount, float contraste) {
            this(nombre, efecto, c1, c2, c3);
            this.spinAmount = spinAmount;
            this.contraste = contraste;
        }
    }

    public static final TemaFondo[] TEMAS = {
        // efecto 0 — cielo
        new TemaFondo("Truco Clásico", 0,
            new float[]{0.18f, 0.55f, 0.85f},
            new float[]{0.95f, 0.96f, 1.0f},
            new float[]{0.85f, 0.75f, 0.20f}),

        // efecto 1 — ondas retro
        new TemaFondo("Ondas Retro", 1,
            new float[]{0.05f, 0.05f, 0.15f},
            new float[]{0.1f,  0.8f,  0.9f},
            new float[]{0.9f,  0.2f,  0.6f}),

        // efecto 2 — matrix
        new TemaFondo("Matriz de Datos", 2,
            new float[]{0.0f, 0.1f, 0.0f},
            new float[]{0.0f, 0.5f, 0.1f},
            new float[]{0.4f, 1.0f, 0.4f}),

        // efecto 3 — plasma Balatro
        new TemaFondo("Plasma Púrpura", 3,
            new float[]{0.28f, 0.06f, 0.45f},
            new float[]{0.85f, 0.18f, 0.72f},
            new float[]{1.00f, 0.72f, 0.95f},
            0.55f, 1.1f),

        new TemaFondo("Sangre y Fuego", 3,
            new float[]{0.35f, 0.03f, 0.03f},
            new float[]{0.95f, 0.25f, 0.05f},
            new float[]{1.00f, 0.85f, 0.20f},
            0.40f, 1.4f),

        new TemaFondo("Hielo Eterno", 3,
            new float[]{0.04f, 0.10f, 0.28f},
            new float[]{0.35f, 0.72f, 0.95f},
            new float[]{0.92f, 0.98f, 1.00f},
            0.25f, 0.8f),

        // ─── NUEVOS EFECTOS (4 al 11) ────────────────────────────────

        new TemaFondo("Aurora Boreal", 4,
            new float[]{0.02f, 0.08f, 0.15f}, // c1: Noche profunda
            new float[]{0.0f,  0.8f,  0.4f},  // c2: Verde neón
            new float[]{0.1f,  0.5f,  0.9f}), // c3: Azul brillante

        new TemaFondo("Campo Estelar", 5,
            new float[]{0.01f, 0.01f, 0.03f}, // c1: Espacio
            new float[]{0.6f,  0.2f,  0.8f},  // c2: Nebulosa púrpura
            new float[]{0.9f,  0.9f,  1.0f}), // c3: Estrellas blancas

        new TemaFondo("Agua Profunda", 6,
            new float[]{0.0f,  0.15f, 0.3f},  // c1: Fondo marino
            new float[]{0.0f,  0.6f,  0.8f},  // c2: Reflejos cian
            new float[]{0.5f,  0.9f,  1.0f}), // c3: Cáusticas claras

        new TemaFondo("Brasas Flotantes", 7,
            new float[]{0.1f,  0.0f,  0.0f},  // c1: Oscuridad rojiza
            new float[]{0.9f,  0.3f,  0.0f},  // c2: Fuego intenso
            new float[]{1.0f,  0.8f,  0.2f}), // c3: Chispas amarillas

        new TemaFondo("Radar Topográfico", 8,
            new float[]{0.05f, 0.08f, 0.05f}, // c1: Pantalla militar
            new float[]{0.2f,  0.8f,  0.3f},  // c2: Líneas verdes
            new float[]{0.6f,  1.0f,  0.7f}), // c3: Relieves marcados

        new TemaFondo("Portal Dimensional", 9,
            new float[]{0.05f, 0.0f,  0.1f},  // c1: Borde del portal
            new float[]{0.7f,  0.0f,  0.5f},  // c2: Magia magenta
            new float[]{0.9f,  0.5f,  1.0f}), // c3: Núcleo brillante

        new TemaFondo("Interferencia Glitch", 10,
            new float[]{0.08f, 0.08f, 0.08f}, // c1: Estática oscura
            new float[]{0.0f,  1.0f,  0.8f},  // c2: Cian digital
            new float[]{1.0f,  0.0f,  0.4f}), // c3: Magenta digital

        new TemaFondo("Luciérnagas", 11,
            new float[]{0.03f, 0.08f, 0.05f}, // c1: Bosque nocturno
            new float[]{0.5f,  0.9f,  0.2f},  // c2: Bioluminiscencia verde
            new float[]{0.8f,  1.0f,  0.5f})  // c3: Núcleo amarillo
    };

    public Background() {
        this.temaActual = TEMAS[0];
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
                "uniform float u_spin_amount;\n" +
                "uniform float u_contrast;\n" +
                "uniform sampler2D u_texture;\n" +
                // ─── utilidades ────────────────────────────────────────
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
                "    float t = u_time * velocidad;\n" +
                "    q.x += sin(q.y * 2.1 + t + semilla) * 0.22;\n" +
                "    q.y += cos(q.x * 1.7 - t * 0.85 + semilla) * 0.18;\n" +
                "    vec2 warp1 = vec2(\n" +
                "        fbm(q * 1.15 + vec2(t * 0.22 + semilla, -t * 0.16)),\n" +
                "        fbm(q * 1.15 + vec2(-t * 0.18, t * 0.24 + semilla))\n" +
                "    );\n" +
                "    q += (warp1 - 0.5) * 1.15;\n" +
                "    float n1 = fbm(q);\n" +
                "    float n2 = fbm(q * 2.1 + vec2(sin(t * 0.7 + semilla), cos(t * 0.55 + semilla)));\n" +
                "    return mix(n1, n2, 0.28);\n" +
                "}\n" +
                // ─── FUNCIONES DE FONDOS NUEVOS (4 a 11) ───────────────
                FondosShaders.FUNCIONES +
                // ─── efecto 3: plasma Balatro ─────────────────────────
                "vec3 efectoPlasma() {\n" +
                "    float pixel_size = length(u_resolution) / 700.0;\n" +
                "    vec2 puv = (floor(gl_FragCoord.xy * (1.0 / pixel_size)) * pixel_size - 0.5 * u_resolution) / length(u_resolution) - vec2(0.12, 0.0);\n" +
                "    float uv_len = length(puv);\n" +
                "    float speed = (u_time * 0.5 * 0.2) + 302.2;\n" +
                "    float new_angle = atan(puv.y, puv.x) + speed - 0.5 * 20.0 * (1.0 * u_spin_amount * uv_len + (1.0 - 1.0 * u_spin_amount));\n" +
                "    vec2 mid = (u_resolution / length(u_resolution)) / 2.0;\n" +
                "    puv = (vec2(uv_len * cos(new_angle) + mid.x, uv_len * sin(new_angle) + mid.y) - mid);\n" +
                "    puv *= 30.0;\n" +
                "    speed = u_time * 2.0;\n" +
                "    vec2 uv2 = vec2(puv.x + puv.y);\n" +
                "    for (int i = 0; i < 5; i++) {\n" +
                "        uv2 += vec2(sin(max(puv.x, puv.y))) + puv;\n" +
                "        puv += 0.5 * vec2(cos(5.1123314 + 0.353 * uv2.y + speed * 0.131121),\n" +
                "                          sin(uv2.x - 0.113 * speed));\n" +
                "        puv -= 1.0 * cos(puv.x + puv.y) - 1.0 * sin(puv.x * 0.711 - puv.y);\n" +
                "    }\n" +
                "    float contrast_mod = (0.25 * u_contrast + 0.5 * u_spin_amount + 1.2);\n" +
                "    float paint_res = min(2.0, max(0.0, length(puv) * 0.035 * contrast_mod));\n" +
                "    float c1p = max(0.0, 1.0 - contrast_mod * abs(1.0 - paint_res));\n" +
                "    float c2p = max(0.0, 1.0 - contrast_mod * abs(paint_res));\n" +
                "    float c3p = 1.0 - min(1.0, c1p + c2p);\n" +
                "    return (0.3 / u_contrast) * u_c1 + (1.0 - 0.3 / u_contrast) * (u_c1 * c1p + u_c2 * c2p + u_c3 * c3p);\n" +
                "}\n" +
                "void main(){\n" +
                "    vec2 uv = gl_FragCoord.xy / u_resolution.xy;\n" +
                "    uv.x *= u_resolution.x / u_resolution.y;\n" +
                "    float t = u_time;\n" +
                "    vec3 color = vec3(0.0);\n" +
                // ───────────────────────── EFECTO 0: CIELO (intacto) ─────────────────────
                "    if (u_efecto == 0) {\n" +
                "        vec3 cieloArriba = mix(vec3(0.12, 0.32, 0.58), u_c1, 0.35) * u_brillo;\n" +
                "        vec3 cieloAbajo  = mix(vec3(0.58, 0.78, 0.93), u_c2, 0.22) * u_brillo;\n" +
                "        float gradiente = smoothstep(0.0, 1.0, uv.y);\n" +
                "        color = mix(cieloAbajo, cieloArriba, gradiente);\n" +
                "        float nube1 = nubeOrganica(uv + vec2(0.0, 0.20), 2.1, 0.22, 1.7);\n" +
                "        float mascara1 = smoothstep(0.50, 0.70, nube1);\n" +
                "        color = mix(color, vec3(0.78, 0.86, 0.94), mascara1 * 0.26);\n" +
                "        float nube2 = nubeOrganica(uv + vec2(3.7, -0.4), 3.4, 0.40, 7.2);\n" +
                "        float mascara2 = smoothstep(0.48, 0.70, nube2);\n" +
                "        color = mix(color, vec3(0.88, 0.92, 0.96), mascara2 * 0.34);\n" +
                "        float nube3 = nubeOrganica(uv + vec2(-2.5, 1.4), 5.0, 0.68, 13.5);\n" +
                "        float mascara3 = smoothstep(0.52, 0.72, nube3);\n" +
                "        color = mix(color, vec3(0.94, 0.96, 0.98), mascara3 * 0.34);\n" +
                "        float nube4 = nubeOrganica(uv + vec2(-5.0, -2.0), 1.5, 0.12, 21.4);\n" +
                "        float mascara4 = smoothstep(0.55, 0.75, nube4);\n" +
                "        color = mix(color, vec3(0.85, 0.90, 0.95), mascara4 * 0.20);\n" +
                "        float nube5 = nubeOrganica(uv + vec2(8.0, 3.0), 4.2, 0.55, 33.1);\n" +
                "        float mascara5 = smoothstep(0.46, 0.68, nube5);\n" +
                "        color = mix(color, vec3(0.90, 0.94, 0.97), mascara5 * 0.25);\n" +
                "        float sombra = smoothstep(0.40, 0.60, nube2) * 0.6 + smoothstep(0.45, 0.65, nube4) * 0.4;\n" +
                "        color -= vec3(0.025, 0.035, 0.05) * sombra;\n" +
                "        vec2 posicionSol = vec2(0.72, 0.78);\n" +
                "        vec2 uvNormalizada = vec2(uv.x / (u_resolution.x / u_resolution.y), uv.y);\n" +
                "        float distanciaSol = distance(uvNormalizada, posicionSol);\n" +
                "        float halo = smoothstep(0.48, 0.0, distanciaSol);\n" +
                "        color += vec3(1.0, 0.91, 0.72) * halo * 0.09;\n" +
                "        color *= 0.985 + 0.015 * sin(u_time * 0.25);\n" +
                "    }\n" +
                // ───────────────────────── EFECTO 1: ONDAS RETRO ─────────────────────────
                "    else if (u_efecto == 1) {\n" +
                "        vec3 base = mix(u_c1, u_c2, 0.5) * u_brillo;\n" +
                "        color = base * 0.55;\n" +
                "        float horizonte = 0.45;\n" +
                "        if (uv.y > horizonte) {\n" +
                "            vec2 solCentro = vec2(uv.x / (u_resolution.x / u_resolution.y) * 0.5 + 0.5, 1.0);\n" +
                "        }\n" +
                "        vec2 uvs = uv;\n" +
                "        uvs.y += sin(uvs.x * 5.0 + u_time) * 0.10;\n" +
                "        float linea = abs(0.02 / sin(uvs.y * 20.0 - u_time * 2.0));\n" +
                "        color = mix(u_c1, u_c2 * u_brillo, linea);\n" +
                "        color += u_c3 * (1.0 - uvs.y) * 0.30;\n" +
                "        float yGrid = uv.y - 0.45;\n" +
                "        if (yGrid > 0.001) {\n" +
                "            float persp = 1.0 / max(yGrid, 0.05);\n" +
                "            float gridX = fract((uv.x - 0.5) * persp * 3.0);\n" +
                "            float gridY = fract(persp * 0.6 - u_time * 0.4);\n" +
                "            float lineaGrid = step(0.94, gridX) + step(0.94, gridY);\n" +
                "            color += u_c3 * lineaGrid * 0.35 * smoothstep(0.0, 0.35, yGrid);\n" +
                "        }\n" +
                "        float sol = smoothstep(0.28, 0.24, distance(uv, vec2(0.55, 0.45)));\n" +
                "        color += u_c3 * sol * 0.6;\n" +
                "    }\n" +
                // ───────────────────────── EFECTO 2: MATRIX ─────────────────────────
                "    else if (u_efecto == 2) {\n" +
                "        float n = fbm(uv * 10.0 - vec2(0.0, u_time * 0.5));\n" +
                "        color = mix(u_c1, u_c2 * u_brillo, smoothstep(0.4, 0.6, n));\n" +
                "        vec2 grid = fract(uv * 15.0);\n" +
                "        float border = step(0.95, grid.x) + step(0.95, grid.y);\n" +
                "        color = mix(color, u_c3, border * 0.5);\n" +
                "        float pulso = 0.5 + 0.5 * sin(u_time * 2.0 + uv.y * 8.0);\n" +
                "        color += u_c3 * pulso * 0.04;\n" +
                "    }\n" +
                // ───────────────────────── EFECTO 3: PLASMA BALATRO ─────────────────────────
                "    else if (u_efecto == 3) {\n" +
                "        color = efectoPlasma();\n" +
                "    }\n" +
                // ─── RAMAS DE FONDOS NUEVOS (4 a 11) ───────────────────
                FondosShaders.RAMAS +
                "    vec4 tex = texture2D(u_texture, v_texCoords) * 0.00001;\n" +
                "    gl_FragColor = vec4(color, 1.0) + tex;\n" +
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
        shaderPlasma.setUniformf("u_spin_amount", temaActual.spinAmount);
        shaderPlasma.setUniformf("u_contrast",   temaActual.contraste);
        batch.draw(texturaVacia, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setShader(shaderOriginal);
    }

    public void dispose() {
        if (shaderPlasma != null) shaderPlasma.dispose();
        if (texturaVacia != null) texturaVacia.dispose();
    }
}
