package io.github.HarryCodeProg.TrucoSurvivors.Estados;

import com.badlogic.gdx.graphics.Color;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.UITheme;

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

    public Color getColor() {
        return color;
    }

    public static String colorearTexto(String texto) {
        if (texto == null) return "";
        // Usamos la versión que preserva el grupo 2 (las palabras)
        texto = reemplazarNumeroConPalabras(texto, MULTIPLICADOR, "(?i)([x×+]?\\s*\\d+(?:[.,]\\d+)?)\\s+(multiplicador(?:\\s+de)?\\s+(?:truco|envido))");
        texto = reemplazarNumeroConPalabras(texto, VALOR_TRUCO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+truco)");
        texto = reemplazarNumeroConPalabras(texto, PUNTOS_TRUCO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?truco)");
        texto = reemplazarNumeroConPalabras(texto, VALOR_ENVIDO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+envido)");
        texto = reemplazarNumeroConPalabras(texto, PUNTOS_ENVIDO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?envido)");

        // Para pesos, el patrón tiene un solo grupo (el $ y el número), así que usamos la función normal
        texto = reemplazarNumeroSimple(texto, PESOS, "(\\$\\s*\\d+(?:[.,]\\d+)?)");

        String patronNumero = "(?<![\\w#])\\d+(?:[.,]\\d+)?(?![\\w])";
        StringBuilder resultado = new StringBuilder();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\[#[0-9A-Fa-f]{6}\\].*?\\[\\])").matcher(texto);
        int ultimo = 0;
        while (matcher.find()) {
            String textoNormal = texto.substring(ultimo, matcher.start());
            resultado.append(textoNormal.replaceAll(patronNumero, NUMERO.colorHex() + "$0[]"));
            resultado.append(matcher.group());
            ultimo = matcher.end();
        }
        String textoFinal = texto.substring(ultimo);
        resultado.append(textoFinal.replaceAll(patronNumero, NUMERO.colorHex() + "$0[]"));
        return resultado.toString();
    }

    /** Preserva el grupo 2 (las palabras que acompañan al número). */
    private static String reemplazarNumeroConPalabras(String texto, ColorMecanica color, String patron) {
        return texto.replaceAll(patron, color.colorHex() + "$1[] $2");
    }

    /** Para patrones de un solo grupo (como PESOS). */
    private static String reemplazarNumeroSimple(String texto, ColorMecanica color, String patron) {
        return texto.replaceAll(patron, color.colorHex() + "$1[]");
    }

    private String colorHex() {
        return "[#" + color.toString().substring(0, 6) + "]";
    }
}
