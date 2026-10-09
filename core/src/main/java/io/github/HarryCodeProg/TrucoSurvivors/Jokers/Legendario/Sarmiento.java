package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Legendario;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class Sarmiento extends Joker {

    private static final int ESPACIO_EXTRA = 3;
    private static final int PUNTOS_BONUS = 20;

    public Sarmiento() {
        super(
            148,
            "Sarmiento",
            "Sarmiento",
            "+3 espacios en la Tienda en todos los items. Cada vez que compras un item, Todas las cartas de tu mazo actual obtienen +20 Puntos de Truco y Envido.",
            Rareza.legendario,
            12,
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarEspacioJokersTiendaPersistente(ESPACIO_EXTRA);
        jugador.sumarEspacioCartasTiendaPersistente(ESPACIO_EXTRA);
        jugador.sumarEspacioSantosTiendaPersistente(ESPACIO_EXTRA);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.sumarEspacioJokersTiendaPersistente(-ESPACIO_EXTRA);
        jugador.sumarEspacioCartasTiendaPersistente(-ESPACIO_EXTRA);
        jugador.sumarEspacioSantosTiendaPersistente(-ESPACIO_EXTRA);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // disparo real via PanelTienda, no por evento
    }

    /** Dedupe por identidad: una carta puede existir simultaneamente en mano Y en cartasTomadas (mismo objeto). */
    public static void aplicarBuffATodasLasCartas(Jugador jugador) {
        Set<Carta> todas = Collections.newSetFromMap(new IdentityHashMap<>());
        Mazo mazo = jugador.getMazo();
        todas.addAll(mazo.getMazo());
        todas.addAll(mazo.getCartasTomadas());
        todas.addAll(mazo.getCartasDescartadas());
        todas.addAll(jugador.getMano());
        for (Carta c : todas) aplicarBuff(c);
    }

    private static void aplicarBuff(Carta c) {
        c.modificarPuntosTrucoAportePermanente(PUNTOS_BONUS);
        c.modificarPuntosEnvidoAportePermanente(PUNTOS_BONUS);
    }

    @Override
    public Joker copiar() {
        Sarmiento copia = new Sarmiento();
        copiarEstado(copia);
        return copia;
    }
}
