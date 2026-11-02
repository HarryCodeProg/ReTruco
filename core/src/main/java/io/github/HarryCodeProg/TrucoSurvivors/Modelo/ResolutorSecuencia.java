package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.Activacion;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.JokerCopiaVecino;

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

    private static final int MAX_ACTIVACIONES_POR_RESOLUCION = 500; // tope de seguridad global

    private void procesarCola(ArrayDeque<Activacion> cola, ContextoJuego ctx, Function<Carta, Double> extractorPuntos, int[] procesadas) {
        while (!cola.isEmpty()) {
            if (procesadas[0]++ >= MAX_ACTIVACIONES_POR_RESOLUCION) {
                System.err.println("ADVERTENCIA: se cortó la cola de activaciones por exceder el límite de seguridad ("
                    + MAX_ACTIVACIONES_POR_RESOLUCION + "). Revisar combinación de jokers.");
                cola.clear();
                break;
            }
            Activacion act = cola.poll();
            if (act.esCarta()) {
                procesarActivacionDeCarta(act, ctx, cola, extractorPuntos);
            } else {
                act.joker.aplicarEfecto(act.evento, ctx, juego);
            }
        }
    }

    private void resolverGenerico(ArrayList<Carta> cartas, ContextoJuego ctx,
                                  EventoJuego eventoPorCarta, EventoJuego eventoIndependiente,
                                  Function<Carta, Double> extractorPuntos) {
        ArrayDeque<Activacion> cola = new ArrayDeque<>();
        for (Carta carta : cartas) {
            cola.addLast(Activacion.deCarta(carta, eventoPorCarta));
        }
// Rasgo Activación: reactiva la primera carta N veces
        int reactivaciones = juego.getGestorRasgos().getReactivacionesPrimeraCarta();
        if (reactivaciones > 0 && !cartas.isEmpty()) {
            for (int i = 0; i < reactivaciones; i++) {
                cola.addLast(Activacion.deCarta(cartas.get(0), eventoPorCarta));
            }
        }
        ArrayDeque<Activacion> colaAnterior = ctx.getColaActivaciones();
        Carta cartaAnterior = ctx.getCartaEnResolucion();
        int[] procesadas = {0}; // Un único presupuesto para todas las etapas.
        ctx.setColaActivaciones(cola);
        try {
            // Vaciar también las reactivaciones agregadas por los efectos por carta.
            procesarCola(cola, ctx, extractorPuntos, procesadas);
            ctx.setCartaEnResolucion(null);
            encolarFase(cola, ctx, Joker.FaseActivacion.REACTIVACION, eventoIndependiente);
            procesarCola(cola, ctx, extractorPuntos, procesadas);
            // Los independientes recién se agregan cuando no quedan cartas pendientes.
            ctx.setCartaEnResolucion(null);
            encolarFase(cola, ctx, Joker.FaseActivacion.INDEPENDIENTE, eventoIndependiente);
            procesarCola(cola, ctx, extractorPuntos, procesadas);
            dispararRasgos(ctx, eventoIndependiente);
        } finally {
            ctx.setColaActivaciones(colaAnterior);
            ctx.setCartaEnResolucion(cartaAnterior);
        }
    }

    private void encolarFase(ArrayDeque<Activacion> cola, ContextoJuego ctx,
                             Joker.FaseActivacion fase, EventoJuego evento) {
        for (Joker j : ctx.getJugador().getJokers()) {
            if (faseEfectiva(j, ctx) == fase && ctx.jokerPermitidoPorFiltro(j)) {
                cola.addLast(Activacion.deJoker(j, evento));
            }
        }
    }

    // Una copia participa en la etapa de su destino, incluso en cadenas de copias.
    private Joker.FaseActivacion faseEfectiva(Joker joker, ContextoJuego ctx) {
        java.util.Set<Joker> visitados = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        while (joker instanceof JokerCopiaVecino) {
            if (!visitados.add(joker)) return null;
            joker = ((JokerCopiaVecino) joker).getVecinoActual(ctx);
            if (joker == null) return null;
        }
        return joker.getFase();
    }

    private void procesarActivacionDeCarta(Activacion act, ContextoJuego ctx, ArrayDeque<Activacion> cola,
                                           Function<Carta, Double> extractorPuntos) {
        Carta carta = act.carta;
        String nombreCarta = carta.getNumero() + " de " + carta.paloToString();
        ctx.setCartaEnResolucion(carta);
        if (act.puntua) {
            double puntos = extractorPuntos.apply(carta);
            ctx.getResolucionActual().sumarChips(puntos, nombreCarta, carta);
        }
        gestorJokers.disparar(EventoJuego.AL_ACTIVAR_CARTA, ctx, juego);
        juego.getGestorRasgos().dispararEvento(EventoJuego.AL_ACTIVAR_CARTA, ctx, juego);
        for (Joker j : ctx.getJugador().getJokers()) {
            if (!ctx.jokerPermitidoPorFiltro(j)) continue;
            if (faseEfectiva(j, ctx) == Joker.FaseActivacion.AL_PUNTUAR_CARTA) {
                j.aplicarEfecto(act.evento, ctx, juego);
            }
        }
    }

    private void dispararRasgos(ContextoJuego ctx, EventoJuego ev) {
        if (ev == EventoJuego.ANTES_DE_SUMAR_TRUCO || ev == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            juego.getGestorRasgos().dispararEvento(ev, ctx, juego);
        }
    }
}
