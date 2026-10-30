package io.github.HarryCodeProg.TrucoSurvivors.Dev;

import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.CategoriaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolSantosTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.SantaRita;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Estado y operaciones del entorno de desarrollo. No dibuja nada: arma un Jugador, un rival y un Juego reales
 * (los mismos del juego) y reproduce el flujo de GestorAccion / ControladorCombate sin pantalla.
 * Todo lo que pasa se escribe en un log que muestra DevScreen.
 */
public class EntornoDev {

    public enum Tipo { INFO, OK, ERROR, PASO, TITULO, ECO }

    public static final class Linea {
        public final Tipo tipo;
        public final String texto;
        Linea(Tipo tipo, String texto) { this.tipo = tipo; this.texto = texto; }
    }

    public static final int JOKERS_MAX = 12;

    private final ArrayList<Linea> log = new ArrayList<>();
    private int epocaLog = 0;
    private final PoolJokersTienda poolJokers = new PoolJokersTienda();
    private final PoolSantosTienda poolSantos = new PoolSantosTienda();
    private ArrayList<Joker> catalogoJokers;
    private ArrayList<Santo> catalogoSantos;

    private Jugador jugador;
    private Jugador rival;
    private Juego juego;
    /** Cartas de la mano tal como se escribieron ("jugar 2" = la 2da de esta lista), aunque ya se hayan jugado. */
    private final ArrayList<Carta> cartasJugador = new ArrayList<>();
    private final ArrayList<Carta> cartasRival = new ArrayList<>();
    private int toco = 0;
    private double meta = 1000000d;
    private int chequeos = 0;
    private int fallos = 0;

    public EntornoDev() {
        reiniciar();
        info("Entorno de desarrollo listo. Escribi 'ayuda' para ver los comandos.");
    }

    // ======================= LOG =======================

    public ArrayList<Linea> getLog() { return log; }
    public int getEpocaLog() { return epocaLog; }
    public void limpiarLog() { log.clear(); epocaLog++; }
    private void emitir(Tipo t, String s) { log.add(new Linea(t, s)); }
    public void info(String s) { emitir(Tipo.INFO, s); }
    public void ok(String s) { emitir(Tipo.OK, s); }
    public void error(String s) { emitir(Tipo.ERROR, s); }
    public void paso(String s) { emitir(Tipo.PASO, s); }
    public void titulo(String s) { emitir(Tipo.TITULO, s); }
    public void eco(String s) { emitir(Tipo.ECO, s); }
    public int marca() { return log.size(); }

    public int erroresDesde(int marca) {
        int n = 0;
        for (int i = marca; i < log.size(); i++) if (log.get(i).tipo == Tipo.ERROR) n++;
        return n;
    }

    private static Throwable raiz(Throwable ex) {
        Throwable r = ex;
        while (r.getCause() != null && r.getCause() != r) r = r.getCause();
        return r;
    }

    /** "IllegalStateException: mensaje  [en Clase.metodo (linea N)]" con el primer frame del juego que no sea del entorno. */
    private static String resumenExcepcion(Throwable ex) {
        Throwable r = raiz(ex);
        String donde = "";
        for (StackTraceElement f : r.getStackTrace()) {
            String cn = f.getClassName();
            if (!cn.startsWith("io.github.HarryCodeProg") || cn.contains(".Dev.")) continue;
            donde = "  [en " + cn.substring(cn.lastIndexOf('.') + 1) + "." + f.getMethodName() + " (linea " + f.getLineNumber() + ")]";
            break;
        }
        return r.getClass().getSimpleName() + ": " + r.getMessage() + donde;
    }

    /** Muestra una excepcion con las clases del juego que aparecen en el stack, marcando el joker/santo sospechoso. */
    public void excepcion(Throwable ex) {
        Throwable raiz = raiz(ex);
        error("EXCEPCION " + raiz.getClass().getSimpleName() + ": " + raiz.getMessage());
        String sospechoso = null;
        int mostradas = 0;
        for (StackTraceElement f : raiz.getStackTrace()) {
            String cn = f.getClassName();
            if (!cn.startsWith("io.github.HarryCodeProg") || cn.contains(".Dev.")) continue;
            String simple = cn.substring(cn.lastIndexOf('.') + 1);
            if (sospechoso == null && (cn.contains(".Jokers.") || cn.contains(".Santos."))) sospechoso = simple;
            if (mostradas < 8) { info("   en " + simple + "." + f.getMethodName() + " (linea " + f.getLineNumber() + ")"); mostradas++; }
        }
        if (sospechoso != null) error("Clase sospechosa: " + sospechoso);
        ex.printStackTrace();
    }

    // ======================= ACCESO PARA LA UI =======================

