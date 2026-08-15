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
            "Gana 1 peso por cada carta que tengas",
            4
        );
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        jugador.sumarPesos(jugador.getMazo().getTamañoMazo());
    }
}
