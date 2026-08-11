package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Pastafrola: cartas que TE maten (del rival) reciben +10 puntos truco
public class Pastafrola extends Joker {

    public Pastafrola(){
        super(13,
            "Pastafrola",
            "Pastafrola",
            "Las cartas que te maten reciben +10 puntos truco",
            Rareza.comun,
            1,
            FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_SER_MATADO) return;
        Carta cGanadora = ctx.getCartaOponenteEnResolucion();
        if (cGanadora == null) return;
        cGanadora.modificarPuntosTrucoAportePermanente(10);
    }
}
