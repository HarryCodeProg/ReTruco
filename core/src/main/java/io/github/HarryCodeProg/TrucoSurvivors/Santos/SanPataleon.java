package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanPataleon extends Santo{

    public SanPataleon() {
        super(
            22,
            "San Pataleon",
            "SanPataleon",
            "Restaura todos tus Descartes\n",
            5
        );
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
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        if (ctx == null) return;
        ctx.getJuego().restaurarDescartes();
    }
}
