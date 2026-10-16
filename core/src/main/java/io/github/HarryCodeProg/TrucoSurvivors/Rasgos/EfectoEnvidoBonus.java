package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class EfectoEnvidoBonus implements EfectoRasgo {
    private final double bonus;
    public EfectoEnvidoBonus(double bonus) { this.bonus = bonus; }
    public double getBonus() { return bonus; }
    @Override public void onActivar(Juego juego) {}
    @Override public void onDesactivar(Juego juego) {}
}
