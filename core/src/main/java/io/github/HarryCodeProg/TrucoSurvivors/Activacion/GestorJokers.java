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
        ArrayDeque<Activacion> cola = new ArrayDeque<>();
        for (Joker.FaseActivacion fase : Joker.FaseActivacion.values()) {
            for (Joker j : jugador.getJokers()) {
                if (j.getFase() == fase) {
                    cola.add(Activacion.deJoker(j, evento));
                }
            }
        }
        ctx.setColaActivaciones(cola);
        while (!cola.isEmpty()) {
            Activacion act = cola.poll();
            act.joker.aplicarEfecto(act.evento, ctx, juego);
        }
    }
}
