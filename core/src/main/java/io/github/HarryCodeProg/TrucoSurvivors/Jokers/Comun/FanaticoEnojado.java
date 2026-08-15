package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Fanatico Enojado: cada copa que mata suma +50 puntos truco al activarse
public class FanaticoEnojado extends Joker {

    public FanaticoEnojado(){
        super(45, "Fanatico Enojado", "FanaticoEnojado", "Cada copa que mata otorga +50 puntos truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        FanaticoEnojado copia = new FanaticoEnojado();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.COPA) return;
        if (!ctx.cartaMato(c)) return;
        ctx.getResolucionActual().sumarChips(50, getNombre());
    }
}
