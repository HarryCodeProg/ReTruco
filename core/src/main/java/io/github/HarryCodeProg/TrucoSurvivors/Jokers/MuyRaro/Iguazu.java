package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Iguazu extends Joker {

    public Iguazu() {
        super(119, "Iguazu", "Iguazu", "+50 puntos por cada vez que\nuna carta cambia de palo",
            Rareza.muyRaro,
            8,
            FaseActivacion.INDEPENDIENTE
        );
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcion() + "\n(actual:+" + (int) getAcumulado() + ")";
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        if (evento == EventoJuego.AL_CAMBIAR_PALO) {
            this.sumarAcumulado(50);
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            if (getAcumulado() > 0 && contexto.getResolucionActual() != null) {
                contexto.getResolucionActual().sumarChips(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public Joker copiar() {
        Iguazu copia = new Iguazu();
        this.copiarEstado(copia);
        return copia;
    }
}
