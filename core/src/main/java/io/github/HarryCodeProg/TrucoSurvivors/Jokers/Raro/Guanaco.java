package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro.Firulais;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Guanaco extends Joker {
    private static final int DESCARTES_EXTRA = 1;
    private static final double MULT_POR_DESCARTE = 12;

    public Guanaco() {
        super(
            67,
            "Guanaco",
            "Guanaco",
            "+1 descarte. 12 multiplicador por cada descarte restante (actual: +0)",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.ANIMAL
        );
        // El acumulado va a guardar el total histórico de descartes ahorrados
        setAcumulado(0);
    }

    @Override
    public Joker copiar() {
        Guanaco copia = new Guanaco();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(DESCARTES_EXTRA);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(-DESCARTES_EXTRA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        int descartesRestantes = juego.getDescartesActuales();
        if (descartesRestantes <= 0) return;
        double mult = MULT_POR_DESCARTE * descartesRestantes;
        ctx.getResolucionActual().multiplicarMult(mult, getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+2 descarte. 12 multiplicador por cada descarte restante (actual: +0)";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego != null) {
            double actual = MULT_POR_DESCARTE * juego.getDescartesActuales();
            return "+2 descarte. 12 multiplicador por cada descarte restante (actual: +" + (int) actual + ")";
        }
        return getDescripcionRenderizada();
    }

}
