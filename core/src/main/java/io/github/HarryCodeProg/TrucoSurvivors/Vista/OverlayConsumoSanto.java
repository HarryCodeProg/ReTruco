package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorShaderDisolucion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

public class OverlayConsumoSanto {

    public enum Estado { OCULTO, ENTRANDO, QUEMANDO, CERRADO }

    private Estado estado = Estado.OCULTO;
    private Santo santo;
    private TextureRegion region;
    private float x, y, w, h;
    private float tiempo = 0f;
    private Runnable alAplicar;

    private static final float DURACION_ENTRADA = 0.22f;
    private static final float DURACION_QUEMA   = 0.85f;

    private final GestorShaderDisolucion gestorDisolucion = new GestorShaderDisolucion();
    private float progresoDisolucion = 0f;
    private float rotacion = 0f;
    private float scale = 1f;

    public void abrir(Santo santo, TextureRegion region,
                      float x, float y, float w, float h,
                      Runnable alAplicar) {
        this.santo = santo;
        this.region = region;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.alAplicar = alAplicar;
        estado = Estado.ENTRANDO;
        tiempo = 0f;
        progresoDisolucion = 0f;
        rotacion = -10f;
        scale = 1f;
        GestorSonidos s = Main.getInstance().getGestorSonidos();
        if (s != null) s.reproducirConVariacion("consumir_zodiaco");
    }

    public void update(float delta) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) return;

        tiempo += delta;
        gestorDisolucion.update(delta);

        if (estado == Estado.ENTRANDO) {
            float t = Math.min(tiempo / DURACION_ENTRADA, 1f);
            float ease = Interpolation.swingOut.apply(t);
            scale = 1f + 0.15f * ease;
            rotacion = -10f * (1f - ease);
            if (t >= 1f) {
                estado = Estado.QUEMANDO;
                tiempo = 0f;
                rotacion = 0f;
                scale = 1f;
            }
        } else if (estado == Estado.QUEMANDO) {
            float t = Math.min(tiempo / DURACION_QUEMA, 1f);
            progresoDisolucion = t;
            // Vibración que crece con la disolución
            rotacion = MathUtils.sin(tiempo * 38f) * (1.8f * t);
            // Micro-expansión durante la quema
            scale = 1f + 0.05f * t;
            if (t >= 1f) {
                if (alAplicar != null) { alAplicar.run(); alAplicar = null; }
                estado = Estado.CERRADO;
            }
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) return;
        if (region == null) return;

        float sizeW = w * scale;
        float sizeH = h * scale;
        float drawX = x + (w - sizeW) / 2f;
        float drawY = y + (h - sizeH) / 2f;

        // Rotación sobre el centro de la carta
        batch.flush();
        Matrix4 oldMatrix = batch.getTransformMatrix().cpy();
        Matrix4 newMatrix = oldMatrix.cpy();
        float cx = x + w / 2f;
        float cy = y + h / 2f;
        newMatrix.translate(cx, cy, 0);
        newMatrix.rotate(0, 0, 1, rotacion);
        newMatrix.translate(-cx, -cy, 0);
        batch.setTransformMatrix(newMatrix);

        if (estado == Estado.QUEMANDO) {
            // Halo dorado pulsante detrás de la carta que se quema
            float pulse = 0.30f + MathUtils.sin(tiempo * 18f) * 0.12f;
            float haloAlpha = pulse * (1f - progresoDisolucion);
            float haloW = sizeW * 1.25f;
            float haloH = sizeH * 1.25f;
            batch.setColor(1f, 0.8f, 0.2f, haloAlpha);
            batch.draw(region,
                drawX - (haloW - sizeW) / 2f,
                drawY - (haloH - sizeH) / 2f,
                haloW, haloH);
            batch.setColor(1f, 1f, 1f, 1f);

            gestorDisolucion.dibujarConDisolucion(batch, region,
                drawX, drawY, sizeW, sizeH,
                progresoDisolucion, false,
                new Color(1f, 0.55f, 0.1f, 1f),
                new Color(0.8f, 0.15f, 0.05f, 1f));
        } else {
            // ENTRANDO
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(region, drawX, drawY, sizeW, sizeH);
        }

        batch.flush();
        batch.setTransformMatrix(oldMatrix);
    }

    public boolean estaActivo() { return estado != Estado.OCULTO && estado != Estado.CERRADO; }
    public void confirmarCierre() { estado = Estado.OCULTO; }
}
