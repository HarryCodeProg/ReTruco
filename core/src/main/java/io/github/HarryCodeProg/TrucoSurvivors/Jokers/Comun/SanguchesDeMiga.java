package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class SanguchesDeMiga extends Joker {

    public SanguchesDeMiga() {
        super(
            53,
            "Sanguches de Miga",
            "SanguchesDeMiga",
            "Gana +25 puntos truco por cada joker 'Comida'",
            Rareza.comun,
            1,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.COMIDA, CategoriaJoker.NACIONAL, CategoriaJoker.SALADO
        );
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Gana +25 puntos truco por cada joker 'Comida' (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        SanguchesDeMiga copia = new SanguchesDeMiga();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.TERMINO_MANO) {
            int cantidadComida = 0;
            for (Joker joker : ctx.getJugador().getJokers()) {
                if (joker.tieneCategoria(CategoriaJoker.COMIDA)) {
                    cantidadComida++;
                }
            }
            sumarAcumulado(cantidadComida * 25);
            return;
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (getAcumulado() <= 0) return;
            ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
        }
    }
}
