package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import com.badlogic.gdx.graphics.Color;
import io.github.HarryCodeProg.TrucoSurvivors.Main;

public enum CategoriaJoker {
    //                       color                                  fila mitad  umbrales
    TRUCO        (new Color(0.30f, 0.35f, 0.40f, 1f),   0,   0,  2, 4, 6),
    NACIONAL     (new Color(0.20f, 0.70f, 0.70f, 1f),   0,   1,  2, 4, 6, 8),

    ACTIVACION   (new Color(0.20f, 0.60f, 0.60f, 1f),   1,   0,  2, 4, 6),
    CULTURA      (new Color(0.55f, 0.20f, 0.30f, 1f),   1,   1,  2, 3, 4),

    ECONOMIA     (new Color(0.10f, 0.65f, 0.40f, 1f),   2,   0,  2, 4, 6),
    CONSUMIBLES  (new Color(0.85f, 0.50f, 0.55f, 1f),   2,   1,  2, 4, 6),

    GASTRONOMIA  (new Color(0.50f, 0.55f, 0.20f, 1f),   3,   0,  2, 4, 6),
    BEBIDA       (new Color(0.20f, 0.30f, 0.60f, 1f),   3,   1,  2, 4, 6),

    FAUNA        (new Color(0.60f, 0.55f, 0.40f, 1f),   4,   0,  2, 4, 6),
    TERRITORIO   (new Color(0.65f, 0.35f, 0.25f, 1f),   4,   1,  2, 4, 6),

    ENVIDO       (new Color(0.15f, 0.50f, 0.30f, 1f),   5,   0,  2, 4, 6),
    PERSONA      (new Color(0.75f, 0.65f, 0.55f, 1f),   5,   1,  2, 4, 6),

    RACHAS       (new Color(0.60f, 0.80f, 0.20f, 1f),   6,   0,  2, 4, 6),
    OBJETO       (new Color(0.50f, 0.55f, 0.60f, 1f),   6,   1,  2, 3, 5),

    MAZO         (new Color(0.50f, 0.35f, 0.25f, 1f),   7,   0,  2, 4, 6),
    FIGURA       (new Color(0.30f, 0.20f, 0.50f, 1f),   7,   1,  2, 4, 6),

    AZAR         (new Color(0.40f, 0.80f, 0.60f, 1f),   8,   0,  2, 3, 5),
    DEPORTE      (new Color(0.45f, 0.35f, 0.20f, 1f),   8,   1,  2),

    // Sin slot propio en el sheet: reutiliza el icono de DEPORTE (pesa).
    // Si preferís sin icono, poné (-1, -1) y AssetsRasgos lo saltea.
    TRANSPORTE   (new Color(0.40f, 0.50f, 0.65f, 1f),   8,   1,  1);

    private final Color color;
    private final int fila;
    private final int mitad;
    private final int[] umbrales;

    CategoriaJoker(Color color, int fila, int mitad, int... umbrales) {
        this.color = color;
        this.fila = fila;
        this.mitad = mitad;
        this.umbrales = umbrales;
    }

    public String getTexto() { return Main.getTexto("categoria." + this.name()); }
    public Color getColor()  { return color; }
    public int getFila()     { return fila; }
    public int getMitad()    { return mitad; }
    public int[] getUmbrales() { return umbrales.clone(); }
    public int getMaxTier()  { return umbrales.length; }

    public int getTier(int cantidad) {
        int tier = 0;
        for (int i = 0; i < umbrales.length; i++) {
            if (cantidad >= umbrales[i]) tier = i + 1;
            else break;
        }
        return tier;
    }

    public int getCantidadParaTier(int tier) {
        if (tier < 1 || tier > umbrales.length) return -1;
        return umbrales[tier - 1];
    }

    public boolean tieneIcono() { return fila >= 0 && mitad >= 0; }

    /**
     * Color del fondo del badge según el tier.
     *   tier 0 = inactivo, 1 = bronce, 2 = plata, 3 = oro, 4 = prismático.
     */
    public static Color getColorTier(int tier) {
        switch (tier) {
            case 0:  return new Color(0.08f, 0.08f, 0.10f, 0.75f); // negro transparente
            case 1:  return new Color(0.62f, 0.42f, 0.22f, 1f);    // marrón claro (bronce)
            case 2:  return new Color(0.78f, 0.80f, 0.84f, 1f);    // plateado
            case 3:  return new Color(0.88f, 0.68f, 0.18f, 1f);    // amarillo oscuro (oro)
            case 4:  return new Color(0.85f, 0.90f, 1.00f, 1f);    // blanco azulado (prismático)
            default: return Color.DARK_GRAY;
        }
    }
}
