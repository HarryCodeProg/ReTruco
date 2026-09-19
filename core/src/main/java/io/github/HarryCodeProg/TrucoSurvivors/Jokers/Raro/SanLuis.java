package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class SanLuis extends Joker {

    public SanLuis() {
        super(
            90,
            "SanLuis",
            "SanLuis",
            "+3 multiplicador envido por cada mano jugada, -1 por cada descarte.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(0);
    }

    @Override
    public Joker copiar() {
        SanLuis copia = new SanLuis();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            double multActual = getAcumulado();
            if (multActual > 0) {
                ctx.getResolucionActual().sumarMult(multActual, this.getNombre(), this);
            }
        }
        if (evento == EventoJuego.TERMINO_MANO) {
            setAcumulado(getAcumulado() + 3);
        }
        if (evento == EventoJuego.AL_DESCARTAR) {
            // Restamos 1, pero evitamos que baje de 0
            setAcumulado(Math.max(0, getAcumulado() - 1));
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int actual = (int) getAcumulado();
        return getDescripcion() + "\n(Actual: +" + actual + " mult)";
    }
}
