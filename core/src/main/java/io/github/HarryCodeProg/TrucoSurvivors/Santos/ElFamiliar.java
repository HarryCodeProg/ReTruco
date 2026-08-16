package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class ElFamiliar extends Santo {

    public ElFamiliar() {
        super(2, "El Familiar", "ElFamiliar", "Selecciona 2 cartas: la izquierda se convierte en la derecha", 4);
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
        izquierda.cambiarPalo(derecha.getPalo());
        izquierda.cambiarNumero(derecha.getNumero());
    }
}
