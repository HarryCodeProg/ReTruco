package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Chimichurri extends Joker {
    public Chimichurri(){
        super(27, "Chimichurri", "Chimichurri", "+2 Mult truco por cada mano ganada de forma consecutiva",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL, CategoriaJoker.COMIDA, CategoriaJoker.TRADICIONAL);
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+2 Mult truco por cada mano ganada de forma consecutiva (Actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public Joker copiar() {
        Chimichurri copia = new Chimichurri();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        double acumuladoNuevo = juego.getManosGanadasConsecutivas() * 2.0;
        // sincronizamos el campo interno (para mostrar en cartel) y sumamos al mult
        while (getAcumulado() < acumuladoNuevo) sumarAcumulado(2);
        ctx.getResolucionActual().sumarMult(getAcumulado(), getNombre(), this);
    }
}
