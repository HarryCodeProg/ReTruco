package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import java.util.ArrayList;

public class ResolucionPuntaje {
    private double chips;
    private double mult;
    private final ArrayList<PasoResolucion> log = new ArrayList<>();

    public ResolucionPuntaje(double chipsIniciales, double multInicial) {
        this.chips = chipsIniciales;
        this.mult = multInicial;
        log.add(new PasoResolucion("Base", TipoPaso.BASE, 0, chips, mult));
    }

    public void sumarChips(double cantidad, String origen) {
        chips += cantidad;
        log.add(new PasoResolucion(origen, TipoPaso.SUMA_CHIPS, cantidad, chips, mult));
    }

    public void sumarMult(double cantidad, String origen) {
        mult += cantidad;
        log.add(new PasoResolucion(origen, TipoPaso.SUMA_MULT, cantidad, chips, mult));
    }

    public void multiplicarMult(double factor, String origen) {
        mult *= factor;
        log.add(new PasoResolucion(origen, TipoPaso.MULT_MULT, factor, chips, mult));
    }

    public double getChips() { return chips; }
    public double getMult() { return mult; }
    public double calcularPuntajeFinal() { return chips * mult; }
    public ArrayList<PasoResolucion> getLog() { return log; }

    public enum TipoPaso { BASE, SUMA_CHIPS, SUMA_MULT, MULT_MULT }

    /** Un paso individual de la resolucion. Guarda el snapshot COMPLETO de chips y mult
     * despues de aplicarse este paso, para que la UI pueda mostrar ambos valores corriendo
     * en simultaneo durante toda la animacion, no solo el que cambio en este paso puntual. */
    public static class PasoResolucion {
        public final String origen;
        public final TipoPaso tipo;
        public final double valor;        // cuanto sumo/multiplico en ESTE paso
        public final double chipsActual;  // snapshot de chips despues de este paso
        public final double multActual;   // snapshot de mult despues de este paso

        public PasoResolucion(String origen, TipoPaso tipo, double valor, double chipsActual, double multActual) {
            this.origen = origen;
            this.tipo = tipo;
            this.valor = valor;
            this.chipsActual = chipsActual;
            this.multActual = multActual;
        }

        @Override
        public String toString() {
            switch (tipo) {
                case BASE: return "Base: " + chipsActual + " chips x " + multActual + " mult";
                case SUMA_CHIPS: return origen + ": +" + (int) valor + " chips";
                case SUMA_MULT: return origen + ": +" + (int) valor + " mult";
                case MULT_MULT: return origen + ": x" + valor + " mult";
                default: return "";
            }
        }
    }
}
