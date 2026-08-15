package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class AK47 extends Joker {

    public AK47() {
        super(38, "AK-47", "AK-47", "+2 tamaño mano",
                Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        AK47 copia = new AK47();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarTamañoMano(2);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarTamañoMano(-2);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){}
}