    public Jugador getJugador() { return jugador; }
    public Jugador getRival() { return rival; }
    public Juego getJuego() { return juego; }
    public ArrayList<Carta> getCartasJugador() { return cartasJugador; }
    public ArrayList<Carta> getCartasRival() { return cartasRival; }
    public int getChequeos() { return chequeos; }
    public int getFallos() { return fallos; }
    public void resetChequeos() { chequeos = 0; fallos = 0; }

    public void cerrar() { if (juego != null) juego.desconectar(); }

    public String resumenLinea1() {
        return "Puntos: jugador " + fmt(juego.getPuntosJugador()) + "  |  rival " + fmt(juego.getPuntosRival())
            + "  |  meta " + fmt(juego.getPuntajeMeta());
    }

    public String resumenLinea2() {
        return "Truco x" + fmt(jugador.getMultiplicadorTruco()) + "  Envido x" + fmt(jugador.getMultiplicadorEnvido())
            + "  |  manos " + jugador.getManosActuales() + "  descartes " + juego.getDescartesActuales()
            + "  |  $" + jugador.getPesos();
    }

    // ======================= CICLO DE VIDA =======================

    public void reiniciar() {
        if (juego != null) juego.desconectar();
        jugador = new Jugador("Dev");
        jugador.setMazo(Mazo.crearMazoBase());
        jugador.setTamañoJokers(JOKERS_MAX);
        jugador.sumarEspacioSantos(9);
        rival = new Jugador("Rival");
        cartasJugador.clear();
        cartasRival.clear();
        toco = 0;
        juego = new Juego(jugador, rival, Mazo.crearMazoBase(), true);
        juego.setPuntajeMeta(meta);
        info("Entorno reiniciado (jugador limpio, sin jokers ni cartas).");
    }

    /** Arranca un combate como el juego real: reparto aleatorio + eventos INICIO_COMBATE y POST_REPARTO. */
    public void iniciar() {
        juego.desconectar();
        cartasJugador.clear();
        cartasRival.clear();
        toco = 0;
        rival = new Jugador("Rival");
        juego = new Juego(jugador, rival, Mazo.crearMazoBase(), false);
        juego.setPuntajeMeta(meta);
        cartasJugador.addAll(jugador.getMano());
        cartasRival.addAll(rival.getMano());
        ok("Combate iniciado (reparto aleatorio + INICIO_COMBATE).");
        mostrarManos();
        avisarConsumidos();
    }

    /** Equivale a "IR AL MAZO" / nueva ronda del juego real (irAlMazo + reparto). */
    public void nuevaMano() {
        juego.setManoFinalizada(false);
        juego.irAlMazo();
        toco = 0;
        cartasJugador.clear();
        cartasRival.clear();
        cartasJugador.addAll(jugador.getMano());
        cartasRival.addAll(rival.getMano());
        ok("Nueva mano repartida.");
        mostrarManos();
        avisarConsumidos();
    }

    private void mostrarManos() {
        info("Mano jugador: " + textoCartas(cartasJugador) + "   |   Mano rival: " + textoCartas(cartasRival));
    }

    // ======================= JOKERS =======================

    private ArrayList<Joker> catalogoJokers() {
        if (catalogoJokers == null) catalogoJokers = new ArrayList<Joker>(poolJokers.crearTodos());
        return catalogoJokers;
    }

    private ArrayList<Santo> catalogoSantos() {
        if (catalogoSantos == null) catalogoSantos = new ArrayList<Santo>(poolSantos.crearTodos());
        return catalogoSantos;
    }

    private static String categorias(Joker j) {
        StringBuilder sb = new StringBuilder();
        for (CategoriaJoker c : j.getCategorias()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(c.name());
        }
        return sb.toString();
    }

    public void listarJokers(String filtro) {
        String f = normalizar(filtro);
        int n = 0;
        for (Joker j : catalogoJokers()) {
            String linea = "#" + j.getId() + "  " + j.getNombre() + "  [" + j.getRareza() + "]  " + categorias(j);
            if (f.isEmpty() || normalizar(linea).contains(f)) { info(linea); n++; }
        }
        ok(n + " jokers" + (f.isEmpty() ? "" : " que coinciden con '" + filtro + "'"));
    }

    public void listarSantos(String filtro) {
        String f = normalizar(filtro);
        int n = 0;
        for (Santo s : catalogoSantos()) {
            String linea = "#" + s.getId() + "  " + s.getNombre() + "  (cartas requeridas: " + s.cartasRequeridas() + ")";
            if (f.isEmpty() || normalizar(linea).contains(f)) { info(linea); n++; }
        }
        ok(n + " santos" + (f.isEmpty() ? "" : " que coinciden con '" + filtro + "'"));
    }

