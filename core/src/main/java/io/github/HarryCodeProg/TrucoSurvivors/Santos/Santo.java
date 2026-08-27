package io.github.HarryCodeProg.TrucoSurvivors.Santos;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;

import java.util.ArrayList;

public abstract class Santo {
    private final int id;
    private final String nombre;
    private final String nombreArchivo;
    private final String descripcion;
    private final int coste;
    private final ArrayList<Carta> cartasDiferidas = new ArrayList<>();
    private final ArrayList<Runnable> accionesDiferidas = new ArrayList<>();

    public Santo(int id, String nombre, String nombreArchivo, String descripcion, int coste) {
        this.id = id;
        this.nombre = nombre;
        this.nombreArchivo = nombreArchivo;
        this.descripcion = descripcion;
        this.coste = coste;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getCoste() { return coste; }
    public String getNombreRegion() { return id + "_" + nombreArchivo; }

    /** Cuantas cartas necesita seleccionadas para poder usarse (0 = uso instantaneo, sin seleccion). */
    public abstract int cartasRequeridas();

    /** Ejecuta el efecto. 'seleccionadas' respeta el orden de seleccion (para efectos tipo "izquierda->derecha"). */
    public abstract void aplicarEfecto(Jugador jugador, ArrayList<Carta> seleccionadas, ContextoJuego ctxOpcional);

    public abstract int maxCartasSeleccionables();

    protected void diferirCambioVisual(Carta carta, Runnable accion) {
        cartasDiferidas.add(carta);
        accionesDiferidas.add(accion);
    }

    public boolean tieneCambiosDiferidos() { return !cartasDiferidas.isEmpty(); }
    public ArrayList<Carta> getCartasDiferidas() { return cartasDiferidas; }
    public ArrayList<Runnable> getAccionesDiferidas() { return accionesDiferidas; }
    public void limpiarDiferidos() { cartasDiferidas.clear(); accionesDiferidas.clear(); }

    public void transferirDiferidosDesde(Santo otro) {
        this.cartasDiferidas.addAll(otro.getCartasDiferidas());
        this.accionesDiferidas.addAll(otro.getAccionesDiferidas());
        otro.limpiarDiferidos();
    }
}
