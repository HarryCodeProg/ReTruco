package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanCayetano extends Santo {

    public SanCayetano() {
        super(
            15,
            "San Cayetano",
            "SanCayetano",
            "Otorga pesos igual a la cantidad total de venta de todos los jokers",
            4
        );
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        int total = 0;
        for (Joker joker : jugador.getJokers()) {
            total += joker.getPrecioVenta();
        }
        jugador.sumarPesos(total);
    }
}
