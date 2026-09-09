package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Hornero extends Joker {

    public Hornero() {
        super(79, "Hornero", "Hornero",
            "+1 rolleo de tienda gratuito cada vez que derrotes un rival (actual: 0)",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.ANIMAL, CategoriaJoker.NACIONAL);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_GANAR_TRUCO) {
            // Acumulamos +1 reroll pendiente por cada victoria
            sumarAcumulado(1);
            return;
        }

        if (evento == EventoJuego.TERMINO_MANO) {
            // Al terminar la mano (antes de ir a la tienda), volcamos los rerolls acumulados al jugador
            int pendientes = (int) getAcumulado();
            if (pendientes > 0) {
                ctx.getJugador().sumarRerollsGratisTienda(pendientes);
                setAcumulado(0); // Reseteamos para la próxima ronda de combates
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+1 rolleo de tienda gratuito cada vez que derrotes un rival (actual: " + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Hornero copia = new Hornero();
        copiarEstado(copia);
        return copia;
    }
}
