package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class NoEstaTanMal extends Joker {

    private static final double MULT_POR_DERROTA = 4.0;

    public NoEstaTanMal(){
        super(50, "No Esta Tan Mal", "NoEstaTanMal", "Si perdés la mano, aumenta +4 multiplicador truco",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TV, CategoriaJoker.SECUENCIA);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Si perdés la mano, aumenta +4 multiplicador truco (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "Si perdés la mano, aumenta +4 multiplicador truco (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        NoEstaTanMal copia = new NoEstaTanMal();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento == EventoJuego.AL_PERDER_TRUCO) {
            sumarAcumulado(MULT_POR_DERROTA);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;

        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
