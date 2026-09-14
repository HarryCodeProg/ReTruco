package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MarcoRareza {

    public static void dibujar(SpriteBatch batch, Texture pixel, float x, float y, float w, float h, Color colorRareza, boolean esRara) {
        float margen = 4f;

        float mx = x - margen;
        float my = y - margen;
        float mw = w + margen * 2f;
        float mh = h + margen * 2f;

        float radio = 8f;

        // =========================
        // COMUN
        // =========================
        if (!esRara) {
            dibujarRectRedondeado(batch, pixel, mx, my, mw, mh, radio, new Color(colorRareza.r * 0.65f, colorRareza.g * 0.65f, colorRareza.b * 0.65f, 0.95f));

            batch.setColor(colorRareza.r, colorRareza.g, colorRareza.b, 0.55f);

            dibujarLineaSuperior(batch, pixel, mx, my, mw, radio, 1.5f);

            batch.setColor(Color.WHITE);
            return;
        }

        // =========================
        // GLOW EXTERIOR
        // =========================
        batch.setColor(colorRareza.r, colorRareza.g, colorRareza.b, 0.06f);

        dibujarRectRedondeado(batch, pixel, mx - 12f, my - 12f, mw + 24f, mh + 24f, radio + 5f, new Color(colorRareza.r, colorRareza.g, colorRareza.b, 0.06f));

        batch.setColor(colorRareza.r, colorRareza.g, colorRareza.b, 0.10f);

        dibujarRectRedondeado(batch, pixel, mx - 7f, my - 7f, mw + 14f, mh + 14f, radio + 3f, new Color(colorRareza.r, colorRareza.g, colorRareza.b, 0.10f));

        // =========================
        // BORDE OSCURO
        // =========================
        dibujarRectRedondeado(batch, pixel, mx, my, mw, mh, radio, new Color(colorRareza.r * 0.35f, colorRareza.g * 0.35f, colorRareza.b * 0.35f, 1f));

        // =========================
        // BORDE PRINCIPAL
        // =========================
        dibujarRectRedondeado(batch, pixel, mx + 2f, my + 2f, mw - 4f, mh - 4f, radio - 2f, new Color(colorRareza.r, colorRareza.g, colorRareza.b, 0.95f));

        // =========================
        // BORDE INTERIOR OSCURO
        // =========================
        dibujarRectRedondeado(batch, pixel, mx + 4f, my + 4f, mw - 8f, mh - 8f, radio - 3f, new Color(colorRareza.r * 0.25f, colorRareza.g * 0.25f, colorRareza.b * 0.25f, 0.90f));

        // =========================
        // HIGHLIGHT SUPERIOR
        // =========================
        batch.setColor(colorRareza.r * 1.15f, colorRareza.g * 1.15f, colorRareza.b * 1.15f, 0.85f);

        dibujarLineaSuperior(batch, pixel, mx + 4f, my + 4f, mw - 8f, radio - 2f, 2f);

        // =========================
        // BRILLO LATERAL
        // =========================
        batch.setColor(colorRareza.r, colorRareza.g, colorRareza.b, 0.42f);

        batch.draw(pixel, mx + 3f, my + radio, 2f, mh - radio * 2f);

        // =========================
        // RESTAURAR
        // =========================
        batch.setColor(Color.WHITE);
    }

    private static void dibujarLineaSuperior(SpriteBatch batch, Texture pixel, float x, float y, float w, float radio, float grosor) {
        batch.draw(pixel, x + radio, y + radio, w - radio * 2f, grosor);
    }

    private static void dibujarRectRedondeado(SpriteBatch batch, Texture pixel, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;

        radio = Math.min(radio, Math.min(width, height) / 2f);

        batch.setColor(color);

        // Centro
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);

        // Laterales
        batch.draw(pixel, x, y + radio, radio, height - radio * 2f);

        batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);

        // Curvas
        int pasos = Math.max(2, (int) radio);

        for (int i = 0; i < pasos; i++) {

            float dy = i + 0.5f;
            float distancia = radio - dy;

            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));

            float inset = radio - raiz;

            batch.draw(pixel, x + inset, y + i, width - inset * 2f, 1f);

            batch.draw(pixel, x + inset, y + height - i - 1f, width - inset * 2f, 1f);
        }

        batch.setColor(Color.WHITE);
    }
}
