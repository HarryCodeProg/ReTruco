package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;

public class Argentavis extends Joker {

    private static final int PESOS_POR_REACTIVACION = 5;

    public Argentavis() {
        super(122, "Argentavis", "Argentavis",
            "Reactiva la primer carta que mata una vez por cada $5 que tengas",
            Rareza.muyRaro, 6, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.TRUCO, CategoriaJoker.ACTIVACION, CategoriaJoker.FAUNA);
    }

    @Override
    public Joker copiar() {
        Argentavis copia = new Argentavis();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        boolean esTruco  = (evento == EventoJuego.AL_PUNTUAR_CARTA);
        boolean esEnvido = (evento == EventoJuego.AL_PUNTUAR_CARTA_ENVIDO);
        if (!esTruco && !esEnvido) return;
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;
        // 1) Validar que la carta sea relevante (primero, antes de marcar el guard)
        if (esTruco) {
            if (!ctx.cartaMato(carta)) return;
        } else {
            ArrayList<Carta> contribuyentes = ctx.getCartasContribuyentesEnvido();
            if (contribuyentes == null || !contribuyentes.contains(carta)) return;
        }
        // 2) Guard: solo la primera carta relevante dispara, por resolución
        String claveGuard = esTruco ? "argentavis_truco" : "argentavis_envido";
        if (!ctx.marcarUsado(this, claveGuard)) return;
        // 3) Reactivar N veces según pesos actuales
        int reactivaciones = ctx.getJugador().getPesos() / PESOS_POR_REACTIVACION;
        for (int i = 0; i < reactivaciones; i++) {
            ctx.reencolarActivacionCarta(carta, evento);  // ← usar el evento recibido
        }
    }
}
