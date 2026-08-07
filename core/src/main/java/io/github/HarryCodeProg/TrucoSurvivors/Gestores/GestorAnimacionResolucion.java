package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Reproduce el log de una ResolucionPuntaje paso a paso, con delay entre cada uno,
 * estilo Balatro (aparece "+300 (Cola)", despues "x2 (Fernet)", etc.).
 * No toca el puntaje real del jugador — eso ya se aplico en Juego. Esto es 100% visual.
 */
public class GestorAnimacionResolucion {

    private static final float DELAY_ENTRE_PASOS = 0.6f;

    private Queue<ResolucionPuntaje.PasoResolucion> cola = new ArrayDeque<>();
    private ResolucionPuntaje.PasoResolucion pasoActual;
    private float cronometro;
    private boolean activa;
    private Runnable callbackAlTerminar;
    private java.util.function.Consumer<ResolucionPuntaje.PasoResolucion> callbackPorPaso;

    public void iniciar(ResolucionPuntaje resolucion, java.util.function.Consumer<ResolucionPuntaje.PasoResolucion> callbackPorPaso, Runnable callbackAlTerminar) {
        cola.clear();
        cola.addAll(resolucion.getLog());
        this.callbackPorPaso = callbackPorPaso;
        this.callbackAlTerminar = callbackAlTerminar;
        this.activa = true;
        this.cronometro = 0f;
        this.pasoActual = null;
        avanzarPaso(); // muestra el primer paso (Base) inmediatamente
    }

    public void update(float delta) {
        if (!activa) return;
        cronometro += delta;
        if (cronometro >= DELAY_ENTRE_PASOS) {
            cronometro = 0f;
            avanzarPaso();
        }
    }

    private void avanzarPaso() {
        if (cola.isEmpty()) {
            activa = false;
            pasoActual = null;
            if (callbackAlTerminar != null) callbackAlTerminar.run();
            return;
        }
        pasoActual = cola.poll();
        if (callbackPorPaso != null) callbackPorPaso.accept(pasoActual);
    }

    public boolean isActiva() { return activa; }
    public ResolucionPuntaje.PasoResolucion getPasoActual() { return pasoActual; }
}
