package io.github.HarryCodeProg.TrucoSurvivors.Estados;

public enum CantoTruco {

    TRUCO(2),
    RETRUCO(3),
    VALE_CUATRO(4);

    private final double puntos;

    CantoTruco(int puntos) {
        this.puntos = puntos;
    }

    public double getPuntos() {
        return puntos;
    }
}
