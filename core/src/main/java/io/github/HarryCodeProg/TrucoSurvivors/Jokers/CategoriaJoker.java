package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Main;

public enum CategoriaJoker {
    ANIMAL,
    AMIGABLE,
    AGUA,
    COMIDA,
    DULCE,
    BEBIDA,
    AMARGO,
    TRADICIONAL,
    NACIONAL,
    INTERNACIONAL,
    ALCOHOL,
    MUSICA,
    DEPORTE,
    HISTORIA,
    OFICIO,
    CAMPO,
    CIUDAD,
    NATURALEZA,
    MASCOTA,
    TRANSPORTE,
    HERRAMIENTA,
    POSTRE,
    SECUENCIA,
    TV,
    SALADO;

    public String getTexto() {
        return Main.getTexto("categoria." + this.name());
    }

}
