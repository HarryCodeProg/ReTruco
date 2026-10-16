package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.CatalogoRasgos;
import io.github.HarryCodeProg.TrucoSurvivors.Rasgos.EfectoRasgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Juego juegoRef; // para pasar a los efectos en onActivar/onDesactivar

    public GestorRasgos() {
        for (CategoriaJoker cat : CategoriaJoker.values()) {
            conteos.put(cat, 0);
            tiers.put(cat, 0);
        }
    }

    public void addListener(Listener l) {
        if (l != null && !listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(Listener l) { listeners.remove(l); }

    /** Recalcula conteos y tiers. No cuenta jokers con el mismo id dos veces. */
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

            if (tierNuevo != tierAnterior) {
                notificarCambio(cat, tierAnterior, tierNuevo);
            }
        }
    }

    public int getCantidad(CategoriaJoker cat) { return conteos.getOrDefault(cat, 0); }
    public int getTier(CategoriaJoker cat) { return tiers.getOrDefault(cat, 0); }
    public boolean estaActiva(CategoriaJoker cat) { return getTier(cat) > 0; }

    public List<CategoriaJoker> getCategoriasActivas() {
        List<CategoriaJoker> activas = new ArrayList<>();
        for (CategoriaJoker cat : CategoriaJoker.values()) {
            if (estaActiva(cat)) activas.add(cat);
        }
        activas.sort((a, b) -> Integer.compare(getTier(b), getTier(a)));
        return activas;
    }

    public void setJuego(Juego juego) { this.juegoRef = juego; }

    // Reemplazá el método notificarCambio por este:
    private void notificarCambio(CategoriaJoker cat, int tierAnterior, int tierNuevo) {
        // 1. Desactivar efecto del tier anterior si había
        EfectoRasgo efectoAnterior = efectosActivos.remove(cat);
        if (efectoAnterior != null) {
            efectoAnterior.onDesactivar(juegoRef);
        }
        // 2. Activar efecto del tier nuevo si tiene
        if (tierNuevo > 0) {
            EfectoRasgo efectoNuevo = CatalogoRasgos.getEfecto(cat, tierNuevo);
            if (efectoNuevo != null) {
                efectosActivos.put(cat, efectoNuevo);
                efectoNuevo.onActivar(juegoRef);
            }
        }
        // 3. Notificar a los listeners externos (por si querés UI, sonidos, etc.)
        for (Listener l : new ArrayList<>(listeners)) {
            if (tierAnterior == 0 && tierNuevo > 0)      l.onRasgoActivado(cat, tierNuevo);
            else if (tierNuevo == 0 && tierAnterior > 0) l.onRasgoDesactivado(cat, tierAnterior);
            else                                         l.onTierCambiado(cat, tierAnterior, tierNuevo);
        }
    }

    /** Consultá el efecto activo de una categoría (puede ser null). */
    public EfectoRasgo getEfectoActivo(CategoriaJoker cat) {
        return efectosActivos.get(cat);
    }

    /** Propaga un evento del juego a todos los efectos activos. */
    public void dispararEvento(EventoJuego evento, ContextoJuego ctx, Juego juego) {
        for (EfectoRasgo e : new ArrayList<>(efectosActivos.values())) {
            e.onEvento(evento, ctx, juego);
        }
    }
}