    /** "12 45 101" o "12, don quijote, 45" -> lista de referencias. */
    public static List<String> dividirRefs(String texto) {
        ArrayList<String> refs = new ArrayList<>();
        String t = texto.trim();
        if (t.isEmpty()) return refs;
        if (t.contains(",")) {
            for (String p : t.split(",")) if (!p.trim().isEmpty()) refs.add(p.trim());
            return refs;
        }
        String[] partes = t.split("\\s+");
        boolean todosNumeros = true;
        for (String p : partes) if (!p.matches("#?\\d+")) todosNumeros = false;
        if (todosNumeros) { for (String p : partes) refs.add(p); }
        else refs.add(t);
        return refs;
    }

    private Joker nuevoJoker(String ref) {
        if (ref.matches("\\d+")) {
            Joker j = poolJokers.crearPorId(Integer.parseInt(ref));
            if (j == null) error("No existe un joker con id " + ref + ". Usa 'jokers' para ver el catalogo.");
            return j;
        }
        String f = normalizar(ref);
        ArrayList<Joker> coincidencias = new ArrayList<>();
        for (Joker j : catalogoJokers()) {
            String nombre = normalizar(j.getNombre());
            if (nombre.equals(f)) { coincidencias.clear(); coincidencias.add(j); break; }
            if (nombre.contains(f)) coincidencias.add(j);
        }
        if (coincidencias.isEmpty()) { error("No encontre ningun joker llamado '" + ref + "'."); return null; }
        if (coincidencias.size() > 1) {
            StringBuilder sb = new StringBuilder("'" + ref + "' es ambiguo:");
            for (Joker j : coincidencias) sb.append("  #").append(j.getId()).append(" ").append(j.getNombre()).append(";");
            error(sb.toString());
            return null;
        }
        return poolJokers.crearPorId(coincidencias.get(0).getId());
    }

    private Santo nuevoSanto(String ref) {
        if (ref.matches("\\d+")) {
            Santo s = poolSantos.crearPorId(Integer.parseInt(ref));
            if (s == null) error("No existe un santo con id " + ref + ". Usa 'santos' para ver el catalogo.");
            return s;
        }
        String f = normalizar(ref);
        ArrayList<Santo> coincidencias = new ArrayList<>();
        for (Santo s : catalogoSantos()) {
            String nombre = normalizar(s.getNombre());
            if (nombre.equals(f)) { coincidencias.clear(); coincidencias.add(s); break; }
            if (nombre.contains(f)) coincidencias.add(s);
        }
        if (coincidencias.isEmpty()) { error("No encontre ningun santo llamado '" + ref + "'."); return null; }
        if (coincidencias.size() > 1) {
            StringBuilder sb = new StringBuilder("'" + ref + "' es ambiguo:");
            for (Santo s : coincidencias) sb.append("  #").append(s.getId()).append(" ").append(s.getNombre()).append(";");
            error(sb.toString());
            return null;
        }
        return poolSantos.crearPorId(coincidencias.get(0).getId());
    }

    public void agregarJokers(String refs) {
        for (String ref : dividirRefs(refs)) {
            Joker j = nuevoJoker(ref);
            if (j == null) continue;
            if (jugador.agregarJoker(j)) ok("+ Joker #" + j.getId() + " " + j.getNombre() + "  (pos " + jugador.getJokers().size() + ")");
            else error("No entra '" + j.getNombre() + "': tablero lleno (" + jugador.getTamañoJokers() + ").");
        }
        avisarConsumidos();
    }

    public void comprarJoker(String ref) {
        Joker j = nuevoJoker(ref);
        if (j == null) return;
        if (!jugador.agregarJoker(j)) { error("Tablero lleno."); return; }
        juego.notificarJokerComprado(j);
        ok("Compraste #" + j.getId() + " " + j.getNombre() + " (se disparo AL_COMPRAR_JOKER; no cobra plata).");
        avisarConsumidos();
    }

    private Joker buscarEnTablero(String ref) {
        ArrayList<Joker> lista = jugador.getJokers();
        String r = ref.trim();
        if (r.startsWith("#") && r.substring(1).matches("\\d+")) {
            int pos = Integer.parseInt(r.substring(1));
            if (pos < 1 || pos > lista.size()) { error("No hay joker en la posicion " + pos + " (hay " + lista.size() + ")."); return null; }
            return lista.get(pos - 1);
        }
        if (r.matches("\\d+")) {
            int id = Integer.parseInt(r);
            for (Joker j : lista) if (j.getId() == id) return j;
            error("No tenes el joker con id " + id + ".");
            return null;
        }
        String f = normalizar(r);
        for (Joker j : lista) if (normalizar(j.getNombre()).contains(f)) return j;
        error("No tenes ningun joker llamado '" + ref + "'.");
        return null;
    }

    public void quitarJokers(String refs) {
        for (String ref : dividirRefs(refs)) {
            Joker j = buscarEnTablero(ref);
            if (j == null) continue;
            jugador.eliminarJoker(j);
            ok("- Joker #" + j.getId() + " " + j.getNombre());
        }
        avisarConsumidos();
    }

