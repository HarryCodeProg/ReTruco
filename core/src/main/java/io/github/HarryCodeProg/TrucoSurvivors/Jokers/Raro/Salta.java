package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

/** Joker pasivo: su efecto lo consulta Jugador.mismoPaloParaEnvido() directamente vía tieneJoker(Salta.class).
 * No reacciona a ningún evento — aplicarEfecto queda vacío. */
public class Salta extends Joker {

    public Salta() {
        super(76, "Salta", "Salta", "Basto y Copa cuentan como el mismo palo para el envido",
            Rareza.raro, 6, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        Salta copia = new Salta();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Efecto pasivo, sin lógica acá: lo consulta Jugador directamente por presencia del joker.
    }
}
