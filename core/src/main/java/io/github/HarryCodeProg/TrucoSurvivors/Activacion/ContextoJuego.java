package io.github.HarryCodeProg.TrucoSurvivors.Activacion;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mesa;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ContextoJuego {
    private final Jugador jugador;
    private final Jugador rival;
    private final Mazo mazo;
    private final Mesa mesa;
    private final Juego juego;
    private double puntosBaseCalculo;
    private ResolucionPuntaje resolucionActual;
    private Carta cartaEnResolucion;
    private ArrayDeque<Activacion> colaActivaciones;
    private ArrayList<Carta> cartasContribuyentesEnvido = new ArrayList<>();
    // Flags para "primera carta que mata/no mata" durante la resolución actual
    private boolean primerCartaQueMataAplicada = false;
    private boolean primerCartaQueNoMataAplicada = false;
    // Flags para "primera carta figura/no figura que puntua" durante la resolución actual
    private boolean primerFiguraPuntuadaAplicada = false;
    private boolean primerNoFiguraPuntuadaAplicada = false;
    private Carta cartaOponenteEnResolucion;

    public ContextoJuego(Jugador jugador, Jugador rival, Mazo mazo, Mesa mesa, Juego juego) {
        this.jugador = jugador;
        this.rival = rival;
        this.mazo = mazo;
        this.mesa = mesa;
        this.juego = juego;
    }

    public Jugador getJugador() { return jugador; }
    public Jugador getRival() { return rival; }
    public Mazo getMazo() { return mazo; }
    public Mesa getMesa() { return mesa; }
    public Juego getJuego() { return juego; }

    public double getPuntosBaseCalculo() { return puntosBaseCalculo; }
    public void setPuntosBaseCalculo(double valor) { this.puntosBaseCalculo = valor; }
    public void sumarPuntosBaseCalculo(double cantidad) { this.puntosBaseCalculo += cantidad; }

    public boolean tieneJoker(Class<? extends Joker> clase) {
        for (Joker j : jugador.getJokers()) {
            if (clase.isInstance(j)) return true;
        }
        return false;
    }

    public boolean tieneCategoriaJoker(CategoriaJoker categoriaJoker) {
        for (Joker j : jugador.getJokers()) {
            if (j.tieneCategoria(categoriaJoker)) return true;
        }
        return false;
    }

    public int contarJokersConCategoria(CategoriaJoker categoria, Joker excluir) {
        int cantidad = 0;
        for (Joker j : jugador.getJokers()) {
            if (j == excluir) continue;
            if (j.tieneCategoria(categoria)) cantidad++;
        }
        return cantidad;
    }

    public ResolucionPuntaje getResolucionActual() { return resolucionActual; }
    public void setResolucionActual(ResolucionPuntaje resolucion) { this.resolucionActual = resolucion; }

    /** La carta que se esta puntuando en este preciso momento de la secuencia (o null si no aplica). */
    public Carta getCartaEnResolucion() { return cartaEnResolucion; }
    public void setCartaEnResolucion(Carta carta) { this.cartaEnResolucion = carta; }

    /** Cola compartida de activaciones pendientes de la secuencia actual. Solo valida durante una resolucion. */
    public void setColaActivaciones(ArrayDeque<Activacion> cola) { this.colaActivaciones = cola; }

    /**
     * REACTIVAR: encola una nueva activacion para que se resuelva mas adelante en la secuencia
     * (despues de todo lo que ya estaba encolado antes). Usar para "reactiva este joker/carta".
     */
    public void reencolarActivacionJoker(Joker joker, EventoJuego evento) {
        if (colaActivaciones == null) return;
        colaActivaciones.addLast(Activacion.deJoker(joker, evento));
    }

    public void reencolarActivacionCarta(Carta carta, EventoJuego evento) {
        if (colaActivaciones == null) return;
        colaActivaciones.addLast(Activacion.deCarta(carta, evento));
    }

    /** Devuelve el joker inmediatamente a la derecha del dado, en el orden actual de la fila, o null si no hay. */
    public Joker obtenerJokerALaDerecha(Joker referencia) {
        ArrayList<Joker> lista = jugador.getJokers();
        int idx = lista.indexOf(referencia);
        if (idx == -1 || idx + 1 >= lista.size()) return null;
        return lista.get(idx + 1);
    }

    public ArrayList<Carta> getCartasContribuyentesEnvido() {
        return cartasContribuyentesEnvido;
    }
    public void setCartasContribuyentesEnvido(ArrayList<Carta> cartas) {
        this.cartasContribuyentesEnvido = (cartas == null) ? new ArrayList<>() : cartas;
    }
    public void clearCartasContribuyentesEnvido() { this.cartasContribuyentesEnvido.clear(); }

    public boolean isPrimerCartaQueMataAplicada() { return primerCartaQueMataAplicada; }
    public void marcarPrimerCartaQueMataAplicada() { this.primerCartaQueMataAplicada = true; }

    public boolean isPrimerCartaQueNoMataAplicada() { return primerCartaQueNoMataAplicada; }
    public void marcarPrimerCartaQueNoMataAplicada() { this.primerCartaQueNoMataAplicada = true; }
    public boolean isPrimerFiguraPuntuadaAplicada() { return primerFiguraPuntuadaAplicada; }
    public void marcarPrimerFiguraPuntuadaAplicada() { this.primerFiguraPuntuadaAplicada = true; }

    public boolean isPrimerNoFiguraPuntuadaAplicada() { return primerNoFiguraPuntuadaAplicada; }
    public void marcarPrimerNoFiguraPuntuadaAplicada() { this.primerNoFiguraPuntuadaAplicada = true; }

    public Carta getCartaOponenteEnResolucion() { return cartaOponenteEnResolucion; }
    public void setCartaOponenteEnResolucion(Carta carta) { this.cartaOponenteEnResolucion = carta; }
}
