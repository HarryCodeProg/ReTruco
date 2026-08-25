package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class CuraBrochero extends Santo {

    public CuraBrochero() {
        super(
            6,
            "Cura Brochero",
            "CuraBrochero",
            "Cambia hasta 3 cartas seleccionadas al Palo Espada",
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
            Carta carta = seleccionadas.get(i);
            diferirCambioVisual(carta, () -> carta.cambiarPalo(Palo.ESPADA));
        }
    }
}
