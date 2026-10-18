package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.CatalogoRasgos;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.EfectoRasgo;

import java.util.*;

public class GestorRasgos {

    public interface Listener {
        void onRasgoActivado(CategoriaJoker cat, int tierNuevo);
        void onRasgoDesactivado(CategoriaJoker cat, int tierAnterior);
        void onTierCambiado(CategoriaJoker cat, int tierAnterior, int tierNuevo);
    }

    private final Map<CategoriaJoker, Integer> conteos = new EnumMap<>(CategoriaJoker.class);
    private final Map<CategoriaJoker, Integer> tiers    = new EnumMap<>(CategoriaJoker.class);
    private final List<Listener> listeners = new ArrayList<>();
    private final Map<CategoriaJoker, EfectoRasgo> efectosActivos = new EnumMap<>(CategoriaJoker.class);
    private final Map<String, Double> contadores = new HashMap<>(); // por combate (vive lo que vive el Juego)
    private Juego juegoRef;
    private Joker ultimoJokerComprado;
    private Joker ultimoJokerVendido;

    public GestorRasgos() {
        for (CategoriaJoker cat : CategoriaJoker.values()) {
            conteos.put(cat, 0);
            tiers.put(cat, 0);
        }
    }

    public void addListener(Listener l) { if (l != null && !listeners.contains(l)) listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }
    public void setJuego(Juego juego) { this.juegoRef = juego; }

    public void recalcular(List<Joker> jokers) {
        Map<CategoriaJoker, Set<Integer>> idsPorCategoria = new EnumMap<>(CategoriaJoker.class);
        if (jokers != null) {
            for (Joker j : jokers) {
                if (j == null) continue;
                for (CategoriaJoker cat : j.getCategorias()) {
                    idsPorCategoria.computeIfAbsent(cat, k -> new HashSet<>()).add(j.getId());
                }
            }
        }
        for (CategoriaJoker cat : CategoriaJoker.values()) {
            int cantidadNueva = idsPorCategoria.getOrDefault(cat, Collections.emptySet()).size();
            int tierNuevo     = cat.getTier(cantidadNueva);
            int tierAnterior  = tiers.get(cat);
            conteos.put(cat, cantidadNueva);
            tiers.put(cat, tierNuevo);
            if (tierNuevo != tierAnterior) notificarCambio(cat, tierAnterior, tierNuevo);
        }
    }

    private void notificarCambio(CategoriaJoker cat, int tierAnterior, int tierNuevo) {
        EfectoRasgo anterior = efectosActivos.remove(cat);
        if (anterior != null) anterior.onDesactivar(juegoRef);
        if (tierNuevo > 0) {
            EfectoRasgo nuevo = CatalogoRasgos.getEfecto(cat, tierNuevo);
            if (nuevo != null) {
                efectosActivos.put(cat, nuevo);
                nuevo.onActivar(juegoRef);
            }
        }
        for (Listener l : new ArrayList<>(listeners)) {
            if (tierAnterior == 0 && tierNuevo > 0)      l.onRasgoActivado(cat, tierNuevo);
            else if (tierNuevo == 0 && tierAnterior > 0) l.onRasgoDesactivado(cat, tierAnterior);
            else                                         l.onTierCambiado(cat, tierAnterior, tierNuevo);
        }
    }

    public int getCantidad(CategoriaJoker cat) { return conteos.getOrDefault(cat, 0); }
    public int getTier(CategoriaJoker cat) { return tiers.getOrDefault(cat, 0); }
    public boolean estaActiva(CategoriaJoker cat) { return getTier(cat) > 0; }
    public EfectoRasgo getEfectoActivo(CategoriaJoker cat) { return efectosActivos.get(cat); }

    public List<CategoriaJoker> getCategoriasActivas() {
        List<CategoriaJoker> activas = new ArrayList<>();
        for (CategoriaJoker cat : CategoriaJoker.values()) if (estaActiva(cat)) activas.add(cat);
        activas.sort((a, b) -> Integer.compare(getTier(b), getTier(a)));
        return activas;
    }

    public List<CategoriaJoker> getCategoriasParaMostrar() {
        List<CategoriaJoker> lista = new ArrayList<>();
        for (CategoriaJoker cat : CategoriaJoker.values()) if (getCantidad(cat) >= 1) lista.add(cat);
        lista.sort((a, b) -> {
            int ta = getTier(a), tb = getTier(b);
            if (ta != tb) return Integer.compare(tb, ta);
            return Integer.compare(getCantidad(b), getCantidad(a));
        });
        return lista;
    }

