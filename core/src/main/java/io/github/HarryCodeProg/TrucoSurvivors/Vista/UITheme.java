package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;

public class UITheme {
    public static final Color PANEL_PRINCIPAL = new Color(0.110f, 0.118f, 0.169f, 1f); // #1C1E2B
    public static final Color PANEL_SECUNDARIO = new Color(0.165f, 0.176f, 0.243f, 1f); // #2A2D3E
    public static final Color BORDE = new Color(0.043f, 0.043f, 0.059f, 1f); // #0B0B0F
    public static final Color TEXTO_PRINCIPAL = new Color(0.961f, 0.961f, 0.961f, 1f); // #F5F5F5
    public static final Color TEXTO_SECUNDARIO = new Color(0.608f, 0.639f, 0.722f, 1f); // #9BA3B8
    public static final Color NARANJA = new Color(1.0f, 0.624f, 0.110f, 1f); // #FF9F1C
    public static final Color TURQUESA = new Color(0.180f, 0.769f, 0.714f, 1f); // #2EC4B6
    public static final Color ENVIDO = new Color(0.298f, 0.788f, 0.941f, 1f); // #4CC9F0
    public static final Color ROJO = new Color(0.902f, 0.224f, 0.275f, 1f); // #E63946
    public static final Color DORADO = new Color(1.0f, 0.839f, 0.039f, 1f); // #FFD60A
    public static final Color TRUCO = new Color(0.502f, 0.929f, 0.600f, 1f); // #80ED99
    public static final Color VIOLETA = new Color(0.608f, 0.365f, 0.898f, 1f); // #9B5DE5
    public static final Color AZUL = new Color(0.227f, 0.525f, 1.0f, 1f); // #3A86FF
    public static final Color FONDO_GENERAL = new Color(0.043f, 0.067f, 0.102f, 1f);
    public static final Color AZUL_HOVER = new Color(0.204f, 0.467f, 0.816f, 1f);
    public static final Color DORADO_BRILLANTE = new Color(0.953f, 0.827f, 0.416f, 1f);
    public static final Color VERDE = new Color(0.125f, 0.722f, 0.616f, 1f);
    public static final Color VERDE_HOVER = new Color(0.208f, 0.816f, 0.702f, 1f);
    public static final Color ROJO_SECCION = new Color(0.788f, 0.290f, 0.286f, 1f);
    // Rareza (mantiene coherencia con los colores usados en el mockup)
    public static final Color RAREZA_COMUN = new Color(0.45f, 0.48f, 0.55f, 1f);
    public static final Color RAREZA_RARO = new Color(0.24f, 0.49f, 0.78f, 1f);
    public static final Color RAREZA_MUY_RARO = new Color(0.28f, 0.72f, 0.82f, 1f);
    public static final Color RAREZA_EPICO = new Color(0.63f, 0.37f, 0.88f, 1f);
    public static final Color RAREZA_LEGENDARIO = new Color(0.91f, 0.77f, 0.42f, 1f);
    public static final Color BOTON_DESHABILITADO = new Color(0.22f, 0.24f, 0.28f, 1f);
    public static final Color BOTON_DESHABILITADO_BORDE = new Color(0.12f, 0.13f, 0.16f, 1f);
    public static final Color BOTON_DESHABILITADO_TEXTO = new Color(0.48f, 0.50f, 0.54f, 1f);
    public static final Color SOMBRA = new Color(0.01f, 0.015f, 0.025f, 0.70f);
    public static final Color SOMBRA_SUAVE = new Color(0f, 0f, 0f, 0.16f);
    public static final Color BRILLO = new Color(1f, 1f, 1f, 1f);

    public static Color porNombreRareza(String nombreRarezaLower) {
        if (nombreRarezaLower == null) return RAREZA_COMUN;
        String n = nombreRarezaLower
            .toLowerCase()
            .replace("_", "")
            .replace(" ", "")
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u");
        if (n.contains("legendario")) return RAREZA_LEGENDARIO;
        if (n.contains("epico")) return RAREZA_EPICO;
        if (n.contains("muyraro")) return RAREZA_MUY_RARO;
        if (n.equals("raro") || n.contains("raro")) return RAREZA_RARO;

        return RAREZA_COMUN;
    }
}
