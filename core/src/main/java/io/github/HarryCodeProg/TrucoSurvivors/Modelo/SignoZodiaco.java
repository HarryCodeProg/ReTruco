package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

public enum SignoZodiaco {
    ARIES("01_aries"), TAURO("02_tauro"), GEMINIS("03_geminis"), CANCER("04_cancer"),
    LEO("05_leo"), VIRGO("06_virgo"), LIBRA("07_libra"), ESCORPIO("08_escorpio"),
    SAGITARIO("09_sagitario"), CAPRICORNIO("10_capricornio"), ACUARIO("11_acuario"), PISCIS("12_piscis");

    private final String nombreRegion;
    SignoZodiaco(String nombreRegion) { this.nombreRegion = nombreRegion; }
    public String getNombreRegion() { return nombreRegion; }

    public void aplicarEfecto(Jugador jugador, Juego juego, EstadoTienda tienda, Carta cartaSeleccionada) {
        int mult = (this == LIBRA) ? 1 : jugador.consumirMultiplicadorZodiaco();
        for (int rep = 0; rep < mult; rep++) aplicarUnaVez(jugador, juego, tienda, cartaSeleccionada);
    }

    private void aplicarUnaVez(Jugador jugador, Juego juego, EstadoTienda tienda, Carta cartaSeleccionada) {
        switch (this) {
            case ARIES: if (tienda != null) tienda.sumarEspacioJokersTienda(1); break;
            case TAURO: jugador.sumarDescartesExtra(1); break;
            case GEMINIS: jugador.sumarPesos(jugador.getPesos()); break;
            case CANCER: if (tienda != null) tienda.sumarEspacioCartasTienda(1); break;case LEO: jugador.aumentarTamañoMano(1); break;
            case VIRGO: jugador.sumarEspacioSantos(1); break;
            case LIBRA: jugador.activarDobleProximoZodiaco(); break;
            case ESCORPIO: if (tienda != null) tienda.sumarEspacioSantosTienda(1); break;
            case SAGITARIO: jugador.setTamañoJokers(jugador.getTamañoJokers() + 1); break;
            case CAPRICORNIO: if (tienda != null) tienda.aplicarDescuento50(); break;
            case ACUARIO: if (tienda != null) tienda.sumarRerollsGratis(10); break;
            case PISCIS: /* pendiente jokers legendarios */ break;
        }
    }
}
