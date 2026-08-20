package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class AndaALaCancha extends Joker {

    private static final double MULT_POR_DESCARTE = 5.0;

    public AndaALaCancha(){
        super(42, "Anda A La Cancha", "AndaALaCancha", "+5 multiplicador truco y envido por cada descarte restante",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TV, CategoriaJoker.SECUENCIA);
    }

    @Override
    public Joker copiar() {
        AndaALaCancha copia = new AndaALaCancha();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO
            && evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        double bonus = juego.getDescartesActuales() * MULT_POR_DESCARTE;
        if (bonus <= 0) return;
        ctx.getResolucionActual().sumarMult(bonus, getNombre(), this);
    }
}
