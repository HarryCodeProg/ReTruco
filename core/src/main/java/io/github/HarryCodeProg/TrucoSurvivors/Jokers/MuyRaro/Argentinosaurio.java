package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Argentinosaurio extends Joker {

    private static final double PUNTOS_INICIALES = 2;

    public Argentinosaurio() {
        super(
            106,
            "Argentinosaurio",
            "Argentinosaurio",
            "(+2) puntos, se duplica despues de derrotar un rival",
            Rareza.muyRaro,
            6,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(PUNTOS_INICIALES);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            sumarAcumulado(getAcumulado()); // duplica: +N == *2
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "(+" + (int) getAcumulado() + ") puntos, se duplica despues de derrotar un rival";
    }

    @Override
    public Joker copiar() {
        Argentinosaurio copia = new Argentinosaurio();
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }
}
