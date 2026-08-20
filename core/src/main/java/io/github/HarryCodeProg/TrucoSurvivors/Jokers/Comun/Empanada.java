package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Empanada extends Joker {
    private static final double BONUS_ENVIDO = 10;

    public Empanada(){
        super(11, "Empanada", "Empanada", "+10 al valor envido final",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.COMIDA);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarBonusEnvidoFinal(BONUS_ENVIDO);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarBonusEnvidoFinal(-BONUS_ENVIDO);
    }

    @Override
    public Joker copiar() {
        Empanada copia = new Empanada();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){}
}
