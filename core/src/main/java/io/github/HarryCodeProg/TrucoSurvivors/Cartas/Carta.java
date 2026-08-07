package io.github.HarryCodeProg.TrucoSurvivors.Cartas;

public class Carta {
    private int numero;
    private Palo palo;
    private int valorTrucoBase;
    private int valorTrucoActual;
    private int valorEnvidoBase;
    private int valorEnvidoActual;
    private int bonusTrucoPermanente = 0;
    private int bonusEnvidoPermanente = 0;
    // Multiplicador propio de la carta, separado en persistente (base) y no-persistente (temporal).
    // Efectivo = base + temporal. El temporal se resetea en cada resetearValores().
    private double multiplicadorTrucoPropioBase = 1.0;
    private double multiplicadorTrucoPropioTemporal = 0.0;
    private double multiplicadorEnvidoPropioBase = 1.0;
    private double multiplicadorEnvidoPropioTemporal = 0.0;

    public Carta(int numero, Palo palo){
        this.numero = numero;
        this.palo = palo;
        calcularValoresBase();
        resetearValores();
    }

    public int getNumero(){
        return this.numero;
    }

    public Palo getPalo(){
        return this.palo;
    }

    public int getValorTrucoBase() {
        return this.valorTrucoBase;
    }

    public int getValorEnvidoBase(){
        return this.valorEnvidoBase;
    }

    public int getValorTrucoActual() {
        return this.valorTrucoActual;
    }

    public int getValorEnvidoActual(){
        return this.valorEnvidoActual;
    }

    public void calcularValoresBase() {
        if (numero == 1 && palo == Palo.ESPADA) valorTrucoBase = 16;
        else if (numero == 1 && palo == Palo.BASTO) valorTrucoBase = 15;
        else if (numero == 7 && palo == Palo.ESPADA) valorTrucoBase = 14;
        else if (numero == 7 && palo == Palo.ORO) valorTrucoBase = 13;
        else {
            switch (numero) {
                case 3: valorTrucoBase = 12; break;
                case 2: valorTrucoBase = 11; break;
                case 1: valorTrucoBase = 10; break;
                case 12: valorTrucoBase = 9; break;
                case 11: valorTrucoBase = 8; break;
                case 10: valorTrucoBase = 7; break;
                case 9: valorTrucoBase = 6; break;
                case 8: valorTrucoBase = 5; break;
                case 7: valorTrucoBase = 4; break;
                case 6: valorTrucoBase = 3; break;
                case 5: valorTrucoBase = 2; break;
                case 4: valorTrucoBase = 1; break;
            }
        }
        this.valorEnvidoBase = (numero >= 10) ? 0 : numero;
        this.valorEnvidoActual = valorEnvidoBase;
    }

    /**
     * Se llama al pasar de mano (devolverCartas). Restaura valorTruco/valorEnvido
     * a su base + bonus permanente, y limpia cualquier multiplicador temporal
     * (no-persistente) que se haya acumulado durante la mano anterior.
     * Los bonus permanentes y los multiplicadores base NO se tocan aca: persisten.
     */
    public void resetearValores() {
        valorTrucoActual = valorTrucoBase + bonusTrucoPermanente;
        valorEnvidoActual = valorEnvidoBase + bonusEnvidoPermanente;
        multiplicadorTrucoPropioTemporal = 0.0;
        multiplicadorEnvidoPropioTemporal = 0.0;
    }

    /** No persiste: se resetea en resetearValores(). Se acumula con otros efectos iguales durante la mano. */
    public void modificarValorTruco(int cantidad) {
        valorTrucoActual += cantidad;
    }

    /** No persiste: idem para envido. */
    public void modificarValorEnvido(int cantidad) {
        valorEnvidoActual += cantidad;
    }

    /** Persiste para siempre en esta carta (hasta que algo especial lo revierta). */
    public void modificarValorTrucoPermanente(int cantidad) {
        this.bonusTrucoPermanente += cantidad;
        this.valorTrucoActual += cantidad;
    }

    /** Persiste para siempre en esta carta. */
    public void modificarValorEnvidoPermanente(int cantidad) {
        this.bonusEnvidoPermanente += cantidad;
        this.valorEnvidoActual += cantidad;
    }

    // ---------------- Multiplicador propio: TRUCO ----------------

    /** Persiste: queda para siempre en la carta. */
    public void sumarMultiplicadorTrucoPermanente(double cantidad) {
        this.multiplicadorTrucoPropioBase += cantidad;
    }

    /** No persiste: se acumula durante la mano, se resetea en resetearValores(). */
    public void sumarMultiplicadorTrucoTemporal(double cantidad) {
        this.multiplicadorTrucoPropioTemporal += cantidad;
    }

    public double getMultiplicadorTrucoPropioBase() { return multiplicadorTrucoPropioBase; }
    public double getMultiplicadorTrucoPropioTemporal() { return multiplicadorTrucoPropioTemporal; }

    /** Multiplicador propio efectivo: base (persistente) + temporal (de esta mano). */
    public double getMultiplicadorTrucoPropio() {
        return multiplicadorTrucoPropioBase + multiplicadorTrucoPropioTemporal;
    }

    public double getValorTrucoEfectivo() {
        return valorTrucoActual * getMultiplicadorTrucoPropio();
    }

    // ---------------- Multiplicador propio: ENVIDO ----------------

    /** Persiste: queda para siempre en la carta. */
    public void sumarMultiplicadorEnvidoPermanente(double cantidad) {
        this.multiplicadorEnvidoPropioBase += cantidad;
    }

    /** No persiste: se acumula durante la mano, se resetea en resetearValores(). */
    public void sumarMultiplicadorEnvidoTemporal(double cantidad) {
        this.multiplicadorEnvidoPropioTemporal += cantidad;
    }

    public double getMultiplicadorEnvidoPropioBase() { return multiplicadorEnvidoPropioBase; }
    public double getMultiplicadorEnvidoPropioTemporal() { return multiplicadorEnvidoPropioTemporal; }

    /** Multiplicador propio efectivo: base (persistente) + temporal (de esta mano). */
    public double getMultiplicadorEnvidoPropio() {
        return multiplicadorEnvidoPropioBase + multiplicadorEnvidoPropioTemporal;
    }

    public double getValorEnvidoEfectivo() {
        return valorEnvidoActual * getMultiplicadorEnvidoPropio();
    }

    public String getRutaImagen() {
        return "imagenesCartas/" +
            numero +
            "_" +
            palo.name().toLowerCase() +
            ".PNG";
    }

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
