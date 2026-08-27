package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SanMartinDePorres extends Santo{

    public SanMartinDePorres() {
        super(
            20,
            "San Martin De Porres",
            "SanMartinDePorres",
            "+1 rolleo de tienda gratuito",
            8
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
        jugador.sumarRerollsGratisTienda(1);
    }
}
