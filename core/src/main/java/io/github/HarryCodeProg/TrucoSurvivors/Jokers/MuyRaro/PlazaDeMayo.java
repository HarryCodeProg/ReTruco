package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class PlazaDeMayo extends Joker {

    public PlazaDeMayo() {
        super(
            127,
            "Plaza de Mayo",
            "PlazaDeMayo",
            "+7 puntos envido por cada activación de carta (Actual: +)",
            Rareza.muyRaro,
            12,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public Joker copiar() {
        PlazaDeMayo copia = new PlazaDeMayo();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public String getDescripcion() {
        return "+7 puntos envido por cada activación de carta.\n(Actual: +" + (int) getAcumulado() + " puntos)";
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_PUNTUAR_CARTA) {
            setAcumulado(getAcumulado() + 7);
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
            }
        }
    }
}
