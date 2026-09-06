package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Belgrano extends Joker {

    public Belgrano() {
        super(147, "Belgrano", "Belgrano",
            "Al final de las activaciones, equilibra los puntos y el multiplicador",
            Rareza.legendario, 20, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA);
    }

    @Override
    public Joker copiar() {
        Belgrano copia = new Belgrano();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        ctx.getResolucionActual().igualarChipsYMult(getNombre(), this);
    }
}
