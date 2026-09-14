package io.github.HarryCodeProg.TrucoSurvivors.Estados;

import com.badlogic.gdx.graphics.Color;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.UITheme;

/** Colores visuales compartidos para valores de juego mostrados con markup de LibGDX. */
public enum ColorMecanica {
    MULTIPLICADOR(UITheme.ROJO),
    VALOR_TRUCO(UITheme.TRUCO),
    PUNTOS_TRUCO(UITheme.TURQUESA),
    VALOR_ENVIDO(UITheme.ENVIDO),
    PUNTOS_ENVIDO(UITheme.AZUL),
    PESOS(UITheme.DORADO),
    NUMERO(UITheme.VIOLETA);

    private final Color color;

    ColorMecanica(Color color) {
        this.color = color;
    }

    public String getColorHex() {
        return color.toString();
    }

    public Color getColor() {
        return color;
    }

    public String colorear(String texto) {
        return "[#" + color.toString().substring(0, 6) + "]" + texto + "[]";
    }

    /**
     * Colorea valores escritos en descripciones.
     *
     * Ejemplos:
     * x2 multiplicador truco
     * +10 puntos envido
     * $50
     * selecciona 2 cartas
     */
    public static String colorearTexto(String texto) {
        if (texto == null) return "";
        texto = reemplazarNumero(texto, MULTIPLICADOR, "(?i)([x×+]?\\s*\\d+(?:[.,]\\d+)?)\\s+(multiplicador(?:\\s+de)?\\s+(?:truco|envido))");
        texto = reemplazarNumero(texto, VALOR_TRUCO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+truco)");
        texto = reemplazarNumero(texto, PUNTOS_TRUCO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?truco)");
        texto = reemplazarNumero(texto, VALOR_ENVIDO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+envido)");
        texto = reemplazarNumero(texto, PUNTOS_ENVIDO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?envido)");
        texto = reemplazarNumero(texto, PESOS, "(\\$\\s*\\d+(?:[.,]\\d+)?)");
        StringBuilder resultado = new StringBuilder();
        String[] partes = texto.split("(\\[#[0-9A-Fa-f]{6}\\].*?\\[\\])", -1);
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\[#[0-9A-Fa-f]{6}\\].*?\\[\\])").matcher(texto);
        int ultimo = 0;
        while (matcher.find()) {
            String textoNormal = texto.substring(ultimo, matcher.start());
            resultado.append(textoNormal.replaceAll("(?<![\\w#])\\d+(?![\\w])", NUMERO.colorHex() + "$0[]"));
            resultado.append(matcher.group());
            ultimo = matcher.end();
        }
        String textoFinal = texto.substring(ultimo);
        resultado.append(textoFinal.replaceAll("(?<![\\w#])\\d+(?![\\w])", NUMERO.colorHex() + "$0[]"));
        return resultado.toString();
    }

    private static String reemplazarNumero(String texto, ColorMecanica color, String patron) {
        return texto.replaceAll(patron, color.colorHex() + "$1[]");
    }

    private String colorHex() {
        return "[#" + color.toString().substring(0, 6) + "]";
    }
}
