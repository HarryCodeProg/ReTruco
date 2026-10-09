package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Argentavis extends Joker {

    private static final int PESOS_POR_REACTIVACION = 5;

    public Argentavis() {
        super(122, "Argentavis", "Argentavis",
            "Reactiva la primer carta que mata una vez por cada $5 que tengas",
            Rareza.muyRaro, 6, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Argentavis copia = new Argentavis();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;
        if (!ctx.cartaMato(carta)) return;
        if (ctx.isPrimerCartaQueMataAplicada(this)) return;
        ctx.marcarPrimerCartaQueMataAplicada(this);
        int reactivaciones = ctx.getJugador().getPesos() / PESOS_POR_REACTIVACION;
        for (int i = 0; i < reactivaciones; i++) {
            ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }
}