    public void venderJoker(String ref) {
        Joker j = buscarEnTablero(ref);
        if (j == null) return;
        jugador.eliminarJoker(j);
        jugador.sumarPesos(j.getPrecioVenta());
        juego.notificarJokerVendido(j);
        ok("Vendiste #" + j.getId() + " " + j.getNombre() + " por $" + j.getPrecioVenta() + " (se disparo AL_VENDER_JOKER).");
        avisarConsumidos();
    }

    public void quitarTodosLosJokers() {
        for (Joker j : new ArrayList<>(jugador.getJokers())) jugador.eliminarJoker(j);
        ok("Tablero vacio.");
    }

    public void mover(int de, int a) {
        ArrayList<Joker> lista = jugador.getJokers();
        if (de < 1 || de > lista.size() || a < 1 || a > lista.size()) { error("Posiciones validas: 1 a " + lista.size() + "."); return; }
        Joker j = lista.remove(de - 1);
        lista.add(a - 1, j);
        ok("Movido " + j.getNombre() + " a la posicion " + a + ".");
        mostrarTablero();
    }

    public void mostrarTablero() {
        ArrayList<Joker> lista = jugador.getJokers();
        if (lista.isEmpty()) { info("Tablero vacio."); return; }
        int pos = 1;
        for (Joker j : lista) {
            String extra = j.getEstadoExtra();
            info(pos + ".  #" + j.getId() + " " + j.getNombre() + "   acum=" + fmt(j.getAcumulado())
                + (extra != null && !extra.isEmpty() ? "   extra=" + extra : ""));
            pos++;
        }
        StringBuilder sb = new StringBuilder("Rasgos activos: ");
        boolean alguno = false;
        for (CategoriaJoker c : juego.getGestorRasgos().getCategoriasActivas()) {
            sb.append(c.name()).append(" t").append(juego.getGestorRasgos().getTier(c)).append("  ");
            alguno = true;
        }
        info(alguno ? sb.toString() : "Rasgos activos: ninguno");
    }

    public void setAcumulado(int pos, double valor) {
        Joker j = jokerEn(pos);
        if (j == null) return;
        j.setAcumulado(valor);
        ok(j.getNombre() + ": acumulado = " + fmt(valor));
    }

    public void setExtra(int pos, String texto) {
        Joker j = jokerEn(pos);
        if (j == null) return;
        j.setEstadoExtra(texto);
        ok(j.getNombre() + ": estadoExtra = " + texto);
    }

    private Joker jokerEn(int pos) {
        ArrayList<Joker> lista = jugador.getJokers();
        if (pos < 1 || pos > lista.size()) { error("No hay joker en la posicion " + pos + " (hay " + lista.size() + ")."); return null; }
        return lista.get(pos - 1);
    }

    // ======================= SANTOS =======================

    public void agregarSantos(String refs) {
        for (String ref : dividirRefs(refs)) {
            Santo s = nuevoSanto(ref);
            if (s == null) continue;
            if (jugador.agregarSanto(s)) ok("+ Santo #" + s.getId() + " " + s.getNombre() + "  (pos " + jugador.getSantos().size() + ")");
            else error("No entra '" + s.getNombre() + "': santos llenos.");
        }
    }

    /** Usa el santo de la posicion pos (1-based) con las cartas elegidas de la mano (1-based). */
    public void usarSanto(int pos, int[] idxCartas) {
        ArrayList<Santo> lista = jugador.getSantos();
        if (pos < 1 || pos > lista.size()) { error("No hay santo en la posicion " + pos + " (hay " + lista.size() + ")."); return; }
        Santo s = lista.get(pos - 1);
        ArrayList<Carta> seleccion = new ArrayList<>();
        for (int i : idxCartas) {
            Carta c = cartaDeMano(i);
            if (c == null) return;
            seleccion.add(c);
        }
        Santo paraRequisitos = (s instanceof SantaRita) ? jugador.getUltimoSantoUsado() : s;
        int req = paraRequisitos != null ? paraRequisitos.cartasRequeridas() : 0;
        if (req == -1 && seleccion.isEmpty()) { error(s.getNombre() + " necesita al menos 1 carta: usar " + pos + " <n> ..."); return; }
        if (req > 0 && seleccion.size() != req) { error(s.getNombre() + " necesita exactamente " + req + " carta(s): usar " + pos + " <n> ..."); return; }
        if (req == 0 && !seleccion.isEmpty()) { error(s.getNombre() + " no usa cartas."); return; }
        titulo("SANTO " + s.getNombre() + (seleccion.isEmpty() ? "" : " sobre " + textoCartas(seleccion)));
        s.aplicarEfecto(jugador, seleccion, juego.crearContexto());
        if (!(s instanceof SantaRita)) jugador.setUltimoSantoUsado(s);
        if (s.tieneCambiosDiferidos()) {
            for (Runnable r : new ArrayList<>(s.getAccionesDiferidas())) r.run();
            s.limpiarDiferidos();
        }
        juego.notificarSantoConsumido();
        jugador.eliminarSanto(s);
        ok("Santo usado y consumido.");
        mostrarManos();
        avisarConsumidos();
    }

