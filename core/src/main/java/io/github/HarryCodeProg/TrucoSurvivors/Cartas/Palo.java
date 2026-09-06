package io.github.HarryCodeProg.TrucoSurvivors.Cartas;

public enum Palo {
    ESPADA("[#2D9CDB]"), // Celeste / Azul claro
    BASTO("[#27AE60]"),  // Verde oscuro / esmeralda
    COPA("[#B87333]"),   // Marrón madera / cobrizo
    ORO("[#F2C94C]");    // Amarillo mostaza suave

    private final String colorHex;

    Palo(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getColorHex() {
        return colorHex;
    }

    /**
     * Busca los nombres de los palos en cualquier texto y les inyecta su color correspondiente.
     * El "[]" al final resetea el color al que tenía antes.
     */
    public static String colorearTexto(String texto) {
        if (texto == null) return "";
        texto = texto.replaceAll("(?i)\\b(oro|oros)\\b", ORO.colorHex + "$1[]");
        texto = texto.replaceAll("(?i)\\b(basto|bastos)\\b", BASTO.colorHex + "$1[]");
        texto = texto.replaceAll("(?i)\\b(copa|copas)\\b", COPA.colorHex + "$1[]");
        texto = texto.replaceAll("(?i)\\b(espada|espadas)\\b", ESPADA.colorHex + "$1[]");
        return texto;
    }
}
