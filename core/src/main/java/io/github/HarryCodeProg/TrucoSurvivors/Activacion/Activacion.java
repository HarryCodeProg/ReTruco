package io.github.HarryCodeProg.TrucoSurvivors.Activacion;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;

/**
 * Una unidad de activacion dentro de una secuencia de resolucion (estilo Balatro).
 * Puede ser una carta puntuando, o un joker reaccionando. Nunca ambos a la vez.
 */
public class Activacion {
    public final Joker joker; // null si es una carta
    public final Carta carta; // null si es un joker
    public final EventoJuego evento;

    public Activacion(Joker joker, Carta carta, EventoJuego evento) {
        this.joker = joker;
        this.carta = carta;
        this.evento = evento;
    }

    public static Activacion deJoker(Joker joker, EventoJuego evento) {
        return new Activacion(joker, null, evento);
    }

    public static Activacion deCarta(Carta carta, EventoJuego evento) {
        return new Activacion(null, carta, evento);
    }

    public boolean esCarta() { return carta != null; }
    public boolean esJoker() { return joker != null; }
}
