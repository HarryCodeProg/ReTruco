package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class RioNegro extends Joker {

    public RioNegro() {
        super(96, "Río Negro", "RioNegro", "Cada vez que se active una figura en envido, x2 multiplicador envido",
            Rareza.raro, 6, Joker.FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        RioNegro copia = new RioNegro();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PUNTUAR_CARTA_ENVIDO) return; // solo reacciona a cartas de ENVIDO
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null || carta.getNumero() < 10) return; // figura = número >= 10
        ctx.getResolucionActual().multiplicarMult(2, getNombre(), this);
    }
}
