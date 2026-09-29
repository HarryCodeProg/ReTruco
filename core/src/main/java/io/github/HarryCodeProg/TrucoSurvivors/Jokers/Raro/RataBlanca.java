package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class RataBlanca extends Joker {

    public RataBlanca() {
        super(
            69,
            "RataBlanca",
            "RataBlanca",
            "+1 mano.\n+10 puntos truco cada vez que una de tus cartas mate.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.ANIMAL
        );
        setAcumulado(0);
    }

    @Override
    public Joker copiar() {
        RataBlanca copia = new RataBlanca();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarManosMaximas(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarManosMaximas(-1);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Escala el acumulador cuando tu carta le gana a la del rival
        if (evento == EventoJuego.AL_MATAR_CARTA) {
            setAcumulado(getAcumulado() + 10);
        }
        // 2. Aplica los puntos guardados al Truco
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            double puntosActuales = getAcumulado();
            if (puntosActuales > 0) {
                ctx.getResolucionActual().sumarChips(puntosActuales, this.getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int actual = (int) getAcumulado();
        return getDescripcion() + "\n\n(Actual: +" + actual + " puntos)";
    }
}
