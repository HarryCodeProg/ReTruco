package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;

import java.util.ArrayList;
import java.util.Random;

public class Pachamama extends Santo {

    private final Random random = new Random();

    public Pachamama() {
        super(
            10,
            "Pachamama",
            "Pachamama",
            "Crea un joker aleatorio (si hay espacio)",
            5
        );
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        if (jugador.getJokers().size() >= jugador.getTamañoJokers()) {
            return;
        }

        PoolJokersTienda pool = new PoolJokersTienda();
        Joker joker = pool.tomarAleatorio(random, jugador);

        if (joker != null) {
            jugador.agregarJoker(joker);
        }
    }
}
