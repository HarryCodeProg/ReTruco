package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanExpedito extends Santo {

    public SanExpedito() {
        super(
            16,
            "San Expedito",
            "SanExpedito",
            "Otorga $5 + $1 por cada 10 cartas en tu mazo",
            4
        );
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        int cartas = jugador.getMazo().getTamañoMazo();
        int recompensa = 5 + (cartas / 10);
        if (jugador.tieneDobleEfectoSantos()) {
            jugador.sumarPesos(recompensa * 2);
            return;
        }
        jugador.sumarPesos(recompensa);
    }
}
