package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;
import java.util.Random;

public class SanFrancisco extends Santo {

    private final Random random = new Random();

    public SanFrancisco() {
        super(
            17,
            "San Francisco",
            "SanFrancisco",
            "Elimina 1 carta seleccionada, genera 3 aleatorias",
            4
        );
    }

    @Override
    public int cartasRequeridas() {
        return 1;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        if (seleccionadas.size() != 1) return;
        Carta carta = seleccionadas.get(0);
        jugador.eliminarCarta(carta);
        Palo[] palos = Palo.values();
        for (int i = 0; i < 3; i++) {
            int numero = random.nextInt(12) + 1;
            Palo palo = palos[random.nextInt(palos.length)];
            jugador.getMazo().agregarCarta(
                new Carta(numero, palo)
            );
        }
    }
}
