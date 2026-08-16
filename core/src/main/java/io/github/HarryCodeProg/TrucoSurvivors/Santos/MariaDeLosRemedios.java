package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class MariaDeLosRemedios extends Santo {

    public MariaDeLosRemedios() {
        super(
            8,
            "María de los Remedios",
            "MariaDeLosRemedios",
            "Selecciona hasta 2 cartas, +15 puntos envido",
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
            seleccionadas.get(i).modificarPuntosEnvidoAportePermanente(15);
        }
    }
}
