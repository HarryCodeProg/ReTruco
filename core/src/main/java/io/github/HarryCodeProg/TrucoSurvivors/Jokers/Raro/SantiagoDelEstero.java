package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class SantiagoDelEstero extends Joker {

    public SantiagoDelEstero() {
        super(
            82,
            "Santiago del Estero",
            "SantiagoDelEstero",
            "X1 Multiplicador Truco y Envido por cada espacio de consumible (Santo) vacío.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public Joker copiar() {
        SantiagoDelEstero copia = new SantiagoDelEstero();
        copiarEstado(copia);
        return copia;
    }

    private double calcularMultiplicadorActual(ContextoJuego ctx) {
        int capacidadMaxima = ctx.getJugador().getTamañoSantos();
        int santosActuales = ctx.getJugador().getSantos().size();
        int espaciosVacios = Math.max(0, capacidadMaxima - santosActuales);
        return Math.max(1.0, (double) espaciosVacios);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Escucha ambos eventos para aplicar el multiplicador tanto al Truco como al Envido
        if (evento == EventoJuego.ANTES_DE_SUMAR_TRUCO || evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            double multActual = calcularMultiplicadorActual(ctx);
            // Solo lo aplicamos si es mayor a 1, para no multiplicar por 1 (que es redundante)
            // ni multiplicar por 0 (que arruinaría el puntaje)
            if (multActual > 1.0) {
                ctx.getResolucionActual().multiplicarMult(multActual, getNombre(), this);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego == null || juego.getJugador() == null) {
            return getDescripcion() + "\n\n(Actual: X1)";
        }
        int capacidadMaxima = juego.getJugador().getTamañoSantos();
        int santosActuales = juego.getJugador().getSantos().size();
        int espaciosVacios = Math.max(0, capacidadMaxima - santosActuales);
        double multActual = Math.max(1.0, (double) espaciosVacios);
        String multTexto = String.valueOf(multActual);
        if (multTexto.endsWith(".0")) {
            multTexto = multTexto.substring(0, multTexto.length() - 2);
        }
        return getDescripcion() + "\n\n(Actual: X" + multTexto + ")";
    }
}
