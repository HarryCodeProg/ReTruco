package io.github.HarryCodeProg.TrucoSurvivors.Activacion;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.Activacion;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayDeque;

public class GestorJokers {
    private final Jugador jugador;

    public GestorJokers(Jugador jugador) {
        this.jugador = jugador;
    }

    public void disparar(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Guardamos la cola activa para no pisar la del llamador (p. ej. la del
        // ResolutorSecuencia cuando dispara AL_ACTIVAR_CARTA en medio de una resolución).
        // Así, los jokers que reencolan cartas siguen escribiendo en la cola correcta.
        ArrayDeque<Activacion> colaAnterior = ctx.getColaActivaciones();
        ArrayDeque<Activacion> cola = new ArrayDeque<>();
        for (Joker.FaseActivacion fase : Joker.FaseActivacion.values()) {
            for (Joker j : jugador.getJokers()) {
                if (j.getFase() == fase && ctx.jokerPermitidoPorFiltro(j)) {
                    cola.add(Activacion.deJoker(j, evento));
                }
            }
        }
        ctx.setColaActivaciones(cola);
        while (!cola.isEmpty()) {
            Activacion act = cola.poll();
            if (act.esJoker()) {
                act.joker.aplicarEfecto(act.evento, ctx, juego);
                for (Joker otro : jugador.getJokers()) {
                    if (otro != act.joker) {
                        otro.onOtroJokerActivado(act.joker, act.evento, ctx, juego);
                    }
                }
            }
        }
        ctx.setColaActivaciones(colaAnterior);
    }
}
