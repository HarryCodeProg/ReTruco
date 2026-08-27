package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class SantaRita extends Santo {

    public SantaRita() {
        super(23, "Santa Rita", "SantaRita", "Repite el último efecto de Santo utilizado", 3);
    }

    // cartasRequeridas() / maxCartasSeleccionables() se dejan con el default de Santo (probablemente 0/0),
    // porque GestorSantos ya resuelve los requisitos reales consultando jugador.getUltimoSantoUsado()
    // ANTES de decidir el flujo (ver resolverSantoEfectivo en GestorSantos). No hace falta overridearlos acá.


    public String getDescripcionRenderizada() {
        return "Repite el último efecto de Santo utilizado";
        // Si querés texto dinámico con el nombre del objetivo, hace falta exponer el jugador
        // hasta acá (ver nota abajo sobre firmas de Santo).
    }

    @Override
    public int cartasRequeridas() {
        return 0;
    }

    @Override
    public void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctx) {
        Santo ultimo = jugador.getUltimoSantoUsado();
        if (ultimo == null) return; // sin objetivo: no hace nada, se consume igual (como The Fool)
        ultimo.aplicarEfecto(jugador, seleccionadas, ctx);
        this.transferirDiferidosDesde(ultimo); // mueve cualquier flip pendiente hacia esta instancia
    }

    @Override
    public int maxCartasSeleccionables() {
        return 0;
    }
}
