package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

public class Neuquen extends Joker {

    public Neuquen() {
        super(95, "Neuquen", "Neuquen", "El proximo zodiaco es Piscis garantizado, despues consume este joker",
            Rareza.raro,
            7,
            FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.setProximoZodiacoForzado(SignoZodiaco.PISCIS);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        boolean tieneOtroNeuquen = jugador.getJokers().stream().anyMatch(j -> j.getId() == 95);
        if (!tieneOtroNeuquen) {
            jugador.setProximoZodiacoForzado(null);
        }
    }

    @Override
    public Joker copiar() {
        Neuquen copia = new Neuquen();
        copiarEstado(copia);
        return copia;
    }
}
