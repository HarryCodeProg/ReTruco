package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.Random;

public class Catamarca extends Joker {

    private int numeroAleatorio;
    private final Random random;

    public Catamarca() {
        super(78, "Catamarca", "Catamarca",
            "+1 tamaño mano. Gana $2 peso cada vez que [numero] se active (numero cambia cada ronda)",
            Rareza.raro, 6, Joker.FaseActivacion.AL_PUNTUAR_CARTA,
            CategoriaJoker.NACIONAL);
        this.random = new Random();
        this.numeroAleatorio = random.nextInt(12) + 1; // 1 a 12
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarTamañoMano(1);
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarTamañoMano(-1);
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento == EventoJuego.POST_REPARTO) {
            this.numeroAleatorio = random.nextInt(12) + 1;
            return;
        }

        if (evento == EventoJuego.AL_PUNTUAR_CARTA) {
            Carta c = ctx.getCartaEnResolucion();
            if (c != null && c.getNumero() == this.numeroAleatorio) {
                ctx.getJugador().sumarPesos(2);
            }
        }
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+1 tamaño mano\nGana $2 pesos cada vez que el " + this.numeroAleatorio + " se active (numero cambia cada ronda)";
    }

    @Override
    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    @Override
    public Joker copiar() {
        Catamarca copia = new Catamarca();
        copiarEstado(copia);
        copia.numeroAleatorio = this.numeroAleatorio;
        return copia;
    }
}
