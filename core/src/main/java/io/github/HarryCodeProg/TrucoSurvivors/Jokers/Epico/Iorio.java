package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Iorio extends Joker {

    public Iorio() {
        super(135, "Iorio", "Iorio",
            "+3 manos",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.MUSICA);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarManosMaximas(3);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarManosMaximas(-3);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Efecto pasivo
    }

    @Override
    public Joker copiar() {
        Iorio copia = new Iorio();
        copiarEstado(copia);
        return copia;
    }
}
