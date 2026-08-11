package io.github.HarryCodeProg.TrucoSurvivors.Cartas;

public class Carta {
    private int numero;
    private Palo palo;
    // --- 1. JERARQUÍA / PODER DE LA CARTA (qué mata a qué) ---
    private int valorTrucoPoderBase;
    private int valorTrucoPoderActual;
    private int bonusPoderTrucoPermanente = 0;
    private double multiplicadorPoderTrucoBase = 1.0;
    private double multiplicadorPoderTrucoTemporal = 0.0;
    // --- 2. APORTE A LA PUNTUACIÓN (Puntos Truco / Chips) ---
    private int puntosTrucoAporteBase;      // Ej: 10 para 7 de ORO
    private int puntosTrucoAporteActual;    // Aporte actual (incluye permanentes)
    private int bonusAporteTrucoPermanente = 0;
    private double multiplicadorAporteTrucoBase = 1.0;
    private double multiplicadorAporteTrucoTemporal = 0.0;
    // --- 3. ENVIDO (poder/valor y aporte) ---
    private int valorEnvidoPoderBase;
    private int valorEnvidoPoderActual;
    private int bonusPoderEnvidoPermanente = 0;
    private double multiplicadorPoderEnvidoBase = 1.0;
    private double multiplicadorPoderEnvidoTemporal = 0.0;
    private int puntosEnvidoAporteBase;     // aporte numérico de la carta cuando participa en envido
    private int puntosEnvidoAporteActual;
    private int bonusAporteEnvidoPermanente = 0;
    private double multiplicadorAporteEnvidoBase = 1.0;
    private double multiplicadorAporteEnvidoTemporal = 0.0;

    public Carta(int numero, Palo palo){
        this.numero = numero;
        this.palo = palo;
        calcularValoresBase();
        resetearValores();
    }

    // -------------------- GETTERS BÁSICOS --------------------

    public int getNumero(){ return this.numero; }
    public Palo getPalo(){ return this.palo; }

    // Jerarquía / Poder (compatibilidad con API previa)
    public int getValorTrucoBase() { return this.valorTrucoPoderBase; }
    public int getValorTrucoActual() { return this.valorTrucoPoderActual; }

    // Envido base/actual
    public int getValorEnvidoBase() { return this.valorEnvidoPoderBase; }
    public int getValorEnvidoActual() { return this.valorEnvidoPoderActual; }

    // Puntos / Aporte
    public int getPuntosTrucoAporteActual() { return this.puntosTrucoAporteActual; }
    public int getPuntosEnvidoAporteActual() { return this.puntosEnvidoAporteActual; }

    // ---------------- Multiplicadores efectivos ----------------

    // Poder (jerarquía)
    public double getMultiplicadorPoderTruco() {
        return multiplicadorPoderTrucoBase + multiplicadorPoderTrucoTemporal;
    }
    public void sumarMultiplicadorTrucoTemporal(double cantidad) { this.multiplicadorPoderTrucoTemporal += cantidad; }
    public void sumarMultiplicadorTrucoPermanente(double cantidad) { this.multiplicadorPoderTrucoBase += cantidad; }

    // Aporte (chips) - truco
    public double getMultiplicadorAporteTruco() {
        return multiplicadorAporteTrucoBase + multiplicadorAporteTrucoTemporal;
    }
    public void sumarMultiplicadorTrucoAporteTemporal(double cantidad) { this.multiplicadorAporteTrucoTemporal += cantidad; }
    public void sumarMultiplicadorTrucoAportePermanente(double cantidad) { this.multiplicadorAporteTrucoBase += cantidad; }

    // Poder envido
    public double getMultiplicadorPoderEnvido() {
        return multiplicadorPoderEnvidoBase + multiplicadorPoderEnvidoTemporal;
    }
    public void sumarMultiplicadorPoderEnvidoTemporal(double cantidad) { this.multiplicadorPoderEnvidoTemporal += cantidad; }
    public void sumarMultiplicadorPoderEnvidoPermanente(double cantidad) { this.multiplicadorPoderEnvidoBase += cantidad; }

