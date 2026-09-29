package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class BuenosAires extends Joker {

    private static final double MULT_ENVIDO = 10;

    public BuenosAires() {
        super(
            94,
            "BuenosAires",
            "BuenosAires",
            "+10 multiplicador envido. Evita que aparezcan jokers \"comun\" en la tienda",
            Rareza.raro,
            6, // placeholder: ajustá el coste que corresponda
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá categorías si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        ctx.getResolucionActual().sumarMult(MULT_ENVIDO, getNombre(), this);
    }

    @Override
    public boolean bloqueaJokersComunes() {
        return true;
    }

    @Override
    public Joker copiar() {
        BuenosAires copia = new BuenosAires();
        copiarEstado(copia);
        return copia;
    }
}
