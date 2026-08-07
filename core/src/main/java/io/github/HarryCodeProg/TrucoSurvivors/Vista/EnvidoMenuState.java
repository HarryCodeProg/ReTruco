package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static io.github.HarryCodeProg.TrucoSurvivors.Vista.GameLayout.ALTO_BOTON;

public class EnvidoMenuState {
    private boolean abierto = false;

    public boolean isAbierto() {
        return abierto;
    }

    public void alternar() {
        abierto = !abierto;
    }

    public void cerrar() {
        abierto = false;
    }

    public boolean debeMostrarOpciones(boolean hayCantoPendiente) {
        return abierto || hayCantoPendiente;
    }

    public void renderFondo(SpriteBatch batch, Texture pixelBlanco, Boton botonOpciones, int botonesVisibles) {
        if (!abierto || pixelBlanco == null || botonesVisibles <= 0) return;
        float padding = 6f;
        float gapX = 8f;
        float anchoBoton = 120f;
        float xFondo = botonOpciones.getX() - padding;
        float yFondo = botonOpciones.getY() + ALTO_BOTON + (padding + 2f);
        float anchoFondo = (anchoBoton * botonesVisibles) + (gapX * (botonesVisibles - 1)) + (padding * 2f);
        float altoFondo = ALTO_BOTON + (padding * 2f);
        Color colorPrevio = batch.getColor();
        // Fondo semitransparente
        batch.setColor(0.08f, 0.08f, 0.12f, 0.92f);
        batch.draw(pixelBlanco, xFondo, yFondo, anchoFondo, altoFondo);
        // Borde
        batch.setColor(0.35f, 0.4f, 0.5f, 0.8f);
        float grosor = 1.5f;
        batch.draw(pixelBlanco, xFondo, yFondo, anchoFondo, grosor);
        batch.draw(pixelBlanco, xFondo, yFondo + altoFondo - grosor, anchoFondo, grosor);
        batch.draw(pixelBlanco, xFondo, yFondo, grosor, altoFondo);
        batch.draw(pixelBlanco, xFondo + anchoFondo - grosor, yFondo, grosor, altoFondo);
        batch.setColor(colorPrevio);
    }
}
