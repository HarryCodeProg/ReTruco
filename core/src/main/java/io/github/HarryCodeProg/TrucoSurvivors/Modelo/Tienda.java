package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;

import java.util.ArrayList;

public class Tienda {
    private ArrayList<Joker> jokers;

    public Tienda(){
        this.jokers = new ArrayList<>();
    }

    public void agregarJoker(Joker joker){
        this.jokers.add(joker);
    }

    public ArrayList<Joker> getJokers() {
        return jokers;
    }
}
