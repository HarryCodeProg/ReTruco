package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import java.util.Random;

public class Probabilidad {

    private static final Random RANDOM = new Random();

    public static boolean porcentaje(int porcentaje) {
        return RANDOM.nextInt(100) < porcentaje;
    }

    public static int numero(int maximo) {
        return RANDOM.nextInt(maximo);
    }

    public static int numero(int minimo, int maximo) {
        return RANDOM.nextInt(maximo - minimo + 1) + minimo;
    }

    public static Random random() {
        return RANDOM;
    }
}
