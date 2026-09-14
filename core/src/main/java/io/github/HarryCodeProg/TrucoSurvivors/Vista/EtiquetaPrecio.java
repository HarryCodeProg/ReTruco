package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class EtiquetaPrecio {

    public static void dibujar(SpriteBatch batch, Texture pixel, BitmapFont fuenteNumeros,
                               float centroX, float topeY, int precio) {
        String texto = "$" + precio;

        // Tamaños más compactos y delicados como en la referencia
        float etiqW = 46f;
        float etiqH = 24f;
        float etiqX = centroX - etiqW / 2f;

        // Centramos la etiqueta EXACTAMENTE sobre el borde superior de la carta (mitad arriba, mitad abajo)
        float etiqY = topeY - (etiqH / 2f);

        // === FONDO OSCURO ===
        // Un azul marino muy oscuro para contrastar limpio
        batch.setColor(0.08f, 0.11f, 0.15f, 1f);
        // Dibujamos el fondo como una cruz gruesa para dejar las 4 esquinas libres (redondeo)
        batch.draw(pixel, etiqX + 2f, etiqY, etiqW - 4f, etiqH);
        batch.draw(pixel, etiqX, etiqY + 2f, etiqW, etiqH - 4f);

        // === BORDE DORADO FINO Y ELEGANTE ===
        batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 1f);
        float g = 1.5f; // Grosor de la línea (1.5f da un trazo nítido pero visible)

        // Líneas rectas principales (Arriba, Abajo, Izquierda, Derecha)
        batch.draw(pixel, etiqX + 2f, etiqY + etiqH - g, etiqW - 4f, g); // Top
        batch.draw(pixel, etiqX + 2f, etiqY, etiqW - 4f, g);             // Bottom
        batch.draw(pixel, etiqX, etiqY + 2f, g, etiqH - 4f);             // Left
        batch.draw(pixel, etiqX + etiqW - g, etiqY + 2f, g, etiqH - 4f); // Right

        // Píxeles de las esquinas para suavizar el redondeo
        batch.draw(pixel, etiqX + 1f, etiqY + 1f, g, g); // Abajo-Izquierda
        batch.draw(pixel, etiqX + etiqW - g - 1f, etiqY + 1f, g, g); // Abajo-Derecha
        batch.draw(pixel, etiqX + 1f, etiqY + etiqH - g - 1f, g, g); // Arriba-Izquierda
        batch.draw(pixel, etiqX + etiqW - g - 1f, etiqY + etiqH - g - 1f, g, g); // Arriba-Derecha

        // === TEXTO BLANCO NÍTIDO ===
        // En la referencia el texto es blanco/crema claro, no dorado brillante
        fuenteNumeros.setColor(0.95f, 0.95f, 0.95f, 1f);
        fuenteNumeros.getData().setScale(0.85f); // Un poco más pequeño

        // Sutil sombra paralela para legibilidad (1px hacia abajo)
        batch.setColor(0f, 0f, 0f, 0.8f);
        fuenteNumeros.draw(batch, texto, etiqX, etiqY + etiqH - 6.5f, etiqW, Align.center, false);

        // Dibujar texto principal
        batch.setColor(1f, 1f, 1f, 1f);
        fuenteNumeros.draw(batch, texto, etiqX, etiqY + etiqH - 5.5f, etiqW, Align.center, false);

        // Restaurar estado
        fuenteNumeros.getData().setScale(1f);
        fuenteNumeros.setColor(1f, 1f, 1f, 1f);
    }
}
