package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

public class ItemTienda {
    public enum Tipo { CARTA, JOKER, SANTO, ZODIACO }

    private final Tipo tipo;
    private final Carta carta;
    private final Joker joker;
    private final int precio;
    private final Santo santo;

    public static ItemTienda deCarta(Carta carta, int precio) {
        return new ItemTienda(Tipo.CARTA, carta, null, null, precio);
    }

    public static ItemTienda deJoker(Joker joker, int precio) {
        return new ItemTienda(Tipo.JOKER, null, joker, null, precio);
    }

    private ItemTienda(Tipo tipo, Carta carta, Joker joker, Santo santo,int precio) {
        this.tipo = tipo;
        this.carta = carta;
        this.joker = joker;
        this.santo = santo;
        this.precio = precio;
    }

    public static ItemTienda deSanto(Santo santo, int precio) {
        return new ItemTienda(Tipo.SANTO, null, null, santo, precio);
    }


    public Tipo getTipo() { return tipo; }
    public Carta getCarta() { return carta; }
    public Joker getJoker() { return joker; }
    public int getPrecio() { return precio; }
    public Santo getSanto() { return santo; }
}
