package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Ceibo extends Joker {

    public Ceibo() {
        super(110, "Ceibo", "Ceibo",
            "Al seleccionar rival, consume el Joker a la derecha y agrega permanentemente el doble de su valor de venta como Multiplicador truco (actual: +0)",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.INICIO_COMBATE) return;
        Jugador jugador = ctx.getJugador();
        Joker vecino = ctx.obtenerJokerALaDerecha(this);
        if (vecino == null) return;
        double bonus = vecino.getPrecioVenta() * 2;
        jugador.eliminarJoker(vecino);
        jugador.aumentarMultiplicadorTruco(bonus);
        sumarAcumulado(bonus);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Al seleccionar rival, consume el Joker a la derecha y agrega permanentemente el doble de su valor de venta como Multiplicador truco (actual: +"
            + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Ceibo copia = new Ceibo();
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }
}
