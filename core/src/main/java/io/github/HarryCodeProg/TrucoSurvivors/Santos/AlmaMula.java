package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class AlmaMula extends Santo {

    public AlmaMula() {
        super(
            4,
            "Alma Mula",
            "AlmaMula",
            "Cambia hasta 3 cartas seleccionadas al Palo Basto",
            3
        );
    }

    @Override
    public int cartasRequeridas() {return -1;}

    @Override
    public int maxCartasSeleccionables() {
        return 3;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        int limite = Math.min(3, seleccionadas.size());
        for (int i = 0; i < limite; i++) {
            seleccionadas.get(i).cambiarPalo(Palo.BASTO);
        }
    }
}