    // ======================= CARTAS =======================

    public static String cartaATexto(Carta c) { return c.getNumero() + letra(c.getPalo()); }

    private static String letra(Palo p) {
        switch (p) {
            case ESPADA: return "E";
            case BASTO: return "B";
            case ORO: return "O";
            default: return "C";
        }
    }

    public static String textoCartas(List<Carta> cartas) {
        if (cartas.isEmpty()) return "(ninguna)";
        StringBuilder sb = new StringBuilder();
        for (Carta c : cartas) { if (sb.length() > 0) sb.append(' '); sb.append(cartaATexto(c)); }
        return sb.toString();
    }

    private static Palo paloDe(String letras) {
        if ("ESPADA".startsWith(letras)) return Palo.ESPADA;
        if ("BASTO".startsWith(letras)) return Palo.BASTO;
        if ("ORO".startsWith(letras)) return Palo.ORO;
        if ("COPA".startsWith(letras)) return Palo.COPA;
        return null;
    }

    /** "7O", "O7", "12basto", "1e" -> Carta nueva (o null si no se entiende). */
    public static Carta textoACarta(String s) {
        String t = s.trim().toUpperCase(Locale.ROOT);
        String num;
        String pal;
        Matcher m = Pattern.compile("^(\\d{1,2})([A-Z]+)$").matcher(t);
        if (m.matches()) { num = m.group(1); pal = m.group(2); }
        else {
            m = Pattern.compile("^([A-Z]+)(\\d{1,2})$").matcher(t);
            if (!m.matches()) return null;
            pal = m.group(1);
            num = m.group(2);
        }
        Palo palo = paloDe(pal);
        int n = Integer.parseInt(num);
        if (palo == null || n < 1 || n > 12) return null;
        return new Carta(n, palo);
    }

    /** Convierte tokens a cartas; si alguno no se entiende, avisa y devuelve null. */
    public ArrayList<Carta> parsearCartas(String[] tokens, int desde) {
        ArrayList<Carta> cartas = new ArrayList<>();
        for (int i = desde; i < tokens.length; i++) {
            Carta c = textoACarta(tokens[i]);
            if (c == null) { error("No entiendo la carta '" + tokens[i] + "'. Formato: 7O (numero + E/B/O/C)."); return null; }
            cartas.add(c);
        }
        return cartas;
    }

    private boolean mesaVacia() {
        return juego.getMesa().getMesaJugador().isEmpty() && juego.getMesa().getMesaRival().isEmpty();
    }

    public void setManoJugador(List<Carta> cartas) {
        if (!mesaVacia()) { error("Hay cartas en la mesa. Usa 'nueva' para empezar otra mano."); return; }
        jugador.limpiarMano();
        cartasJugador.clear();
        for (Carta c : cartas) { jugador.agregarCarta(c); cartasJugador.add(c); }
        toco = 0;
        ok("Mano del jugador: " + textoCartas(cartasJugador) + "   (puntos de envido: " + fmt(jugador.getPuntosEnvido()) + ")");
    }

    public void setManoRival(List<Carta> cartas) {
        if (!mesaVacia()) { error("Hay cartas en la mesa. Usa 'nueva' para empezar otra mano."); return; }
        rival.getMano().clear();
        cartasRival.clear();
        for (Carta c : cartas) { rival.agregarCarta(c); cartasRival.add(c); }
        toco = 0;
        ok("Mano del rival: " + textoCartas(cartasRival) + "   (puntos de envido: " + fmt(rival.getPuntosEnvido()) + ")");
    }

    public void setMazo(List<Carta> cartas) {
        Mazo m = new Mazo();
        for (Carta c : cartas) m.agregarCarta(c);
        jugador.setMazo(m);
        ok("Mazo del jugador reemplazado: " + cartas.size() + " cartas.");
    }

    /** Carta n (1-based) de la mano original, solo si todavia esta en la mano. */
    private Carta cartaDeMano(int n) {
        if (n < 1 || n > cartasJugador.size()) { error("La mano tiene " + cartasJugador.size() + " carta(s): elegi de 1 a " + cartasJugador.size() + "."); return null; }
        Carta c = cartasJugador.get(n - 1);
        if (!jugador.getMano().contains(c)) { error("La carta " + n + " (" + cartaATexto(c) + ") ya no esta en la mano."); return null; }
        return c;
    }

    public void modificarValor(boolean deJugador, int n, int delta) {
        ArrayList<Carta> lista = deJugador ? cartasJugador : cartasRival;
        if (n < 1 || n > lista.size()) { error("Elegi una carta de 1 a " + lista.size() + "."); return; }
        Carta c = lista.get(n - 1);
        c.modificarValorTrucoPermanente(delta);
        ok("Carta " + cartaATexto(c) + ": valor truco permanente " + (delta >= 0 ? "+" : "") + delta
            + "  (valor actual " + fmt(c.getValorTrucoActual()) + ")");
    }

