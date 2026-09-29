package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Gaucho extends Joker {

    public Gaucho() {
        super(
            70,
            "Gaucho",
            "Gaucho",
            "Aumenta el máximo de interés ganado por ronda a $10.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public Joker copiar() {
        Gaucho copia = new Gaucho();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarTopeInteresExtra(5);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarTopeInteresExtra(-5);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
    }
}
