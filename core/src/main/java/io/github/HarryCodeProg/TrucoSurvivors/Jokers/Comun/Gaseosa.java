package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Gaseosa extends Joker {

    public Gaseosa(){
        super(
            3,
            "Gaseosa",
            "Gaseosa",
            "+100 puntos truco, +100 puntos envido. Si tienes un 'Fernet', obtienes +200 puntos mas de cada uno.",
            Rareza.comun,
            1,
            INDEPENDIENTE,
            CategoriaJoker.INTERNACIONAL,CategoriaJoker.BEBIDA,CategoriaJoker.DULCE
        );
    }

    @Override
    public Joker copiar() {
        Gaseosa copia = new Gaseosa();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO
            && evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        double bonus = 100;
        if (ctx.tieneJoker(Fernet.class)) {
            bonus += 200;
        }
        ctx.getResolucionActual().sumarChips(bonus, this.getNombre(),this);
    }
}

