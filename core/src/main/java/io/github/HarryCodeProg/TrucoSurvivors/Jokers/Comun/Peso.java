package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Peso extends Joker {

    public Peso() {
        super(33, "Peso", "Peso", "+1 peso por cada joker 'Nacional' al final de la ronda",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.TERMINO_MANO) return;
        int cantidadNacionales = 0;
        for (Joker j : ctx.getJugador().getJokers()) {
            if (j.tieneCategoria(CategoriaJoker.NACIONAL)) cantidadNacionales++;
        }
        ctx.getJugador().sumarPesos(cantidadNacionales);
    }
}
