package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.ResultadoMano;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.*;

public class GestorGuardado {
    private static final String ARCHIVO = "save.json";
    private static final String ARCHIVO_TMP = "save.json.tmp";
    private static final String ARCHIVO_BACKUP = "save.bak.json";

    /** Solo cuenta si hay un guardado que realmente se puede leer. */
    public static boolean existeGuardado() {
        return cargar() != null;
    }

    public static void borrarGuardado() {
        for (String nombre : new String[]{ARCHIVO, ARCHIVO_TMP, ARCHIVO_BACKUP}) {
            FileHandle f = Gdx.files.local(nombre);
            if (f.exists()) f.delete();
        }
    }

    public static void guardar(DatosGuardado datos) {
        try {
            datos.version = DatosGuardado.VERSION_ACTUAL;
            FileHandle tmp = Gdx.files.local(ARCHIVO_TMP);
            tmp.writeString(new Json().toJson(datos), false);
            // Si lo que escribimos no se puede leer, no pisamos nada
            if (leer(ARCHIVO_TMP) == null) { tmp.delete(); return; }
            FileHandle principal = Gdx.files.local(ARCHIVO);
            // El respaldo solo se actualiza desde un archivo principal sano
            if (principal.exists() && leer(ARCHIVO) != null) {
                principal.copyTo(Gdx.files.local(ARCHIVO_BACKUP));
            }
            tmp.moveTo(principal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatosGuardado cargar() {
        for (String nombre : new String[]{ARCHIVO, ARCHIVO_TMP, ARCHIVO_BACKUP}) {
            DatosGuardado d = leer(nombre);
            if (d != null) return d;
        }
        return null;
    }

    private static DatosGuardado leer(String nombre) {
        FileHandle f = Gdx.files.local(nombre);
        if (!f.exists()) return null;
        try {
            Json json = new Json();
            json.setIgnoreUnknownFields(true); // permite quitar/renombrar campos entre versiones
            DatosGuardado d = json.fromJson(DatosGuardado.class, f.readString());
            return (d != null && d.jugador != null && d.estadoPantalla != null) ? d : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Arma el snapshot completo a partir del estado actual de GameScreen. */
    public static DatosGuardado crearSnapshot(String estadoPantalla, int rivalIndice, int nivelActual,
                                              Jugador jugador, Juego juegoOpcional, EstadoTienda estadoTiendaOpcional,
                                              int tocoJugarActual) {
        DatosGuardado d = new DatosGuardado();
        d.estadoPantalla = estadoPantalla;
        d.rivalIndice = rivalIndice;
        d.nivelActual = nivelActual;
        d.jugador = GestorConverterSerializacion.aDatos(jugador);
        if (juegoOpcional != null) {
            DatosJuego dj = new DatosJuego();
            dj.puntosJugador = juegoOpcional.getPuntosJugador();
            dj.puntosRival = juegoOpcional.getPuntosRival();
            dj.puntajeMeta = juegoOpcional.getPuntajeMeta();
            dj.jugadorEsMano = juegoOpcional.isJugadorEsMano();
            dj.faseActual = juegoOpcional.getFaseActual();
            dj.rondaActual = juegoOpcional.getRondaActual();
            dj.descartesActuales = juegoOpcional.getDescartesActuales();
            for (Carta c : juegoOpcional.getRival().getMano()) dj.manoRival.add(GestorConverterSerializacion.aDatos(c));
            for (Carta c : juegoOpcional.getMesa().getMesaJugador()) dj.mesaJugador.add(GestorConverterSerializacion.aDatos(c));
            for (Carta c : juegoOpcional.getMesa().getMesaRival()) dj.mesaRival.add(GestorConverterSerializacion.aDatos(c));
            for (ResultadoMano r : juegoOpcional.getResultados()) dj.resultados.add(r.name());
            dj.manoFinalizada = juegoOpcional.isManoFinalizada();
            dj.cartasJugadasTotal = juegoOpcional.getCartasJugadasTotal();
            dj.cartasDescartadasTotal = juegoOpcional.getCartasDescartadasTotal();
            dj.cartasCompradasTotal = juegoOpcional.getCartasCompradasTotal();
            dj.renovacionesTotal = juegoOpcional.getRenovacionesTotal();
            dj.tocoJugar = tocoJugarActual;
            juegoOpcional.exportarEstado(dj);   // NUEVO: cantos, turno, multiplicadores, rachas, contadores
            dj.mazoRival = GestorConverterSerializacion.aDatos(juegoOpcional.getRival().getMazo()); // NUEVO
            d.juego = dj;
        }
        if (estadoTiendaOpcional != null) {
            d.estadoTienda = GestorConverterSerializacion.aDatos(estadoTiendaOpcional);
        }
        return d;
    }
}
