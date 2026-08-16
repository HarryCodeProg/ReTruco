package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Mazo {
    private ArrayList<Carta> mazo;
    private ArrayList<Carta> cartasTomadas;
    private ArrayList<Carta> cartasDescartadas;
    private int tamañoMazo;


    public Mazo(){
        this.mazo = new ArrayList<>();
        this.cartasTomadas = new ArrayList<>();
        this.cartasDescartadas = new ArrayList<>();
    }

    public static Mazo crearMazoBase() {
        Mazo mazo = new Mazo();
        for (Palo palo : Palo.values()) {
            for (int numero = 1; numero <= 12; numero++) {
                if (numero != 8 && numero != 9) {
                    mazo.agregarCarta(new Carta(numero, palo));
                }
            }
        }
        return mazo;
    }

    public void agregarCarta(Carta carta){
        this.mazo.add(carta);
        sumarTamañoMazo(1);
    }

    public void devolverCarta(Carta carta) {
        this.mazo.add(carta);
    }

    public void quitarCarta(Carta carta){
        this.mazo.remove(carta);
    }

    public void barajar(){
        Random random = new Random();
        for (int i = mazo.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Carta temp = mazo.get(i);
            mazo.set(i, mazo.get(j));
            mazo.set(j, temp);
        }
    }

    public Carta tomarCarta() {
        if (mazo.isEmpty()) {
            return null;
        }
        Carta carta = mazo.remove(0);
        cartasTomadas.add(carta);
        return carta;
    }

    public void sumarTamañoMazo(int i){
        this.tamañoMazo += i;
    }

    public void restarTamañoMazo(int i){
        this.tamañoMazo -= i;
    }

    public Carta getCarta(int i){return this.mazo.get(i);}

    public ArrayList<Carta> getMazo(){
        return this.mazo;
    }

    public int getCantidadDisponibles(){
        return this.mazo.size();
    }

    public List<Carta> getCartasRestantes() {return new ArrayList<>(this.mazo);}

    public int getTamañoMazo() {return tamañoMazo;}

    public void agregarCartas(List<Carta> nuevasCartas) {
        this.mazo.addAll(nuevasCartas);
    }

    public void descartarCarta(Carta carta){
        this.cartasDescartadas.add(carta);
    }

    public ArrayList<Carta> getCartasDescartadas(){
        return this.cartasDescartadas;
    }

    public void limpiarDescartadas() {
        this.mazo.addAll(this.cartasDescartadas);
        this.cartasDescartadas.clear();
    }

    public List<Carta> getCartasRestantesOrdenadas() {
        List<Carta> copia = new ArrayList<>(this.mazo);
        // Ordena primero por Palo y luego por Número
        copia.sort((c1, c2) -> {
            int compPalo = c1.getPalo().compareTo(c2.getPalo());
            if (compPalo != 0) return compPalo;
            return Integer.compare(c1.getNumero(), c2.getNumero());
        });
        return copia;
    }

    public ArrayList<Carta> getCartasAleatoriasParaSanto(int cantidad) {
        ArrayList<Carta> disponibles = new ArrayList<>(mazo);
        Collections.shuffle(disponibles);
        int limite = Math.min(cantidad, disponibles.size());
        return new ArrayList<>(disponibles.subList(0, limite));
    }

    public ArrayList<Carta> tomarCartasAleatorias(int cantidad) {
        ArrayList<Carta> disponibles = new ArrayList<>(mazo);
        Collections.shuffle(disponibles);
        int limite = Math.min(cantidad, disponibles.size());
        return new ArrayList<>(disponibles.subList(0, limite));
    }

    public void reciclarCartasTomadas() {
        this.mazo.addAll(this.cartasTomadas);
        this.cartasTomadas.clear();
        this.cartasDescartadas.clear();
    }
}
