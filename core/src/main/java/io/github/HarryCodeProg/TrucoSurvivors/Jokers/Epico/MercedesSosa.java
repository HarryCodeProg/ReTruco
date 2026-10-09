package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class MercedesSosa extends Joker {

    public MercedesSosa() {
        super(137, "Mercedes Sosa",
            "MercedesSosa",
            "+2 tamaño jokers.\n+2 Multiplicador de Envido\npor cada joker que tengas.",
            Rareza.epico,
            10,
            FaseActivacion.INDEPENDIENTE
        );
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int cantidadJokers = 0;
        // Buscamos cuántos Jokers tiene el jugador actual para mostrar el bonus en tiempo real
        if (getJugadorPropietario() != null) {
            cantidadJokers = getJugadorPropietario().getJokers().size();
        } else if (juego != null && juego.getJugador() != null) {
            cantidadJokers = juego.getJugador().getJokers().size();
        }
        int bonusActual = cantidadJokers * 2;
        // Usamos ROJO (o el color que prefieras) para el Multiplicador
        return getDescripcion() + "\n(actual: [RED]+" + bonusActual + " Mult[])";
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        // Aumenta el límite de Jokers al instante de comprarla/obtenerla
        jugador.setTamañoJokers(jugador.getTamañoJokers() + 2);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        // Revierte el límite si se vende o se destruye
        jugador.setTamañoJokers(jugador.getTamañoJokers() - 2);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        // Evaluamos el efecto justo antes de que se sumen los puntos del Envido
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            // Contamos cuántos Jokers tiene el jugador (esto incluye a la propia Mercedes Sosa)
            int cantidadJokers = contexto.getJugador().getJokers().size();
            double multExtra = cantidadJokers * 2.0;
            if (multExtra > 0 && contexto.getResolucionActual() != null) {
                contexto.getResolucionActual().sumarMult(multExtra, getNombre(), this);
            }
        }
    }

    @Override
    public Joker copiar() {
        MercedesSosa copia = new MercedesSosa();
        this.copiarEstado(copia);
        return copia;
    }
}
