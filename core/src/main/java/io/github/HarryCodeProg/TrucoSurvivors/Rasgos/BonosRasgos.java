package io.github.HarryCodeProg.TrucoSurvivors.Rasgos;

public class BonosRasgos {
    public int tamanoMano, descartes, espacioJokers, espacioSantos, topeInteres;
    public int tiendaJokers, tiendaCartas, tiendaSantos;
    private boolean visible = true;

    /** En false los getters del Jugador devuelven solo la base (se usa al guardar). */
    public void setVisible(boolean v) { visible = v; }
    public int v(int x) { return visible ? x : 0; }

    public void limpiar() {
        tamanoMano = descartes = espacioJokers = espacioSantos = topeInteres = 0;
        tiendaJokers = tiendaCartas = tiendaSantos = 0;
    }
}
