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
}
