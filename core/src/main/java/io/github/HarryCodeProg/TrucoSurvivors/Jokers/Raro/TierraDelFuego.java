package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class TierraDelFuego extends Joker {

    private static final double PUNTOS_POR_ACTIVACION = 5;

    public TierraDelFuego() {
        super(
            99,
            "TierraDelFuego",
            "TierraDelFuego",
            "+5 puntos envido y truco cada vez que se active otro joker (actual: +0)",
            Rareza.raro,
            6, // placeholder: ajustá el coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void onOtroJokerActivado(Joker otro, EventoJuego evento, ContextoJuego ctx, Juego juego) {
        sumarAcumulado(PUNTOS_POR_ACTIVACION);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
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
        return "+5 puntos envido y truco cada vez que se active otro joker (actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        TierraDelFuego copia = new TierraDelFuego();
        copiarEstado(copia);
        return copia;
    }
}
