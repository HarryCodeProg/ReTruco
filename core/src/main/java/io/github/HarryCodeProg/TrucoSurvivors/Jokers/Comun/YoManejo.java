package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class YoManejo extends Joker {

    public YoManejo() {
        super(
            52,
            "YoManejo",
            "YoManejo",
            "Reactiva 3 veces la primera carta que mata",
            Rareza.comun,
            1,
            Joker.FaseActivacion.AL_PUNTUAR_CARTA
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_MATAR_CARTA) return;

        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;

        if (ctx.isPrimerCartaQueMataAplicada()) return;

        ctx.marcarPrimerCartaQueMataAplicada();

        for (int i = 0; i < 3; i++) {
            ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }
}
