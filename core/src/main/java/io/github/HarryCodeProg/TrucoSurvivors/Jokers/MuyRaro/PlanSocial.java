package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class PlanSocial extends Joker {

    public PlanSocial() {
        super(102, "PlanSocial", "PlanSocial", "+5 multiplicador envido por cada carta comprada",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE);
    }

    @Override
    public Joker copiar() {
        PlanSocial copia = new PlanSocial();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_COMPRAR_CARTA) {
            sumarAcumulado(5);
        }
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            if (getAcumulado() > 0) {
                ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return "+5 multiplicador envido por cada carta comprada (actual: +" + (int)getAcumulado() + ")";
    }
}