    // ---------------- eventos y contadores ----------------

    public void dispararEvento(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        // Bookkeeping que corre siempre, haya o no un efecto activo que lo use
        if (evento == EventoJuego.AL_CONSUMIR_SANTO) sumarContador("santosUsados", 1);
        else if (evento == EventoJuego.AL_GANAR_TRUCO) sumarContador("manosGanadas", 1);
        for (EfectoRasgo e : new ArrayList<>(efectosActivos.values())) {
            e.onEvento(evento, ctx, juego);
        }
    }

    public double getContador(String clave) { return contadores.getOrDefault(clave, 0.0); }
    public double getContador(String clave, double defecto) { return contadores.getOrDefault(clave, defecto); }
    public void setContador(String clave, double v) { contadores.put(clave, v); }
    public void sumarContador(String clave, double v) { contadores.merge(clave, v, Double::sum); }

    public Joker getUltimoJokerComprado() { return ultimoJokerComprado; }
    public void setUltimoJokerComprado(Joker j) { ultimoJokerComprado = j; }
    public Joker getUltimoJokerVendido() { return ultimoJokerVendido; }
    public void setUltimoJokerVendido(Joker j) { ultimoJokerVendido = j; }

    // ---------------- agregados que consulta Juego / Resolutor ----------------

    public double getBonusCanto(EfectoRasgo.TipoCanto tipo) {
        double s = 0;
        for (EfectoRasgo e : efectosActivos.values()) s += e.getBonusCanto(tipo);
        return s;
    }

    public double getMultiplicadorCanto(EfectoRasgo.TipoCanto tipo) {
        double m = 1;
        for (EfectoRasgo e : efectosActivos.values()) m *= e.getMultiplicadorCanto(tipo);
        return m;
    }

    public int getReactivacionesPrimeraCarta() {
        int n = 0;
        for (EfectoRasgo e : efectosActivos.values()) n += e.getReactivacionesPrimeraCarta();
        return n;
    }

    /** Nacional tier 4 (8 jokers): la tienda deberia poder ofrecer a CarpinchoGauchoMatero. */
    public boolean isLegendarioNacionalDisponible() { return getTier(CategoriaJoker.NACIONAL) >= 4; }

    // ---------------- textos dinamicos del hover ----------------

    /** Valores {0}, {1}... para rasgo.X.desc ("actual: ..."). */
    public Object[] getArgsDescripcion(CategoriaJoker cat) {
        int tier = getTier(cat);
        Jugador jug = juegoRef != null ? juegoRef.getJugador() : null;
        switch (cat) {
            case CONSUMIBLES: {
                double pts = tier == 2 ? 25 : tier == 3 ? 50 : 0;
                return new Object[]{ fmt(getContador("santosUsados") * pts) };
            }
            case PERSONA: {
                double v = tier == 1 ? 2 : tier == 2 ? 5 : tier == 3 ? 10 : 0;
                double n = jug != null ? jug.getAcumuladoRasgo(CatalogoRasgos.ACUM_PERSONA) : 0;
                return new Object[]{ fmt(n * v) };
            }
            case CULTURA:
                return new Object[]{
                    fmt(jug != null ? jug.getAcumuladoRasgo(CatalogoRasgos.ACUM_CULTURA_PUNTOS) : 0),
                    fmt(jug != null ? jug.getAcumuladoRasgo(CatalogoRasgos.ACUM_CULTURA_MULT) : 0) };
            case RACHAS: {
                int racha = juegoRef != null ? juegoRef.getManosGanadasConsecutivas() : 0;
                return new Object[]{ fmt(racha * tier), fmt(tier >= 3 ? racha : 0) };
            }
            case FIGURA:
                return new Object[]{ String.valueOf(jug != null ? CatalogoRasgos.contarFiguras(jug) : 0) };
            case AZAR:
                return new Object[]{ fmt(getContador("azar.prob", 0.25) * 100) };
            case DEPORTE:
                return new Object[]{ fmt(getContador("manosGanadas")) };
            default:
                return new Object[0];
        }
    }

    private static String fmt(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }
}
