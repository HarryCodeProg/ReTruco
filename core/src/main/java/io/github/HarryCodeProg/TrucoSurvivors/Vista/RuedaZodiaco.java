package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

import java.util.Random;
import java.util.function.Consumer;

public class RuedaZodiaco {
    public enum Estado { SIN_GIRAR, GIRANDO, DETENIDA, CONSUMIENDO }
    private Estado estado = Estado.SIN_GIRAR;
    private final SignoZodiaco[] signos = SignoZodiaco.values();
    private int indiceResultado = -1;
    private float anguloActual = 0f, anguloObjetivo = 0f;
    private float x, y, radio;
    private Texture texturaFondo;
    private float tiempoGiro = 0f;
    private boolean girando = false;
    private Runnable alIniciarGiro;
    private static final float DURACION_GIRO = 3.683f;
    private float anguloInicial = 0f;
    private final ShaderProgram shaderBrillo;
    private float tiempoBrillo = 0f;

    private static final String VERTEX_SHADER_BRILLO =
        "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER_BRILLO =
        "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform float u_time;\n" +
            "void main() {\n" +
            "    vec4 color = texture2D(u_texture, v_texCoords) * v_color;\n" +
            "    vec2 centro = v_texCoords - vec2(0.5);\n" +
            "    float distancia = length(centro) * 2.0;\n" +
            "    float aro = smoothstep(0.58, 0.92, distancia) * (1.0 - smoothstep(0.92, 1.0, distancia));\n" +
            "    float pulso = 0.62 + 0.38 * sin(u_time * 2.4);\n" +
            "    vec3 dorado = vec3(1.0, 0.72, 0.10);\n" +
            "    color.rgb += dorado * aro * pulso * 0.72 * color.a;\n" +
            "    gl_FragColor = color;\n" +
            "}\n";

    public RuedaZodiaco(float x, float y, float radio, Texture texturaFondo) {
        this.x = x; this.y = y; this.radio = radio;
        this.texturaFondo = texturaFondo;
        ShaderProgram.pedantic = false;
        shaderBrillo = new ShaderProgram(VERTEX_SHADER_BRILLO, FRAGMENT_SHADER_BRILLO);
        if (!shaderBrillo.isCompiled()) {
            Gdx.app.error("RuedaZodiaco", "No se pudo compilar el brillo: " + shaderBrillo.getLog());
        }
    }

    public void click(float mouseX, float mouseY, Consumer<SignoZodiaco> alConsumir) {
        float dx = mouseX - x, dy = mouseY - y;
        if (dx * dx + dy * dy > radio * radio) return;
        if (estado == Estado.SIN_GIRAR) {
            estado = Estado.GIRANDO;
            girando = true;
            tiempoGiro = 0f;
            indiceResultado = new Random().nextInt(signos.length);
            float anguloPorSlice = 360f / signos.length;
            // Guardamos de dónde sale para interpolar el tiempo exacto
            anguloInicial = anguloActual;
            // Le damos más vueltas (ej: 6) para que gire lindo durante los casi 4 segundos
            anguloObjetivo = anguloInicial + (360f * 6) + (indiceResultado * anguloPorSlice);
            if (alIniciarGiro != null) alIniciarGiro.run();
        } else if (estado == Estado.DETENIDA) {
            estado = Estado.CONSUMIENDO;
            alConsumir.accept(signos[indiceResultado]);
        }
    }

    public void update(float delta) {
        tiempoBrillo += delta;
        if (estado != Estado.GIRANDO) return;
        if (girando) {
            tiempoGiro += delta;
            if (tiempoGiro >= DURACION_GIRO) {
                tiempoGiro = DURACION_GIRO;
                girando = false;
                estado = Estado.DETENIDA;
                anguloActual = anguloObjetivo; // acá dejás la rueda exactamente en su posición final
            } else {
                // Animación fluida: desacelera progresivamente (Ease-Out Cubic)
                float t = tiempoGiro / DURACION_GIRO;
                float easeOut = 1f - (float) Math.pow(1f - t, 3);
                anguloActual = anguloInicial + (anguloObjetivo - anguloInicial) * easeOut;
            }
        }
    }

    public void render(SpriteBatch batch) {
        renderFondo(batch);
    }

    public void renderFondo(SpriteBatch batch) {
        float diametro = radio * 2f;
        ShaderProgram shaderAnterior = batch.getShader();
        boolean brilloDisponible = estado == Estado.SIN_GIRAR && shaderBrillo.isCompiled();
        if (brilloDisponible) {
            batch.setShader(shaderBrillo);
            shaderBrillo.bind();
            shaderBrillo.setUniformf("u_time", tiempoBrillo);
        }
        batch.draw(texturaFondo,
            x - radio, y - radio,     // posicion (esquina inferior-izq)
            radio, radio,             // origen de rotacion (centro relativo)
            diametro, diametro,       // tamaño
            1f, 1f,                   // escala
            anguloActual,             // rotacion en grados
            0, 0, texturaFondo.getWidth(), texturaFondo.getHeight(),
            false, false);
        if (brilloDisponible) batch.setShader(shaderAnterior);
    }

    public Estado getEstado() { return estado; }

    public void dispose() { shaderBrillo.dispose(); }

    public SignoZodiaco getUltimoSignoConsumido() { return signos[indiceResultado]; }

    public void setAlIniciarGiro(Runnable callback) {
        this.alIniciarGiro = callback;
    }
}
