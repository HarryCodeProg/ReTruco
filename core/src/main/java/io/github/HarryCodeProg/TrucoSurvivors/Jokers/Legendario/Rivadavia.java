package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Rivadavia extends Joker {

    private static final int UMBRAL_CARTAS = 30;
    private static final double FACTOR_POR_UMBRAL = 1.5;

    private int cartasRestantes = UMBRAL_CARTAS;
    private double multiplicadorAcumulado = 1.0;

    public Rivadavia() {
        super(146, "Rivadavia", "Rivadavia", "Cada 30 cartas repartidas ganas x1.5 multiplicador truco",
            Rareza.legendario, 15, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Cada ["+cartasRestantes+"] cartas repartidas ganas x1.5 multiplicador truco (actual: x"
            + String.format("%.1f", multiplicadorAcumulado);
    }

    @Override
    public Joker copiar() {
        Rivadavia copia = new Rivadavia();
        copiarEstado(copia);
        copia.cartasRestantes = this.cartasRestantes;
        copia.multiplicadorAcumulado = this.multiplicadorAcumulado;
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.POST_REPARTO) {
            int repartidas = ctx.getCartasRepartidasEsteEvento();
            if (repartidas <= 0) return;
            cartasRestantes -= repartidas;
            while (cartasRestantes <= 0) { // por si en un solo reparto se cruzan varios umbrales
                multiplicadorAcumulado *= FACTOR_POR_UMBRAL;
                cartasRestantes += UMBRAL_CARTAS;
            }
            return;
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (multiplicadorAcumulado > 1.0) {
                ctx.getResolucionActual().multiplicarMult(multiplicadorAcumulado, getNombre(), this);
            }
        }
    }
}
