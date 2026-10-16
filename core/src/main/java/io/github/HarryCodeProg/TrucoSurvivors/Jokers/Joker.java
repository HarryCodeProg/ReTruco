package io.github.HarryCodeProg.TrucoSurvivors.Jokers;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class Joker {
    private final String nombre;
    private final String nombreArchivo;
    private final String descripcion;
    private final Rareza rareza;
    private final int coste;
    private int id;
    private final Set<CategoriaJoker> categorias;
    private final FaseActivacion fase;
    private double acumulado = 0;
    private int precioVenta;
    private Jugador jugadorPropietario;
    public enum FaseActivacion {
        AL_JUGAR,           // Balatro: "On Played"
        AL_PUNTUAR_CARTA,   // Balatro: "On Scored"
        AL_MANTENER_CARTA,  // Balatro: "On Held" (futuro)
        INDEPENDIENTE        // Balatro: jokers generales, se activan al final
    }

    public Joker(int id, String nombre, String nombreArchivo, String descripcion, Rareza rareza, int coste,
                 FaseActivacion fase, CategoriaJoker... categorias) {
        this.id = id;
        this.nombre = nombre;
        this.nombreArchivo = nombreArchivo;
        this.descripcion = descripcion;
        this.rareza = rareza;
        this.coste = coste;
        this.precioVenta = coste / 2;
        this.fase = fase;
        if (categorias != null && categorias.length > 0) {
            this.categorias = EnumSet.noneOf(CategoriaJoker.class);
            Collections.addAll(this.categorias, categorias);
        } else {
            this.categorias = EnumSet.noneOf(CategoriaJoker.class);
        }
    }

    public int getId() {return id;}
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Rareza getRareza() { return rareza; }
    public int getCoste() { return coste; }
    public Set<CategoriaJoker> getCategorias() {return Collections.unmodifiableSet(categorias);}
    public boolean tieneCategoria(CategoriaJoker categoria) {return categorias.contains(categoria);}

    public String getNombreRegion() {
        return id + "_" + nombreArchivo;
    }

    public FaseActivacion getFase() { return fase; }

    public abstract void aplicarEfecto(EventoJuego evento, ContextoJuego contexto, Juego juego);
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public double getAcumulado() { return acumulado; }

    /** Descripcion final mostrada, con placeholders resueltos. Override si querés mostrar el acumulado. */
    public String getDescripcionRenderizada() {
        return descripcion;
    }

    /** Para jokers "Instantaneo": se ejecuta una sola vez al agregarse al jugador. Override si aplica. */
    public void aplicarEfectoInstantaneo(Jugador jugador) {}

    /** Se ejecuta cuando cualquier joker es vendido (incluyendo este mismo). Override si aplica. */
    public void onVendido(Joker jokerVendido, Jugador jugador) {}

    public void desAplicarEfectoInstantaneo(Jugador jugador) {}

    public int getPrecioVenta() { return precioVenta; }

    public void aumentarPrecioVenta(int cantidad) { this.precioVenta += cantidad; }

    public abstract Joker copiar();

    protected void copiarEstado(Joker copia) {
        copia.acumulado = this.acumulado;
        copia.precioVenta = this.precioVenta;
    }

    /** Si este joker ocupa un slot de jokers. Override si aplica (ej: Perro). */
    public boolean ocupaEspacio() { return true; }

    /** Se llama cada vez que el jugador gasta pesos (tienda, rerolls, etc). */
    public void onPesosGastados(int cantidad, Jugador jugador) {}

    public String getDescripcionRenderizada(Juego juego) {
        return getDescripcionRenderizada();
    }

    /** Si tener este joker impide que aparezcan jokers de rareza "comun" en la tienda. */
    public boolean bloqueaJokersComunes() { return false; }

    /** Se llama cada vez que OTRO joker (no este) termina de aplicar su efecto. */
    public void onOtroJokerActivado(Joker otro, EventoJuego evento, ContextoJuego ctx, Juego juego) {}

    /** Seteado por Jugador al agregarse a la fila. No confundir con un parámetro puntual de un método. */
    public void setJugadorPropietario(Jugador jugador) {
        this.jugadorPropietario = jugador;
    }

    public Jugador getJugadorPropietario() {
        return jugadorPropietario;
    }

    public void sumarAcumulado(double cantidad) {
        if (estaCongeladoPorGallina()) return;
        this.acumulado += cantidad;
    }

    public void setAcumulado(double acumulado) {
        if (estaCongeladoPorGallina()) return;
        this.acumulado = acumulado;
    }

    private boolean estaCongeladoPorGallina() {
        if (jugadorPropietario == null) return false;
        java.util.ArrayList<Joker> lista = jugadorPropietario.getJokers();
        int idx = lista.indexOf(this);
        if (idx <= 0) return false; // no tiene alguien a su izquierda
        return lista.get(idx - 1) instanceof io.github.HarryCodeProg.TrucoSurvivors.Jokers.Raro.Gallina;
    }

    public String getEstadoExtra() { return null; }
    public void setEstadoExtra(String data) { }
}
