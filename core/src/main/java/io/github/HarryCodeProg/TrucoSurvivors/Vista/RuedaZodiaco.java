package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private ShapeRenderer shapeRenderer;
    private static final Color[] COLORES_SECTOR = {
        new Color(0.6f,0.2f,0.2f,1), new Color(0.5f,0.3f,0.15f,1),
        new Color(0.7f,0.6f,0.2f,1), new Color(0.7f,0.25f,0.15f,1),
        new Color(0.6f,0.45f,0.1f,1), new Color(0.4f,0.4f,0.2f,1),
        new Color(0.7f,0.6f,0.2f,1), new Color(0.5f,0.15f,0.1f,1),
        new Color(0.55f,0.4f,0.1f,1), new Color(0.35f,0.4f,0.25f,1),
        new Color(0.2f,0.4f,0.55f,1), new Color(0.2f,0.4f,0.35f,1)
    };
    private Texture texturaFondo;

    public RuedaZodiaco(float x, float y, float radio, TextureAtlas atlasZodiaco, Texture texturaFondo) {
        this.x = x; this.y = y; this.radio = radio;
        this.texturaFondo = texturaFondo;
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
        renderFondo(batch); // fondo primero
        // sprites de cada signo encima, ya rotados con el mismo anguloActual (heredan la rotacion visual)
        float anguloPorSlice = 360f / signos.length;
        for (int i = 0; i < signos.length; i++) {
            float angulo = anguloActual + i * anguloPorSlice + anguloPorSlice / 2f;
            float rad = (float) Math.toRadians(angulo);
            float px = x + (float) Math.cos(rad) * radio * 0.6f;
            float py = y + (float) Math.sin(rad) * radio * 0.6f;
            if (regiones[i] != null) batch.draw(regiones[i], px - 30, py - 30, 60, 60);
        }
    }

    public void renderFondo(SpriteBatch batch) {
        float diametro = radio * 2f;
        batch.draw(texturaFondo,
            x - radio, y - radio,     // posicion (esquina inferior-izq)
            radio, radio,             // origen de rotacion (centro relativo)
            diametro, diametro,       // tamaño
            1f, 1f,                   // escala
            anguloActual,             // rotacion en grados
            0, 0, texturaFondo.getWidth(), texturaFondo.getHeight(),
            false, false);
    }

    public Estado getEstado() { return estado; }

    public void dispose() { shapeRenderer.dispose(); }
}
