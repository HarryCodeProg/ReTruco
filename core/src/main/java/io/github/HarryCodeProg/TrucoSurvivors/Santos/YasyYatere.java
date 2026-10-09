package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class YasyYatere extends Santo {

    public YasyYatere() {
        super(24, "Yasy Yatere", "YasyYatere", "Selecciona 2 cartas: la derecha se convierte en la izquierda", 4);
    }

    @Override public int cartasRequeridas() { return 2; }

    @Override
    public int maxCartasSeleccionables() {
        return 2;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        if (seleccionadas.size() != 2) return;
        Carta izquierda = seleccionadas.get(0);
        Carta derecha = seleccionadas.get(1);
        Palo paloDestino = izquierda.getPalo();
        int numeroDestino = izquierda.getNumero();
        diferirCambioVisual(derecha, () -> {
            derecha.cambiarPalo(paloDestino);
            derecha.cambiarNumero(numeroDestino);
        });
    }
}
