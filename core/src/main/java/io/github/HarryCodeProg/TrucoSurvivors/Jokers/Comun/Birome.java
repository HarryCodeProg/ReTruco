package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Birome extends Joker {

    public Birome(){
        super(37, "Birome", "Birome", "Las cartas de espada que ganen envido reciben +15 puntos envido",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_GANAR_ENVIDO_CANTO) return;
        for (Carta c : ctx.getJugador().getCartasEnvidoGanador()) {
            if (c.getPalo() == Palo.ESPADA) c.modificarPuntosEnvidoAportePermanente(15);
        }
    }
}
