package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun.Cerveza;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Misiones extends Joker {

    public Misiones(){
        super(
            85,
            "Misiones",
            "Misiones",
            "+400 puntos de envido.",
            Rareza.comun,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.BEBIDA, CategoriaJoker.ALCOHOL
        );
    }

    @Override
    public Joker copiar() {
        Cerveza copia = new Cerveza();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        double bonus = 400;
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre(), this);
    }

}
