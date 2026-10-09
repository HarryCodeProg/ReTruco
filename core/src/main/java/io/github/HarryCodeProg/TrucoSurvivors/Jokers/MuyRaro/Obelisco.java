package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Obelisco extends Joker {

    public Obelisco() {
        // Coste sugerido 8 para un Muy Raro
        super(128, "Obelisco", "Obelisco", "+1 tamaño mano.\nAl finalizar la ronda, gana $1\npor cada carta en tu mano.",
            Rareza.muyRaro,
            8,
            FaseActivacion.INDEPENDIENTE);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        // Aumenta el tamaño de la mano permanentemente mientras tengas el Joker
        jugador.aumentarTamañoMano(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        // Revierte el aumento si lo vendés o destruís
        jugador.aumentarTamañoMano(-1);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        // El momento exacto en el que se supera el puntaje meta
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            // Contamos cuántas cartas le sobraron al jugador en la mano
            int cartasEnMano = contexto.getJugador().getMano().size();
            if (cartasEnMano > 0) {
                // Otorgamos el dinero correspondiente
                contexto.getJugador().sumarPesos(cartasEnMano);
       }
        }
    }

    @Override
    public Joker copiar() {
        Obelisco copia = new Obelisco();
        this.copiarEstado(copia);
        return copia;
    }
}
