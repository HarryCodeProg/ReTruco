package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

public class DatosRival {
    private String nombre;
    private String descripcion;
    private double puntosMeta;
    private boolean desbloqueado;
    private int indice;
    private int nivelDificultad;

    public DatosRival(String nombre, String descripcion, double puntosMeta, boolean desbloqueado, int indice) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosMeta = puntosMeta;
        this.desbloqueado = desbloqueado;
        this.indice = indice;
    }

    public int getNivelDificultad() { return nivelDificultad; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPuntosMeta() { return puntosMeta; }
    public boolean isDesbloqueado() { return desbloqueado; }
    public void setDesbloqueado(boolean desbloqueado) {this.desbloqueado = desbloqueado;}
    public int getIndice() {return indice;}
}
