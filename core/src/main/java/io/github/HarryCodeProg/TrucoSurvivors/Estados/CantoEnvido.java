package io.github.HarryCodeProg.TrucoSurvivors.Estados;

public enum CantoEnvido {

    ENVIDO(2);

    private final double valorBase;

    CantoEnvido(int valorBase) {
        this.valorBase = valorBase;
    }

    public double getValorBase() {
        return valorBase;
    }
}
