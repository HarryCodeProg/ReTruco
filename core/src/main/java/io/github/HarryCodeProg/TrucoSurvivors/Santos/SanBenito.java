package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanBenito extends Santo {

    public SanBenito() {
        super(
            14,
            "San Benito",
            "SanBenito",
            "Selecciona hasta 2 cartas, estas se duplican",
            4
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
            Carta copia = new Carta(seleccionadas.get(i));
            jugador.getMazo().agregarCarta(copia);
        }
    }
}
