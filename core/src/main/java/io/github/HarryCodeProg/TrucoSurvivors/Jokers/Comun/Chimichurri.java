package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Chimichurri extends Joker {

    private static final double MULT_POR_MANO = 2.0;

    public Chimichurri(){
        super(27, "Chimichurri", "Chimichurri", "+2 Multiplicador truco por cada mano ganada de forma consecutiva",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.TRADICIONAL);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+2 Multiplicador truco por cada mano ganada de forma consecutiva (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "+2 Multiplicador truco por cada mano ganada de forma consecutiva (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Chimichurri copia = new Chimichurri();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento == EventoJuego.AL_GANAR_TRUCO) {
            sumarAcumulado(MULT_POR_MANO);
            return;
        }
        if (evento == EventoJuego.AL_PERDER_TRUCO) {
            setAcumulado(0);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
