package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;

public class ItemTienda {
    public enum Tipo { CARTA, JOKER, SANTO, ZODIACO }

    private final Tipo tipo;
    private final Carta carta;
    private final Joker joker;
    private final int precio;

    public static ItemTienda deCarta(Carta carta, int precio) {
        return new ItemTienda(Tipo.CARTA, carta, null, precio);
    }

    public static ItemTienda deJoker(Joker joker, int precio) {
        return new ItemTienda(Tipo.JOKER, null, joker, precio);
    }

    private ItemTienda(Tipo tipo, Carta carta, Joker joker, int precio) {
        this.tipo = tipo;
        this.carta = carta;
        this.joker = joker;
        this.precio = precio;
    }

    public Tipo getTipo() { return tipo; }
    public Carta getCarta() { return carta; }
    public Joker getJoker() { return joker; }
    public int getPrecio() { return precio; }
}
