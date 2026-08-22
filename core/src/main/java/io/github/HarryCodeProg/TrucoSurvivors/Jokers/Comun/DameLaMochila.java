package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class DameLaMochila extends Joker {

    private static final double MULT_POR_REROLL = 2.0;
    private int rerollsAlComprarse = 0;

    public DameLaMochila(){
        super(49, "Dame La Mochila", "DameLaMochila", "+2 multiplicador envido por cada roll en la tienda",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TV, CategoriaJoker.SECUENCIA);
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
    public String getDescripcionRenderizada(Juego juego) {
        if (juego != null && juego.getJugador() != null) {
            int rerolls = juego.getJugador().getRerollsTienda() - rerollsAlComprarse;
            double actual = Math.max(0, rerolls) * MULT_POR_REROLL;
            return "+2 multiplicador envido por cada roll en la tienda (Actual: +" + (int) actual + ")";
        }
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        DameLaMochila copia = new DameLaMochila();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        copia.rerollsAlComprarse = this.rerollsAlComprarse;
        return copia;
    }

    /*@Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        int rerolls = ctx.getJugador().getRerollsTienda() - rerollsAlComprarse;
        double acumuladoNuevo = Math.max(0, rerolls) * MULT_POR_REROLL;
        setAcumulado(acumuladoNuevo);
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }*/

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        System.out.println("DameLaMochila -> evento=" + evento + " | rerollsAlComprarse=" + rerollsAlComprarse + " | rerollsActuales=" + ctx.getJugador().getRerollsTienda() + " | resolucion=" + ctx.getResolucionActual());
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        int rerolls = ctx.getJugador().getRerollsTienda() - rerollsAlComprarse;
        double acumuladoNuevo = Math.max(0, rerolls) * MULT_POR_REROLL;
        setAcumulado(acumuladoNuevo);
        System.out.println("DameLaMochila -> rerolls=" + rerolls + " | acumuladoNuevo=" + acumuladoNuevo + " | acumuladoActual=" + getAcumulado());
        if (getAcumulado() > 0) {
            System.out.println("DameLaMochila -> SUMANDO MULT: +" + getAcumulado());
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
            System.out.println("DameLaMochila -> MULT DESPUES: " + ctx.getResolucionActual().getMult());
        }
    }
}
