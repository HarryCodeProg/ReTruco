package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;

import java.util.Random;

public class Escarapela extends Joker {

    private int numeroActual;
    private static final Random random = new Random();

    public Escarapela() {
        super(107, "Escarapela", "Escarapela", "",
            Rareza.muyRaro, 8, Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL);
        cambiarNumeroAleatorio();
    }

    private void cambiarNumeroAleatorio() {
        int[] validos = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
        numeroActual = validos[random.nextInt(validos.length)];
    }

    public void setNumeroActual(int numero) {
        this.numeroActual = numero;
    }

    @Override
    public Joker copiar() {
        Escarapela copia = new Escarapela();
        copiarEstado(copia);
        copia.setNumeroActual(this.numeroActual);
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Aplica multiplicador envido
        if (evento == EventoJuego.ANTES_DE_SUMAR_ENVIDO) {
            ctx.getResolucionActual().multiplicarMult(5.0, getNombre(), this);
        }

        // Se consume si se activa una carta con el numero
        if (evento == EventoJuego.AL_PUNTUAR_CARTA || evento == EventoJuego.AL_PUNTUAR_CARTA_ENVIDO) {
            Carta carta = ctx.getCartaEnResolucion();
            if (carta != null && carta.getNumero() == numeroActual) {
                // Consumir el joker
                Jugador jugador = ctx.getJugador();
                if (jugador != null) {
                    jugador.eliminarJoker(this);
                    
                    // Opcional: Sonido
                    GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
                    if (sonidos != null) sonidos.reproducirSonidoGastarPeso();
                }
            }
        }

        // Cambiar número al ganar combate
        if (evento == EventoJuego.AL_GANAR_COMBATE) {
            cambiarNumeroAleatorio();
        }
    }

    @Override
    public String getDescripcion() {
        return "x5 multiplicador envido, se consume si se activa un " + numeroActual + ".\n(El número cambia cada ronda)";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcion();
    }
}
