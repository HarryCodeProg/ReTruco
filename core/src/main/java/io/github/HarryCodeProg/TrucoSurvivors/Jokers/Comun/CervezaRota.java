package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class CervezaRota extends Joker {

    public CervezaRota(){
        super(
            9,
            "Cerveza Rota",
            "CervezaRota",
            "+100 puntos truco y envido. +25 por cada otro joker con la categoria 'Alcohol'",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.ALCOHOL
        );
    }

    @Override
    public Joker copiar() {
        CervezaRota copia = new CervezaRota();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        double bonus = 100;
        int otrosConAlcohol = ctx.contarJokersConCategoria(CategoriaJoker.ALCOHOL, this);
        bonus += 25 * otrosConAlcohol;
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre(), this);
    }
}
