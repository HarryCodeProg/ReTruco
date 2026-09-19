package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Caballo extends Joker {

    public Caballo() {
        super(66, "Caballo", "Caballo", "+6 multiplicador truco por cada figura en tu mano",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Caballo copia = new Caballo();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        int figuras = 0;
        for (Carta c : ctx.getJugador().getMano()) {
            if (c.getNumero() >= 10) figuras++;
        }
        if (figuras > 0) {
            ctx.getResolucionActual().sumarMult(6 * figuras, getNombre(), this);
        }
    }
}
