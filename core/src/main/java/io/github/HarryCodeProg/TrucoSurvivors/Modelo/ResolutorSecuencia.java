package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.Activacion;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Function;

public class ResolutorSecuencia {
    private final GestorJokers gestorJokers;
    private final Juego juego;

    public ResolutorSecuencia(GestorJokers gestorJokers, Juego juego) {
        this.gestorJokers = gestorJokers;
        this.juego = juego;
    }

    /** Resolución de TRUCO: usa puntosTrucoAporteEfectivo por carta. */
    public void resolver(ArrayList<Carta> cartasGanadoras, ContextoJuego ctx,
                         EventoJuego eventoPorCarta, EventoJuego eventoIndependiente) {
        resolverGenerico(cartasGanadoras, ctx, eventoPorCarta, eventoIndependiente,
            carta -> (double) carta.getPuntosTrucoAporteEfectivo()); // <-- FIX: cast explícito int -> double
    }

    public void resolverEnvido(ArrayList<Carta> cartasContribuyentes, ContextoJuego ctx,
                               EventoJuego eventoPorCarta, EventoJuego eventoIndependiente) {
        resolverGenerico(cartasContribuyentes, ctx, eventoPorCarta, eventoIndependiente,
            carta -> (double) carta.getPuntosEnvidoAporteEfectivo()); // <-- FIX
    }

    /** Resuelve SOLO jokers independientes, sin cartas (ej: envido sin cartas que puntúen individualmente). */
    public void resolverSoloJokers(ContextoJuego ctx, EventoJuego eventoIndependiente) {
        ArrayDeque<Activacion> cola = new ArrayDeque<>();
        for (Joker j : ctx.getJugador().getJokers()) {
            if (j.getFase() == Joker.FaseActivacion.INDEPENDIENTE) {
                cola.addLast(Activacion.deJoker(j, eventoIndependiente));
            }
        }
        ctx.setColaActivaciones(cola);
        procesarCola(cola, ctx, null);
    }

    private void resolverGenerico(ArrayList<Carta> cartas, ContextoJuego ctx,
                                  EventoJuego eventoPorCarta, EventoJuego eventoIndependiente,
                                  Function<Carta, Double> extractorPuntos) {
        ArrayDeque<Activacion> cola = new ArrayDeque<>();
        for (Carta carta : cartas) {
            cola.addLast(Activacion.deCarta(carta, eventoPorCarta));
        }
        for (Joker j : ctx.getJugador().getJokers()) {
            if (j.getFase() == Joker.FaseActivacion.INDEPENDIENTE) {
                cola.addLast(Activacion.deJoker(j, eventoIndependiente));
            }
        }
        ctx.setColaActivaciones(cola);
        procesarCola(cola, ctx, extractorPuntos);
    }

    private void procesarCola(ArrayDeque<Activacion> cola, ContextoJuego ctx, Function<Carta, Double> extractorPuntos) {
        while (!cola.isEmpty()) {
            Activacion act = cola.poll();
            if (act.esCarta()) {
                procesarActivacionDeCarta(act, ctx, cola, extractorPuntos);
            } else {
                act.joker.aplicarEfecto(act.evento, ctx, juego);
            }
        }
    }

    private void procesarActivacionDeCarta(Activacion act, ContextoJuego ctx, ArrayDeque<Activacion> cola,
                                           Function<Carta, Double> extractorPuntos) {
        Carta carta = act.carta;
        String nombreCarta = carta.getNumero() + " de " + carta.paloToString();
        ctx.setCartaEnResolucion(carta);
        double puntos = extractorPuntos.apply(carta);
        ctx.getResolucionActual().sumarChips(puntos, nombreCarta, carta);
        for (Joker j : ctx.getJugador().getJokers()) {
            if (j.getFase() == Joker.FaseActivacion.AL_PUNTUAR_CARTA) {
                j.aplicarEfecto(act.evento, ctx, juego);
            }
        }
    }
}
