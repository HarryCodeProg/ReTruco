package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public class PerfilJugador {
    private Jugador jugador;
    private int nivelActual; // <--- Nuevo campo

    public PerfilJugador() {
        iniciarNuevaRun();
    }

    public void iniciarNuevaRun() {
        this.jugador = new Jugador("Jugador");
        this.nivelActual = 1; // <--- Reinicia el nivel al comenzar

        Mazo mazoJugador = new Mazo();
        Creacion creacion = new Creacion(mazoJugador);
        this.jugador.setMazo(mazoJugador);

        ArrayList<Joker> jokersIniciales = creacion.getTienda().getJokers();
        int limiteInicial = Math.min(jokersIniciales.size(), jugador.getTamañoJokers());

        for (int i = 0; i < limiteInicial; i++) {
            jugador.agregarJoker(jokersIniciales.get(i));
        }
    }

    /** Incrementa el nivel alcanzado en la run actual */
    public void avanzarNivel() {
        this.nivelActual++;
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public Jugador getJugador() {
        return jugador;
    }
}
