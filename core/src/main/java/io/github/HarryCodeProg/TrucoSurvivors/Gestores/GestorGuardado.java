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
            byte[] contenido = new Json().toJson(datos).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            try (java.io.FileOutputStream salida = new java.io.FileOutputStream(tmp.file())) {
                salida.write(contenido);
                salida.getFD().sync();
            }
            // Si lo que escribimos no se puede leer, no pisamos nada
            if (leer(ARCHIVO_TMP) == null) { tmp.delete(); return; }
            FileHandle principal = Gdx.files.local(ARCHIVO);
            // El respaldo solo se actualiza desde un archivo principal sano
            if (principal.exists() && leer(ARCHIVO) != null) {
                principal.copyTo(Gdx.files.local(ARCHIVO_BACKUP));
            }
            try {
                java.nio.file.Files.move(tmp.file().toPath(), principal.file().toPath(),
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                java.nio.file.Files.move(tmp.file().toPath(), principal.file().toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
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
            DatosGuardado d = json.fromJson(DatosGuardado.class, f.readString("UTF-8"));
            return esValido(d) ? d : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean esValido(DatosGuardado d) {
        if (d == null || d.version < 1 || d.version > DatosGuardado.VERSION_ACTUAL
            || d.jugador == null || d.jugador.mazo == null || d.estadoPantalla == null
            || d.rivalIndice < 0 || d.nivelActual < 1) return false;
        if (!"TIENDA".equals(d.estadoPantalla) && !"SELECCION_RIVAL".equals(d.estadoPantalla)
            && !"JUGANDO".equals(d.estadoPantalla)) return false;
        DatosJugador j = d.jugador;
        if (j.mano == null || j.jokers == null || j.santos == null || j.mazo.mazo == null
            || j.mazo.cartasTomadas == null || j.mazo.cartasDescartadas == null) return false;
        // Validar datos usados por la restauración antes de reemplazar un respaldo sano.
        for (java.util.List<DatosCarta> lista : java.util.Arrays.asList(j.mano, j.mazo.mazo,
            j.mazo.cartasTomadas, j.mazo.cartasDescartadas)) {
            for (DatosCarta c : lista) {
                if (c == null || c.numero < 1 || c.numero > 12 || c.palo == null) return false;
                try { io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo.valueOf(c.palo); }
                catch (IllegalArgumentException e) { return false; }
            }
        }
        if ("JUGANDO".equals(d.estadoPantalla) && (d.juego == null || d.juego.manoRival == null
            || d.juego.mesaJugador == null || d.juego.mesaRival == null || d.juego.resultados == null)) return false;
        return true;
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
