package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Ñoquis extends Joker {
    public Ñoquis(){
        super(26, "Ñoquis", "Ñoquis", "Gana +2 Multiplicador truco cada vez que cantás \"Quiero\"",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.TRADICIONAL);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Gana +2 Multiplicador truco cada vez que cantás \"Quiero\" (Actual: +" + (int) getAcumulado() + ")";
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
        if (evento == EventoJuego.AL_DECIR_QUIERO_TRUCO) {
            sumarAcumulado(2);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(),this);
    }
}
