package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class GauchitoGil extends Santo {

    public GauchitoGil() {
        super(3, "Gauchito Gil", "GauchitoGil", "Otorga $15", 0);
    }

    @Override public int cartasRequeridas() { return 0; }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        int pesos = jugador.tieneDobleEfectoSantos() ? 30 : 15;
        jugador.sumarPesos(pesos);
    }
}
