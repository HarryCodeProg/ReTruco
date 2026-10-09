package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Tero extends Joker {

    private static final double PUNTOS_POR_DESCARTE = 2;

    public Tero() {
        super(
            61,
            "Tero",
            "Tero",
            "+2 puntos por cada carta descartada (actual: +0)",
            Rareza.raro,
            6, // placeholder: ajustá coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_DESCARTAR) {
            sumarAcumulado(ctx.getCartasDescartadasEsteEvento() * PUNTOS_POR_DESCARTE);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        if (getAcumulado() <= 0) return;
        ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+2 puntos por cada carta descartada (actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Tero copia = new Tero();
        copiarEstado(copia);
        return copia;
    }
}
