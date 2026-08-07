package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.AL_JUGAR;

public class Termo extends Joker{

    public Termo(){
        super(
            4,
            "Termo",
            "Termo",
            "La primera carta que juegues cada mano obtiene +15 Valor Envido",
            Rareza.comun,
            1,
            AL_JUGAR,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_JUGAR_PRIMERA_CARTA) return;
        ArrayList<Carta> mesaJugador = ctx.getMesa().getMesaJugador();
        if (mesaJugador.isEmpty()) return;
        Carta cartaJugada = mesaJugador.get(mesaJugador.size() - 1);
        cartaJugada.modificarValorEnvidoPermanente(15);
    }
}
