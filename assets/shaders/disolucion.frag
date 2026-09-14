#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float u_dissolve;      // 0.0 = intacta, 1.0 = totalmente disuelta
uniform float u_time;          // tiempo acumulado, para que el patron de ruido "respire"
uniform vec4 u_regionUV;       // (u0, v0, u1, v1) limites de la region dentro del atlas
uniform bool u_shadow;         // true = version sombra (negra, semitransparente)
uniform vec4 u_burnColor1;
uniform vec4 u_burnColor2;

float hue(float s, float t, float h) {
    float hs = mod(h, 1.0) * 6.0;
    if (hs < 1.0) return (t - s) * hs + s;
    if (hs < 3.0) return t;
    if (hs < 4.0) return (t - s) * (4.0 - hs) + s;
    return s;
}

vec4 rgbFromHsl(vec4 c) {
    if (c.y == 0.0) return vec4(vec3(c.z), c.a);
    float t = (c.z < 0.5) ? c.y * c.z + c.z : -c.y * c.z + (c.y + c.z);
    float s = 2.0 * c.z - t;
    return vec4(hue(s, t, c.x + 1.0/3.0), hue(s, t, c.x), hue(s, t, c.x - 1.0/3.0), c.w);
}

vec4 hslFromRgb(vec4 c) {
    float lo = min(c.r, min(c.g, c.b));
    float hi = max(c.r, max(c.g, c.b));
    float delta = hi - lo;
    float sum = hi + lo;
    vec4 hsl = vec4(0.0, 0.0, 0.5 * sum, c.a);
    if (delta == 0.0) return hsl;
    hsl.y = (hsl.z < 0.5) ? delta / sum : delta / (2.0 - sum);
    if (hi == c.r) hsl.x = (c.g - c.b) / delta;
    else if (hi == c.g) hsl.x = (c.b - c.r) / delta + 2.0;
    else hsl.x = (c.r - c.g) / delta + 4.0;
    hsl.x = mod(hsl.x / 6.0, 1.0);
    return hsl;
}

void main() {
    vec4 tex = texture2D(u_texture, v_texCoords);

    // uv local a la region (0..1 dentro de la carta, no del atlas)
    vec2 regionSize = u_regionUV.zw - u_regionUV.xy;
    vec2 uv = (v_texCoords - u_regionUV.xy) / regionSize;

    // Desaturacion + oscurecimiento sutil, igual al original
    vec4 sat = hslFromRgb(tex);
    sat.g = sat.g * 0.5;
    sat.b = sat.b * 0.8;
    tex = rgbFromHsl(sat);
    tex.a = tex.a * 0.5;
    tex = tex * v_color;

    if (u_dissolve < 0.001) {
        if (u_shadow) {
            gl_FragColor = vec4(0.0, 0.0, 0.0, tex.a * 0.3);
        } else {
            gl_FragColor = tex;
        }
        return;
    }

    float adjustedDissolve = (u_dissolve * u_dissolve * (3.0 - 2.0 * u_dissolve)) * 1.02 - 0.01;

    float t = u_time * 10.0 + 2003.0;
    vec2 uvScaled = (uv - 0.5) * 2.3;

    vec2 field1 = uvScaled + 0.5 * vec2(sin(-t / 143.634), cos(-t / 99.4324));
    vec2 field2 = uvScaled + 0.5 * vec2(cos(t / 53.1532), cos(t / 61.4532));
    vec2 field3 = uvScaled + 0.5 * vec2(sin(-t / 87.53218), sin(-t / 49.0));

    float field = (1.0 + (
    cos(length(field1) / 0.19483) + sin(length(field2) / 0.33155) * cos(field2.y / 0.1573) +
    cos(length(field3) / 0.27193) * sin(field3.x / 0.2192)
    )) / 2.0;

    vec2 borders = vec2(0.2, 0.8);

    float res = (0.5 + 0.5 * cos((adjustedDissolve) / 0.82612 + (field - 0.5) * 3.14159))
    - (uv.x > borders.y ? (uv.x - borders.y) * (5.0 + 5.0 * u_dissolve) : 0.0) * u_dissolve
    - (uv.y > borders.y ? (uv.y - borders.y) * (5.0 + 5.0 * u_dissolve) : 0.0) * u_dissolve
    - (uv.x < borders.x ? (borders.x - uv.x) * (5.0 + 5.0 * u_dissolve) : 0.0) * u_dissolve
    - (uv.y < borders.x ? (borders.x - uv.y) * (5.0 + 5.0 * u_dissolve) : 0.0) * u_dissolve;

    if (tex.a > 0.01 && u_burnColor1.a > 0.01 && !u_shadow
        && res < adjustedDissolve + 0.8 * (0.5 - abs(adjustedDissolve - 0.5))
        && res > adjustedDissolve) {
        if (res < adjustedDissolve + 0.5 * (0.5 - abs(adjustedDissolve - 0.5))) {
            tex = u_burnColor1;
        } else if (u_burnColor2.a > 0.01) {
            tex = u_burnColor2;
        }
    }

    float alphaFinal = (res > adjustedDissolve) ? (u_shadow ? tex.a * 0.3 : tex.a) : 0.0;
    gl_FragColor = vec4(u_shadow ? vec3(0.0) : tex.rgb, alphaFinal);
}
