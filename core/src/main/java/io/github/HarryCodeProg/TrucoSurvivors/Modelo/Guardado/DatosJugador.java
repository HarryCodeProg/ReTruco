package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

import java.util.ArrayList;

public class DatosJugador {
    public int pesos;
    public int tamañoMano;
    public int tamañoJokers;
    public int descartesExtra;
    public int tamañoManoExtra;
    public int manosMaximas;
    public int manosActuales;
    public int descartesBaseActuales;
    public double multiplicadorTruco;
    public double multiplicadorEnvido;
    public double totalPesosGanados;
    public double totalPesosGastados;
    public DatosMazo mazo;
    public ArrayList<DatosCarta> mano = new ArrayList<>();
    public ArrayList<DatosJoker> jokers = new ArrayList<>();
    public ArrayList<DatosSanto> santos = new ArrayList<>();
    // --- Extras (persistencia de efectos de zodiaco, tienda y rasgos) ---
    public boolean tieneExtras = false; // false = guardado viejo, no se toca nada
    public double multiplicadorTrucoBase = 1;
    public double multiplicadorEnvidoBase = 1;
    public int espacioSantosExtra;
    public int deudaMaxima;
    public int topeInteresExtra;
    public double bonusEnvidoFinal;
    public int multiplicadorZodiaco = 1;
    public String proximoZodiacoForzado;
    public int rerollsTienda;
    public int rerollsGratisTienda;
    public int espacioJokersTiendaExtra;
    public int espacioCartasTiendaExtra;
    public int espacioSantosTiendaExtra;
    public double multiplicadorPrecioTienda = 1.0;
    public java.util.ArrayList<String> acumuladosClaves = new java.util.ArrayList<>();
    public java.util.ArrayList<Double> acumuladosValores = new java.util.ArrayList<>();
    public int ultimoSantoUsadoId;
}
