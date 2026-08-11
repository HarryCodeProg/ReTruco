package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Cañoncito: cartas de ORO que maten, +15 puntos truco
public class Cañoncito extends Joker {

    public Cañoncito(){
        super(19, "Cañoncito", "Canoncito", "Las cartas de oro que maten reciben +15 puntos truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_MATAR_CARTA) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.ORO) return;
        c.modificarPuntosTrucoAportePermanente(15);
    }
}
