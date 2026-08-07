package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.AL_JUGAR;

public class BotellaCortada extends Joker{

    public BotellaCortada(){
        super(
            6,
            "Botella Cortada",
            "BotellaCortada",
            "La segunda carta que juegues, obtiene +5 multiplicador truco",
            Rareza.comun,
            1,
            AL_JUGAR,
            CategoriaJoker.INTERNACIONAL, CategoriaJoker.BEBIDA, CategoriaJoker.ALCOHOL
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_JUGAR_SEGUNDA_CARTA) return;
        ArrayList<Carta> mesaJugador = ctx.getMesa().getMesaJugador();
        if (mesaJugador.isEmpty()) return;
        Carta cartaJugada = mesaJugador.get(mesaJugador.size() - 1);
        cartaJugada.sumarMultiplicadorTrucoTemporal(5);
    }
}
