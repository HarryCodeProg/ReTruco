package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;
import java.util.Random;

public class EstadoTienda {
    private final ArrayList<ItemTienda> filaCartas = new ArrayList<>();
    private final ArrayList<ItemTienda> filaJokers = new ArrayList<>();
    private final ArrayList<ItemTienda> filaSantos = new ArrayList<>();
    private final ArrayList<ItemTienda> filaZodiaco = new ArrayList<>();
    private int cantidadCartas = 2;
    private int cantidadJokers = 2;
    private int rerollsCartas = 0;
    private int rerollsJokers = 0;
    private final Random random = new Random();
    private final PoolCartasTienda poolCartas = new PoolCartasTienda();
    private final PoolJokersTienda poolJokers = new PoolJokersTienda();
    private double multiplicadorPrecio = 1.0;
    private int rerollsGratis = 0;
    private int espacioJokersExtra = 0;
    private int espacioSantosExtra = 0;


    public EstadoTienda(Jugador jugador) {
        generarFilaCartas();
        generarFilaJokers(jugador);
    }

    public void generarFilaCartas() {
        filaCartas.clear();
        for (int i = 0; i < cantidadCartas; i++) {
            filaCartas.add(ItemTienda.deCarta(poolCartas.tomarAleatoria(random), 3));
        }
    }

    public void generarFilaJokers(Jugador jugador) {
        filaJokers.clear();
        for (int i = 0; i < cantidadJokers; i++) {
            Joker joker = poolJokers.tomarAleatorio(random, jugador);
            if (joker != null) {
                filaJokers.add(ItemTienda.deJoker(joker, joker.getCoste()));
            }
        }
    }

    public int costoRerollCartas() {
        return ConfiguracionEconomia.COSTO_REROLL_BASE + rerollsCartas;
    }

    public int costoRerollJokers() {
        return ConfiguracionEconomia.COSTO_REROLL_BASE + rerollsJokers;
    }

    public ArrayList<ItemTienda> getFilaCartas() { return filaCartas; }
    public ArrayList<ItemTienda> getFilaJokers() { return filaJokers; }
    public ArrayList<ItemTienda> getFilaSantos() { return filaSantos; }
    public ArrayList<ItemTienda> getFilaZodiaco() { return filaZodiaco; }

    public void removerItemComprado(ItemTienda item) {
        filaCartas.remove(item);
        filaJokers.remove(item);
        filaSantos.remove(item);
        filaZodiaco.remove(item);
    }

    public void aplicarDescuento50() { multiplicadorPrecio = 0.5; }
    public double getMultiplicadorPrecio() { return multiplicadorPrecio; }

    public void sumarRerollsGratis(int cantidad) { rerollsGratis += cantidad; }

    public boolean rerollearJokers(Jugador jugador) {
        if (!jugador.gastarPesos(costoRerollJokers())) return false;
        rerollsJokers++;
        jugador.sumarRerollTienda();
        generarFilaJokers(jugador);
        return true;
    }

    public boolean rerollearCartas(Jugador jugador) {
        int costo = costoRerollCartas();
        if (rerollsGratis > 0) { rerollsGratis--; }
        else if (!jugador.gastarPesos(costo)) return false;
        rerollsCartas++;
        jugador.sumarRerollTienda();
        generarFilaCartas();
        return true;
    }

    public void sumarEspacioJokersTienda(int c) { espacioJokersExtra += c; cantidadJokers += c; }
    public void sumarEspacioSantosTienda(int c) { espacioSantosExtra += c; }
}
