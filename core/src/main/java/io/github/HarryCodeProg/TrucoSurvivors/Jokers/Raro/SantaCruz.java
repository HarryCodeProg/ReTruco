package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.Random;

import static io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker.FaseActivacion.INDEPENDIENTE;

public class SantaCruz extends Joker {

    private Palo paloActual;
    private static final Random random = new Random();

    public SantaCruz() {
        super(
            98,
            "SantaCruz",
            "SantaCruz",
            "", // La descripción la armamos dinámica para mostrar el palo
            Rareza.raro,
            5,
            INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        // Sorteamos el primer palo apenas se crea el Joker
        cambiarPaloAleatorio();
    }

    private void cambiarPaloAleatorio() {
        Palo[] palos = Palo.values();
        paloActual = palos[random.nextInt(palos.length)];
    }

    public void setPaloActual(Palo palo) {
        this.paloActual = palo;
    }

    @Override
    public Joker copiar() {
        SantaCruz copia = new SantaCruz();
        copiarEstado(copia);
        // Es MUY importante que la copia mantenga el mismo palo que tenía (por ejemplo, al guardarlo o comprarlo)
        copia.setPaloActual(this.paloActual);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // 1. Cada vez que una carta puntúa, verificamos si es del palo correcto
        if (evento == EventoJuego.AL_PUNTUAR_CARTA) {
            if (ctx.getCartaEnResolucion() != null && ctx.getCartaEnResolucion().getPalo() == paloActual) {
                // Sumamos +7 al Multiplicador (aparecerá en la pantalla con el nombre de SantaCruz)
                ctx.getResolucionActual().sumarMult(7.0, this.getNombre(), this);
            }
        }
        // 2. Al ganar el combate/ronda, rotamos el palo para el siguiente rival
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            cambiarPaloAleatorio();
        }
    }

    @Override
    public String getDescripcion() {
        // Formateamos el texto base con el palo actual
        String nombrePalo = paloActual.toString();
        // Capitalizamos la primera letra por si el enum está todo en minúsculas/mayúsculas
        nombrePalo = nombrePalo.substring(0, 1).toUpperCase() + nombrePalo.substring(1).toLowerCase();
        return "Cada vez que un [" + nombrePalo + "] se activa, +7 de multiplicador truco.\n(El palo cambia cada ronda)";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        String nombrePalo = paloActual.toString();
        nombrePalo = nombrePalo.substring(0, 1).toUpperCase() + nombrePalo.substring(1).toLowerCase();
        String descripcionBase = "Cada vez que un " + nombrePalo + " se activa, +7 multiplicador truco.\n(El palo cambia cada ronda)";
        return descripcionBase;
    }
}
