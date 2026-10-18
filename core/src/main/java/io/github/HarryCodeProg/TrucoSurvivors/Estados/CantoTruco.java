package io.github.HarryCodeProg.TrucoSurvivors.Estados;

public enum CantoTruco {

    TRUCO(2);

    private final double puntos;

    CantoTruco(int puntos) {
        this.puntos = puntos;
    }

    public double getPuntos() {
        return puntos;
    }
}
