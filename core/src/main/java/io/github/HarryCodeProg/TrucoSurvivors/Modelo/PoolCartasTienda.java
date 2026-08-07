package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;

import java.util.Random;

/** Genera cartas normales al azar (mazo español de 40) para ofrecer en la tienda. */
public class PoolCartasTienda {
    private static final int[] NUMEROS = {1,2,3,4,5,6,7,8,9,10,11,12};

    public Carta tomarAleatoria(Random random) {
        int numero = NUMEROS[random.nextInt(NUMEROS.length)];
        Palo palo = Palo.values()[random.nextInt(Palo.values().length)];
        return new Carta(numero, palo);
    }
}
