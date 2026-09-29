package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class LaRioja extends Joker {

    public LaRioja() {
        super(
            86,
            "LaRioja",
            "LaRioja",
            "Agrega a tu mazo una copia de todas las cartas jugadas en la mano.",
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public Joker copiar() {
        LaRioja copia = new LaRioja();
        copiarEstado(copia);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Se activa al terminar la ronda (la "mano" de Truco)
        if (evento == EventoJuego.TERMINO_MANO) {
            // Verificamos si el jugador efectivamente jugó cartas en la mesa
            if (!juego.getMesa().getMesaJugador().isEmpty()) {
                // Recorremos todas las cartas que el jugador tiró a la mesa en esta mano
                for (Carta cartaJugada : juego.getMesa().getMesaJugador()) {
                    // Creamos una copia exacta (mismo número y palo)
                    Carta copiaCarta = new Carta(cartaJugada.getNumero(), cartaJugada.getPalo());
                    juego.agregarCartaAlMazoJugador(copiaCarta);
                }
            }
        }
    }
}
