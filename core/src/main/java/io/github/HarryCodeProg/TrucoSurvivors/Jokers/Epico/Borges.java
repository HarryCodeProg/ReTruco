package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Epico;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Borges extends Joker {

    public Borges() {
        super(140, "Borges", "Borges", "+1 tamaño mano, jokers y santos.\n+1 espacio en tienda (todas las zonas).",
            Rareza.epico,
            10,
            FaseActivacion.INDEPENDIENTE
        );
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        // Aumenta los límites del jugador
        jugador.aumentarTamañoMano(1);
        jugador.setTamañoJokers(jugador.getTamañoJokers() + 1);
        jugador.sumarEspacioSantos(1);

        // Aumenta un slot en TODAS las zonas de la tienda
        jugador.sumarEspacioJokersTiendaPersistente(1);
        jugador.sumarEspacioCartasTiendaPersistente(1);
        jugador.sumarEspacioSantosTiendaPersistente(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        // Revierte todo si el Joker es vendido o destruido
        jugador.aumentarTamañoMano(-1);
        jugador.setTamañoJokers(jugador.getTamañoJokers() - 1);
        jugador.sumarEspacioSantos(-1);

        // Revierte el espacio de la tienda
        jugador.sumarEspacioJokersTiendaPersistente(-1);
        jugador.sumarEspacioCartasTiendaPersistente(-1);
        jugador.sumarEspacioSantosTiendaPersistente(-1);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego) {
        // Joker 100% pasivo. Su magia ocurre instantáneamente al obtenerlo.
    }

    @Override
    public Joker copiar() {
        Borges copia = new Borges();
        this.copiarEstado(copia);
        return copia;
    }
}
