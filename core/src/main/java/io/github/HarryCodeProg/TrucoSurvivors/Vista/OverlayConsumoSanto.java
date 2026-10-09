package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
    // Tiempos ajustados para dar lugar al rebote y al flash
    private static final float DURACION_ENTRADA = 0.45f;
    private static final float DURACION_QUEMA = 0.9f;
    private static final float DURACION_FLASH = 0.25f;
    private GestorShaderDisolucion gestorDisolucion = new GestorShaderDisolucion();
    private float progresoDisolucion = 0f;
    // Variables para físicas y jugosidad
    private float rotacion = 0f;
    private float offsetY = 0f;
    private float flashAlpha = 0f;

    public void abrir(Santo santo, TextureRegion region, Runnable alAplicar) {
        this.santo = santo;
        this.region = region;
        this.alAplicar = alAplicar;
        estado = Estado.ENTRANDO;
        scale = 0.3f;
        alpha = 1f;
        tiempo = 0f;
        offsetY = 0f;
        rotacion = -25f; // Inicia inclinado para enderezarse con el rebote
        flashAlpha = 0f;
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
        gestorDisolucion.update(delta);
        if (estado == Estado.ENTRANDO) {
            float t = Math.min(tiempo / DURACION_ENTRADA, 1f);
            // Animación de entrada con overshoot (rebote al pasarse del 100%)
            float ease = Interpolation.swingOut.apply(t);
            scale = 0.3f + 0.9f * ease;
            rotacion = (1f - ease) * -25f; // Gira suavemente hasta 0
            if (t >= 1f) {
                estado = Estado.QUEMANDO;
                tiempo = 0f;
                progresoDisolucion = 0f;
                rotacion = 0f;
            }
        } else if (estado == Estado.QUEMANDO) {
            float t = Math.min(tiempo / DURACION_QUEMA, 1f);
            progresoDisolucion = t; // El shader necesita avance lineal
            // Acelera la reducción al final simulando absorción
            float easeShrink = Interpolation.pow2In.apply(t);
            scale = 1.2f - (0.4f * easeShrink);
            // Deriva sutil hacia arriba (efecto humo)
            offsetY = easeShrink * 60f;
            // Vibración caótica aumentando con el tiempo
            rotacion = MathUtils.sin(tiempo * 50f) * (1f + easeShrink * 3f);
            if (t >= 1f) {
                estado = Estado.APLICANDO_EFECTO;
                tiempo = 0f;
                if (alAplicar != null) {
                    alAplicar.run(); // Se aplica inmediatamente al terminar de quemar
                    alAplicar = null;
                }
            }
        } else if (estado == Estado.APLICANDO_EFECTO) {
            // Flash bang blanco que se desvanece
            float t = Math.min(tiempo / DURACION_FLASH, 1f);
            flashAlpha = 1f - t;

            if (t >= 1f) {
                estado = Estado.CERRADO;
            }
        }
    }

    public void render(SpriteBatch batch, Main game) {
        if (estado == Estado.OCULTO || estado == Estado.CERRADO) {
            return;
        }
        Texture pixel = game.getPixelBlanco();
        // 1. Fondo oscurecido
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixel, 0, 0, 1280, 720);
        batch.setColor(1f, 1f, 1f, 1f);
        float drawY = y + offsetY;
        float size = 220 * scale;
        // Modificamos la matriz del batch para poder aplicar la rotación de la vibración
        // globalmente, incluso al gestorDisolucion que no soporta rotación nativa
        batch.flush();
        Matrix4 oldMatrix = batch.getTransformMatrix().cpy();
        Matrix4 newMatrix = oldMatrix.cpy();
        newMatrix.translate(x, drawY, 0);
        newMatrix.rotate(0, 0, 1, rotacion);
        newMatrix.translate(-x, -drawY, 0);
        batch.setTransformMatrix(newMatrix);
        if (estado == Estado.QUEMANDO) {
            // A. Halo pulsante dorado detrás del objeto
            float pulseAlpha = 0.4f + MathUtils.sin(tiempo * 20f) * 0.2f;
            batch.setColor(1f, 0.85f, 0.2f, pulseAlpha);
            float haloSize = size * 1.15f;
            batch.draw(region, x - haloSize / 2f, drawY - haloSize / 2f, haloSize, haloSize);
            batch.setColor(1f, 1f, 1f, 1f);
            // B. Santo quemándose
            batch.flush();
            gestorDisolucion.dibujarConDisolucion(batch, region,
                x - size / 2f, drawY - size / 2f, size, size,
                progresoDisolucion, false,
                new Color(1f, 0.55f, 0.1f, 1f),
                new Color(0.8f, 0.15f, 0.05f, 1f));

        } else if (estado == Estado.ENTRANDO) {
            batch.draw(region, x - size / 2f, drawY - size / 2f, size, size);
        }
        // Restaurar matriz obligatoriamente
        batch.flush();
        batch.setTransformMatrix(oldMatrix);
        // Flash blanco final por encima de todo
        if (estado == Estado.APLICANDO_EFECTO && flashAlpha > 0) {
            batch.setColor(1f, 1f, 1f, flashAlpha);
            batch.draw(pixel, 0, 0, 1280, 720);
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    public boolean estaActivo() {return estado != Estado.OCULTO && estado != Estado.CERRADO;}
    public boolean estaCerrado() {return estado == Estado.CERRADO;}
    public void confirmarCierre() {estado = Estado.OCULTO;}
}