    // Aporte envido
    public double getMultiplicadorAporteEnvido() {
        return multiplicadorAporteEnvidoBase + multiplicadorAporteEnvidoTemporal;
    }
    public void sumarMultiplicadorAporteEnvidoTemporal(double cantidad) { this.multiplicadorAporteEnvidoTemporal += cantidad; }
    public void sumarMultiplicadorAporteEnvidoPermanente(double cantidad) { this.multiplicadorAporteEnvidoBase += cantidad; }

    // ---------------- Valores efectivos calculados ----------------

    /** La jerarquía final que se usa para comparar contra otra carta (para "matar"). */
    public int getValorTrucoPoderActual() {
        return valorTrucoPoderActual;
    }

    /** Valor de truco final usado en comparaciones (aplica multiplicador de poder). */
    public int getValorTrucoEfectivo() {
        return (int) Math.round(valorTrucoPoderActual * getMultiplicadorPoderTruco());
    }

    public int getValorEnvidoEfectivo() {
        return (int) Math.round(valorEnvidoPoderActual * getMultiplicadorPoderEnvido());
    }

    /** La cantidad final de Chips que aporta esta carta al activarse en Truco. */
    public int getPuntosTrucoAporteEfectivo() {
        return (int) Math.round(puntosTrucoAporteActual * getMultiplicadorAporteTruco());
    }

    /** La cantidad final de Puntos Envido que aporta esta carta al participar en el envido. */
    public int getPuntosEnvidoAporteEfectivo() {
        return (int) Math.round(puntosEnvidoAporteActual * getMultiplicadorAporteEnvido());
    }

    // -------------------- MODIFICADORES --------------------

    // ---------- PODER (jerarquía) ----------
    /** No persiste: modifica poder jerárquico temporalmente (se resetea en resetearValores()). */
    public void modificarValorTruco(int cantidad) { valorTrucoPoderActual += cantidad; }

    /** Persiste: modifica poder jerárquico permanentemente. */
    public void modificarValorTrucoPermanente(int cantidad) {
        this.bonusPoderTrucoPermanente += cantidad;
        this.valorTrucoPoderActual += cantidad;
    }

    // ---------- APORTE (Puntos Truco / Chips) ----------
    /** No persiste: modifica el aporte de puntos truco temporalmente (se resetea en resetearValores()). */
    public void modificarPuntosTrucoAporte(int cantidad) { puntosTrucoAporteActual += cantidad; }

    /** Persiste: modifica el aporte de puntos truco permanentemente. */
    public void modificarPuntosTrucoAportePermanente(int cantidad) {
        this.bonusAporteTrucoPermanente += cantidad;
        this.puntosTrucoAporteActual += cantidad;
    }

    // ---------- ENVIDO ----------
    /** No persiste: modifica valor de envido temporalmente. */
    public void modificarValorEnvido(int cantidad) { valorEnvidoPoderActual += cantidad; }

    /** Persiste: modifica valor de envido permanentemente. */
    public void modificarValorEnvidoPermanente(int cantidad) {
        this.bonusPoderEnvidoPermanente += cantidad;
        this.valorEnvidoPoderActual += cantidad;
    }

    /** No persiste: modifica aporte de envido temporalmente. */
    public void modificarPuntosEnvidoAporte(int cantidad) { puntosEnvidoAporteActual += cantidad; }

    /** Persiste: modifica aporte de envido permanentemente. */
    public void modificarPuntosEnvidoAportePermanente(int cantidad) {
        this.bonusAporteEnvidoPermanente += cantidad;
        this.puntosEnvidoAporteActual += cantidad;
    }

