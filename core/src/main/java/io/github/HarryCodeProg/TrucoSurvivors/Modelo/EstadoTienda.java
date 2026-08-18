package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

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
    private final PoolSantosTienda poolSantos = new PoolSantosTienda();
    private int cantidadSantos = 2;
    private int rerollsTienda = 0;

    public EstadoTienda(Jugador jugador) {
        generarFilaCartas();
        generarFilaJokers(jugador);
        generarFilaSantos();
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

    public void sumarEspacioCartasTienda(int c) {
        cantidadCartas += c;
    }

    public void generarFilaSantos() {
        filaSantos.clear();
        for (int i = 0; i < cantidadSantos; i++) {
            Santo santo = poolSantos.tomarAleatorio(random);
            System.out.println("Santo tienda: " + (santo != null ? santo.getNombre() : "NULL"));
            if (santo != null) {
                filaSantos.add(
                    ItemTienda.deSanto(santo, santo.getCoste())
                );
            }
        }
    }

    public void sumarEspacioSantosTienda(int cantidad) {
        espacioSantosExtra += cantidad;
        cantidadSantos += cantidad;
    }

    public boolean rerollearTodo(Jugador jugador) {
        int costoTotal = costoRerollCartas() + costoRerollJokers();
        if (rerollsGratis > 0) {
            // decidir conducta: consumir 1 reroll gratis para cartas (o para todo). Aquí lo dejamos simple:
            // si hay rerollsGratis, lo aplicamos sólo a las cartas (como ahora) y cobramos jokers.
        }
        if (!jugador.gastarPesos(costoTotal)) return false;
        rerollsCartas++;
        rerollsJokers++;
        jugador.sumarRerollTienda();
        generarFilaCartas();
        generarFilaJokers(jugador);
        generarFilaSantos();
        return true;
    }

    public int costoRerollTienda() {
        if (rerollsGratis > 0) return 0;
        return ConfiguracionEconomia.COSTO_REROLL_BASE + rerollsTienda;
    }

    public boolean rerollearTienda(Jugador jugador) {
        int costo = costoRerollTienda();
        if (costo == 0) {
            if (rerollsGratis > 0) rerollsGratis--;
            generarFilaCartas();
            generarFilaJokers(jugador);
            generarFilaSantos();
            return true;
        }
        if (!jugador.gastarPesos(costo)) return false;
        // Pago exitoso: incrementar contador local de esta instancia de tienda
        rerollsTienda++;
        jugador.sumarRerollTienda(); // si quieres contar usos totales del jugador globalmente
        generarFilaCartas();
        generarFilaJokers(jugador);
        generarFilaSantos();
        return true;
    }
}
