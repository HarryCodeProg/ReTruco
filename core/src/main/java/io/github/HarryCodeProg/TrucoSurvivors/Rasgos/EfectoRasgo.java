package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public interface EfectoRasgo {
    enum TipoCanto { TRUCO, ENVIDO }

    default void onActivar(Juego juego) {}
    default void onDesactivar(Juego juego) {}
    default void onEvento(EventoJuego evento, ContextoJuego ctx, Juego juego) {}

    /** Suma al valor del canto (se aplica antes del multiplicador). */
    default double getBonusCanto(TipoCanto tipo) { return 0.0; }
    default double getMultiplicadorCanto(TipoCanto tipo) { return 1.0; }
    default int getReactivacionesPrimeraCarta() { return 0; }

    static boolean esPuntaje(EventoJuego e) {
        return e == EventoJuego.ANTES_DE_SUMAR_TRUCO || e == EventoJuego.ANTES_DE_SUMAR_ENVIDO;
    }
}
