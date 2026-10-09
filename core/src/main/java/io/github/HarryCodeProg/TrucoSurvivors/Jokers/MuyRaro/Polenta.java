package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Polenta extends Joker {

    private static final double MULT_EN_PRIMER_MANO = 3.0;

    public Polenta() {
        super(118, "Polenta", "Polenta",
            "X3 multiplicador en la primer mano",
            Rareza.muyRaro, 8, FaseActivacion.INDEPENDIENTE);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        int manosMax = juego.getJugador().getManosMaximas();
        int manosAct = juego.getJugador().getManosActuales();
        // Lógica clave: El Envido ocurre ANTES de consumir la mano (manosAct == manosMax).
        // El Truco ocurre DESPUÉS de ejecutar consumirHand() en finalizarManoTruco() (manosAct == manosMax - 1).
        boolean esPrimeraMano = (manosAct == manosMax) ||
            (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO && manosAct == manosMax - 1);
        if (esPrimeraMano) {
            ctx.getResolucionActual().multiplicarMult(MULT_EN_PRIMER_MANO, getNombre(), this);
        }
    }

    @Override
    public Joker copiar() {
        Polenta copia = new Polenta();
        copiarEstado(copia);
        return copia;
    }
}
