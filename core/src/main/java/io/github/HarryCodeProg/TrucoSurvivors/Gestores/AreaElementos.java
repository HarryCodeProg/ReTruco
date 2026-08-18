package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Vista.Arrastrable;
import java.util.ArrayList;

/**
 * Administra automáticamente la distribución de una fila de elementos arrastrables
 * dentro de un área rectangular. Calcula posiciones, espaciado y solapamiento.
 * No conoce reglas de juego, solo layout. El elemento que se está arrastrando
 * no recibe nueva posición objetivo (sigue al cursor libremente).
 */
public class AreaElementos<T extends Arrastrable> {
    private float x, y, ancho, alto;
    private float anchoElemento, altoElemento;
    private float separacionDeseada;
    private final ArrayList<Float> slots = new ArrayList<>();

    public AreaElementos(float x, float y, float ancho, float alto,
                         float anchoElemento, float altoElemento, float separacionDeseada) {
        this.x = x; this.y = y; this.ancho = ancho; this.alto = alto;
        this.anchoElemento = anchoElemento; this.altoElemento = altoElemento;
        this.separacionDeseada = separacionDeseada;
    }

    /** Recalcula y aplica las posiciones objetivo de todos los elementos, salvo el que se está arrastrando. */
    public void distribuir(ArrayList<T> elementos, T arrastrado) {
        int cantidad = elementos.size();
        slots.clear();
        if (cantidad == 0) return;
        float paso = GestorReordenamiento.calcularPaso(cantidad, anchoElemento, separacionDeseada, ancho);
        float anchoTotal = anchoElemento + (cantidad - 1) * paso;
        float inicioX = x + (ancho - anchoTotal) / 2f;
        for (int i = 0; i < cantidad; i++) {
            float px = inicioX + i * paso;
            slots.add(px);
            T elemento = elementos.get(i);
            if (elemento == arrastrado) continue;
            elemento.setHandPosition(px, y);
        }
    }

    /** Fuerza la posición real (x/y) además del objetivo — útil para spawns iniciales sin animación de entrada. */
    public void distribuirInstantaneo(ArrayList<T> elementos, T arrastrado) {
        distribuir(elementos, arrastrado);
        // No hay setPosition en Arrastrable por diseño (solo setHandPosition), así que
        // esto queda como hook si en el futuro se necesita snap inmediato sin interpolar.
    }

    public ArrayList<Float> getSlots() { return slots; }

    public void setBounds(float x, float y, float ancho, float alto) {
        this.x = x; this.y = y; this.ancho = ancho; this.alto = alto;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }

}
