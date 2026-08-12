package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private TextureRegion[] regiones; // llenar con atlas 4x3 cuando este listo

    public RuedaZodiaco(float x, float y, float radio, TextureAtlas atlasZodiaco) {
        this.x = x; this.y = y; this.radio = radio;
        this.regiones = new TextureRegion[signos.length];
        for (int i = 0; i < signos.length; i++) {
            regiones[i] = atlasZodiaco.findRegion(signos[i].getNombreRegion());
        }
    }

    public void click(float mouseX, float mouseY, Consumer<SignoZodiaco> alConsumir) {
        float dx = mouseX - x, dy = mouseY - y;
        if (dx * dx + dy * dy > radio * radio) return;
        if (estado == Estado.SIN_GIRAR) {
            estado = Estado.GIRANDO;
            indiceResultado = new Random().nextInt(signos.length);
            float anguloPorSlice = 360f / signos.length;
            anguloObjetivo = anguloActual + 360f * 4 + (indiceResultado * anguloPorSlice);
            GestorSonidos s = Main.getInstance().getGestorSonidos();
            if (s != null) s.reproducirConVariacion("ruleta_giro");
        } else if (estado == Estado.DETENIDA) {
            estado = Estado.CONSUMIENDO;
            alConsumir.accept(signos[indiceResultado]);
        }
    }

    public void update(float delta) {
        if (estado != Estado.GIRANDO) return;
        float restante = anguloObjetivo - anguloActual;
        float velocidad = Math.max(restante * 3f, 60f); // desacelera, minimo 60 grados/seg
        float avance = Math.min(velocidad * delta, restante);
        anguloActual += avance;
        if (anguloActual >= anguloObjetivo) {
            anguloActual = anguloObjetivo;
            estado = Estado.DETENIDA;
        }
    }

    public void render(SpriteBatch batch) {
        float anguloPorSlice = 360f / signos.length;
        for (int i = 0; i < signos.length; i++) {
            float angulo = anguloActual + i * anguloPorSlice;
            float rad = (float) Math.toRadians(angulo);
            float px = x + (float) Math.cos(rad) * radio * 0.7f;
            float py = y + (float) Math.sin(rad) * radio * 0.7f;
            if (regiones != null && regiones[i] != null) {
                batch.draw(regiones[i], px - 30, py - 30, 60, 60);
            }
        }
    }

    public Estado getEstado() { return estado; }
}
