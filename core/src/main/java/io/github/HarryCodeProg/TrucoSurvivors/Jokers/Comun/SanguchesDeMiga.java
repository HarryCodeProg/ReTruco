package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.ColorMecanica;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class SanguchesDeMiga extends Joker {

    private static final int BONUS_POR_GASTRONOMIA = 25;

    public SanguchesDeMiga() {
        super(
            53,
            "Sanguches de Miga",
            "SanguchesDeMiga",
            "Gana +25 puntos truco por cada joker 'GASTRONOMIA'",
            Rareza.comun,
            3,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TRUCO, CategoriaJoker.GASTRONOMIA
        );
    }

    @Override
    public Joker copiar() {
        SanguchesDeMiga copia = new SanguchesDeMiga();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;

        int bonus = calcularBonusActual(ctx.getJugador());
        if (bonus <= 0) return;

        ctx.getResolucionActual().sumarChips(bonus, getNombre(), this);
    }

    /** Cuenta cuántos jokers tienen GASTRONOMIA (incluyéndose) y devuelve el bono del momento. */
    private int calcularBonusActual(Jugador jugador) {
        if (jugador == null) return 0;
        int cantidad = 0;
        for (Joker joker : jugador.getJokers()) {
            if (joker.tieneCategoria(CategoriaJoker.GASTRONOMIA)) {
                cantidad++;
            }
        }
        return cantidad * BONUS_POR_GASTRONOMIA;
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int bonus = (juego != null) ? calcularBonusActual(juego.getJugador()) : 0;
        return getDescripcion() + "\n(Actual: "
            + ColorMecanica.PUNTOS_TRUCO.colorHex() + "+" + bonus + "[])";
    }

    @Override
    public String getDescripcionRenderizada() {
        // Sin acceso al estado del juego, no hay forma de calcular el bonus actual.
        return getDescripcion();
    }
}
