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
}
