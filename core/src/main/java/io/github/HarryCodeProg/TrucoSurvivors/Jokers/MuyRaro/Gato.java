package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Gato extends Joker {

    private static final double PUNTOS_POR_CONSUMIBLE = 70;

    public Gato() {
        super(
            124,
            "Gato",
            "Gato",
            "Al inicio de la ronda consume todos tus consumibles, gana +70 puntos truco y envido por cada uno (actual: +0)",
            Rareza.muyRaro,
            6,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.INICIO_COMBATE) {
            Jugador jugador = ctx.getJugador();
            int consumidos = jugador.consumirTodosLosSantos();
            if (consumidos > 0) {
                sumarAcumulado(consumidos * PUNTOS_POR_CONSUMIBLE);
            }
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO &&
            evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        if (ctx.getResolucionActual() == null) return;
        if (getAcumulado() <= 0) return;
        ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Al inicio de la ronda consume todos tus consumibles, gana +70 puntos truco y envido por cada uno (actual: +"
            + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Gato copia = new Gato();
        copiarEstado(copia);
        return copia;
    }
}
