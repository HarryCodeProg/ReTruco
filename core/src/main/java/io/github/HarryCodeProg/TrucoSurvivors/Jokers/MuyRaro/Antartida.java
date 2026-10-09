package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Antartida extends Joker {

    private static final int META = 20;
    private static final double MULT = 4;
    private int restante = META;
    private double multAcumulado = 1;

    public Antartida() {
        super(123, "Antartida", "Antartida",
            "X4 multiplicador cada 20 activaciones de cartas (restante: 20)",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_PUNTUAR_CARTA || evento == EventoJuego.AL_PUNTUAR_CARTA_ENVIDO) {
            restante--;
            if (restante <= 0) {
                multAcumulado *= MULT;
                restante = META;
            }
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        if (multAcumulado <= 1) return;
        ctx.getResolucionActual().multiplicarMult(multAcumulado, getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "X4 multiplicador cada 20 activaciones de cartas (restante: " + restante + ")";
    }

    @Override
    public Joker copiar() {
        Antartida copia = new Antartida();
        copia.restante = this.restante;
        copia.multAcumulado = this.multAcumulado;
        copiarEstado(copia);
        return copia;
    }
}
