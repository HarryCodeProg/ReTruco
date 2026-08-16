package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class MboiTui extends Santo {

    public MboiTui() {
        super(
            9,
            "Mboi Tu'i",
            "MboiTui",
            "Aumenta el numero de hasta 2 cartas seleccionadas",
            3
        );
    }

    @Override
    public int cartasRequeridas() {
        return -1;
    }

    @Override
    public int maxCartasSeleccionables() {
        return 2;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        int limite = Math.min(2, seleccionadas.size());
        for (int i = 0; i < limite; i++) {
            Carta carta = seleccionadas.get(i);
            if (carta.getNumero() < 12) {
                carta.cambiarNumero(carta.getNumero() + 1);
            }
        }
    }
}
