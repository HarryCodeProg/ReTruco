package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanAntonioDePadua extends Santo {

    public SanAntonioDePadua() {
        super(
            13,
            "San Antonio de Padua",
            "SanAntonioDePadua",
            "Selecciona hasta 2 cartas, +3 valor envido",
            3
        );
    }

    @Override
    public int cartasRequeridas() {
        return -1;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        int limite = Math.min(2, seleccionadas.size());
        for (int i = 0; i < limite; i++) {
            seleccionadas.get(i).modificarValorEnvidoPermanente(3);
        }
    }
}
