package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Pelota extends Joker {

    private static final double PUNTOS_POR_CONSECUTIVO = 50;

    public Pelota() {
        super(112, "Pelota", "Pelota",
            "+1 mano. +50 puntos envido por cada envido ganado de forma consecutiva (Actual: +0)",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) { jugador.aumentarManosMaximas(1); }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) { jugador.aumentarManosMaximas(-1); }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        int racha = juego.getEnvidosGanadosConsecutivos();
        if (racha <= 0) return;
        ctx.getResolucionActual().sumarChips(racha * PUNTOS_POR_CONSECUTIVO, getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int actual = juego != null ? juego.getEnvidosGanadosConsecutivos() : 0;
        return "+1 mano. +50 puntos envido por cada envido ganado de forma consecutiva (Actual: +" + (actual * (int) PUNTOS_POR_CONSECUTIVO) + ")";
    }

    @Override
    public Joker copiar() {
        Pelota copia = new Pelota();
        copiarEstado(copia);
        return copia;
    }
}
