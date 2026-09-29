package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class SanJuan extends Joker {

    public SanJuan() {
        super(
            89,
            "SanJuan",
            "SanJuan",
            "+50 multiplicador truco, -1 por cada mano jugada.\n(Se destruye al llegar a 0)",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(50);
    }

    @Override
    public Joker copiar() {
        SanJuan copia = new SanJuan();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            double multActual = getAcumulado();
            if (multActual > 0) {
                ctx.getResolucionActual().sumarMult(multActual, this.getNombre(), this);
            }
        }
        if (evento == EventoJuego.TERMINO_MANO) {
            setAcumulado(getAcumulado() - 1);
            if (getAcumulado() <= 0) {
                juego.getJugador().getJokers().remove(this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int actual = (int) getAcumulado();
        return getDescripcion() + "\n(Actual: +" + actual + " mult)";
    }
}
