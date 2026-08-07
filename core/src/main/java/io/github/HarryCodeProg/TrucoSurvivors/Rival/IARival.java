package io.github.HarryCodeProg.TrucoSurvivors.Rival;

import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;


import java.util.Random;

public class IARival {
    private final Juego juego;
    private final Random random;

    public IARival(Juego juego) {
        this.juego = juego;
        this.random = new Random();
    }

    /** Llamar una vez por frame o tick de lógica mientras el combate está activo. */
    public void actualizar() {
        responderEnvidoSiCorresponde();
        responderTrucoSiCorresponde();
    }

    private void responderEnvidoSiCorresponde() {
        if (!juego.hayCantoEnvidoPendiente()) return;
        if (!juego.getCantorEnvidoPendiente().equals(juego.getJugador())) return;

        Decision decision = decidir(juego.puedeEscalarEnvido(juego.getRival()));
        aplicarDecisionEnvido(decision);
    }

    private void responderTrucoSiCorresponde() {
        if (!juego.hayCantoTrucoPendiente()) return;
        if (!juego.getCantorTrucoPendiente().equals(juego.getJugador())) return;

        Decision decision = decidir(juego.puedeEscalarTruco(juego.getRival()));
        aplicarDecisionTruco(decision);
    }

    private enum Decision { ESCALAR, QUIERO, NO_QUIERO }

    private Decision decidir(boolean puedeEscalar) {
        double roll = random.nextDouble();
        if (puedeEscalar && roll < 0.25) return Decision.ESCALAR;
        if (roll < 0.75) return Decision.QUIERO;
        return Decision.NO_QUIERO;
    }

    private void aplicarDecisionEnvido(Decision decision) {
        Jugador rival = juego.getRival();
        switch (decision) {
            case ESCALAR:    juego.escalarEnvido(rival); break;
            case QUIERO:     juego.responderEnvido(true); break;
            case NO_QUIERO:  juego.responderEnvido(false); break;
        }
    }

    private void aplicarDecisionTruco(Decision decision) {
        Jugador rival = juego.getRival();
        switch (decision) {
            case ESCALAR:    juego.escalarTruco(rival); break;
            case QUIERO:     juego.responderTruco(true); break;
            case NO_QUIERO:  juego.responderTruco(false); break;
        }
    }
}
