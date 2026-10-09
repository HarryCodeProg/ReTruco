package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

public class DatosGuardado {
    public String estadoPantalla; // "JUGANDO", "TIENDA", "SELECCION_RIVAL"
    public int rivalIndice;
    public int nivelActual;
    public DatosJugador jugador;
    public DatosJuego juego; // null si no está en JUGANDO
    public DatosEstadoTienda estadoTienda; // solo si estadoPantalla == "TIENDA"
}
