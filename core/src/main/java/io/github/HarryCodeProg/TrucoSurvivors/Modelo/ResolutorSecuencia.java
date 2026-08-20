package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.Activacion;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Procesa una secuencia completa de activaciones (cartas + jokers) para una resolucion
 * de puntaje estilo Balatro. Arma la cola inicial respetando fases y orden izquierda-derecha,
 * y la consume permitiendo que cualquier activacion (carta o joker) encole mas activaciones
 * durante su propia resolucion (reactivar), o ejecute el efecto de otro joker de inmediato (copiar).
 */
public class ResolutorSecuencia {
    private final GestorJokers gestorJokers;
    private final Juego juego;

    public ResolutorSecuencia(GestorJokers gestorJokers, Juego juego) {
        this.gestorJokers = gestorJokers;
        this.juego = juego;
    }

    /**
     * Resuelve la puntuacion de una mano de truco/envido ganada.
     * @param cartasGanadoras cartas que efectivamente ganaron su baza (o la unica carta de envido en juego)
     * @param ctx contexto ya con resolucionActual seteada
     * @param eventoPorCarta evento a disparar cuando cada carta puntua (ej: AL_PUNTUAR_CARTA)
     * @param eventoIndependiente evento de jokers generales, se agrega al final de la cola (ej: ANTES_DE_SUMAR_TRUCO)
     */
    public void resolver(ArrayList<io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta> cartasGanadoras,
                         ContextoJuego ctx, EventoJuego eventoPorCarta, EventoJuego eventoIndependiente) {

        ArrayDeque<Activacion> cola = new ArrayDeque<>();

        // 1. Cartas ganadoras, de izquierda a derecha (orden en que se jugaron)
        for (Carta carta : cartasGanadoras) {
            cola.addLast(Activacion.deCarta(carta, eventoPorCarta));
        }
        // 2. Jokers independientes al final, de izquierda a derecha
        for (Joker j : ctx.getJugador().getJokers()) {
            if (j.getFase() == Joker.FaseActivacion.INDEPENDIENTE) {
                cola.addLast(Activacion.deJoker(j, eventoIndependiente));
            }
        }
        ctx.setColaActivaciones(cola);
        procesarCola(cola, ctx);
    }

    private void procesarCola(ArrayDeque<Activacion> cola, ContextoJuego ctx) {
        while (!cola.isEmpty()) {
            Activacion act = cola.poll();
            if (act.esCarta()) {
                procesarActivacionDeCarta(act, ctx, cola);
            } else {
                act.joker.aplicarEfecto(act.evento, ctx, juego);
            }
        }
    }

    /** Una carta puntuando: suma sus chips y dispara los jokers de fase AL_PUNTUAR_CARTA para que reaccionen. */
    private void procesarActivacionDeCarta(Activacion act, ContextoJuego ctx, ArrayDeque<Activacion> cola) {
        Carta carta = act.carta;
        String nombreCarta = carta.getNumero() + " de " + carta.paloToString();
        ctx.setCartaEnResolucion(carta);
        ctx.getResolucionActual().sumarChips(carta.getPuntosTrucoAporteEfectivo(), nombreCarta, carta); // <-- agregar carta como origenRef
        for (Joker j : ctx.getJugador().getJokers()) {
            if (j.getFase() == Joker.FaseActivacion.AL_PUNTUAR_CARTA) {
                j.aplicarEfecto(act.evento, ctx, juego);
            }
        }
    }
}
