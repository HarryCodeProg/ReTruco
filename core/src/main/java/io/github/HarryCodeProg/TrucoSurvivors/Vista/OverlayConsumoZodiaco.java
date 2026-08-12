package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

public class OverlayConsumoZodiaco {
    public enum Estado { OCULTO, ENTRANDO, QUEMANDO, APLICANDO_EFECTO, CERRADO }
    private Estado estado = Estado.OCULTO;
    private SignoZodiaco signo;
    private TextureRegion region;
    private float x = 640, y = 360;
    private float scale = 0.3f;
    private float alpha = 1f;
    private float tiempo = 0f;
    private Runnable alTerminar;

    private static final float DURACION_ENTRADA = 0.35f;
    private static final float DURACION_QUEMA = 0.9f;

    public void abrir(SignoZodiaco signo, TextureRegion region, Runnable alTerminar) {
        this.signo = signo;
        this.region = region;
        this.alTerminar = alTerminar;
        this.estado = Estado.ENTRANDO;
        this.scale = 0.3f;
        this.alpha = 1f;
        this.tiempo = 0f;
        GestorSonidos s = Main.getInstance().getGestorSonidos();
        if (s != null) s.reproducirConVariacion("consumir_zodiaco");
    }

    public void update(float delta) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) return;
        tiempo += delta;
        if (estado == Estado.ENTRANDO) {
            float t = Math.min(tiempo / DURACION_ENTRADA, 1f);
            scale = 0.3f + 0.9f * t; // crece hasta 1.2 con overshoot simple
            if (t >= 1f) { estado = Estado.QUEMANDO; tiempo = 0f; }
        } else if (estado == Estado.QUEMANDO) {
            float t = Math.min(tiempo / DURACION_QUEMA, 1f);
            alpha = 1f - t;
            scale = 1.2f - 0.3f * t;
            if (t >= 1f) {
                estado = Estado.APLICANDO_EFECTO;
            }
        } else if (estado == Estado.APLICANDO_EFECTO) {
            estado = Estado.CERRADO;
        }
    }

    public boolean debeAplicarEfectoAhora() { return estado == Estado.APLICANDO_EFECTO; }
    public boolean estaCerrado() { return estado == Estado.CERRADO; }

    public void confirmarCierre() {
        estado = Estado.OCULTO;
        if (alTerminar != null) alTerminar.run();
        alTerminar = null;
    }

    public void render(SpriteBatch batch, Main game) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) return;
        Texture pixel = game.getPixelBlanco();
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixel, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, alpha);
        float size = 220 * scale;
        batch.draw(region, x - size / 2f, y - size / 2f, size, size);
        batch.setColor(1, 1, 1, 1);
    }
}
