package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanRoque extends Santo {

    public SanRoque() {
        super(
            12,
            "San Roque",
            "SanRoque",
            "Selecciona hasta 2 cartas, +3 valor truco",
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
            seleccionadas.get(i).modificarValorTrucoPermanente(3);
        }
    }
}
