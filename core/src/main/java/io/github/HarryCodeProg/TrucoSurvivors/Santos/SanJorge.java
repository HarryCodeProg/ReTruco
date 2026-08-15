package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;

import java.util.ArrayList;
import java.util.Random;

public class SanJorge extends Santo {

    private final Random random = new Random();

    public SanJorge() {
        super(
            18,
            "San Jorge",
            "SanJorge",
            "Duplica un joker aleatorio (si hay espacio)",
            5
        );
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        if (jugador.getJokers().isEmpty()) return;
        if (jugador.getJokers().size() >= jugador.getTamañoJokers()) return;
        Joker original = jugador.getJokers().get(random.nextInt(jugador.getJokers().size()));
        Joker copia = original.copiar();
        if (copia != null) {
            jugador.agregarJoker(copia);
        }
    }
}
