package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Vista.Arrastrable;
import java.util.ArrayList;

public class AreaElementos<T extends Arrastrable> {
    private float x, y, ancho, alto;
    private float anchoElemento, altoElemento;
    private float separacionDeseada;
    private final ArrayList<Float> slots = new ArrayList<>();
    // private float separacion;   ← ELIMINADO (nunca se leía)

    public AreaElementos(float x, float y, float ancho, float alto,
                         float anchoElemento, float altoElemento, float separacionDeseada) {
        this.x = x; this.y = y; this.ancho = ancho; this.alto = alto;
        this.anchoElemento = anchoElemento; this.altoElemento = altoElemento;
        this.separacionDeseada = separacionDeseada;
    }

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

    /** Cambia la separación deseada en runtime (afecta la próxima distribución). */
    public void setSeparacion(float s) { this.separacionDeseada = s; }   // ← ahora sí afecta

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }
}
