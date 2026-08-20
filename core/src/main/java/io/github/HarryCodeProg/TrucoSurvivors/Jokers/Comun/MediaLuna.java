package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class MediaLuna extends Joker {

    public MediaLuna(){
        super(21, "Media Luna", "MediaLuna", "Las cartas de copa que no maten reciben +20 puntos truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.DULCE);
    }

    @Override
    public Joker copiar() {
        MediaLuna copia = new MediaLuna();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_SER_MATADO) return;
        Carta c = ctx.getCartaEnResolucion(); // carta propia que perdio
        if (c == null || c.getPalo() != Palo.COPA) return;
        c.modificarPuntosTrucoAportePermanente(20);
    }
}
