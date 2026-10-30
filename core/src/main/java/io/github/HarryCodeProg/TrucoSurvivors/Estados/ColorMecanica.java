package io.github.HarryCodeProg.TrucoSurvivors.Estados;

import com.badlogic.gdx.graphics.Color;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.UITheme;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ColorMecanica {
    MULTIPLICADOR(UITheme.ROJO),
    VALOR_TRUCO(UITheme.TRUCO),
    PUNTOS_TRUCO(UITheme.TURQUESA),
    VALOR_ENVIDO(UITheme.ENVIDO),
    PUNTOS_ENVIDO(UITheme.ENVIDO),
    PESOS(UITheme.DORADO),
    NUMERO(UITheme.VIOLETA),
    PARENTESIS(UITheme.ROJO),
    PUNTOS(UITheme.NARANJA);

    private final Color color;

    ColorMecanica(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static String colorearTexto(String texto) {
        if (texto == null) return "";

        // 1) Primero marcamos SOLO los números/símbolos dentro de (...)
        texto = colorearParentesis(texto);

        // 2) Luego los patrones específicos (con protección para no meterse
        //    dentro de los bloques [#hex]...[] que acabamos de crear)
        //texto = reemplazarNumeroConPalabras(texto, MULTIPLICADOR, "(?i)([x×+]?\\s*\\d+(?:[.,]\\d+)?)\\s+(multiplicador(?:\\s+de)?\\s+(?:truco|envido))");
        texto = reemplazarNumeroConPalabras(texto, MULTIPLICADOR, "(?i)([x×+]\\s*\\d+(?:[.,]\\d+)?)\\s+(multiplicador)");
        texto = reemplazarNumeroConPalabras(texto, VALOR_TRUCO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+truco)");
        texto = reemplazarNumeroConPalabras(texto, PUNTOS_TRUCO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?truco)");
        texto = reemplazarNumeroConPalabras(texto, VALOR_ENVIDO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(valor(?:\\s+de)?\\s+envido)");
        texto = reemplazarNumeroConPalabras(texto, PUNTOS_ENVIDO, "(?i)([+−-]?\\s*\\d+(?:[.,]\\d+)?)\\s+(puntos?\\s+(?:de\\s+)?envido)");

        texto = reemplazarNumeroSimple(texto, PESOS, "(\\$\\s*\\d+(?:[.,]\\d+)?)");

        // 3) Al final, colorear los números "sueltos" en violeta (NUMERO),
        //    respetando los bloques de markup ya existentes
        String patronNumero = "(?<![\\w#])\\d+(?:[.,]\\d+)?(?![\\w])";
        StringBuilder resultado = new StringBuilder();
        Matcher matcher = Pattern.compile("(\\[#[0-9A-Fa-f]{6}\\].*?\\[\\])").matcher(texto);
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

    private static String colorearParentesis(String texto) {
        Pattern p = Pattern.compile("\\(([^()]*)\\)");
        Matcher m = p.matcher(texto);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String contenido = m.group(1);
            if (contenido.contains("[#")) {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group()));
                continue;
            }
            if (!contenido.matches(".*[\\dxX+×].*")) {
                m.appendReplacement(sb, Matcher.quoteReplacement(m.group()));
                continue;
            }
            String coloreado = contenido.replaceAll(
                "(?<![\\w#$/])([+\\-−xX×]?\\s*\\d+(?:[.,]\\d+)?)(?![\\w/])",
                PARENTESIS.colorHex() + "$1[]"
            );
            m.appendReplacement(sb, Matcher.quoteReplacement("(" + coloreado + ")"));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** Preserva el grupo 2, sin tocar contenido que ya está dentro de un bloque [#hex]...[] */
    private static String reemplazarNumeroConPalabras(String texto, ColorMecanica color, String patron) {
        return aplicarPatronSinMarcado(texto, patron, color.colorHex() + "$1[] $2");
    }

    /** Para patrones de un solo grupo (como PESOS), idem protección. */
    private static String reemplazarNumeroSimple(String texto, ColorMecanica color, String patron) {
        return aplicarPatronSinMarcado(texto, patron, color.colorHex() + "$1[]");
    }

    /** Aplica un replaceAll solo al texto que NO está dentro de un bloque [#hex]...[] */
    private static String aplicarPatronSinMarcado(String texto, String patron, String reemplazo) {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile("(\\[#[0-9A-Fa-f]{6}\\].*?\\[\\])").matcher(texto);
        int ultimo = 0;
        while (matcher.find()) {
            sb.append(texto.substring(ultimo, matcher.start()).replaceAll(patron, reemplazo));
            sb.append(matcher.group());
            ultimo = matcher.end();
        }
        sb.append(texto.substring(ultimo).replaceAll(patron, reemplazo));
        return sb.toString();
    }

    public String colorHex() {
        return "[#" + color.toString().substring(0, 6) + "]";
    }
}
