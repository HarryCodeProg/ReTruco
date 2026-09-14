package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PanelUI {

    public static void dibujarFondoPanel(SpriteBatch batch, Texture pixel, float x, float y, float w, float h) {
        float radio = 12f;
        // SOMBRA EXTERIOR
        dibujarRectRedondeado(batch, pixel, x + 8f, y - 8f, w, h, radio + 2f, new Color(0.005f, 0.008f, 0.015f, 0.72f));
        dibujarRectRedondeado(batch, pixel, x + 3f, y - 3f, w, h, radio + 1f, new Color(0.015f, 0.022f, 0.035f, 0.75f));
        // BORDE EXTERIOR
        dibujarRectRedondeado(batch, pixel, x, y, w, h, radio, UITheme.BORDE);
        // CUERPO PRINCIPAL
        dibujarRectRedondeado(batch, pixel, x + 2f, y + 2f, w - 4f, h - 4f, radio - 2f, UITheme.PANEL_PRINCIPAL);
        // CAPA INTERIOR SUPERIOR
        dibujarRectRedondeado(batch, pixel, x + 6f, y + h * 0.42f, w - 12f, h * 0.52f, radio - 3f, new Color(0.075f, 0.105f, 0.155f, 0.32f));
        // LINEA SUPERIOR DORADA
        batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.85f);
        batch.draw(pixel, x + radio, y + h - 3f, w - radio * 2f, 2f);
        // Pequeño brillo encima
        batch.setColor(UITheme.DORADO_BRILLANTE.r, UITheme.DORADO_BRILLANTE.g, UITheme.DORADO_BRILLANTE.b, 0.22f);
        batch.draw(pixel, x + radio + 10f, y + h - 1f, w - radio * 2f - 20f, 1f);
        // BORDE INTERIOR
        batch.setColor(0.35f, 0.42f, 0.52f, 0.22f);
        batch.draw(pixel, x + 3f, y + 3f, w - 6f, 1f);
        batch.draw(pixel, x + 3f, y + 3f, 1f, h - 6f);
        batch.draw(pixel, x + w - 4f, y + 3f, 1f, h - 6f);
        batch.setColor(Color.WHITE);
    }

    public static void dibujarFondoFila(SpriteBatch batch, Texture pixel, float x, float y, float w, float h, Color acento) {
        float radio = 8f;
        // SOMBRA
        dibujarRectRedondeado(batch, pixel, x + 4f, y - 5f, w, h, radio + 1f, new Color(0.005f, 0.008f, 0.015f, 0.60f));
        // FONDO
        dibujarRectRedondeado(batch, pixel, x, y, w, h, radio, new Color(0.045f, 0.060f, 0.085f, 0.96f));
        // CAPA INTERIOR
        dibujarRectRedondeado(batch, pixel, x + 2f, y + 2f, w - 4f, h - 4f, radio - 2f, new Color(0.055f, 0.075f, 0.105f, 0.88f));
        // BRILLO DEL ACENTO
        batch.setColor(acento.r, acento.g, acento.b, 0.75f);
        batch.draw(pixel, x + radio, y + h - 2f, w - radio * 2f, 2f);
        // BRILLO LATERAL
        batch.setColor(acento.r, acento.g, acento.b, 0.22f);
        batch.draw(pixel, x + 1f, y + radio, 2f, h - radio * 2f);
        // BORDE SUTIL
        batch.setColor(0.30f, 0.38f, 0.48f, 0.30f);
        batch.draw(pixel, x + 3f, y + 2f, w - 6f, 1f);
        batch.draw(pixel, x + 3f, y + 2f, 1f, h - 4f);
        batch.draw(pixel, x + w - 4f, y + 2f, 1f, h - 4f);
        batch.setColor(Color.WHITE);
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
            float izquierda = radio - raiz;
            batch.draw(pixel, x + izquierda, y + i, width - izquierda * 2f, 1f);
            batch.draw(pixel, x + izquierda, y + height - i - 1f, width - izquierda * 2f, 1f);
        }
        batch.setColor(Color.WHITE);
    }
}
