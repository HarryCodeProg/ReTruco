package io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado;

import java.util.ArrayList;

public class DatosEstadoTienda {
    public ArrayList<DatosItemTienda> filaCartas = new ArrayList<>();
    public ArrayList<DatosItemTienda> filaJokers = new ArrayList<>();
    public ArrayList<DatosItemTienda> filaSantos = new ArrayList<>();
    public int rerollsCartas;
    public int rerollsJokers;
    public int rerollsTienda;
    // Rueda del zodiaco
    public boolean ruedaUsada = false;
    public String signoPendiente = null; // signo ya sacado cuyo efecto todavía no se aplicó
    // Ajustes que los signos le hacen a la tienda (-1 = guardado viejo, no restaurar)
    public double multiplicadorPrecio = -1;
    public int rerollsGratis = -1;
    public int cantidadCartas = -1;
    public int cantidadJokers = -1;
    public int cantidadSantos = -1;
}
