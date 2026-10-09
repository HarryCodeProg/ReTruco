package io.github.HarryCodeProg.TrucoSurvivors.Jokers.MuyRaro;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class Vaca extends Joker {

    private static final int TAMAÑO_INICIAL = 5;
    private boolean primerCombate = true; // para no restar en el combate en que se compra

    public Vaca() {
        super(
            103,
            "Vaca",
            "Vaca",
            "+5 tamaño mano, se reduce en 1 en cada ronda",
            Rareza.muyRaro,
            8, // placeholder
            Joker.FaseActivacion.INDEPENDIENTE,
            CategoriaJoker.NACIONAL
        );
        setAcumulado(TAMAÑO_INICIAL);
    }

    @Override
    public void aplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarTamañoMano((int) getAcumulado());
    }

    @Override
    public void desAplicarEfectoInstantaneo(Jugador jugador) {
        jugador.aumentarTamañoMano(-(int) getAcumulado());
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        if (evento != EventoJuego.INICIO_COMBATE) return;
        if (primerCombate) {
            primerCombate = false;
            return;
        }
        Jugador jugador = ctx.getJugador();
        jugador.aumentarTamañoMano(-1); // retira el bonus de mano que tenía
        sumarAcumulado(-1);
        if (getAcumulado() <= 0) {
            jugador.eliminarJoker(this);
            return;
        }
        jugador.aumentarTamañoMano(1); // vuelve a aplicar el bonus ya reducido
    }

    @Override
    public String getDescripcionRenderizada() {
        return "+" + (int) getAcumulado() + " tamaño mano, se reduce en 1 en cada ronda";
    }

    @Override
    public Joker copiar() {
        Vaca copia = new Vaca();
        copia.setAcumulado(this.getAcumulado());
        copia.primerCombate = false; // una copia (Rosas, etc) no debería resetear el contador de "primer combate"
        copiarEstado(copia);
        return copia;
    }
}
