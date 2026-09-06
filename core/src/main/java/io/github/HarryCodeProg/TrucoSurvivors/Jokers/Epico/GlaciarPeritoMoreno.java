package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class GlaciarPeritoMoreno extends Joker {

    private static final double MULT_POR_VENTA = 0.15;

    public GlaciarPeritoMoreno() {
        super(142, "Glaciar Perito Moreno", "GlaciarPeritoMoreno",
            "Gana x0.15 de multiplicador truco cada vez que vendas un joker (Actual: x1)",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.NATURALEZA);
        // Acumulado representa el multiplicador total: arranca en 1
        setAcumulado(1.0);
    }

    @Override
    public void onVendido(Joker jokerVendido, Jugador jugador) {
        sumarAcumulado(MULT_POR_VENTA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (getAcumulado() > 1.0) {
            ctx.getResolucionActual().multiplicarMult(getAcumulado(), getNombre(), this);
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Gana x0.15 de multiplicador truco cada vez que vendas un joker (Actual: x"
            + String.format("%.2f", getAcumulado()) + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        GlaciarPeritoMoreno copia = new GlaciarPeritoMoreno();
        copiarEstado(copia);
        return copia;
    }
}
