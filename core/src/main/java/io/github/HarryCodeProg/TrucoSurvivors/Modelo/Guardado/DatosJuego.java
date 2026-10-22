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
    public ArrayList<String> resultados = new ArrayList<>();
    public boolean manoFinalizada;
    public int tocoJugar;
    public boolean tieneEstadoCantos = false; // false = guardado viejo, no se toca nada
    public boolean turnoEsJugador = true;
    // 0 = nadie, 1 = jugador, 2 = rival
    public int nivelTrucoActual, ultimoCantorTruco, cantorTrucoPendiente, nivelTrucoPendiente;
    public ArrayList<String> cantosTruco = new ArrayList<>();
    public int nivelEnvidoActual, ultimoCantorEnvido, cantorEnvidoPendiente, nivelEnvidoPendiente;
    public ArrayList<String> cantosEnvido = new ArrayList<>();
    public double multTrucoJugador = 1, multEnvidoJugador = 1, multTrucoRival = 1, multEnvidoRival = 1;
    public int manosGanadasConsecutivas, envidosGanadosConsecutivos;
    public boolean primeraCartaQueMataAplicada, primeraCartaQueNoMataAplicada;
    public ArrayList<String> contadoresClaves = new ArrayList<>();
    public ArrayList<Double> contadoresValores = new ArrayList<>();
    public DatosMazo mazoRival; // el mazo del rival también se guarda (si no, vuelve a repartir cartas que ya salieron)
}
