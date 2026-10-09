package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

import java.util.ArrayList;

public class DatosJuego {
    public double puntosJugador;
    public double puntosRival;
    public double puntajeMeta;
    public boolean jugadorEsMano;
    public int faseActual;
    public int rondaActual;
    public int descartesActuales;
    public ArrayList<DatosCarta> manoRival = new ArrayList<>();
    public ArrayList<DatosCarta> mesaJugador = new ArrayList<>();
    public ArrayList<DatosCarta> mesaRival = new ArrayList<>();
    public int cartasJugadasTotal;
    public int cartasDescartadasTotal;
    public int cartasCompradasTotal;
    public int renovacionesTotal;
}
