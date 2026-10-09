package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.*;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Guardado.*;

import java.util.ArrayList;

public class GestorGuardado {
    private static final String ARCHIVO = "save.json";

    public static boolean existeGuardado() {
        return Gdx.files.local(ARCHIVO).exists();
    }

    public static void borrarGuardado() {
        FileHandle f = Gdx.files.local(ARCHIVO);
        if (f.exists()) f.delete();
    }

    public static void guardar(DatosGuardado datos) {
        Json json = new Json();
        FileHandle f = Gdx.files.local(ARCHIVO);
        f.writeString(json.toJson(datos), false);
    }

    public static DatosGuardado cargar() {
        FileHandle f = Gdx.files.local(ARCHIVO);
        if (!f.exists()) return null;
        Json json = new Json();
        try {
            return json.fromJson(DatosGuardado.class, f.readString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Arma el snapshot completo a partir del estado actual de GameScreenV2. */
    public static DatosGuardado crearSnapshot(String estadoPantalla, int rivalIndice, int nivelActual,
                                              Jugador jugador, Juego juegoOpcional, EstadoTienda estadoTiendaOpcional) {
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
            dj.cartasJugadasTotal = juegoOpcional.getCartasJugadasTotal();
            dj.cartasDescartadasTotal = juegoOpcional.getCartasDescartadasTotal();
            dj.cartasCompradasTotal = juegoOpcional.getCartasCompradasTotal();
            dj.renovacionesTotal = juegoOpcional.getRenovacionesTotal();
            d.juego = dj;
        }

        if (estadoTiendaOpcional != null) {
            d.estadoTienda = GestorConverterSerializacion.aDatos(estadoTiendaOpcional);
        }

        return d;
    }
}
