package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;

public class SanMartin extends Joker {

    public SanMartin() {
        super(150, "San Martín", "SanMartin",
            "Reactiva todas las cartas Espada. Cada vez que una Espada mata, gana +5 Mult. "
                + "x3 mult truco cada vez que se activa una Espada. Desactiva jokers no-Nacionales.",
            Rareza.legendario, 20, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Reactiva todas las cartas Espada. Cada vez que una Espada mata, +5 multiplicador truco(actual: +" + (int) getAcumulado() + "). "
            + "Reactiva Espadas, x3 multiplicador truco por cada activación. Desactiva jokers no-Nacionales.";
    }

    @Override
    public Joker copiar() {
        SanMartin copia = new SanMartin();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;
        if (!ctx.getJugador().cartaCuentaComoPalo(carta, Palo.ESPADA)) return;
        ctx.getResolucionActual().multiplicarMult(3, getNombre(), this);
        if (ctx.cartaMato(carta)) {
            sumarAcumulado(5);
        }
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
        if (ctx.marcarUsado(this, carta)) {
            ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }
}
