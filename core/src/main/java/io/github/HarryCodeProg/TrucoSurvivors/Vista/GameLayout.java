package io.github.HarryCodeProg.TrucoSurvivors.Vista;

public final class GameLayout {
    public static final float Y_BOTONES = 20f;
    public static final float ALTO_BOTON = 50f;
    public static final float MARGEN_BOTONES_MANO = 45f;
    public static final float ALTO_CARTA = 180f;
    public static final float Y_MANO_JUGADOR = Y_BOTONES + ALTO_BOTON + MARGEN_BOTONES_MANO;
    public static final float TECHO_MANO_JUGADOR = Y_MANO_JUGADOR + ALTO_CARTA;
    public static final float MARGEN_MANO_MESA = 8f;
    public static final float ANCHO_CARTA_MESA = 60f;
    public static final float ALTO_CARTA_MESA = 90f;
    public static final float ESPACIO_ENTRE_MESA = 8f;
    public static final float Y_MESA_JUGADOR = TECHO_MANO_JUGADOR + MARGEN_MANO_MESA;
    public static final float Y_MESA_RIVAL = Y_MESA_JUGADOR + ALTO_CARTA_MESA + ESPACIO_ENTRE_MESA;
    public static final float TECHO_MESA = Y_MESA_RIVAL + ALTO_CARTA_MESA;
    public static final float MARGEN_MESA_MANO_RIVAL = 8f;
    public static final float ANCHO_CARTA_RIVAL = 70f;
    public static final float ALTO_CARTA_RIVAL = 80f;
    public static final float Y_MANO_RIVAL = TECHO_MESA + MARGEN_MESA_MANO_RIVAL;
    public static final float TECHO_MANO_RIVAL = Y_MANO_RIVAL + ALTO_CARTA_RIVAL;
    public static final float MARGEN_MANO_RIVAL_JOKERS = 8f;
    public static final float ANCHO_JOKER = 71f;
    public static final float ALTO_JOKER = 95f;
    public static final float SEPARACION_JOKER = 8f;
    public static final float Y_JOKERS = TECHO_MANO_RIVAL + MARGEN_MANO_RIVAL_JOKERS;
    public static final float PANEL_PUNTAJES_X = 20f;
    public static final float PANEL_PUNTAJES_Y = TECHO_MESA - 20f;
    public static final float Y_BOTONES_CANTOS = Y_BOTONES + ALTO_BOTON + 10f;

    private GameLayout() {}
}
