package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Fernet extends Joker {

    public Fernet(){
        super(
            5,
            "Fernet",
            "Fernet",
            "+20 puntos truco, +20 puntos envido. Si tienes una 'Cola', obtienes +20 puntos mas de cada uno.",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL,CategoriaJoker.BEBIDA,CategoriaJoker.ALCOHOL
        );
    }

    @Override
    public Joker copiar() {
        Fernet copia = new Fernet();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO && evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        double bonus = 20;
        if (ctx.tieneJoker(Gaseosa.class)) {
            bonus += 20;
        }
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre(), this);
    }
}