    public void comprarCarta(Carta c) {
        juego.agregarCartaAlMazoJugador(c);
        juego.notificarCartaComprada();
        ok("Carta " + cartaATexto(c) + " agregada al mazo (se disparo AL_AGREGAR_CARTA_AL_MAZO y AL_COMPRAR_CARTA).");
        avisarConsumidos();
    }

    // ======================= PARAMETROS =======================

    public void setMeta(double v) { meta = v; juego.setPuntajeMeta(v); ok("Meta del combate: " + fmt(v)); }
    public void setPesos(int v) { jugador.sumarPesos(v - jugador.getPesos()); ok("Pesos: $" + jugador.getPesos()); }

    public void setManos(int n) {
        int dif = n - jugador.getManosMaximas();
        if (dif != 0) jugador.aumentarManosMaximas(dif);
        jugador.reiniciarManos();
        ok("Manos: " + jugador.getManosActuales());
    }

    public void setDescartes(int n) {
        jugador.sumarDescartesExtra(n - jugador.getDescartesMaximos());
        juego.restaurarDescartes();
        ok("Descartes: " + juego.getDescartesActuales());
    }

    public void setEsMano(boolean jugadorEsMano) {
        if (!mesaVacia()) { error("Hay cartas en la mesa. Usa 'nueva' primero."); return; }
        if (juego.isJugadorEsMano() != jugadorEsMano) juego.avanzarMano();
        juego.setTurnoActual(jugadorEsMano ? jugador : rival);
        ok(jugadorEsMano ? "El jugador es mano (juega primero)." : "El rival es mano (abre la baza).");
    }

    // ======================= JUGAR =======================

    private ArrayList<Carta> mesaJugador() { return juego.getMesa().getMesaJugador(); }
    private ArrayList<Carta> mesaRival() { return juego.getMesa().getMesaRival(); }

    private boolean rivalAbre() {
        return juego.getTurnoActual() == rival && mesaRival().size() == mesaJugador().size() && !rival.getMano().isEmpty();
    }

    /** Juega la carta n (1-based; 0 = la proxima en orden). El rival responde como lo hace el juego: siempre su primera carta. */
    public void jugar(int n) {
        if (juego.isManoFinalizada()) { error("La mano ya termino. Usa 'nueva'."); return; }
        if (cartasJugador.isEmpty()) { error("El jugador no tiene mano. Usa 'mano ...' o 'iniciar'."); return; }
        // se valida ANTES de jugar nada, para no dejar la mano a medias
        if (rival.getMano().isEmpty() && mesaRival().size() <= mesaJugador().size()) { error("El rival no tiene cartas para responder. Usa 'rival 4C 5C 6C'."); return; }
        Carta carta = null;
        if (n <= 0) {
            for (Carta c : cartasJugador) if (jugador.getMano().contains(c)) { carta = c; break; }
            if (carta == null) { error("No quedan cartas para jugar."); return; }
        } else {
            carta = cartaDeMano(n);
            if (carta == null) return;
        }
        if (rivalAbre()) jugarCartaRival();
        jugarCartaJugador(carta);
        if (!juego.isManoFinalizada() && mesaRival().size() < mesaJugador().size()) jugarCartaRival();
        avisarConsumidos();
    }

    /** Juega todas las cartas en orden hasta que termina la mano. */
    public void auto() {
        int seguridad = 0;
        while (!juego.isManoFinalizada() && seguridad++ < 6) {
            boolean quedan = false;
            for (Carta c : cartasJugador) if (jugador.getMano().contains(c)) { quedan = true; break; }
            if (!quedan) break;
            jugar(0);
        }
    }

    private void jugarCartaJugador(Carta c) {
        juego.agregarCartaJugador(c);
        info("Jugador juega " + cartaATexto(c));
        if (mesaJugador().size() == mesaRival().size()) { juego.jugarMano(toco); toco++; resultadoBaza(); }
        else juego.setTurnoActual(rival);
        chequearFinDeMano();
    }

    private void jugarCartaRival() {
        if (rival.getMano().isEmpty()) { error("El rival no tiene cartas."); return; }
        Carta c = rival.getMano().get(0);
        juego.agregarCartaRival(c);
        info("Rival juega " + cartaATexto(c));
        if (mesaJugador().size() == mesaRival().size()) { juego.jugarMano(toco); toco++; resultadoBaza(); }
        else juego.setTurnoActual(jugador);
        chequearFinDeMano();
    }

    private void resultadoBaza() {
        int i = juego.getResultados().size() - 1;
        if (i < 0) return;
        info("Baza " + (i + 1) + ": " + juego.getResultados().get(i)
            + "   (" + cartaATexto(mesaJugador().get(i)) + " vs " + cartaATexto(mesaRival().get(i)) + ")");
    }

