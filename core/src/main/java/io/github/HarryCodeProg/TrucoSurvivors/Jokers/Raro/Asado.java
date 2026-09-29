package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Asado extends Joker {

    public Asado() {
        super(64, "Asado", "Asado", "+1 multiplicador envido por cada santo consumido",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+1 multiplicador envido por cada santo consumido (actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Asado copia = new Asado();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_CONSUMIR_SANTO) {
            sumarAcumulado(1);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
