package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Cerveza extends Joker{

    public Cerveza(){
        super(
            8,
            "Cerveza",
            "Cerveza",
            "+300 puntos de envido. Por cada otro joker con la categoria 'Alcohol' -50 puntos",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.BEBIDA, CategoriaJoker.ALCOHOL
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        double bonus = 300;
        int otrosConAlcohol = ctx.contarJokersConCategoria(CategoriaJoker.ALCOHOL, this);
        bonus -= 50 * otrosConAlcohol;
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre());
    }

}
