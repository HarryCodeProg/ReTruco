package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;


public class Damajuana extends Joker {

    private static final int BEBIDAS_REQUERIDAS = 3;

    public Damajuana(){
        super(41, "Damajuana", "Damajuana", "Si tenés 3 jokers 'Bebida', x3 multiplicador truco",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Damajuana copia = new Damajuana();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        if (ctx.contarJokersConCategoria(CategoriaJoker.BEBIDA, this) < BEBIDAS_REQUERIDAS) return;
        ctx.getResolucionActual().multiplicarMult(3, getNombre());
    }
}
