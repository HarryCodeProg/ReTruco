package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Ñoquis extends Joker {

    private static final double MULT_POR_CANTO = 2.0;

    public Ñoquis(){
        super(26, "Ñoquis", "Ñoquis", "Gana +2 Multiplicador envido cada vez que cantás \"Envido\"",
            Rareza.comun, 4, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.ENVIDO,
            CategoriaJoker.RACHAS,
            CategoriaJoker.GASTRONOMIA
        );
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Gana +2 Multiplicador envido cada vez que cantás \"Envido\" (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "Gana +2 Multiplicador envido cada vez que cantás \"Envido\" (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Ñoquis copia = new Ñoquis();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento == EventoJuego.AL_CANTAR_ENVIDO) {
            sumarAcumulado(MULT_POR_CANTO);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
