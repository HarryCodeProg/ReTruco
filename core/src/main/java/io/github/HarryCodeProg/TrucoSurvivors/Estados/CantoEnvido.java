package io.github.HarryCodeProg.TrucoSurvivors.Estados;

public enum CantoEnvido {

    ENVIDO(2),
    REAL_ENVIDO(3),
    FALTA_ENVIDO(10);

    private final double valorBase;

    CantoEnvido(int valorBase) {
        this.valorBase = valorBase;
    }

    public double getValorBase() {
        return valorBase;
    }
}
