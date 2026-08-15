package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

// Dame La Mochila: +2 mult envido por cada reroll hecho en la tienda desde que se compro el joker
public class DameLaMochila extends Joker {

    private static final double MULT_POR_REROLL = 2.0;
    private int rerollsAlComprarse = 0;

    public DameLaMochila(){
        super(49, "Dame La Mochila", "DameLaMochila", "+2 multiplicador envido por cada roll en la tienda",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        this.rerollsAlComprarse = jugador.getRerollsTienda();
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+2 multiplicador envido por cada roll en la tienda (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        DameLaMochila copia = new DameLaMochila();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        int rerolls = ctx.getJugador().getRerollsTienda() - rerollsAlComprarse;
        double acumuladoNuevo = Math.max(0, rerolls) * MULT_POR_REROLL;
        while (getAcumulado() < acumuladoNuevo) sumarAcumulado(MULT_POR_REROLL);
        ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre());
    }
}
