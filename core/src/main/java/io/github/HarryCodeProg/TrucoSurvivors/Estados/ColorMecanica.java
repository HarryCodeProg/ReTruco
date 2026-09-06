package io.github.HarryCodeProg.TrucoSurvivors.Estados;

import com.badlogic.gdx.graphics.Color;

/** Colores visuales compartidos para valores de juego mostrados con markup de LibGDX. */
public enum ColorMecanica {
    MULTIPLICADOR("[#FF5A5F]"), // rojo: refuerza el multiplicador y dialoga con las cajas del HUD
    VALOR_TRUCO("[#38BDF8]"), // azul claro: valor base de truco
    PUNTOS_TRUCO("[#2DD4BF]"), // turquesa: puntos aportados de truco
    VALOR_ENVIDO("[#F7C948]"), // dorado: valor base de envido
    PUNTOS_ENVIDO("[#E89B5A]"); // bronce: puntos aportados de envido

    private final String colorHex;

    ColorMecanica(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getColorHex() {
        return colorHex;
    }

    public Color getColor() {
        return Color.valueOf(colorHex.substring(2, colorHex.length() - 1));
    }

    public String colorear(String texto) {
        return colorHex + texto + "[]";
    }

    /**
     * Colorea valores escritos en descripciones, por ejemplo "x2 multiplicador truco"
     * o "+10 puntos envido". Requiere markupEnabled en la BitmapFont que lo dibuje.
     */
    public static String colorearTexto(String texto) {
        if (texto == null) return "";
        texto = reemplazarNumero(texto, MULTIPLICADOR,
            "(?i)([x×+]?\\s*\\d+(?:[.,]\\d+)?)\\s+(multiplicador(?:\\s+de)?\\s+(?:truco|envido))");
        texto = reemplazarNumero(texto, VALOR_TRUCO,
            "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+truco)");
        texto = reemplazarNumero(texto, PUNTOS_TRUCO,
            "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?truco)");
        texto = reemplazarNumero(texto, VALOR_ENVIDO,
            "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+envido)");
        return reemplazarNumero(texto, PUNTOS_ENVIDO,
            "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?envido)");
    }

    private static String reemplazarNumero(String texto, ColorMecanica color, String patron) {
        return texto.replaceAll(patron, color.colorHex + "$1[] $2");
    }
}

