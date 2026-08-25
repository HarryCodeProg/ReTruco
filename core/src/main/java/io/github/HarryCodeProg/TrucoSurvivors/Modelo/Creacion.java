package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.*;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Comun.*;

public class Creacion {
    private int[] numeros = {1,2,3,4,5,6,7,10,11,12};
    private Tienda tienda;

    public Creacion(Mazo mazo){
        this.tienda = new Tienda();
        for (int numero : numeros){
            Carta cartaBasto;
            Carta cartaEspada;
            Carta cartaCopa;
            Carta cartaOro;
            cartaBasto = new Carta(numero, Palo.BASTO);
            cartaEspada = new Carta(numero, Palo.ESPADA);
            cartaCopa = new Carta(numero, Palo.COPA);
            cartaOro = new Carta(numero, Palo.ORO);
            mazo.agregarCarta(cartaBasto);
            mazo.agregarCarta(cartaEspada);
            mazo.agregarCarta(cartaCopa);
            mazo.agregarCarta(cartaOro);
        }

    }

    public Tienda getTienda(){
        return this.tienda;
    }
}
