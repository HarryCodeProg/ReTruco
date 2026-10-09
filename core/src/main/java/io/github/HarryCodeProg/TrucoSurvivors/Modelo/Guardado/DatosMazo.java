package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

import java.util.ArrayList;

public class DatosMazo {
    public ArrayList<DatosCarta> mazo = new ArrayList<>();
    public ArrayList<DatosCarta> cartasTomadas = new ArrayList<>();
    public ArrayList<DatosCarta> cartasDescartadas = new ArrayList<>();
    public int tamañoMazo;
}
