package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Oblea extends Joker{

    public Oblea(){
        super(32, "Oblea", "Oblea", "+1 tamaño mano, -1 descarte",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.DULCE, CategoriaJoker.POSTRE);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(-1);
        jugador.aumentarTamañoMano(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(+1);
        jugador.aumentarTamañoMano(-1);
    }

    @Override
    public Joker copiar() {
        Oblea copia = new Oblea();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){}
}
