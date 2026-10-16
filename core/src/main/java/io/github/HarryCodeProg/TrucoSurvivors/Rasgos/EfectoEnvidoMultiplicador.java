package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class EfectoEnvidoMultiplicador implements EfectoRasgo {
    private final double multiplicador;
    public EfectoEnvidoMultiplicador(double multiplicador) { this.multiplicador = multiplicador; }
    public double getMultiplicador() { return multiplicador; }
    @Override public void onActivar(Juego juego) {}
    @Override public void onDesactivar(Juego juego) {}
}