    private void chequearFinDeMano() {
        if (juego.isManoFinalizada() || !juego.terminoLaMano()) return;
        double antesJ = juego.getPuntosJugador();
        double antesR = juego.getPuntosRival();
        juego.finalizarManoTruco();
        ResolucionPuntaje r = juego.getUltimaResolucion();
        if (r != null) volcarResolucion("TRUCO - gana el jugador", r);
        else titulo("TRUCO - gana el rival");
        info("Puntos: jugador " + fmt(antesJ) + " -> " + fmt(juego.getPuntosJugador())
            + "   |   rival " + fmt(antesR) + " -> " + fmt(juego.getPuntosRival()));
        info("Estado del combate: " + juego.verificarEstadoCombate());
    }

    public void envido(boolean cantaJugador) {
        Jugador cantor = cantaJugador ? jugador : rival;
        if (!juego.cantarEnvido(cantor, 1)) {
            error("No se puede cantar envido ahora (ya se canto, o ya se jugaron cartas de los dos lados).");
            return;
        }
        info((cantaJugador ? "Jugador" : "Rival") + " canta envido. Envido: jugador " + fmt(jugador.getPuntosEnvido())
            + " vs rival " + fmt(rival.getPuntosEnvido()));
        juego.responderEnvido(true);
        double antesJ = juego.getPuntosJugador();
        double antesR = juego.getPuntosRival();
        ResolucionPuntaje r = juego.getUltimaResolucionEnvido();
        if (r != null) volcarResolucion("ENVIDO", r);
        juego.aplicarResultadoEnvido();
        info("Puntos: jugador " + fmt(antesJ) + " -> " + fmt(juego.getPuntosJugador())
            + "   |   rival " + fmt(antesR) + " -> " + fmt(juego.getPuntosRival()));
        avisarConsumidos();
    }

    public void truco(boolean cantaJugador) {
        Jugador cantor = cantaJugador ? jugador : rival;
        if (!juego.cantarTruco(cantor, 1)) { error("No se puede cantar truco ahora (ya se canto)."); return; }
        juego.responderTruco(true);
        ok((cantaJugador ? "Jugador" : "Rival") + " canto truco y fue aceptado.  Mult truco: jugador x"
            + fmt(jugador.getMultiplicadorTruco()) + " / rival x" + fmt(rival.getMultiplicadorTruco()));
        avisarConsumidos();
    }

    public void descartar(int[] idx) {
        ArrayList<Carta> aDescartar = new ArrayList<>();
        for (int i : idx) {
            Carta c = cartaDeMano(i);
            if (c == null) return;
            if (aDescartar.contains(c)) { error("La carta " + i + " esta repetida."); return; }
            aDescartar.add(c);
        }
        ArrayList<Carta> nuevas = juego.descartarCartas(aDescartar);
        if (nuevas.isEmpty()) { error("No se pudo descartar (sin descartes disponibles o sin cartas en el mazo)."); return; }
        for (int k = 0; k < idx.length && k < nuevas.size(); k++) cartasJugador.set(idx[k] - 1, nuevas.get(k));
        ok("Descartaste " + textoCartas(aDescartar) + " y robaste " + textoCartas(nuevas)
            + "   (descartes restantes: " + juego.getDescartesActuales() + ")");
        avisarConsumidos();
    }

    // ======================= RESULTADOS =======================

    private void volcarResolucion(String titulo, ResolucionPuntaje r) {
        titulo("== " + titulo + " ==");
        for (ResolucionPuntaje.PasoResolucion p : r.getLog()) {
            String que;
            switch (p.tipo) {
                case BASE: que = "Base"; break;
                case SUMA_CHIPS: que = p.origen + "  +" + fmt(p.valor) + " puntos"; break;
                case SUMA_MULT: que = p.origen + "  +" + fmt(p.valor) + " mult"; break;
                case MULT_MULT: que = p.origen + "  x" + fmt(p.valor) + " mult"; break;
                default: que = p.origen + "  (equilibra)"; break;
            }
            paso("  " + que + "   ->   " + fmt(p.chipsActual) + " x " + fmt(p.multActual));
        }
        ok("Puntaje final: " + fmt(r.calcularPuntajeFinal()) + "   (" + fmt(r.getChips()) + " x " + fmt(r.getMult()) + ")");
    }

    private void avisarConsumidos() {
        if (juego == null) return;
        for (Joker j : juego.getJokersConsumidos()) info("(consumido) joker " + j.getNombre());
        juego.limpiarJokersConsumidos();
        for (Carta c : juego.getCartasConsumidas()) info("(consumida) carta " + cartaATexto(c));
        juego.limpiarCartasConsumidas();
    }

