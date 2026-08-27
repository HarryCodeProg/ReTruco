package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanNicolas extends Santo {

    public SanNicolas() {
        super(21, "San Nicolás", "SanNicolas", "Restaura una jugada de mano", 6);
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctxOpcional) {
        jugador.restaurarUnaMano(); // ya chequea internamente que no supere el máximo
    }
}
