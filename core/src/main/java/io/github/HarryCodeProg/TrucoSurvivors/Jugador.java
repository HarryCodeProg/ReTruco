package io.github.HarryCodeProg.TrucoSurvivors;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro.Salta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro.Tucuman;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Jugador {
    private String nombre;
    private ArrayList<Carta> mano;
    private ArrayList<Joker> jokers;
    private Mazo mazo;
    private int tamañoMano;
    private int tamañoJokers;
    private double envidoActual;
    private double multiplicadorTruco;
    private double multiplicadorEnvido;
    private double multiplicadorTrucoTemporal;
    private double multiplicadorEnvidoTemporal;
    private double puntajeTotal;
    private int pesos = 10000;
    private static final int INTERVALO_INTERES = 5;
    private static final int TOPE_INTERES = 5;
    private int descartesBase = 6;
    private int descartesExtra = 0;
    private int espacioSantosExtra = 0;
    private int proximoEfectoZodiacoMultiplicador = 1; // Libra
    private int rerollsTienda = 0;
    private double bonusEnvidoFinal = 0;
    private ArrayList<Santo> santos = new ArrayList<>();
    private int tamañoSantos = 3; // capacidad base
    private final List<Consumer<Joker>> jokerAddedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Joker>> jokerRemovedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Santo>> santoAddedListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<Santo>> santoRemovedListeners = new CopyOnWriteArrayList<>();
    private int espacioJokersTiendaExtra = 0;
    private int espacioCartasTiendaExtra = 0;
    private int espacioSantosTiendaExtra = 0;
    private double multiplicadorPrecioTienda = 1.0;
    private int rerollsGratisTienda = 0;
    private Santo ultimoSantoUsado;
    private int manosMaximas = 6;
    private int manosActuales = manosMaximas;
    private int deudaMaxima = 0;
    private int topeInteresExtra = 0;
    private SignoZodiaco proximoZodiacoForzado;
    private double totalPesosGanados = 0;
    private double totalPesosGastados = 0;

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

    public int getEspacioJokersUsados() {
        int usados = 0;
        for (Joker j : jokers) if (j.ocupaEspacio()) usados++;
        return usados;
    }

    public boolean agregarJoker(Joker joker) {
        int necesita = joker.ocupaEspacio() ? 1 : 0;
        if (getEspacioJokersUsados() + necesita <= tamañoJokers) {
            this.jokers.add(joker);
            joker.setJugadorPropietario(this);
            joker.aplicarEfectoInstantaneo(this);
            for (Consumer<Joker> l : jokerAddedListeners) {
                try { l.accept(joker); } catch (Exception e) { e.printStackTrace(); }
            }
            return true;
        }
        return false;
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

    public void eliminarJoker(Joker joker) {
        this.jokers.remove(joker);
        joker.desAplicarEfectoInstantaneo(this);
        for (Consumer<Joker> l : jokerRemovedListeners) {
            try { l.accept(joker); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public int getPesos() { return pesos; }

    /** Devuelve la cantidad de dinero que tiene el jugador (pesos). */
    public int getDinero() { return pesos; }

    public int calcularInteres() {
        return Math.min(pesos / INTERVALO_INTERES, TOPE_INTERES + topeInteresExtra);
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
                    if (paloEfectivo(mano.get(i).getPalo()) == paloEfectivo(mano.get(j).getPalo())) { // FIX
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
                if (i != j && paloEfectivo(mano.get(i).getPalo()) == paloEfectivo(mano.get(j).getPalo())) { // FIX
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
            if (tomada != null) mano.add(tomada);
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

    public ArrayList<Santo> getSantos() { return santos; }
    public int getTamañoSantos() { return tamañoSantos + espacioSantosExtra; } // espacioSantosExtra ya definido para Virgo

    public void addJokerAddedListener(Consumer<Joker> listener) {
        jokerAddedListeners.add(listener);
    }
    public void removeJokerAddedListener(Consumer<Joker> listener) {
        jokerAddedListeners.remove(listener);
    }
    public void addJokerRemovedListener(Consumer<Joker> listener) {
        jokerRemovedListeners.add(listener);
    }
    public void removeJokerRemovedListener(Consumer<Joker> listener) {
        jokerRemovedListeners.remove(listener);
    }

    public boolean agregarSanto(Santo santo) {
        if (santos.size() < getTamañoSantos()) {
            santos.add(santo);
            for (Consumer<Santo> l : santoAddedListeners) { try { l.accept(santo); } catch (Exception e) { e.printStackTrace(); } }
            return true;
        }
        return false;
    }

    public void eliminarSanto(Santo santo) {
        santos.remove(santo);
        for (Consumer<Santo> l : santoRemovedListeners) { try { l.accept(santo); } catch (Exception e) { e.printStackTrace(); } }
    }

    public void addSantoAddedListener(Consumer<Santo> l) { santoAddedListeners.add(l); }
    public void removeSantoAddedListener(Consumer<Santo> l) { santoAddedListeners.remove(l); }
    public void addSantoRemovedListener(Consumer<Santo> l) { santoRemovedListeners.add(l); }
    public void removeSantoRemovedListener(Consumer<Santo> l) { santoRemovedListeners.remove(l); }

    public void sumarEspacioJokersTiendaPersistente(int c) { espacioJokersTiendaExtra += c; }
    public void sumarEspacioCartasTiendaPersistente(int c) { espacioCartasTiendaExtra += c; }
    public void sumarEspacioSantosTiendaPersistente(int c) { espacioSantosTiendaExtra += c; }
    public void aplicarDescuentoTiendaPersistente(double factor) { multiplicadorPrecioTienda *= factor; }
    public void sumarRerollsGratisTienda(int c) { rerollsGratisTienda += c; }

    public int getEspacioJokersTiendaExtra() { return espacioJokersTiendaExtra; }
    public int getEspacioCartasTiendaExtra() { return espacioCartasTiendaExtra; }
    public int getEspacioSantosTiendaExtra() { return espacioSantosTiendaExtra; }
    public double getMultiplicadorPrecioTienda() { return multiplicadorPrecioTienda; }
    public int getRerollsGratisTienda() { return rerollsGratisTienda; }

    /** Consume un reroll gratis persistente si hay. Devuelve true si lo consumió. */
    public boolean consumirRerollGratisTienda() {
        if (rerollsGratisTienda <= 0) return false;
        rerollsGratisTienda--;
        return true;
    }

    public Santo getUltimoSantoUsado() { return ultimoSantoUsado; }
    public void setUltimoSantoUsado(Santo santo) { this.ultimoSantoUsado = santo; }

    public int getManosActuales() { return manosActuales; }
    public int getManosMaximas() { return manosMaximas; }

    public void aumentarManosMaximas(int cantidad) {
        this.manosMaximas += cantidad;
        this.manosActuales += cantidad;
    }

    public void consumirMano() {
        if (manosActuales > 0) manosActuales--;
    }

    /** Restaura una mano, sin superar el máximo. Usado por SanNicolas. */
    public void restaurarUnaMano() {
        if (manosActuales < manosMaximas) manosActuales++;
    }

    /** Reinicia el contador al máximo — se llama al empezar un combate nuevo (mismo patrón que multTrucoOriginal). */
    public void reiniciarManos() {
        this.manosActuales = manosMaximas;
    }

    public Palo paloEfectivo(Palo palo) {
        if ((palo == Palo.BASTO || palo == Palo.COPA) && tieneJoker(Salta.class)) {
            return Palo.BASTO; // representante canónico del grupo Basto/Copa
        }
        if ((palo == Palo.ESPADA || palo == Palo.ORO) && tieneJoker(Tucuman.class)) {
            return Palo.ESPADA; // representante canónico del grupo Espada/Oro
        }
        return palo;
    }

    public boolean cartaCuentaComoPalo(Carta carta, Palo paloObjetivo) {
        return paloEfectivo(carta.getPalo()) == paloEfectivo(paloObjetivo);
    }

    private boolean tieneJoker(Class<? extends io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker> clase) {
        for (io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker j : jokers) {
            if (clase.isInstance(j)) return true;
        }
        return false;
    }

    public void sumarDeudaMaxima(int cantidad) { deudaMaxima += cantidad; }
    public int getDeudaMaxima() { return deudaMaxima; }

    public void sumarTopeInteresExtra(int cantidad) {
        this.topeInteresExtra += cantidad;
    }

    public void setProximoZodiacoForzado(SignoZodiaco signo) {
        this.proximoZodiacoForzado = signo;
    }

    public SignoZodiaco getProximoZodiacoForzado() {
        return proximoZodiacoForzado;
    }

    /** Saca todos los santos sin ejecutar su efecto (se "sacrifican"). Devuelve cuántos se consumieron. */
    public int consumirTodosLosSantos() {
        int cantidad = santos.size();
        ArrayList<Santo> aQuitar = new ArrayList<>(santos);
        for (Santo s : aQuitar) {
            santos.remove(s);
            for (Consumer<Santo> l : santoRemovedListeners) {
                try { l.accept(s); } catch (Exception e) { e.printStackTrace(); }
            }
        }
        return cantidad;
    }

    public boolean tieneDobleEfectoSantos() {
        return jokers.stream().anyMatch(j -> j.getId() == 121);
    }

    public void sumarPesos(int cantidad) {
        this.pesos += cantidad;
        if (cantidad > 0) totalPesosGanados += cantidad;
    }

    public boolean gastarPesos(int cantidad) {
        if (pesos - cantidad < -deudaMaxima) return false;
        pesos -= cantidad;
        totalPesosGastados += cantidad;
        for (Joker j : new ArrayList<>(jokers)) j.onPesosGastados(cantidad, this);
        return true;
    }

    public double getTotalPesosGanados() { return totalPesosGanados; }
    public double getTotalPesosGastados() { return totalPesosGastados; }
    public void setTotalPesosGanados(double v) { this.totalPesosGanados = v; }
    public void setTotalPesosGastados(double v) { this.totalPesosGastados = v; }
    public void resetEconomiaRun() { totalPesosGanados = 0; totalPesosGastados = 0; }

    public void resetearABase() {
        mano.clear();
        jokers.clear();
        santos.clear();
        tamañoMano = 3;
        tamañoJokers = 5;
        multiplicadorTruco = 1;
        multiplicadorEnvido = 1;
        multiplicadorTrucoTemporal = 1;
        multiplicadorEnvidoTemporal = 1;
        descartesBase = 6;
        descartesExtra = 0;
        //tamañoManoExtra = 0;
        espacioSantosExtra = 0;
        proximoEfectoZodiacoMultiplicador = 1;
        rerollsTienda = 0;
        bonusEnvidoFinal = 0;
        tamañoSantos = 3;
        espacioJokersTiendaExtra = 0;
        espacioCartasTiendaExtra = 0;
        espacioSantosTiendaExtra = 0;
        multiplicadorPrecioTienda = 1.0;
        rerollsGratisTienda = 0;
        manosMaximas = 6;
        manosActuales = manosMaximas;
        deudaMaxima = 0;
        topeInteresExtra = 0;
        totalPesosGanados = 0;
        totalPesosGastados = 0;
    }
}
