package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Moñito extends Joker {
    public Moñito(){
        super(22, "Moñito", "Moñito", "Las cartas de Basto que no maten reciben +20 puntos truco",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.DULCE);
    }

    @Override
    public Joker copiar() {
        Moñito copia = new Moñito();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_SER_MATADO) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.BASTO) return;
        c.modificarPuntosTrucoAportePermanente(20);
    }
}
