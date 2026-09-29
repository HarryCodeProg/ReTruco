package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Constelacion extends Joker {

    public Constelacion() {
        super(80, "Constelacion", "Constelacion", "Jugar tres 7 en una mano\notorga $15",
            Rareza.raro,
            6,
            FaseActivacion.INDEPENDIENTE);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        // Evaluamos al finalizar la ronda completa
        if (evento == EventoJuego.TERMINO_MANO) {
            int cantidadSietes = 0;
            // Recorremos las cartas que el jugador puso en la mesa durante esta mano
            for (Carta carta : contexto.getMesa().getMesaJugador()) {
                if (carta.getNumero() == 7) {
                    cantidadSietes++;
                }
            }
            // Si jugó tres o más 7s, otorgamos la recompensa
            if (cantidadSietes >= 3) {
                contexto.getJugador().sumarPesos(15);
            }
        }
    }

    @Override
    public Joker copiar() {
        Constelacion copia = new Constelacion();
        this.copiarEstado(copia);
        return copia;
    }
}
