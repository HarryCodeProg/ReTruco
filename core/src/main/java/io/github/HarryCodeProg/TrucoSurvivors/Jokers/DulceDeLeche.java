package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class DulceDeLeche extends Joker{

    public DulceDeLeche(){
        super(
            10,
            "Dulce de leche",
            "DulceDeLeche",
            "+10 puntos truco por cada carta del mazo",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.DULCE
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        double bonus = 0;
        int cantidadMazo = ctx.getJugador().getMazo().getTamañoMazo();
        bonus = 10 * cantidadMazo;
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre());
    }

}