    // Wrappers / compatibilidad por si otras partes del código usan nombres anteriores:
    public void modificarValorTrucoPermanenteAlias(int cantidad) { modificarValorTrucoPermanente(cantidad); }
    public void modificarValorEnvidoPermanenteAlias(int cantidad) { modificarValorEnvidoPermanente(cantidad); }
    public void modificarPuntosTruco(int cantidad) { modificarPuntosTrucoAporte(cantidad); }
    public void modificarPuntosTrucoPermanente(int cantidad) { modificarPuntosTrucoAportePermanente(cantidad); }

    // -------------------- Inicialización / reset --------------------

    public void calcularValoresBase() {
        // --- Jerarquía / Poder (mapeo clásico de Truco) ---
        if (numero == 1 && palo == Palo.ESPADA) valorTrucoPoderBase = 16;
        else if (numero == 1 && palo == Palo.BASTO) valorTrucoPoderBase = 15;
        else if (numero == 7 && palo == Palo.ESPADA) valorTrucoPoderBase = 14;
        else if (numero == 7 && palo == Palo.ORO) valorTrucoPoderBase = 13;
        else {
            switch (numero) {
                case 3: valorTrucoPoderBase = 12; break;
                case 2: valorTrucoPoderBase = 11; break;
                case 1: valorTrucoPoderBase = 10; break;
                case 12: valorTrucoPoderBase = 9; break;
                case 11: valorTrucoPoderBase = 8; break;
                case 10: valorTrucoPoderBase = 7; break;
                case 9: valorTrucoPoderBase = 6; break;
                case 8: valorTrucoPoderBase = 5; break;
                case 7: valorTrucoPoderBase = 4; break;
                case 6: valorTrucoPoderBase = 3; break;
                case 5: valorTrucoPoderBase = 2; break;
                case 4: valorTrucoPoderBase = 1; break;
                default: valorTrucoPoderBase = 0; break;
            }
        }

        // --- Puntos Truco / Aporte base (ejemplo -- ajustar si querés otros valores) ---
        if (numero == 7 && palo == Palo.ORO) puntosTrucoAporteBase = 10;
        else if (numero == 7 && palo == Palo.ESPADA) puntosTrucoAporteBase = 8;
        else if (numero == 3) puntosTrucoAporteBase = 5;
        else if (numero == 2) puntosTrucoAporteBase = 3;
        else if (numero == 1) puntosTrucoAporteBase = 1;
        else puntosTrucoAporteBase = 0;

        // --- Envido (poder/valor) ---
        this.valorEnvidoPoderBase = (numero >= 10) ? 0 : numero;
        this.puntosEnvidoAporteBase = this.valorEnvidoPoderBase;
    }

    /**
     * Se llama al finalizar la mano / devolverCartas: restaura valores a su estado base + permanentes
     * y limpia cualquier multiplicador/valor temporal.
     */
    public void resetearValores() {
        // Reset Poder (jerarquía)
        valorTrucoPoderActual = valorTrucoPoderBase + bonusPoderTrucoPermanente;
        multiplicadorPoderTrucoTemporal = 0.0;

        // Reset Aporte (Puntos Truco)
        puntosTrucoAporteActual = puntosTrucoAporteBase + bonusAporteTrucoPermanente;
        multiplicadorAporteTrucoTemporal = 0.0;

        // Reset Envido
        valorEnvidoPoderActual = valorEnvidoPoderBase + bonusPoderEnvidoPermanente;
        multiplicadorPoderEnvidoTemporal = 0.0;
        puntosEnvidoAporteActual = puntosEnvidoAporteBase + bonusAporteEnvidoPermanente;
        multiplicadorAporteEnvidoTemporal = 0.0;
    }

    // -------------------- Utilidades / representación --------------------

    public String paloToString() {
        switch (this.palo) {
            case ESPADA: return "Espada";
            case BASTO: return "Basto";
            case ORO: return "Oro";
            case COPA: return "Copa";
            default: return "Desconocido";
        }
    }

    public String getNombreRegion() {
        return numero + "_" + palo.name().toLowerCase();
    }
}
