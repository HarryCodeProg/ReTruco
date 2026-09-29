package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

/**
 * Cordoba: +5 multiplicador envido por cada $5 que tengas
 */
public class Cordoba extends Joker {

    public Cordoba() {
        super(87, "Cordoba", "Cordoba", "+5 multiplicador envido por cada $5 que tengas (actual: 0)",
                Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE,
                CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Cordoba copia = new Cordoba();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (juego == null || juego.getTurnoActual() == null) {
            return;
        }

        Jugador jugador = juego.getTurnoActual();
        int dinero = jugador.getPesos();
        int bonusCalculado = (dinero / 5) * 5; // Redondea hacia abajo al múltiplo de 5 más cercano

        // El acumulado almacena el último bonus aplicado
        int bonusAplicado = (int) getAcumulado();
        int delta = bonusCalculado - bonusAplicado;

        if (delta != 0) {
            jugador.aumentarMultiplicadorEnvido(delta);
            setAcumulado(bonusCalculado);
        }
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        if (juego != null && juego.getTurnoActual() != null) {
            int dinero = juego.getTurnoActual().getPesos();
            int bonusActual = (dinero / 5) * 5;
            return getDescripcion().replace("(actual: 0)", "(actual: " + bonusActual + ")");
        }
        return getDescripcion();
    }
}