package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;
import java.util.Collections;

public class OverlayConsumoSanto {

    public enum Estado {
        OCULTO,
        ENTRANDO,
        QUEMANDO,
        APLICANDO_EFECTO,
        CERRADO
    }

    private Estado estado = Estado.OCULTO;

    private Santo santo;
    private TextureRegion region;

    private float x = 640;
    private float y = 360;

    private float scale = 0.3f;
    private float alpha = 1f;
    private float tiempo = 0f;

    private Runnable alAplicar;

    private static final float DURACION_ENTRADA = 0.35f;
    private static final float DURACION_QUEMA = 0.9f;

    public void abrir(
        Santo santo,
        TextureRegion region,
        Runnable alAplicar
    ) {
        this.santo = santo;
        this.region = region;
        this.alAplicar = alAplicar;
        estado = Estado.ENTRANDO;
        scale = 0.3f;
        alpha = 1f;
        tiempo = 0f;
        GestorSonidos s = Main.getInstance().getGestorSonidos();
        if (s != null) {
            s.reproducirConVariacion("consumir_zodiaco");
        }
    }

    public void update(float delta) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) {
            return;
        }
        tiempo += delta;
        if (estado == Estado.ENTRANDO) {
            float t = Math.min(tiempo / DURACION_ENTRADA, 1f);
            scale = 0.3f + 0.9f * t;
            if (t >= 1f) {
                estado = Estado.QUEMANDO;
                tiempo = 0f;
            }
        } else if (estado == Estado.QUEMANDO) {
            float t = Math.min(tiempo / DURACION_QUEMA, 1f);
            alpha = 1f - t;
            scale = 1.2f - 0.3f * t;
            if (t >= 1f) {
                estado = Estado.APLICANDO_EFECTO;
            }
        } else if (estado == Estado.APLICANDO_EFECTO) {
            if (alAplicar != null) {
                alAplicar.run();
                alAplicar = null;
            }
            estado = Estado.CERRADO;
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) {
            return;
        }
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixel, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, alpha);
        float size = 220 * scale;
        batch.draw(region, x - size / 2f, y - size / 2f, size, size);
        batch.setColor(1, 1, 1, 1);
    }

    public boolean estaActivo() {
        return estado != Estado.OCULTO
            && estado != Estado.CERRADO;
    }

    public boolean estaCerrado() {
        return estado == Estado.CERRADO;
    }

    public void confirmarCierre() {
        estado = Estado.OCULTO;
    }
}
