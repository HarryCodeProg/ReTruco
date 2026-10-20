package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Hornero extends Joker {

    // Lleva la cuenta de cuántos rerolls ya le entregamos al jugador para no duplicarlos
    private int rerollsDados = 0;

    public Hornero() {
        super(79, "Hornero", "Hornero",
            "+1 rolleo de tienda gratuito cada vez que derrotes a un rival (actual: +0)",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.ECONOMIA, CategoriaJoker.FAUNA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Se dispara cuando el jugador llega a la meta y derrota al rival
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            sumarAcumulado(1);
            int totalAcumulado = (int) getAcumulado();
            int faltantes = totalAcumulado - rerollsDados;
            if (faltantes > 0) {
                // Le sumamos al jugador solo los rerolls que le faltan
                ctx.getJugador().sumarRerollsGratisTienda(faltantes);
                rerollsDados += faltantes;
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+1 rolleo de tienda gratuito cada vez que derrotes a un rival (actual: +" + (int) getAcumulado() + ")";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Hornero copia = new Hornero();
        copiarEstado(copia);
        // Al copiar, reiniciamos el contador de dados porque es un joker nuevo
        copia.rerollsDados = 0;
        return copia;
    }
}
