package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

public enum SignoZodiaco {
    ARIES, TAURO, GEMINIS, CANCER, LEO, VIRGO, LIBRA, ESCORPIO, SAGITARIO, CAPRICORNIO, ACUARIO, PISCIS;

    public void aplicarEfecto(Jugador jugador, Juego juego, EstadoTienda tienda, Carta cartaSeleccionada) {
        int mult = (this == LIBRA) ? 1 : jugador.consumirMultiplicadorZodiaco();
        for (int rep = 0; rep < mult; rep++) {
            aplicarUnaVez(jugador, juego, tienda, cartaSeleccionada);
        }
    }

    public boolean requiereSeleccionCarta() {
        return this == CANCER || this == ESCORPIO;
    }

    private void aplicarUnaVez(Jugador jugador, Juego juego, EstadoTienda tienda, Carta cartaSeleccionada) {
        switch (this) {
            case ARIES:
                jugador.sumarPesos(100);
                break;
            case TAURO:
                jugador.sumarDescartesExtra(1);
                break;
            case GEMINIS:
                jugador.sumarPesos(jugador.getPesos());
                break;
            case CANCER:
                if (cartaSeleccionada != null) cartaSeleccionada.modificarPuntosTrucoAportePermanente(100);
                break;
            case LEO:
                jugador.aumentarTamañoMano(1);
                break;
            case VIRGO:
                jugador.sumarEspacioSantos(1);
                break;
            case LIBRA:
                jugador.activarDobleProximoZodiaco();
                break;
            case ESCORPIO:
                if (cartaSeleccionada != null) cartaSeleccionada.modificarPuntosEnvidoAportePermanente(100);
                break;
            case SAGITARIO:
                jugador.setTamañoJokers(jugador.getTamañoJokers() + 1);
                break;
            case CAPRICORNIO:
                if (tienda != null) tienda.aplicarDescuento50();
                break;
            case ACUARIO:
                if (tienda != null) tienda.sumarRerollsGratis(10);
                break;
            case PISCIS:
                // requiere pool de jokers legendarios; hoy no existe ninguno
                break;
        }
    }
}
