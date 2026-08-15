package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class GalletaConRelleno extends Joker {
    public GalletaConRelleno(){
        super(31, "Galleta Con Relleno", "GalletaConRelleno", "+1 descarte",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarDescartesExtra(-1);
    }

    @Override
    public Joker copiar() {
        GalletaConRelleno copia = new GalletaConRelleno();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){}
}
