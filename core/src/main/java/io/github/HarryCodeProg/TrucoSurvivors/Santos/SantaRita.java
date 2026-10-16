package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SantaRita extends Santo {

    public SantaRita() {
        super(23, "Santa Rita", "SantaRita", "Repite el último efecto de Santo utilizado", 3);
    }

    public String getDescripcionRenderizada() {
        return "Repite el último efecto de Santo utilizado";
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        Santo ultimo = jugador.getUltimoSantoUsado();
        if (ultimo == null) return;
        ultimo.aplicarEfecto(jugador, seleccionadas, ctx);
        this.transferirDiferidosDesde(ultimo);
    }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }
}