    public void estado() {
        titulo("ESTADO");
        info(resumenLinea1());
        info(resumenLinea2());
        info("Mano jugador: " + textoCartas(jugador.getMano()) + "   |   mano rival: " + textoCartas(rival.getMano()));
        info("Mesa jugador: " + textoCartas(mesaJugador()) + "   |   mesa rival: " + textoCartas(mesaRival()));
        info("Envido: jugador " + fmt(jugador.getPuntosEnvido()) + "  rival " + fmt(rival.getPuntosEnvido()));
        info("Combate: " + juego.verificarEstadoCombate());
        mostrarTablero();
    }

    // ======================= BARRIDO =======================

    private ArrayList<Carta> cartas(String... t) {
        ArrayList<Carta> l = new ArrayList<>();
        for (String x : t) l.add(textoACarta(x));
        return l;
    }

    /**
     * Prueba cada joker del catalogo (o los que coincidan con el filtro) SOLO, en un combate completo:
     * reparto real, envido y truco ganados y perdidos, nueva mano, descarte, compra y venta.
     * Solo lista los que tiran excepcion. Deja el entorno reiniciado al terminar.
     */
    public void barrido(String filtro) {
        String f = normalizar(filtro);
        ArrayList<String> fallas = new ArrayList<>();
        int probados = 0;
        for (Joker base : new ArrayList<Joker>(catalogoJokers())) {
            if (!f.isEmpty() && !normalizar(base.getNombre() + " " + categorias(base) + " " + base.getId()).contains(f)) continue;
            int marca = log.size();
            String falla = null;
            try {
                reiniciar();
                agregarJokers(String.valueOf(base.getId()));
                iniciar();
                // mano 1: el jugador gana envido y truco
                nuevaMano();
                setManoJugador(cartas("7O", "6O", "5O"));
                setManoRival(cartas("4C", "3C", "2C"));
                envido(true);
                truco(true);
                auto();
                // mano 2: el jugador pierde (el rival canta)
                nuevaMano();
                setManoJugador(cartas("4C", "3C", "2C"));
                setManoRival(cartas("7E", "1E", "3E"));
                envido(false);
                truco(false);
                auto();
                // mano 3: descarte, compra de carta y venta del propio joker
                nuevaMano();
                setManoJugador(cartas("1B", "4O", "12C"));
                setManoRival(cartas("5B", "6B", "7B"));
                descartar(new int[]{1});
                comprarCarta(textoACarta("7O"));
                venderJoker("#1");
                setManoRival(cartas("5B", "6B", "7B"));
                auto();
            } catch (Throwable t) {
                falla = resumenExcepcion(t);
            }
            while (log.size() > marca) log.remove(log.size() - 1);
            probados++;
            if (falla != null) fallas.add("#" + base.getId() + " " + base.getNombre() + "  ->  " + falla);
        }
        reiniciar();
        titulo("== BARRIDO DE JOKERS ==");
        for (String l : fallas) error(l);
        if (fallas.isEmpty()) ok(probados + " jokers probados: ninguno tiro excepcion.");
        else info(fallas.size() + " de " + probados + " jokers tiraron excepcion. Para reproducir uno: reiniciar, joker <id>, iniciar, y juga una mano (mano/rival/envido/truco/auto).");
    }

    // ======================= ASERCIONES =======================

    private Double valorDe(String que) {
        switch (normalizar(que)) {
            case "puntos": return juego.getPuntosJugador();
            case "rival": return juego.getPuntosRival();
            case "multtruco": return jugador.getMultiplicadorTruco();
            case "multenvido": return jugador.getMultiplicadorEnvido();
            case "jokers": return (double) jugador.getJokers().size();
            case "santos": return (double) jugador.getSantos().size();
            case "pesos": return (double) jugador.getPesos();
            case "manos": return (double) jugador.getManosActuales();
            case "descartes": return (double) juego.getDescartesActuales();
            case "envido": return jugador.getPuntosEnvido();
            case "envidorival": return rival.getPuntosEnvido();
            default: return null;
        }
    }

    public void esperar(String que, double esperado) {
        Double real = valorDe(que);
        if (real == null) {
            error("No se que es '" + que + "'. Opciones: puntos, rival, multtruco, multenvido, jokers, santos, pesos, manos, descartes, envido, envidorival");
            return;
        }
        chequeos++;
        if (Math.abs(real - esperado) < 1e-6) ok("OK     " + que + " = " + fmt(real));
        else { fallos++; error("FALLO  " + que + ": esperado " + fmt(esperado) + " pero es " + fmt(real)); }
    }

    // ======================= UTILIDADES =======================

    public static String normalizar(String s) {
        String t = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return t.toLowerCase(Locale.ROOT).trim();
    }

    public static String fmt(double v) {
        if (v == Math.rint(v) && Math.abs(v) < 1e15) return String.valueOf((long) v);
        String s = String.format(Locale.US, "%.2f", v);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }
}
