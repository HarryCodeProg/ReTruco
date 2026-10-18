package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Perro extends Joker {

    private static final int META_GASTO = 50;
    private static final double MULT = 3;

    public Perro() {
        super(
            131,
            "Perro",
            "Perro",
            "X3 multiplicador, después de gastar ($50) otorga +1 tamaño jokers",
            Rareza.epico,
            8,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.TRUCO,
            CategoriaJoker.ECONOMIA,
            CategoriaJoker.FAUNA
        );
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO &&
            evento != EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            return;
        }
        if (ctx.getResolucionActual() == null) return;
        ctx.getResolucionActual().multiplicarMult(MULT, getNombre(), this);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        // Si al cargarlo desde un guardado ya estaba completado, le devolvemos el espacio
        if (getAcumulado() >= META_GASTO) {
            jugador.setTamañoJokers(jugador.getTamañoJokers() + 1);
        }
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        // Si lo vendemos o destruimos y ya nos había dado el espacio, se lo quitamos
        if (getAcumulado() >= META_GASTO) {
            jugador.setTamañoJokers(jugador.getTamañoJokers() - 1);
        }
    }

    @Override
    public void onPesosGastados(int cantidad, Jugador jugador) {
        boolean estabaCompletado = getAcumulado() >= META_GASTO;

        if (!estabaCompletado) {
            sumarAcumulado(cantidad);

            // Si con este gasto exacto acabamos de llegar a la meta, otorgamos el espacio
            if (getAcumulado() >= META_GASTO) {
                jugador.setTamañoJokers(jugador.getTamañoJokers() + 1);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        int gastado = (int) Math.min(getAcumulado(), META_GASTO);
        if (gastado >= META_GASTO) {
            return "X3 multiplicador. +1 tamaño jokers (gastado $" + META_GASTO + ")";
        }
        return getDescripcion() + " ($" + gastado + "/" + META_GASTO + ")";
    }

    @Override
    public Joker copiar() {
        Perro copia = new Perro();
        copiarEstado(copia);
        return copia;
    }
}
