package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class SanMartin extends Joker {

    public SanMartin() {
        super(150, "San Martín", "SanMartin",
            "Reactiva todas las cartas Espada. x3 multiplicador truco cada vez que se active una Espada.",
            Rareza.legendario, 20, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA);
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "Reactiva todas las cartas Espada. x3 multiplicador truco cada vez que se active una Espada.";
    }

    @Override
    public Joker copiar() {
        SanMartin copia = new SanMartin();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;
        if (!ctx.getJugador().cartaCuentaComoPalo(carta, Palo.ESPADA)) return;
        // 1. Aplica el multiplicador x3
        ctx.getResolucionActual().multiplicarMult(3, getNombre(), this);
        // 2. Reactiva la carta para que vuelva a puntuar
        if (ctx.marcarUsado(this, carta)) {
            ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }

}
