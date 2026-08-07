package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import java.util.Random;

public class CartaDrop {

    private final int peso;
    private final Carta[] cartas;

    public CartaDrop(int peso, Carta... cartas) {
        this.peso = peso;
        this.cartas = cartas;
    }

    public int getPeso() {
        return peso;
    }

    public Carta obtenerCarta(Random random) {
        Carta carta = cartas[random.nextInt(cartas.length)];
        return new Carta(carta.getNumero(), carta.getPalo());
    }
}
