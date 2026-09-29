package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Viajero extends Joker {

    public Viajero() {
        super(108, "Viajero", "Viajero", "+8 multiplicador por cada mano restante al final de la ronda",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.INTERNACIONAL);
    }

    @Override
    public Joker copiar() {
        Viajero copia = new Viajero();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            int manosRestantes = ctx.getJugador().getManosActuales();
            if (manosRestantes > 0) {
                sumarAcumulado(8.0 * manosRestantes);
            }
        }

        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "+8 multiplicador por cada mano restante al final de la ronda\n(actual: +" + (int)getAcumulado() + ")";
    }
}
