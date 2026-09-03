package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Granadero extends Joker {

    private static final double MULT_EN_ULTIMA_MANO = 3.0;

    public Granadero() {
        super(72, "Granadero", "Granadero",
            "x3 multiplicador truco y envido en la última mano",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        
        // Si al jugador le queda 1 mano actual, es su última mano antes de perder (0)
        if (juego.getJugador().getManosActuales() == 1) {
            ctx.getResolucionActual().multiplicarMult(MULT_EN_ULTIMA_MANO, getNombre(), this);
        }
    }

    @Override
    public Joker copiar() {
        Granadero copia = new Granadero();
        copiarEstado(copia);
        return copia;
    }
}
