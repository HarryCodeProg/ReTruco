package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.ArrayList;
import java.util.EnumSet;

public class CasaRosada extends Joker {

    private static final double MULT = 3;

    public CasaRosada() {
        super(
            129,
            "CasaRosada",
            "CasaRosada",
            "X3 multiplicador truco si las cartas jugadas son de distinto palo",
            Rareza.muyRaro,
            8, // placeholder: ajustá el coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (ctx.getResolucionActual() == null) return;
        if (!todosPalosDistintos(ctx.getMesa().getMesaJugador())) return;
        ctx.getResolucionActual().multiplicarMult(MULT, getNombre(), this);
    }

    private boolean todosPalosDistintos(ArrayList<Carta> cartasJugadas) {
        if (cartasJugadas.isEmpty()) return false; // sin cartas no hay condición que celebrar
        EnumSet<Palo> vistos = EnumSet.noneOf(Palo.class);
        for (Carta c : cartasJugadas) {
            if (!vistos.add(c.getPalo())) return false; // palo repetido
        }
        return true;
    }

    @Override
    public Joker copiar() {
        CasaRosada copia = new CasaRosada();
        copiarEstado(copia);
        return copia;
    }
}
