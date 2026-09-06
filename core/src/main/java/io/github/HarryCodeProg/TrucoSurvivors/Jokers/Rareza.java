package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import com.badlogic.gdx.graphics.Color;

public enum Rareza {
    comun(new Color(0.4f, 0.7f, 1f, 1f)),
    raro(new Color(1f, 0.25f, 0.25f, 1f)),
    muyRaro(new Color(1f, 0.6f, 0.1f, 1f)),
    epico(new Color(0.75f, 0.3f, 0.9f, 1f)),
    legendario(new Color(1f, 0.78f, 0.15f, 1f));

    private final Color color;

    Rareza(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Color getColorCopy() {
        return new Color(color);
    }

    public String getColorHex() {
        Color c = color;
        return String.format("#%02X%02X%02X", (int)(c.r * 255), (int)(c.g * 255), (int)(c.b * 255));
    }
}
