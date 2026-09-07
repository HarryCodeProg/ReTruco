package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Gardel extends Joker {

    public Gardel() {
        super(136, "Gardel", "Gardel",
            "+15 puntos truco cada vez que una carta se activa (actual: +0)",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.MUSICA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_MATAR_CARTA) {
            if (ctx.getCartaEnResolucion() != null) {
                sumarAcumulado(15);
            }
            return;
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+15 puntos truco cada vez que una carta se activa (actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Gardel copia = new Gardel();
        copiarEstado(copia);
        return copia;
    }
}
