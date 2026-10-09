package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Cotorra extends Joker {

    private static final double PUNTOS_BASE = 1000;
    private static final double PENALIDAD_POR_CARTA = 150;
    private static final int UMBRAL_MANO = 3;

    public Cotorra() {
        super(116, "Cotorra", "Cotorra",
            "+1000 puntos.\n-150 puntos por cada\n1 tamaño de mano por encima de 3",
            Rareza.muyRaro, 8, FaseActivacion.INDEPENDIENTE);
    }

    private double calcularPuntosActuales(Jugador jugador) {
        if (jugador == null) return PUNTOS_BASE;

        int tamañoManoActual = jugador.getTamañoMano();
        int exceso = Math.max(0, tamañoManoActual - UMBRAL_MANO);

        return PUNTOS_BASE - (exceso * PENALIDAD_POR_CARTA);
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        double puntosActuales = PUNTOS_BASE;

        if (getJugadorPropietario() != null) {
            puntosActuales = calcularPuntosActuales(getJugadorPropietario());
        } else if (juego != null && juego.getJugador() != null) {
            puntosActuales = calcularPuntosActuales(juego.getJugador());
        }

        String signo = puntosActuales >= 0 ? "+" : "";
        return getDescripcion() + "\n(actual: " + signo + (int) puntosActuales + ")";
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {

            double puntos = calcularPuntosActuales(ctx.getJugador());

            if (puntos != 0 && ctx.getResolucionActual() != null) {
                // Sumamos los puntos como Chips base (tanto para el Truco como para el Envido)
                ctx.getResolucionActual().sumarChips(puntos, getNombre(), this);
            }
        }
    }

    @Override
    public Joker copiar() {
        Cotorra copia = new Cotorra();
        this.copiarEstado(copia);
        return copia;
    }
}
