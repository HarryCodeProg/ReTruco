package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Locro: +6 mult envido si el tanto ganador tiene figura
public class Locro extends Joker {
    public Locro(){
        super(29, "Locro", "Locro", "+6 multiplicador envido si tu tanto tiene una figura",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        boolean tieneFigura = ctx.getJugador().getCartasEnvidoGanador().stream().anyMatch(c -> c.getNumero() >= 10);
        if (tieneFigura) ctx.getResolucionActual().sumarMult(6, getNombre());
    }
}
