package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import com.badlogic.gdx.graphics.Color;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

public enum CategoriaJoker {
    ANIMAL(new Color(0.30f, 0.85f, 0.40f, 1f)),
    AMIGABLE(new Color(1.00f, 0.60f, 0.75f, 1f)),
    AGUA(new Color(0.20f, 0.60f, 1.00f, 1f)),
    COMIDA(new Color(0.90f, 0.55f, 0.20f, 1f)),
    DULCE(new Color(0.85f, 0.40f, 0.90f, 1f)),
    BEBIDA(new Color(0.40f, 0.90f, 0.90f, 1f)),
    AMARGO(new Color(0.50f, 0.40f, 0.30f, 1f)),
    TRADICIONAL(new Color(0.85f, 0.85f, 0.50f, 1f)),
    NACIONAL(new Color(0.35f, 0.70f, 0.95f, 1f)),
    INTERNACIONAL(new Color(0.60f, 0.45f, 0.90f, 1f)),
    ALCOHOL(new Color(0.60f, 0.18f, 0.25f, 1f)),
    MUSICA(new Color(0.85f, 0.30f, 0.70f, 1f)),
    DEPORTE(new Color(0.25f, 0.70f, 0.35f, 1f)),
    HISTORIA(new Color(0.70f, 0.50f, 0.25f, 1f)),
    OFICIO(new Color(0.45f, 0.48f, 0.55f, 1f)),
    CAMPO(new Color(0.42f, 0.65f, 0.28f, 1f)),
    CIUDAD(new Color(0.30f, 0.45f, 0.70f, 1f)),
    NATURALEZA(new Color(0.25f, 0.72f, 0.48f, 1f)),
    MASCOTA(new Color(0.95f, 0.62f, 0.30f, 1f)),
    TRANSPORTE(new Color(0.25f, 0.65f, 0.80f, 1f)),
    HERRAMIENTA(new Color(0.55f, 0.58f, 0.62f, 1f)),
    POSTRE(new Color(0.95f, 0.48f, 0.60f, 1f)),
    SECUENCIA(new Color(0.92f, 0.74f, 0.22f, 1f)),
    TV(new Color(0.50f, 0.35f, 0.85f, 1f)),
    SALADO(new Color(0.25f, 0.65f, 0.72f, 1f));

    private final Color color;

    CategoriaJoker(Color color) {
        this.color = color;
    }

    public String getTexto() {
        return Main.getTexto("categoria." + this.name());
    }

    public Color getColor() {
        return color;
    }
}
