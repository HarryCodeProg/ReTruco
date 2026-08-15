package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import java.util.ArrayList;

public class DVD extends Joker {

    public DVD(){
        super(44, "DVD", "DVD", "Activa las cartas que no maten",
            Rareza.comun, 1, Joker.FaseActivacion.INDEPENDIENTE, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        DVD copia = new DVD();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.ANTES_DE_SUMAR_TRUCO) return;
        ArrayList<Carta> cartasJugador = ctx.getMesa().getMesaJugador();
        ArrayList<Carta> cartasRival = ctx.getMesa().getMesaRival();
        int bazas = Math.min(cartasJugador.size(), cartasRival.size());
        for (int i = 0; i < bazas; i++) {
            Carta propia = cartasJugador.get(i);
            if (propia.getValorTrucoActual() > cartasRival.get(i).getValorTrucoActual()) continue;
            ctx.reencolarActivacionCarta(propia, EventoJuego.AL_PUNTUAR_CARTA);
        }
    }
}
