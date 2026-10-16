package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class EfectoTrucoMultiplicador implements EfectoRasgo {
    private final double multiplicador;
    public EfectoTrucoMultiplicador(double multiplicador) { this.multiplicador = multiplicador; }
    public double getMultiplicador() { return multiplicador; }
    @Override public void onActivar(Juego juego) {}
    @Override public void onDesactivar(Juego juego) {}
}
