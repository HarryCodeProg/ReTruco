package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class EfectoTrucoBonus implements EfectoRasgo {
    private final double bonus;
    public EfectoTrucoBonus(double bonus) { this.bonus = bonus; }
    public double getBonus() { return bonus; }
    @Override public void onActivar(Juego juego) {}
    @Override public void onDesactivar(Juego juego) {}
}
