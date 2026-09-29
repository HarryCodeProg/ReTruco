package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Bestiario extends Joker {

    public Bestiario() {
        super(101, "Bestiario", "Bestiario", "+5 multiplicador truco por cada joker comprado",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE);
    }

    @Override
    public Joker copiar() {
        Bestiario copia = new Bestiario();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_COMPRAR_JOKER) {
            sumarAcumulado(5);
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "+5 multiplicador truco por cada joker comprado (actual: +" + (int)getAcumulado() + ")";
    }
}
