package io.github.HarryCodeProg.TrucoSurvivors;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;

import java.util.ArrayList;

public class Jugador {
    private String nombre;
    private ArrayList<Carta> mano;
    private ArrayList<Joker> jokers;
    private Mazo mazo;
    private int tamañoMano;
    private int tamañoJokers;
    private int envidoInicial = 20;
    private double envidoActual;
    private double multiplicadorTruco;
    private double multiplicadorEnvido;
    private double multiplicadorTrucoTemporal;
    private double multiplicadorEnvidoTemporal;
    private double puntajeTotal;
    private int pesos = 100;
    private static final int INTERVALO_INTERES = 5;
    private static final int TOPE_INTERES = 5;
    private int descartesBase = 4;
    private int descartesExtra = 0;
    private int tamañoManoExtra = 0;
    private int espacioSantosExtra = 0;
    private int proximoEfectoZodiacoMultiplicador = 1; // Libra
    private int rerollsTienda = 0;
    private double bonusEnvidoFinal = 0;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.mano = new ArrayList<>();
        this.jokers = new ArrayList<>();
        this.envidoActual = 20;
        this.tamañoMano = 3;
        this.tamañoJokers = 5; // Capacidad por defecto de Jokers
        this.multiplicadorTruco = 1;
        this.multiplicadorEnvido = 1;
        this.multiplicadorTrucoTemporal = 1;
        this.multiplicadorEnvidoTemporal = 1;
        this.mazo = new Mazo();
    }

    public void agregarCarta(Carta carta) { this.mano.add(carta); }
    public void eliminarCarta(Carta carta) { this.mano.remove(carta); }

    public void ordenarMano() {
        for (int i = 0; i < mano.size() - 1; i++) {
            for (int j = 0; j < mano.size() - 1 - i; j++) {
                if (mano.get(j).getNumero() > mano.get(j + 1).getNumero()) {
                    Carta temp = mano.get(j);
                    mano.set(j, mano.get(j + 1));
                    mano.set(j + 1, temp);
                }
            }
        }
    }

    public Mazo getMazo() { return this.mazo; }
    public void setMazo(Mazo mazo) { this.mazo = mazo; }

    public int getTamañoMano() { return this.tamañoMano; }
    public void setTamañoMano(int tamañoMano) { this.tamañoMano = tamañoMano; }
    public void aumentarTamañoMano(int i) { this.tamañoMano += i; }

    public int getTamañoJokers() { return this.tamañoJokers; }
    public void setTamañoJokers(int tamañoJokers) { this.tamañoJokers = tamañoJokers; }

    public ArrayList<Carta> getMano() { return this.mano; }
    public ArrayList<Joker> getJokers() { return this.jokers; }

    public boolean agregarJoker(Joker joker) {
        if (jokers.size() < tamañoJokers) {
            this.jokers.add(joker);
            joker.aplicarEfectoInstantaneo(this);
            return true;
        }
        return false;
    }

    public void eliminarJoker(Joker joker) {
        this.jokers.remove(joker);
        joker.desAplicarEfectoInstantaneo(this);
    }

    public int getPesos() { return pesos; }
    public void sumarPesos(int cantidad) { this.pesos += cantidad; }
    public boolean gastarPesos(int cantidad) {
        if (pesos < cantidad) return false;
        pesos -= cantidad;
        return true;
    }

    public int calcularInteres() {
        return Math.min(pesos / INTERVALO_INTERES, TOPE_INTERES);
    }

    public int getNumeroMasGrande() {
        if (mano.isEmpty()) return 0;
        int masAlto = mano.get(0).getValorEnvidoActual();
        for (int i = 1; i < mano.size(); i++) {
            if (mano.get(i).getValorEnvidoActual() > masAlto) {
                masAlto = mano.get(i).getValorEnvidoActual();
            }
        }
        return masAlto;
    }

    public double getPuntosEnvido() {
        double puntosEnvido = 0;
        double envidoActualG = 0;
        boolean hayMismoPalo = false;
        for (int i = 0; i < mano.size(); i++) {
            for (int j = 0; j < mano.size(); j++) {
                if (i != j) {
                    if (mano.get(i).getPalo() == mano.get(j).getPalo()) {
                        hayMismoPalo = true;
                        envidoActualG = this.envidoActual + mano.get(i).getValorEnvidoActual() + mano.get(j).getValorEnvidoActual();
                        if (envidoActualG > puntosEnvido) {
                            puntosEnvido = envidoActualG;
                        }
                    }
                }
            }
        }
        if (!hayMismoPalo) {
            puntosEnvido = getNumeroMasGrande();
        }
        return puntosEnvido + bonusEnvidoFinal;
    }

    public ArrayList<Carta> getCartasEnvidoGanador() {
        ArrayList<Carta> mejorPar = new ArrayList<>();
        double mejor = 0;
        boolean hayMismoPalo = false;
        for (int i = 0; i < mano.size(); i++) {
            for (int j = 0; j < mano.size(); j++) {
                if (i != j && mano.get(i).getPalo() == mano.get(j).getPalo()) {
                    hayMismoPalo = true;
                    double suma = envidoActual + mano.get(i).getValorEnvidoActual() + mano.get(j).getValorEnvidoActual();
                    if (suma > mejor) {
                        mejor = suma;
                        mejorPar.clear();
                        mejorPar.add(mano.get(i));
                        mejorPar.add(mano.get(j));
                    }
                }
            }
        }
        if (!hayMismoPalo && !mano.isEmpty()) {
            Carta masAlta = mano.get(0);
            for (Carta c : mano) {
                if (c.getValorEnvidoActual() > masAlta.getValorEnvidoActual()) masAlta = c;
            }
            mejorPar.add(masAlta);
        }
        return mejorPar;
    }

    public double getPuntosTruco() { return 0; }

    public double calcularPuntajeEnvido() {
        return getPuntosEnvido() * this.multiplicadorEnvido;
    }

    public double getMultiplicadorEnvido() { return this.multiplicadorEnvidoTemporal; }
    public double getMultiplicadorTruco() { return this.multiplicadorTrucoTemporal; }

    public void robar(Mazo mazo, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            Carta tomada = mazo.tomarCarta();
            if (tomada != null) {
                mano.add(tomada);
            }
        }
    }

    public double getPuntajeTotal() { return puntajeTotal; }
    public void limpiarMano() { this.mano.clear(); }

    public String getNombre() { return nombre; }

    public double getMultiplicadorTrucoTemporal() { return multiplicadorTrucoTemporal; }
    public void aumentarMultiplicadorTrucoTemporal(double cantidad) { this.multiplicadorTrucoTemporal += cantidad; }
    public void aumentarMultiplicadorEnvidoTemporal(double cantidad) { this.multiplicadorEnvidoTemporal += cantidad; }

    public void aumentarMultiplicadorTruco(double multiplicador) { this.multiplicadorTruco += multiplicador; }
    public void aumentarMultiplicadorEnvido(double multiplicador) { this.multiplicadorEnvido += multiplicador; }

    public double getMultiplicadorEnvidoTemporal() { return multiplicadorEnvidoTemporal; }
    public void multEnvidoOriginal() { this.multiplicadorEnvidoTemporal = multiplicadorEnvido; }
    public void multTrucoOriginal() { this.multiplicadorTrucoTemporal = multiplicadorTruco; }

    public int getDescartesMaximos() {return descartesBase + descartesExtra;}

    public void sumarDescartesExtra(int cantidad) {this.descartesExtra += cantidad;}

    public void sumarEspacioSantos(int cantidad) { espacioSantosExtra += cantidad; }
    public int getEspacioSantosExtra() { return espacioSantosExtra; }
    public void activarDobleProximoZodiaco() { proximoEfectoZodiacoMultiplicador = 2; }
    public int consumirMultiplicadorZodiaco() {
        int m = proximoEfectoZodiacoMultiplicador;
        proximoEfectoZodiacoMultiplicador = 1;
        return m;
    }

    public int getRerollsTienda() { return rerollsTienda; }
    public void sumarRerollTienda() { this.rerollsTienda++; }

    public double getBonusEnvidoFinal() { return bonusEnvidoFinal; }
    public void sumarBonusEnvidoFinal(double cantidad) { this.bonusEnvidoFinal += cantidad; }
}
