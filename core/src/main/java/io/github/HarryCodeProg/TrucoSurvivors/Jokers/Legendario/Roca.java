package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Roca extends Joker {

    private static final double MULT_POR_PESO = 0.1;

    public Roca() {
        super(
            149,
            "Roca",
            "Roca",
            "x0.1 por cada peso que tengas (Actual: x1)",
            Rareza.legendario,
            1,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA
        );
    }

    @Override
    public String getDescripcionRenderizada() {
        return "x0.1 por cada peso que tengas (Actual: x" + String.format("%.1f", 1 + getAcumulado()) + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego != null && juego.getJugador() != null) {
            double mult = 1 + juego.getJugador().getPesos() * MULT_POR_PESO;
            return "x0.1 por cada peso que tengas (Actual: x" + String.format("%.1f", mult) + ")";
        }

        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Roca copia = new Roca();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO &&
            evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        if (ctx.getResolucionActual() == null ||
            ctx.getJugador() == null) {
            return;
        }
        double mult = 1 + (ctx.getJugador().getPesos() * MULT_POR_PESO);
        ctx.getResolucionActual().multiplicarMult(mult, getNombre(), this);
    }
}
