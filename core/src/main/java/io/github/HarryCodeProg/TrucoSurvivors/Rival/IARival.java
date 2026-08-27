package io.github.HarryCodeProg.TrucoSurvivors.Rival;

import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;

import java.util.Random;
import java.util.function.BiConsumer;

public class IARival {
    private final Juego juego;
    private final Random random;
    /** Recibe (resolucion, alTerminar) para pedirle a la pantalla que anime. Null-safe: si es null, se aplica sin animar. */
    private BiConsumer<ResolucionPuntaje, Runnable> solicitarAnimacionEnvido;

    public IARival(Juego juego) {
        this.juego = juego;
        this.random = new Random();
    }

    public void setSolicitarAnimacionEnvido(BiConsumer<ResolucionPuntaje, Runnable> callback) {
        this.solicitarAnimacionEnvido = callback;
    }

    /** true si la IA está en medio de una resolución de envido animada (bloquea el resto de la IA ese frame). */
    private boolean animandoEnvido = false;

    public boolean isAnimandoEnvido() { return animandoEnvido; }

    public void actualizar() {
        if (animandoEnvido) return; // no tomar más decisiones mientras se anima su propio envido
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
            case ESCALAR:
                juego.escalarEnvido(rival);
                break;
            case QUIERO: {
                juego.responderEnvido(true);
                dispararAnimacionOAplicar();
                break;
            }
            case NO_QUIERO: {
                juego.responderEnvido(false); // ahora arma resolución simple, no aplica
                dispararAnimacionOAplicar();
                break;
            }
        }
    }

    private void dispararAnimacionOAplicar() {
        ResolucionPuntaje resolucion = juego.getUltimaResolucionEnvido();
        if (resolucion != null && solicitarAnimacionEnvido != null) {
            animandoEnvido = true;
            solicitarAnimacionEnvido.accept(resolucion, () -> {
                juego.aplicarResultadoEnvido();
                animandoEnvido = false;
            });
        } else {
            juego.aplicarResultadoEnvido();
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
