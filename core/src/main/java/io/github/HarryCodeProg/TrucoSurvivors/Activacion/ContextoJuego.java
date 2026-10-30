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

import java.util.*;

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
    private Carta cartaOponenteEnResolucion;
    private final Set<Joker> jokersPrimerCartaQueMataAplicada = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<Joker> jokersPrimerCartaQueNoMataAplicada = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<Joker> jokersPrimerFiguraPuntuadaAplicada = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Set<Joker> jokersPrimerNoFiguraPuntuadaAplicada = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Joker, Set<Object>> marcasPorJoker = new IdentityHashMap<>();
    private final Set<Joker> copiasEnCurso = Collections.newSetFromMap(new IdentityHashMap<>());
    private int cartasRepartidasEsteEvento = 0;
    private int profundidadCopia = 0;
    private static final int MAX_PROFUNDIDAD_COPIA = 8;
    private int cartasDescartadasEsteEvento = 0;

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

    public Carta getCartaEnResolucion() { return cartaEnResolucion; }
    public void setCartaEnResolucion(Carta carta) { this.cartaEnResolucion = carta; }

    public void setColaActivaciones(ArrayDeque<Activacion> cola) { this.colaActivaciones = cola; }

    public ArrayDeque<Activacion> getColaActivaciones() { return colaActivaciones; }

    // ─── Reencolado de activaciones ───────────────────────────────────────

    /** Reactiva un joker (se vuelve a procesar más adelante en la cola). */
    public void reencolarActivacionJoker(Joker joker, EventoJuego evento) {
        if (colaActivaciones == null) return;
        colaActivaciones.addLast(Activacion.deJoker(joker, evento));
    }

    /** Reactiva una carta: vuelve a puntuar Y dispara AL_ACTIVAR_CARTA. */
    public void reencolarActivacionCarta(Carta carta, EventoJuego evento) {
        reencolarActivacionCarta(carta, evento, true);
    }

    /** Solo activa la carta (dispara AL_ACTIVAR_CARTA) sin volver a sumar sus puntos. */
    public void reencolarActivacionCartaSoloActivar(Carta carta, EventoJuego evento) {
        reencolarActivacionCarta(carta, evento, false);
    }

    private void reencolarActivacionCarta(Carta carta, EventoJuego evento, boolean puntua) {
        if (colaActivaciones == null) return;
        colaActivaciones.addLast(Activacion.deCarta(carta, evento, puntua));
    }

    // ─── Consultas de jokers vecinos ──────────────────────────────────────

    public Joker obtenerJokerALaDerecha(Joker referencia) {
        ArrayList<Joker> lista = jugador.getJokers();
        int idx = lista.indexOf(referencia);
        if (idx == -1 || idx + 1 >= lista.size()) return null;
        return lista.get(idx + 1);
    }

    public Joker obtenerJokerALaIzquierda(Joker referencia) {
        ArrayList<Joker> lista = jugador.getJokers();
        int idx = lista.indexOf(referencia);
        if (idx <= 0) return null;
        return lista.get(idx - 1);
    }

    // ─── Cartas contribuyentes de envido ──────────────────────────────────

    public ArrayList<Carta> getCartasContribuyentesEnvido() { return cartasContribuyentesEnvido; }
    public void setCartasContribuyentesEnvido(ArrayList<Carta> cartas) {
        this.cartasContribuyentesEnvido = (cartas == null) ? new ArrayList<>() : cartas;
    }
    public void clearCartasContribuyentesEnvido() { this.cartasContribuyentesEnvido.clear(); }

    public Carta getCartaOponenteEnResolucion() { return cartaOponenteEnResolucion; }
    public void setCartaOponenteEnResolucion(Carta carta) { this.cartaOponenteEnResolucion = carta; }

    // ─── Flags "primera vez" por joker ────────────────────────────────────

    public boolean isPrimerCartaQueMataAplicada(Joker joker) { return jokersPrimerCartaQueMataAplicada.contains(joker); }
    public void marcarPrimerCartaQueMataAplicada(Joker joker) { jokersPrimerCartaQueMataAplicada.add(joker); }

    public boolean isPrimerCartaQueNoMataAplicada(Joker joker) { return jokersPrimerCartaQueNoMataAplicada.contains(joker); }
    public void marcarPrimerCartaQueNoMataAplicada(Joker joker) { jokersPrimerCartaQueNoMataAplicada.add(joker); }

    public boolean isPrimerFiguraPuntuadaAplicada(Joker joker) { return jokersPrimerFiguraPuntuadaAplicada.contains(joker); }
    public void marcarPrimerFiguraPuntuadaAplicada(Joker joker) { jokersPrimerFiguraPuntuadaAplicada.add(joker); }

    public boolean isPrimerNoFiguraPuntuadaAplicada(Joker joker) { return jokersPrimerNoFiguraPuntuadaAplicada.contains(joker); }
    public void marcarPrimerNoFiguraPuntuadaAplicada(Joker joker) { jokersPrimerNoFiguraPuntuadaAplicada.add(joker); }

    // ─── Utilidades ───────────────────────────────────────────────────────

    public boolean cartaMato(Carta carta) {
        if (carta == null) return false;
        int idx = mesa.getMesaJugador().indexOf(carta);
        if (idx == -1 || idx >= mesa.getMesaRival().size()) return false;
        return carta.getValorTrucoActual() > mesa.getMesaRival().get(idx).getValorTrucoActual();
    }

    public boolean marcarUsado(Joker joker, Object objetivo) {
        Set<Object> marcas = marcasPorJoker.computeIfAbsent(joker, k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        return marcas.add(objetivo);
    }

    public boolean fueUsado(Joker joker, Object objetivo) {
        Set<Object> marcas = marcasPorJoker.get(joker);
        return marcas != null && marcas.contains(objetivo);
    }

    // ─── Control de copias / filtro / profundidad ─────────────────────────

    public boolean iniciarCopia(Joker j) { return copiasEnCurso.add(j); }
    public void terminarCopia(Joker j) { copiasEnCurso.remove(j); }

    public int getCartasRepartidasEsteEvento() { return cartasRepartidasEsteEvento; }
    public void setCartasRepartidasEsteEvento(int cantidad) { this.cartasRepartidasEsteEvento = cantidad; }

    public boolean jokerPermitidoPorFiltro(Joker j) { return true; }

    public boolean puedeEntrarACopia() {
        if (profundidadCopia >= MAX_PROFUNDIDAD_COPIA) return false;
        profundidadCopia++;
        return true;
    }

    public void salirDeCopia() { profundidadCopia--; }

    public int getCartasDescartadasEsteEvento() { return cartasDescartadasEsteEvento; }
    public void setCartasDescartadasEsteEvento(int cantidad) { this.cartasDescartadasEsteEvento = cantidad; }
}
