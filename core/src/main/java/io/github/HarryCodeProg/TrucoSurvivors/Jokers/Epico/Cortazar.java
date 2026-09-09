package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Cortazar extends Joker {

    public Cortazar() {
        super(139, "Cortázar", "Cortazar", "Cada vez que agregues una carta al mazo, +1 multiplicador",
            Rareza.epico, 8, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.TV);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "Cada vez que agregues una carta al mazo, +1 multiplicador (actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Cortazar copia = new Cortazar();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_AGREGAR_CARTA_AL_MAZO) {
            sumarAcumulado(1);
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (getAcumulado() > 0) {
            ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
        }
    }
}
