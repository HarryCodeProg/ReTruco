package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;

import java.util.ArrayList;

public class Mesa {
    private ArrayList<Carta> mesaRival;
    private ArrayList<Carta> mesaJugador;

    public Mesa(){
        this.mesaRival = new ArrayList<>();
        this.mesaJugador = new ArrayList<>();
    }

    public void agregarCartaRival(Carta carta){
        this.mesaRival.add(carta);
    }

    public void agregarCartaJugador(Carta carta){
        this.mesaJugador.add(carta);
    }

    public ArrayList<Carta> getMesaJugador() {
        return mesaJugador;
    }

    public ArrayList<Carta> getMesaRival() {
        return mesaRival;
    }

    public void limpiarMesa(){
        this.mesaJugador.clear();
        this.mesaRival.clear();
    }

    public void limpiarCartaJugador(Carta carta){
        this.mesaJugador.remove(carta);
    }

    public void limpiarCartaRival(Carta carta){
      this.mesaRival.remove(carta);
    }
}
