package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Saavedra extends Joker {

    private static final double MULT_POR_SANTO = 0.25;

    public Saavedra() {
        super(
            138,
            "Saavedra",
            "Saavedra",
            "X0.25 multiplicador envido por cada santo usado (actual: X1)",
            Rareza.epico,
            8, // placeholder: ajustá el coste real
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL // ajustá si corresponde
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.AL_CONSUMIR_SANTO) {
            sumarAcumulado(1); // acumulado = cantidad de santos usados, no puntos
            return;
        }
        if (evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) return;
        if (ctx.getResolucionActual() == null) return;
        double mult = 1 + (getAcumulado() * MULT_POR_SANTO);
        ctx.getResolucionActual().multiplicarMult(mult, getNombre(), this);
    }

    @Override
    public String getDescripcionRenderizada() {
        double actual = 1 + (getAcumulado() * MULT_POR_SANTO);
        return "X0.25 multiplicador envido por cada santo usado (actual: X" + formatear(actual) + ")";
    }

    private String formatear(double valor) {
        if (valor == Math.floor(valor)) return String.valueOf((int) valor);
        return String.format("%.2f", valor);
    }

    @Override
    public Joker copiar() {
        Saavedra copia = new Saavedra();
        copiarEstado(copia);
        return copia;
    }
}
