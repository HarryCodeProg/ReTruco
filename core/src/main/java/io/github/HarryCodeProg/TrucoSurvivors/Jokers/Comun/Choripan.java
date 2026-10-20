package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Choripan extends Joker {

    private static final double MULT_POR_CANTO = 4.0;

    public Choripan(){
        super(28, "Choripan", "Choripan", "Gana +4 Multiplicador truco cada vez que cantás \"Truco\"",
            Rareza.comun, 3, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TRUCO,
            CategoriaJoker.RACHAS,
            CategoriaJoker.GASTRONOMIA
        );
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Gana +4 Multiplicador truco cada vez que cantás \"Truco\" (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "Gana +4 Multiplicador truco cada vez que cantás \"Truco\" (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Choripan copia = new Choripan();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        // CAMBIO: Ahora reacciona a cantar truco en vez de decir "No Quiero"
        if (evento == EventoJuego.AL_CANTAR_TRUCO) {
            sumarAcumulado(MULT_POR_CANTO);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
