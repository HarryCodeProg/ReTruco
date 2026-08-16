package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class LuzMala extends Santo {

    public LuzMala() {
        super(27, "Luz Mala", "LuzMala", "Genera 2 santos aleatorios (si hay espacio)", 5);
    }

    @Override public int cartasRequeridas() { return 0; }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        // requiere pool de santos + espacio disponible en jugador; diseño pendiente, ver nota
    }
}
