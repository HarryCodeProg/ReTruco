package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class MartinFierro extends Joker {

    public MartinFierro() {
        super(75, "Martin Fierro", "MartinFierro",
            "Al ganar al rival, genera +1 reactivacion de las cartas por cada otro joker 'Nacional' (actual: 0)",
            Rareza.raro, 6, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL, CategoriaJoker.HISTORIA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;
        if (!ctx.cartaMato(carta)) return;

        // Evita entrar en bucle infinito si la carta se reactiva por este mismo joker
        if (ctx.fueUsado(this, carta)) return;

        int cantidadNacionales = ctx.contarJokersConCategoria(CategoriaJoker.NACIONAL, this);
        if (cantidadNacionales <= 0) return;

        ctx.marcarUsado(this, carta);

        for (int i = 0; i < cantidadNacionales; i++) {
            ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int cantidadNacionales = 0;
        if (juego != null && juego.getJugador() != null) {
            for (Joker j : juego.getJugador().getJokers()) {
                if (j != this && j.tieneCategoria(CategoriaJoker.NACIONAL)) {
                    cantidadNacionales++;
                }
            }
        }
        return "Al ganar al rival, genera +1 reactivacion de las cartas por cada otro joker 'Nacional' (actual: " + cantidadNacionales + ")";
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Al ganar al rival, genera +1 reactivacion de las cartas por cada otro joker 'Nacional' (actual: 0)";
    }

    @Override
    public Joker copiar() {
        MartinFierro copia = new MartinFierro();
        copiarEstado(copia);
        return copia;
    }
}
