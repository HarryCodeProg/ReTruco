package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class YoManejo extends Joker {

    public YoManejo() {
        super(52, "YoManejo", "YoManejo", "Reactiva 3 veces la primera carta que mata",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.SECUENCIA, CategoriaJoker.TV);
    }

    @Override
    public Joker copiar() {
        YoManejo copia = new YoManejo();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return; // FIX: reacciona DENTRO de la resolución de puntaje real
        Carta carta = ctx.getCartaEnResolucion();
        if (carta == null) return;
        if (!ctx.cartaMato(carta)) return; // FIX: solo cartas que efectivamente mataron su baza
        if (ctx.isPrimerCartaQueMataAplicada(this)) return;
        ctx.marcarPrimerCartaQueMataAplicada(this);
        for (int i = 0; i < 3; i++) {
            ctx.reencolarActivacionCarta(carta, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }
}
