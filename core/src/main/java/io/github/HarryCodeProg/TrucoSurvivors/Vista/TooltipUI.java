package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TooltipUI {

    private static final float RADIO_GENERAL = 12f;

    private TooltipUI() {}

    public static void dibujarFondo(SpriteBatch batch, Texture pixel, float x, float y, float w, float h, Color acento) {
        dibujarRectRedondeado(batch, pixel, x + 6f, y - 6f, w, h, RADIO_GENERAL, new Color(0f, 0f, 0f, 0.45f));
        dibujarRectRedondeado(batch, pixel, x - 2f, y - 2f, w + 4f, h + 4f, RADIO_GENERAL + 2f, new Color(0.95f, 0.95f, 0.95f, 1f));
        dibujarRectRedondeado(batch, pixel, x, y, w, h, RADIO_GENERAL, new Color(0.12f, 0.12f, 0.12f, 1f));
        float grosorNegro = 4f;
        dibujarRectRedondeado(batch, pixel, x + grosorNegro, y + grosorNegro, w - grosorNegro * 2f, h - grosorNegro * 2f, RADIO_GENERAL - 2f, new Color(0.28f, 0.31f, 0.34f, 1f));
        batch.setColor(1f, 1f, 1f, 0.12f);
        batch.draw(pixel, x + RADIO_GENERAL, y + h - grosorNegro - 3f, w - RADIO_GENERAL * 2f, 3f);
        batch.setColor(Color.WHITE);
    }

    public static void dibujarCajaBlancaInterna(SpriteBatch batch, Texture pixel, float x, float y, float w, float h) {
        float radioCaja = 8f;
        dibujarRectRedondeado(batch, pixel, x, y, w, h, radioCaja, new Color(0.12f, 0.12f, 0.12f, 1f));
        float grosor = 3f;
        dibujarRectRedondeado(batch, pixel, x + grosor, y + grosor, w - grosor * 2f, h - grosor * 2f, radioCaja - 1f, new Color(0.97f, 0.97f, 0.95f, 1f));
    }

    public static void dibujarTitulo(SpriteBatch batch, BitmapFont font, String texto, float x, float y, float anchoDisponible, Color color) {
        GlyphLayout layout = new GlyphLayout(font, texto, color, anchoDisponible, com.badlogic.gdx.utils.Align.center, true);
        float textX = x + (anchoDisponible - layout.width) / 2f;
        font.setColor(0.12f, 0.12f, 0.12f, 1f);
        font.draw(batch, texto, x, y - 2f, anchoDisponible, com.badlogic.gdx.utils.Align.center, true);
        font.setColor(color);
        font.draw(batch, texto, x, y, anchoDisponible, com.badlogic.gdx.utils.Align.center, true);
        font.setColor(Color.WHITE);
    }

    public static float dibujarBadge(SpriteBatch batch, BitmapFont font, Texture pixel, String texto, Color color,
                                     float centroX, float yTop, float paddingX, float paddingY) {
        GlyphLayout layout = new GlyphLayout(font, texto);
        float padX = paddingX + 8f;
        float textoAncho = layout.width;

        float badgeW = padX + textoAncho + padX;
        float badgeH = font.getCapHeight() + paddingY * 2f + 6f;
        float badgeX = centroX - badgeW / 2f;
        float badgeY = yTop - badgeH;
        float radioPildora = badgeH / 2f;

        // 1. Sombra exterior
        dibujarRectRedondeado(batch, pixel, badgeX + 2f, badgeY - 3f, badgeW, badgeH, radioPildora, new Color(0f, 0f, 0f, 0.40f));
        // 2. Borde negro grueso
        dibujarRectRedondeado(batch, pixel, badgeX, badgeY, badgeW, badgeH, radioPildora, new Color(0.12f, 0.12f, 0.12f, 1f));
        // 3. Fondo de color
        float grosor = 3f;
        dibujarRectRedondeado(batch, pixel, badgeX + grosor, badgeY + grosor, badgeW - grosor * 2f, badgeH - grosor * 2f, radioPildora - 1.5f, color);
        // 4. Highlight superior
        batch.setColor(1f, 1f, 1f, 0.25f);
        batch.draw(pixel, badgeX + radioPildora, badgeY + badgeH - grosor - 3f, badgeW - radioPildora * 2f, 3f);
        // 5. Sombra interior inferior
        batch.setColor(0f, 0f, 0f, 0.15f);
        batch.draw(pixel, badgeX + radioPildora, badgeY + grosor, badgeW - radioPildora * 2f, 3f);
        batch.setColor(Color.WHITE);

        // 6. Texto con sombra dura
        float cursorX = badgeX + padX;
        float textY = yTop - paddingY - 1f;
        font.setColor(0.12f, 0.12f, 0.12f, 0.75f);
        font.draw(batch, texto, cursorX, textY - 2f);
        font.setColor(Color.WHITE);
        font.draw(batch, texto, cursorX, textY);
        batch.setColor(Color.WHITE);
        return badgeY - 4f;
    }

    private static void dibujarRectRedondeado(SpriteBatch batch, Texture pixel, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        batch.setColor(color);
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);
        batch.draw(pixel, x, y + radio, radio, height - radio * 2f);
        batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);
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
