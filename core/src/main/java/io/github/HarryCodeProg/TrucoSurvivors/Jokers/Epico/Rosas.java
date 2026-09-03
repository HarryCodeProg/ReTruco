package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Rosas extends Joker {

    public Rosas() {
        super(141, "Rosas", "Rosas",
            "Cuando vendas un joker, agrega el valor de venta a multiplicador truco (actual: +0)",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public void onVendido(Joker jokerVendido, Jugador jugador) {
        // El precio de venta es coste / 2 (mínimo 1), igual que en GestorVentaJoker
        int precioVenta = Math.max(1, jokerVendido.getCoste() / 2);
        sumarAcumulado(precioVenta);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Cuando vendas un joker, agrega el valor de venta a multiplicador truco (actual: +"
            + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Rosas copia = new Rosas();
        copiarEstado(copia);
        return copia;
    }
}
