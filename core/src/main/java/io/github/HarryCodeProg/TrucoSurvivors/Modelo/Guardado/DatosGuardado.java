package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

public class DatosGuardado {
    public static final int VERSION_ACTUAL = 2;
    public int version = VERSION_ACTUAL;
    public String estadoPantalla;
    public int rivalIndice;
    public int rivalDesbloqueadoIndice = -1; // -1 = guardado viejo
    public int nivelActual;
    public DatosJugador jugador;
    public DatosJuego juego;
    public DatosEstadoTienda estadoTienda;
    public int nivelInfinito = 0;
}
