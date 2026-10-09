package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class QueMiras extends Joker {

    public QueMiras() {
        super(
            59,
            "Que Miras",
            "QueMiras",
            "Las cartas de Espada que pierden envido reciben +15 puntos envido",
            Rareza.comun,
            3,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.INTERNACIONAL, CategoriaJoker.HISTORIA
        );
    }

    @Override
    public Joker copiar() {
        QueMiras copia = new QueMiras();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PERDER_ENVIDO) return;
        for (Carta carta : ctx.getJugador().getMano()) {
            if (carta.getPalo() == Palo.ESPADA) {
                carta.modificarPuntosEnvidoAportePermanente(15);
            }
        }
    }
}
