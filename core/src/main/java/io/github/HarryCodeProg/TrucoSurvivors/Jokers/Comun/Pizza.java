package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Pizza extends Joker {
    public Pizza(){
        super(30, "Pizza", "Pizza", "+4 multiplicador envido si tu tanto no tiene una figura",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.COMIDA, CategoriaJoker.SALADO, CategoriaJoker.INTERNACIONAL);
    }

    @Override
    public Joker copiar() {
        Pizza copia = new Pizza();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        boolean tieneFigura = ctx.getJugador().getCartasEnvidoGanador().stream().anyMatch(c -> c.getNumero() >= 10);
        if (!tieneFigura) ctx.getResolucionActual().sumarMult(4, getNombre(), this);
    }
}
