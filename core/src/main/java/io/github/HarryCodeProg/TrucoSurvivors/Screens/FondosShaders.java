package io.github.HarryCodeProg.TrucoSurvivors.Screens;

/**
 * GLSL de los fondos nuevos (efectos 4 a 11) para {@link Background}.
 *
 *   4 = Aurora boreal            8  = Lineas topograficas
 *   5 = Campo estelar            9  = Portal dimensional
 *   6 = Agua con caustica        10 = Interferencia digital
 *   7 = Brasas flotantes         11 = Luciernagas
 *
 * FUNCIONES va antes del main() del fragment shader (usa random, noise y fbm, que ya existen ahi).
 * RAMAS va dentro del main(), a continuacion del bloque del efecto 3.
 * Todo el GLSL esta escrito en estilo 1.10 (sin #version, sin conversiones int->float implicitas),
 * igual que el resto del shader de Background.
 */
final class FondosShaders {
    private FondosShaders() {}

    static final String FUNCIONES =
        "vec2 random2(vec2 p) {\n" +
            "    return vec2(random(p), random(p + vec2(41.3, 17.7)));\n" +
            "}\n" +
            "\n" +
            "// ----- 4: AURORA BOREAL -----\n" +
            "vec3 fondoAurora(vec2 uv) {\n" +
            "    float t = u_time;\n" +
            "    vec3 col = mix(u_c1 * 1.7, u_c1 * 0.45, uv.y);\n" +
            "    float sr = random(floor(uv * 240.0));\n" +
            "    float tw = 0.55 + 0.45 * sin(t * 1.7 + sr * 90.0);\n" +
            "    col += vec3(0.75, 0.82, 1.0) * step(0.9968, sr) * tw * 0.45;\n" +
            "    vec3 luz = vec3(0.0);\n" +
            "    for (int k = 0; k < 3; k++) {\n" +
            "        float fk = float(k);\n" +
            "        float x = uv.x * (1.25 + 0.3 * fk);\n" +
            "        float cy = 0.70 - 0.06 * fk + 0.09 * sin(x * 1.9 + t * 0.21 + fk * 1.9) + 0.045 * sin(x * 4.1 - t * 0.33 + fk * 0.7);\n" +
            "        float d = uv.y - cy;\n" +
            "        float env = mix(exp(d * 12.0), exp(-d * 3.4), step(0.0, d));\n" +
            "        float pliegue = 0.5 + 0.5 * sin(x * 5.5 + sin(x * 1.7 + t * 0.3 + fk) * 2.6 - t * 0.35);\n" +
            "        float rayos = 0.25 + 0.85 * noise(vec2(x * 9.0 + fk * 13.0 + sin(t * 0.17 + fk) * 1.5, t * 0.12 + fk * 3.0)) + 0.6 * pliegue;\n" +
            "        float flujo = smoothstep(0.25, 0.8, fbm(vec2(x * 1.1 + t * 0.07 + fk * 4.0, t * 0.05 + fk)));\n" +
            "        float inten = env * rayos * (0.25 + 0.9 * flujo);\n" +
            "        vec3 c = mix(u_c2, u_c3, clamp(0.5 * fk + d * 1.6 + 0.2 * sin(t * 0.1 + x), 0.0, 1.0));\n" +
            "        luz += c * inten * 0.85;\n" +
            "    }\n" +
            "    col += luz;\n" +
            "    return 1.0 - exp(-col * 1.4);\n" +
            "}\n" +
            "\n" +
            "// ----- 5: CAMPO ESTELAR CON PERSPECTIVA -----\n" +
            "vec3 fondoEstrellas(vec2 uv) {\n" +
            "    float asp = u_resolution.x / u_resolution.y;\n" +
            "    float t = u_time;\n" +
            "    vec2 p = uv - vec2(asp * 0.5, 0.5);\n" +
            "    vec3 col = mix(u_c1 * 1.9, u_c1 * 0.5, clamp(length(p) * 1.4, 0.0, 1.0));\n" +
            "    vec2 rd = p / max(length(p), 0.0001);\n" +
            "    for (int i = 0; i < 5; i++) {\n" +
            "        float fi = float(i);\n" +
            "        float ciclo = t * 0.05 + fi / 5.0;\n" +
            "        float prof = fract(ciclo);\n" +
            "        float semilla = floor(ciclo) * 7.31 + fi * 19.7;\n" +
            "        float escala = 1.4 + 24.0 * pow(1.0 - prof, 1.7);\n" +
            "        vec2 g = p * escala + vec2(semilla, semilla * 0.37);\n" +
            "        vec2 id = floor(g);\n" +
            "        vec2 f = fract(g) - 0.5;\n" +
            "        float aparece = smoothstep(0.0, 0.2, prof) * (1.0 - smoothstep(0.88, 1.0, prof));\n" +
            "        float estira = 1.0 + 2.2 * prof * prof;\n" +
            "        for (int dy = -1; dy <= 1; dy++) {\n" +
            "            for (int dx = -1; dx <= 1; dx++) {\n" +
            "                vec2 celda = id + vec2(float(dx), float(dy));\n" +
            "                float rnd = random(celda + 5.7);\n" +
            "                vec2 off = (random2(celda) - 0.5) * 0.7;\n" +
            "                vec2 q = f - vec2(float(dx), float(dy)) - off;\n" +
            "                float along = dot(q, rd);\n" +
            "                float perp = dot(q, vec2(-rd.y, rd.x));\n" +
            "                along *= mix(1.0 / estira, 1.0, step(0.0, along));\n" +
            "                float d = length(vec2(along, perp));\n" +
            "                float tam = mix(0.025, 0.075, prof) * (0.6 + 0.8 * rnd);\n" +
            "                float nucleo = 1.0 - smoothstep(0.0, tam, d);\n" +
            "                float halo = exp(-d * d / (tam * tam * 6.0)) * 0.30;\n" +
            "                float brillo = step(0.66, rnd) * aparece * (0.65 + 0.35 * sin(t * (2.0 + rnd * 3.0) + rnd * 60.0));\n" +
            "                vec3 tinte = mix(vec3(1.0), mix(u_c2, u_c3, fract(rnd * 7.0)), 0.5);\n" +
            "                col += tinte * (nucleo + halo) * brillo;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    for (int j = 0; j < 2; j++) {\n" +
            "        float fj = float(j);\n" +
            "        float tt = t * 0.16 + fj * 0.5;\n" +
            "        float cy = floor(tt);\n" +
            "        float ph = fract(tt);\n" +
            "        float r1 = random(vec2(cy, fj + 1.0));\n" +
            "        float r2 = random(vec2(cy + 7.0, fj + 3.0));\n" +
            "        float r3 = random(vec2(cy + 13.0, fj + 5.0));\n" +
            "        float activa = step(0.45, r1);\n" +
            "        vec2 dir = normalize(vec2(mix(-1.0, 1.0, step(0.5, r2)) * 0.9, -0.45 - 0.2 * r3));\n" +
            "        vec2 ini = vec2(asp * (0.15 + 0.7 * r3), 0.55 + 0.4 * r2);\n" +
            "        vec2 cab = ini + dir * ph * 2.6;\n" +
            "        vec2 qs = uv - cab;\n" +
            "        float L = 0.22;\n" +
            "        float s = clamp(dot(qs, -dir), 0.0, L);\n" +
            "        float dl = length(qs + dir * s);\n" +
            "        float vis = smoothstep(0.0, 0.04, ph) * (1.0 - smoothstep(0.2, 0.3, ph));\n" +
            "        float estela = (1.0 - smoothstep(0.0, 0.0045, dl)) * pow(1.0 - s / L, 2.0);\n" +
            "        col += vec3(0.9, 0.95, 1.0) * estela * vis * activa * 0.9;\n" +
            "    }\n" +
            "    return col;\n" +
            "}\n" +
            "\n" +
            "// ----- 6: AGUA CON REFRACCION (causticas) -----\n" +
            "float causticas(vec2 p, float tt) {\n" +
            "    vec2 i = p;\n" +
            "    float c = 1.0;\n" +
            "    float inten = 0.005;\n" +
            "    for (int n = 0; n < 4; n++) {\n" +
            "        float fn = float(n + 1);\n" +
            "        float t2 = tt * (1.0 - 3.5 / fn);\n" +
            "        i = p + vec2(cos(t2 - i.x) + sin(t2 + i.y), sin(t2 - i.y) + cos(t2 + i.x));\n" +
            "        c += 1.0 / length(vec2(p.x / (sin(i.x + t2) / inten), p.y / (cos(i.y + t2) / inten)));\n" +
            "    }\n" +
            "    c /= 4.0;\n" +
            "    c = 1.17 - pow(c, 1.4);\n" +
            "    return pow(abs(c), 8.0);\n" +
            "}\n" +
            "\n" +
            "vec3 fondoAgua(vec2 uv) {\n" +
            "    float t = u_time;\n" +
            "    vec3 col = mix(u_c1 * 0.45, u_c1 * 1.25, uv.y);\n" +
            "    float a = causticas(uv * 5.0 + vec2(-250.0 + t * 0.12, -250.0), t * 0.55 + 23.0);\n" +
            "    float b = causticas(uv * 8.5 + vec2(-250.0 - t * 0.09, -250.0 + t * 0.07), t * 0.42 + 71.0);\n" +
            "    float luz = clamp(a * 0.75 + b * 0.55 + a * b * 0.9, 0.0, 1.6);\n" +
            "    float prof = mix(0.45, 1.0, uv.y);\n" +
            "    col += u_c2 * luz * 0.55 * prof;\n" +
            "    col += u_c3 * pow(clamp(luz, 0.0, 1.0), 2.5) * 0.45 * prof;\n" +
            "    float haz = noise(vec2(uv.x * 2.2 + t * 0.03, uv.y * 0.6 - t * 0.02));\n" +
            "    col += u_c2 * smoothstep(0.55, 0.9, haz) * 0.08 * uv.y;\n" +
            "    return col;\n" +
            "}\n" +
            "\n" +
            "// ----- 7: BRASAS FLOTANTES -----\n" +
            "vec3 fondoBrasas(vec2 uv) {\n" +
            "    float t = u_time;\n" +
            "    vec3 col = mix(u_c1 * 2.2, u_c1 * 0.5, uv.y);\n" +
            "    col += u_c2 * 0.08 * pow(max(1.0 - uv.y, 0.0), 3.0);\n" +
            "    for (int L = 0; L < 3; L++) {\n" +
            "        float fl = float(L);\n" +
            "        float esc = 6.0 + 5.0 * fl;\n" +
            "        float spd = 0.14 - 0.04 * fl;\n" +
            "        vec2 p = vec2(uv.x * esc * 0.9 + fl * 31.0, uv.y * esc - t * spd * esc);\n" +
            "        vec2 id = floor(p);\n" +
            "        vec2 f = fract(p);\n" +
            "        vec2 rnd = random2(id + fl * 11.3);\n" +
            "        float gate = random(id + 3.9 + fl);\n" +
            "        vec2 pos = vec2(0.5 + 0.2 * sin(t * 0.7 + rnd.y * 6.283 + id.y * 0.9) + 0.15 * (rnd.x - 0.5), 0.45 + 0.35 * rnd.y);\n" +
            "        vec2 q = f - pos;\n" +
            "        q.y *= mix(0.5, 1.0, step(0.0, q.y));\n" +
            "        float r = 0.03 + 0.05 * rnd.x;\n" +
            "        float glow = exp(-dot(q, q) / (r * r * 3.0));\n" +
            "        float parp = 0.5 + 0.5 * sin(t * (2.0 + rnd.x * 4.0) + rnd.y * 20.0);\n" +
            "        float chispa = pow(max(0.0, sin(t * 0.9 + rnd.x * 50.0)), 12.0);\n" +
            "        float nucleoB = 1.0 - smoothstep(0.0, r * 0.45, length(q));\n" +
            "        float inten = (glow + nucleoB * 0.8) * (0.35 + 0.65 * parp + 2.2 * chispa) * step(0.35, gate);\n" +
            "        float vida = smoothstep(0.0, 0.08, uv.y) * (1.0 - smoothstep(0.55, 1.0, uv.y));\n" +
            "        vec3 cc = mix(u_c3, u_c2, clamp(uv.y * 1.2 + rnd.x * 0.3 - 0.1, 0.0, 1.0));\n" +
            "        col += cc * inten * vida * (0.85 - 0.15 * fl);\n" +
            "    }\n" +
            "    return col;\n" +
            "}\n" +
            "\n" +
            "// ----- 8: LINEAS TOPOGRAFICAS -----\n" +
            "float campoTopo(vec2 p) {\n" +
            "    float t = u_time;\n" +
            "    float h = noise(p * 0.9 + vec2(t * 0.020, -t * 0.014)) * 0.55;\n" +
            "    h += noise(p * 1.9 + vec2(8.3, 2.1) - vec2(t * 0.015, -t * 0.022)) * 0.30;\n" +
            "    h += noise(p * 3.7 + vec2(3.1, 9.7) + vec2(t * 0.010, t * 0.012)) * 0.15;\n" +
            "    h += 0.06 * sin(p.x * 1.3 + t * 0.15) * sin(p.y * 1.1 - t * 0.12);\n" +
            "    return h;\n" +
            "}\n" +
            "\n" +
            "vec3 fondoTopografico(vec2 uv) {\n" +
            "    float t = u_time;\n" +
            "    vec2 p = uv * 2.2;\n" +
            "    float e = 0.01;\n" +
            "    float h = campoTopo(p);\n" +
            "    float hx = campoTopo(p + vec2(e, 0.0)) - h;\n" +
            "    float hy = campoTopo(p + vec2(0.0, e)) - h;\n" +
            "    float grad = max(length(vec2(hx, hy)) / e, 0.02);\n" +
            "    float niveles = 14.0;\n" +
            "    float lv = h * niveles;\n" +
            "    float dn = abs(fract(lv + 0.5) - 0.5);\n" +
            "    float dist = dn / (grad * niveles);\n" +
            "    float px = 2.2 / u_resolution.y;\n" +
            "    float idx = floor(lv + 0.5);\n" +
            "    float mayor = 1.0 - min(1.0, mod(idx, 5.0));\n" +
            "    float finaL = 1.0 - smoothstep(px * 0.5, px * 1.6, dist);\n" +
            "    float gruesa = 1.0 - smoothstep(px * 1.0, px * 2.8, dist);\n" +
            "    float pulso = 0.65 + 0.35 * sin(lv * 0.9 - t * 0.8);\n" +
            "    float linea = mix(finaL * 0.55, gruesa, mayor) * pulso;\n" +
            "    vec3 col = u_c1 + u_c2 * 0.10 * smoothstep(0.25, 0.85, h);\n" +
            "    col += mix(u_c2, u_c3, mayor) * linea;\n" +
            "    col += mix(u_c2, u_c3, mayor) * exp(-dist / (px * 7.0)) * 0.10;\n" +
            "    return col;\n" +
            "}\n" +
            "\n" +
            "// ----- 9: PORTAL DIMENSIONAL -----\n" +
            "vec3 fondoPortal(vec2 uv) {\n" +
            "    float asp = u_resolution.x / u_resolution.y;\n" +
            "    float t = u_time;\n" +
            "    vec2 p = uv - vec2(asp * 0.5, 0.5);\n" +
            "    float r = length(p);\n" +
            "    float a = atan(p.y, p.x);\n" +
            "    float latido = 0.5 + 0.5 * sin(t * 1.1);\n" +
            "    vec3 col = max(u_c1 * (1.6 - r * 1.8), u_c1 * 0.3);\n" +
            "    float onda = fract(r * 2.2 - t * 0.28);\n" +
            "    float pulso = pow(1.0 - onda, 5.0) * smoothstep(0.05, 0.2, r) * exp(-r * 1.8);\n" +
            "    col += u_c2 * pulso * 0.55;\n" +
            "    for (int i = 0; i < 5; i++) {\n" +
            "        float fi = float(i);\n" +
            "        float R = 0.12 + 0.068 * fi;\n" +
            "        float sentido = mod(fi, 2.0) * 2.0 - 1.0;\n" +
            "        float ang = a + sentido * t * (0.18 + 0.06 * fi);\n" +
            "        float n = 4.0 + 2.0 * fi;\n" +
            "        float sector = ang * n / 6.2831853;\n" +
            "        float sid = mod(floor(sector), n);\n" +
            "        float alea = random(vec2(sid, fi + 1.0));\n" +
            "        float enc = smoothstep(0.25, 0.75, 0.5 + 0.5 * sin(t * (0.8 + alea * 1.6) + alea * 30.0 + fi));\n" +
            "        float dentro = abs(fract(sector) - 0.5) * 2.0;\n" +
            "        float tramo = 1.0 - smoothstep(0.72, 0.9, dentro);\n" +
            "        float grosor = 0.0035 + 0.0005 * fi;\n" +
            "        float anillo = 1.0 - smoothstep(grosor, grosor + 0.004, abs(r - R));\n" +
            "        float vivo = anillo * tramo * enc;\n" +
            "        vec3 c = mix(u_c2, u_c3, mod(fi, 2.0));\n" +
            "        col += c * (anillo * 0.22 + vivo * 0.9);\n" +
            "        col += c * exp(-abs(r - R) * 55.0) * vivo * 0.25;\n" +
            "    }\n" +
            "    {\n" +
            "        float R = 0.43;\n" +
            "        float n = 24.0;\n" +
            "        float sector = (a - t * 0.05) * n / 6.2831853;\n" +
            "        float sid = mod(floor(sector), n);\n" +
            "        vec2 q = vec2((fract(sector) - 0.5) * (6.2831853 / n) * r, r - R);\n" +
            "        float h1 = random(vec2(sid, 9.0));\n" +
            "        float barraR = (1.0 - step(0.004, abs(q.x))) * (1.0 - step(0.017, abs(q.y)));\n" +
            "        float barraT = (1.0 - step(0.004, abs(q.y))) * (1.0 - step(0.011, abs(q.x)));\n" +
            "        float punto = 1.0 - step(0.007, length(q));\n" +
            "        float cruz = max(barraR, barraT);\n" +
            "        float glifo = barraR * (1.0 - step(0.25, h1))\n" +
            "            + barraT * step(0.25, h1) * (1.0 - step(0.5, h1))\n" +
            "            + punto * step(0.5, h1) * (1.0 - step(0.75, h1))\n" +
            "            + cruz * step(0.75, h1);\n" +
            "        float parp = 0.45 + 0.55 * (0.5 + 0.5 * sin(t * 0.7 + h1 * 20.0));\n" +
            "        float guia = (1.0 - smoothstep(0.0015, 0.004, abs(r - R - 0.03))) + (1.0 - smoothstep(0.0015, 0.004, abs(r - R + 0.03)));\n" +
            "        col += u_c3 * glifo * parp * 0.9 + u_c2 * guia * 0.18;\n" +
            "    }\n" +
            "    float nucleo = 0.075 + 0.014 * latido;\n" +
            "    col += u_c3 * exp(-r * r / (0.012 + 0.006 * latido)) * (0.7 + 0.5 * latido);\n" +
            "    col = mix(col, vec3(1.0, 0.97, 0.9), 1.0 - smoothstep(nucleo * 0.7, nucleo, r));\n" +
            "    return col;\n" +
            "}\n" +
            "\n" +
            "// ----- 10: INTERFERENCIA DIGITAL / SENAL ROTA -----\n" +
            "float glitchContenido(vec2 p) {\n" +
            "    vec2 g = p * vec2(4.5, 3.0);\n" +
            "    vec2 id = floor(g);\n" +
            "    vec2 f = fract(g);\n" +
            "    float caja = step(0.60, random(id + 3.1));\n" +
            "    float interior = step(0.10, f.x) * (1.0 - step(0.90, f.x)) * step(0.14, f.y) * (1.0 - step(0.86, f.y));\n" +
            "    float nivel = 0.35 + 0.65 * random(id + 9.4);\n" +
            "    float regla = (1.0 - step(0.03, abs(f.y - 0.5))) * step(0.3, random(id + 1.9)) * 0.25;\n" +
            "    return caja * interior * nivel + regla;\n" +
            "}\n" +
            "\n" +
            "vec3 fondoGlitch(vec2 uv) {\n" +
            "    float t = u_time;\n" +
            "    float ft = floor(t * 16.0);\n" +
            "    float ciclo = t * 0.5;\n" +
            "    float id = floor(ciclo);\n" +
            "    float fase = fract(ciclo);\n" +
            "    float hay = step(0.4, random(vec2(id, 1.7)));\n" +
            "    float dur = 0.16 + 0.32 * random(vec2(id, 4.2));\n" +
            "    float rafaga = hay * (1.0 - step(dur, fase));\n" +
            "    float tira = floor(uv.y * mix(10.0, 48.0, random(vec2(id, 6.6))));\n" +
            "    float rt = random(vec2(tira, ft));\n" +
            "    float desp = rafaga * step(0.62, rt) * (random(vec2(tira + 9.0, ft)) - 0.5) * 0.55;\n" +
            "    float ca = rafaga * (0.006 + 0.016 * random(vec2(ft, 2.0)));\n" +
            "    vec2 q = vec2(uv.x + desp, uv.y);\n" +
            "    float nivelBase = mix(0.30, 1.0, rafaga);\n" +
            "    float fr = glitchContenido(q + vec2(ca, 0.0));\n" +
            "    float fb = glitchContenido(q - vec2(ca, 0.0));\n" +
            "    float rodar = (uv.y - fract(t * 0.06)) * 6.0;\n" +
            "    vec3 col = u_c1 + (u_c2 + u_c3) * 0.02 * exp(-rodar * rodar);\n" +
            "    col += (u_c2 * fr + u_c3 * fb) * 0.55 * nivelBase;\n" +
            "    col += mix(u_c2, u_c3, rt) * rafaga * step(0.93, rt) * 0.30;\n" +
            "    vec2 bl = floor(uv * vec2(14.0, 8.0));\n" +
            "    float parpadeo = rafaga * step(0.965, random(bl + ft * 1.7));\n" +
            "    col += mix(u_c2, u_c3, random(bl + 2.2)) * parpadeo * 0.85;\n" +
            "    col += vec3(1.0) * rafaga * step(0.9985, random(floor(uv * vec2(120.0, 70.0)) + ft));\n" +
            "    col *= 0.92 + 0.08 * sin(uv.y * u_resolution.y * 1.2);\n" +
            "    col += (random(gl_FragCoord.xy + ft) - 0.5) * 0.025;\n" +
            "    return max(col, vec3(0.0));\n" +
            "}\n" +
            "\n" +
            "// ----- 11: LUCIERNAGAS BIOLUMINISCENTES -----\n" +
            "vec3 fondoLuciernagas(vec2 uv) {\n" +
            "    float t = u_time;\n" +
            "    vec3 col = mix(u_c1 * 1.8, u_c1 * 0.6, uv.y);\n" +
            "    col += u_c2 * 0.05 * fbm(uv * 2.5 + vec2(t * 0.02, -t * 0.015)) * (1.0 - uv.y);\n" +
            "    for (int L = 0; L < 3; L++) {\n" +
            "        float fl = float(L);\n" +
            "        float esc = 5.0 + 3.5 * fl;\n" +
            "        vec2 p = uv * esc + vec2(t * 0.02 * (fl + 1.0), t * 0.012 * (fl + 1.0)) + fl * 17.0;\n" +
            "        vec2 id = floor(p);\n" +
            "        vec2 f = fract(p);\n" +
            "        vec2 rnd = random2(id + fl * 5.1);\n" +
            "        vec2 grupo = floor(id / 3.0);\n" +
            "        float dens = random(grupo + 7.0 + fl);\n" +
            "        float vive = step(1.0 - (0.30 + 0.60 * dens), random(id + 3.3 + fl));\n" +
            "        vec2 pos = vec2(0.5) + 0.26 * vec2(sin(t * 0.35 + rnd.x * 6.283), cos(t * 0.29 + rnd.y * 6.283));\n" +
            "        float d = length(f - pos);\n" +
            "        float r = 0.06 + 0.045 * rnd.x;\n" +
            "        float fase = mix(rnd.y * 6.283, random(grupo + fl) * 6.283, 0.7);\n" +
            "        float lat = pow(max(0.0, 0.25 + 0.85 * sin(t * (0.45 + 0.5 * rnd.x) + fase)), 1.6);\n" +
            "        float luz = exp(-d * d / (r * r)) * 0.55 + (1.0 - smoothstep(0.0, r * 0.35, d)) * 0.9;\n" +
            "        vec3 cc = mix(u_c2, u_c3, rnd.y);\n" +
            "        col += cc * luz * lat * vive * (1.25 - 0.2 * fl);\n" +
            "    }\n" +
            "    return col;\n" +
            "}\n" +
            "";

    static final String RAMAS =
        "    else if (u_efecto == 4) { color = fondoAurora(uv); }\n" +
            "    else if (u_efecto == 5) { color = fondoEstrellas(uv); }\n" +
            "    else if (u_efecto == 6) { color = fondoAgua(uv); }\n" +
            "    else if (u_efecto == 7) { color = fondoBrasas(uv); }\n" +
            "    else if (u_efecto == 8) { color = fondoTopografico(uv); }\n" +
            "    else if (u_efecto == 9) { color = fondoPortal(uv); }\n" +
            "    else if (u_efecto == 10) { color = fondoGlitch(uv); }\n" +
            "    else if (u_efecto == 11) { color = fondoLuciernagas(uv); }\n" +
            "    if (u_efecto >= 4) { color *= 0.5 + 0.5 * u_brillo; }\n" +
            "";
}
