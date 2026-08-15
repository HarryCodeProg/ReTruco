package io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.Random;

public class SeTieneQueArrepentir extends Joker {

    private static final int[] NUMEROS_MAZO = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
    private final Random random = new Random();

    public SeTieneQueArrepentir(){
        super(47, "Se Tiene Que Arrepentir", "SeTieneQueArrepentir",
            "Cada oro que mata agrega una carta de oro aleatoria al mazo",
            Rareza.comun, 1, Joker.FaseActivacion.AL_PUNTUAR_CARTA, CategoriaJoker.NACIONAL);
    }

    @Override
    public Joker copiar() {
        SeTieneQueArrepentir copia = new SeTieneQueArrepentir();
        copiarEstado(copia);
        copia.setAcumulado(this.getAcumulado());
        return copia;
    }

    @Override
    public void aplicarEfecto(EventoJuego evento, ContextoJuego ctx, Juego juego){
        if (evento != EventoJuego.AL_PUNTUAR_CARTA) return;
        Carta c = ctx.getCartaEnResolucion();
        if (c == null || c.getPalo() != Palo.ORO) return;
        if (!ctx.cartaMato(c)) return;
        int numero = NUMEROS_MAZO[random.nextInt(NUMEROS_MAZO.length)];
        ctx.getMazo().agregarCarta(new Carta(numero, Palo.ORO));
    }
}
