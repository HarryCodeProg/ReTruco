package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

public enum SignoZodiaco {
    ARIES("01_aries", "Aries", "+1 lugar para ofertas de jokers en las tiendas."),
    TAURO("02_tauro", "Tauro", "+1 descarte disponible por ronda."),
    GEMINIS("03_geminis", "Géminis", "Duplica los pesos que tenés al consumirlo."),
    CANCER("04_cancer", "Cáncer", "+1 lugar para ofertas de cartas en las tiendas."),
    LEO("05_leo", "Leo", "+1 carta al tamaño de tu mano."),
    VIRGO("06_virgo", "Virgo", "+1 espacio para llevar santos."),
    LIBRA("07_libra", "Libra", "El próximo signo que no sea Libra aplica su efecto dos veces. No se acumula."),
    ESCORPIO("08_escorpio", "Escorpio", "+1 lugar para ofertas de santos en las tiendas."),
    SAGITARIO("09_sagitario", "Sagitario", "+1 espacio para llevar jokers."),
    CAPRICORNIO("10_capricornio", "Capricornio", "Reduce a la mitad los precios de los artículos de esta tienda y las siguientes (redondeo hacia arriba)."),
    ACUARIO("11_acuario", "Acuario", "+10 rerolls gratis. Los que sobren se guardan para las próximas tiendas."),
    PISCIS("12_piscis", "Piscis", "Recibís un joker legendario aleatorio, si hay uno disponible y tenés espacio libre.");

    private final String nombreRegion;
    private final String nombre;
    private final String descripcion;

    SignoZodiaco(String nombreRegion, String nombre, String descripcion) {
        this.nombreRegion = nombreRegion;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombreRegion() { return nombreRegion; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    public void aplicarEfecto(Jugador jugador, Juego juego, EstadoTienda tienda, Carta cartaSeleccionada) {
        int mult = (this == LIBRA) ? 1 : jugador.consumirMultiplicadorZodiaco();
        for (int rep = 0; rep < mult; rep++) aplicarUnaVez(jugador, juego, tienda, cartaSeleccionada);
    }

    private void aplicarUnaVez(Jugador jugador, Juego juego, EstadoTienda tienda, Carta cartaSeleccionada) {
        switch (this) {
            case ARIES:
                jugador.sumarEspacioJokersTiendaPersistente(1);
                if (tienda != null) tienda.sumarEspacioJokersTienda(1); // refleja también en la tienda actual, si está abierta
                break;
            case TAURO: jugador.sumarDescartesExtra(1); break;
            case GEMINIS: jugador.sumarPesos(jugador.getPesos()); break;
            case CANCER:
                jugador.sumarEspacioCartasTiendaPersistente(1);
                if (tienda != null) tienda.sumarEspacioCartasTienda(1);
                break;
            case LEO: jugador.aumentarTamañoMano(1); break;
            case VIRGO: jugador.sumarEspacioSantos(1); break;
            case LIBRA: jugador.activarDobleProximoZodiaco(); break;
            case ESCORPIO:
                jugador.sumarEspacioSantosTiendaPersistente(1);
                if (tienda != null) tienda.sumarEspacioSantosTienda(1);
                break;
            case SAGITARIO: jugador.setTamañoJokers(jugador.getTamañoJokers() + 1); break;
            case CAPRICORNIO:
                jugador.aplicarDescuentoTiendaPersistente(0.5);
                if (tienda != null) tienda.aplicarDescuento50();
                break;
            case ACUARIO:
                jugador.sumarRerollsGratisTienda(10);
                if (tienda != null) tienda.sumarRerollsGratis(10);
                break;
            case PISCIS:
                if (jugador.getJokers().size() < jugador.getTamañoJokers()) {
                    PoolJokersTienda pool = new PoolJokersTienda();
                    Joker nuevoLegendario = pool.tomarAleatorioDeRareza(Rareza.legendario, jugador);
                    if (nuevoLegendario != null) {
                        jugador.agregarJoker(nuevoLegendario);
                    }
                }
                break;
        }
    }
}
