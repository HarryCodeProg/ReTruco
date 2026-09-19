package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class Soda extends Joker {

    public Soda() {
        super(
            51,
            "Soda",
            "Soda",
            "+125 puntos truco, -5 puntos truco cada mano jugada.",
            Rareza.comun,
            4,
            INDEPENDIENTE,
            CategoriaJoker.BEBIDA
        );
        // Iniciamos el acumulador con su valor máximo
        setAcumulado(125);
    }

    @Override
    public Joker copiar() {
        Soda copia = new Soda();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Suma los puntos actuales al Truco
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            double puntosActuales = getAcumulado();
            if (puntosActuales > 0) {
                ctx.getResolucionActual().sumarChips(puntosActuales, this.getNombre(), this);
            }
        }
        // 2. Se degrada cada vez que se juega una mano
        if (evento == EventoJuego.TERMINO_MANO) {
            setAcumulado(getAcumulado() - 5);
            // 3. Destrucción si se queda sin "gas"
            if (getAcumulado() <= 0) {
                // Adaptá este método al nombre real que use tu clase Jugador para descartar/vender un Joker
                juego.getJugador().getJokers().remove(this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        // Mostramos dinámicamente cuánta Soda le queda usando el color AZUL para los puntos
        int actual = (int) getAcumulado();
        return "+125 puntos truco, -5 puntos truco cada mano jugada.\n(Actual: +" + actual + ")";
    }
}
