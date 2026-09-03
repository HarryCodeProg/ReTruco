package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class CañaConRuda extends Joker {

    private static final double MULT_POR_REROLL = 2.0;
    private int rerollsAlComprarse = 0;

    public CañaConRuda(){
        super(60, "Caña Con Ruda", "CañaConRuda", "+2 multiplicador truco por cada roll en la tienda",
            Rareza.comun, 5, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.BEBIDA);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        this.rerollsAlComprarse = jugador.getRerollsTienda();
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+2 multiplicador truco por cada roll en la tienda (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego != null && juego.getJugador() != null) {
            int rerolls = juego.getJugador().getRerollsTienda() - rerollsAlComprarse;
            double actual = Math.max(0, rerolls) * MULT_POR_REROLL;
            return "+2 multiplicador truco por cada roll en la tienda (Actual: +" + (int) actual + ")";
        }
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        CañaConRuda copia = new CañaConRuda();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        copia.rerollsAlComprarse = this.rerollsAlComprarse;
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            return;
        }
        int rerolls = ctx.getJugador().getRerollsTienda() - rerollsAlComprarse;
        double acumuladoNuevo = Math.max(0, rerolls) * MULT_POR_REROLL;
        setAcumulado(acumuladoNuevo);
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
