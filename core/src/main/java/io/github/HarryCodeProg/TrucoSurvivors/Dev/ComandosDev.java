package io.github.HarryCodeProg.TrucoSurvivors.Dev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;

import java.util.ArrayList;

/**
 * Interprete de comandos del entorno de desarrollo. Cada linea que funciona se graba en el historial,
 * asi "guardar" arma un script que se puede volver a correr ("correr") y pegar en un reporte de bug.
 */
public class ComandosDev {
    private static final String CARPETA = "dev";

    private final EntornoDev e;
    private final ArrayList<String> historial = new ArrayList<>();

    public ComandosDev(EntornoDev e) { this.e = e; }

    public ArrayList<String> getHistorial() { return historial; }

    /** Texto de varias lineas (pegado o archivo): ejecuta cada una mostrando el eco. */
    public void ejecutarTexto(String texto) {
        for (String cruda : texto.split("\\r?\\n")) {
            String l = limpiar(cruda);
            if (l.isEmpty()) continue;
            e.eco("> " + l);
            ejecutar(l);
        }
    }

    private static String limpiar(String linea) {
        String l = linea.trim();
        if (l.startsWith("#")) return "";
        return l.replaceAll("\\s+#(\\s.*)?$", "").trim();
    }

    public void ejecutar(String lineaCruda) {
        String linea = limpiar(lineaCruda);
        if (linea.isEmpty()) return;
        String[] t = linea.split("\\s+");
        String cmd = EntornoDev.normalizar(t[0]);
        String resto = linea.length() > t[0].length() ? linea.substring(t[0].length()).trim() : "";
        int marca = e.marca();
        boolean registrar = true;
        try {
            switch (cmd) {
                case "ayuda": case "help": case "?": ayuda(); registrar = false; break;
                case "cls": case "limpiarlog": e.limpiarLog(); registrar = false; break;
                case "reiniciar": e.reiniciar(); historial.clear(); registrar = false; break;
                case "iniciar": e.iniciar(); break;
                case "nueva": e.nuevaMano(); break;
                case "jokers": e.listarJokers(resto); registrar = false; break;
                case "santos": e.listarSantos(resto); registrar = false; break;
                case "joker": if (resto.isEmpty()) uso("joker <id|nombre>[, <id|nombre> ...]"); else e.agregarJokers(resto); break;
                case "santo": if (resto.isEmpty()) uso("santo <id|nombre>[, ...]"); else e.agregarSantos(resto); break;
                case "quitar": if (resto.isEmpty()) uso("quitar <id|nombre|#pos>[, ...]"); else e.quitarJokers(resto); break;
                case "limpiar": e.quitarTodosLosJokers(); break;
                case "tablero": e.mostrarTablero(); registrar = false; break;
                case "mover": mover(t); break;
                case "acum": acum(t); break;
                case "extra": extra(t, resto); break;
                case "mano": cartas(t, 0); break;
                case "rival": cartas(t, 1); break;
                case "mazo": cartas(t, 2); break;
                case "valor": valor(t); break;
                case "meta": { Double v = decimal(t, 1); if (v != null) e.setMeta(v); break; }
                case "pesos": { Integer v = entero(t, 1); if (v != null) e.setPesos(v); break; }
                case "manos": { Integer v = entero(t, 1); if (v != null) e.setManos(v); break; }
                case "descartes": { Integer v = entero(t, 1); if (v != null) e.setDescartes(v); break; }
                case "esmano": esMano(t); break;
                case "envido": e.envido(!(t.length > 1 && EntornoDev.normalizar(t[1]).equals("rival"))); break;
                case "truco": e.truco(!(t.length > 1 && EntornoDev.normalizar(t[1]).equals("rival"))); break;
                case "jugar": { if (t.length == 1) e.jugar(0); else { Integer v = entero(t, 1); if (v != null) e.jugar(v); } break; }
                case "auto": e.auto(); break;
                case "descartar": descartar(t); break;
                case "usar": usar(t); break;
                case "comprar": comprar(t, resto); break;
                case "vender": if (resto.isEmpty()) uso("vender <id|nombre|#pos>"); else e.venderJoker(resto); break;
                case "estado": e.estado(); registrar = false; break;
                case "barrido": e.barrido(resto); registrar = false; break;
                case "esperar": esperar(t); break;
                case "guardar": guardar(resto); registrar = false; break;
                case "correr": correr(resto); registrar = false; break;
                case "lista": lista(); registrar = false; break;
                case "exportar": exportar(); registrar = false; break;
                case "copiar": copiar(); registrar = false; break;
                default:
                    e.error("Comando desconocido: '" + t[0] + "'. Escribi 'ayuda'.");
                    registrar = false;
            }
        } catch (Throwable ex) {
            e.excepcion(ex);
            registrar = false;
        }
        if (registrar && e.erroresDesde(marca) == 0) historial.add(linea);
    }

    // ======================= PARSEO =======================

    private void uso(String texto) { e.error("Uso: " + texto); }

    private Integer entero(String[] t, int i) {
        if (t.length <= i) { e.error("Falta un numero."); return null; }
        try { return Integer.parseInt(t[i]); }
        catch (NumberFormatException ex) { e.error("'" + t[i] + "' no es un numero entero."); return null; }
    }

    private Double decimal(String[] t, int i) {
        if (t.length <= i) { e.error("Falta un numero."); return null; }
        try { return Double.parseDouble(t[i].replace(',', '.')); }
        catch (NumberFormatException ex) { e.error("'" + t[i] + "' no es un numero."); return null; }
    }

    private int[] enteros(String[] t, int desde) {
        int[] r = new int[t.length - desde];
        for (int i = desde; i < t.length; i++) {
            Integer v = entero(t, i);
            if (v == null) return null;
            r[i - desde] = v;
        }
        return r;
    }

    private void cartas(String[] t, int que) {
        if (t.length < 2) { uso(t[0] + " 1E 7O 3B   (numero + palo: E, B, O, C)"); return; }
        ArrayList<Carta> cartas = e.parsearCartas(t, 1);
        if (cartas == null) return;
        if (que == 0) e.setManoJugador(cartas);
        else if (que == 1) e.setManoRival(cartas);
        else e.setMazo(cartas);
    }

    private void mover(String[] t) {
        Integer a = entero(t, 1), b = entero(t, 2);
        if (a != null && b != null) e.mover(a, b);
    }

    private void acum(String[] t) {
        Integer pos = entero(t, 1);
        Double v = decimal(t, 2);
        if (pos != null && v != null) e.setAcumulado(pos, v);
    }

    private void extra(String[] t, String resto) {
        Integer pos = entero(t, 1);
        if (pos == null) return;
        String texto = resto.substring(t[1].length()).trim();
        e.setExtra(pos, texto);
    }

    private void valor(String[] t) {
        if (t.length < 4) { uso("valor j|r <n> <delta>   (ej: valor j 2 +3)"); return; }
        boolean deJugador = t[1].toLowerCase().startsWith("j");
        Integer n = entero(t, 2);
        Integer d = entero(t, 3);
        if (n != null && d != null) e.modificarValor(deJugador, n, d);
    }

    private void esMano(String[] t) {
        if (t.length < 2) { uso("esmano si|no"); return; }
        String v = EntornoDev.normalizar(t[1]);
        e.setEsMano(v.equals("si") || v.equals("s") || v.equals("jugador") || v.equals("true"));
    }

    private void descartar(String[] t) {
        if (t.length < 2) { uso("descartar <n> [<n> ...]   (posiciones de la mano)"); return; }
        int[] idx = enteros(t, 1);
        if (idx != null) e.descartar(idx);
    }

    private void usar(String[] t) {
        Integer pos = entero(t, 1);
        if (pos == null) return;
        int[] idx = t.length > 2 ? enteros(t, 2) : new int[0];
        if (idx != null) e.usarSanto(pos, idx);
    }

    private void comprar(String[] t, String resto) {
        if (t.length < 3) { uso("comprar joker <id|nombre>   |   comprar carta 7O"); return; }
        String que = EntornoDev.normalizar(t[1]);
        String arg = resto.substring(t[1].length()).trim();
        if (que.equals("joker")) e.comprarJoker(arg);
        else if (que.equals("carta")) {
            Carta c = EntornoDev.textoACarta(arg);
            if (c == null) e.error("No entiendo la carta '" + arg + "'.");
            else e.comprarCarta(c);
        } else uso("comprar joker <ref>   |   comprar carta 7O");
    }

    private void esperar(String[] t) {
        if (t.length < 3) { uso("esperar <puntos|rival|multtruco|multenvido|jokers|santos|pesos|manos|descartes|envido|envidorival> <valor>"); return; }
        Double v = decimal(t, 2);
        if (v != null) e.esperar(t[1], v);
    }

    // ======================= ESCENARIOS =======================

    private static String nombreSeguro(String n) { return n.trim().replaceAll("[^A-Za-z0-9_\\-]", "_"); }

    private void guardar(String nombre) {
        if (nombre.trim().isEmpty()) { uso("guardar <nombre>"); return; }
        String n = nombreSeguro(nombre);
        FileHandle f = Gdx.files.local(CARPETA + "/" + n + ".txt");
        f.writeString(textoEscenario(), false);
        e.ok("Escenario guardado: " + f.path() + "  (" + historial.size() + " comandos). Se corre con: correr " + n);
    }

    private String textoEscenario() {
        StringBuilder sb = new StringBuilder("reiniciar\n");
        for (String l : historial) sb.append(l).append('\n');
        return sb.toString();
    }

    private void lista() {
        FileHandle dir = Gdx.files.local(CARPETA);
        FileHandle[] archivos = dir.list(".txt");
        if (archivos == null || archivos.length == 0) { e.info("No hay escenarios guardados en la carpeta '" + CARPETA + "'."); return; }
        for (FileHandle f : archivos) e.info("  " + f.nameWithoutExtension());
        e.ok(archivos.length + " escenario(s).");
    }

    private void correr(String nombre) {
        if (nombre.trim().isEmpty()) { uso("correr <nombre>   |   correr todos"); return; }
        if (EntornoDev.normalizar(nombre).equals("todos")) { correrTodos(); return; }
        FileHandle f = Gdx.files.local(CARPETA + "/" + nombreSeguro(nombre) + ".txt");
        if (!f.exists()) { e.error("No existe el escenario '" + nombre + "'. Usa 'lista'."); return; }
        e.resetChequeos();
        e.titulo("== ESCENARIO " + nombre + " ==");
        ejecutarTexto(f.readString());
        if (e.getChequeos() > 0) {
            if (e.getFallos() == 0) e.ok("Escenario OK: " + e.getChequeos() + " chequeo(s) correctos.");
            else e.error("Escenario con FALLOS: " + e.getFallos() + " de " + e.getChequeos() + " chequeo(s).");
        }
    }

    /** Regresion: corre todos los escenarios de la carpeta y resume cuales pasan. */
    private void correrTodos() {
        FileHandle[] archivos = Gdx.files.local(CARPETA).list(".txt");
        if (archivos == null || archivos.length == 0) { e.info("No hay escenarios para correr."); return; }
        ArrayList<String> resumen = new ArrayList<>();
        int pasan = 0;
        for (FileHandle f : archivos) {
            int marca = e.marca();
            e.resetChequeos();
            ejecutarTexto(f.readString());
            int errores = e.erroresDesde(marca);
            boolean ok = errores == 0;
            if (ok) pasan++;
            resumen.add((ok ? "OK      " : "FALLO   ") + f.nameWithoutExtension()
                + "   (" + e.getChequeos() + " chequeos, " + e.getFallos() + " fallos, " + errores + " lineas de error)");
        }
        e.titulo("== RESUMEN DE REGRESION ==");
        for (String l : resumen) { if (l.startsWith("OK")) e.ok(l); else e.error(l); }
        e.info(pasan + " de " + archivos.length + " escenarios pasan.");
    }

    private void exportar() {
        String texto = textoEscenario();
        e.titulo("== ESCENARIO (copiado al portapapeles) ==");
        for (String l : texto.split("\n")) e.info("  " + l);
        copiarAlPortapapeles(texto);
    }

    private void copiar() {
        StringBuilder sb = new StringBuilder();
        for (EntornoDev.Linea l : e.getLog()) sb.append(l.texto).append('\n');
        copiarAlPortapapeles(sb.toString());
    }

    private void copiarAlPortapapeles(String texto) {
        if (Gdx.app != null) {
            Gdx.app.getClipboard().setContents(texto);
            e.ok("Copiado al portapapeles.");
        }
    }

    // ======================= AYUDA =======================

    private void ayuda() {
        e.titulo("== COMANDOS ==");
        String[] l = {
            "CATALOGO     jokers [filtro]   |   santos [filtro]       (el filtro busca en nombre, id y categoria)",
            "JOKERS       joker 12, 45, don quijote   |   quitar #2   |   limpiar   |   tablero",
            "             mover 2 5 (cambia el orden)   |   acum 3 150 (acumulado)   |   extra 3 texto",
            "             comprar joker 12   |   vender #1   (disparan AL_COMPRAR / AL_VENDER)",
            "SANTOS       santo 7   |   usar 1 [cartas...]   (usa el santo 1 sobre las cartas elegidas de la mano)",
            "CARTAS       mano 1E 7O 3B   |   rival 4C 5C 6C   |   mazo ...   |   comprar carta 7O",
            "             palos: E espada, B basto, O oro, C copa   |   valor j 2 +3 (valor truco permanente)",
            "PARAMETROS   meta 100   |   pesos 50   |   manos 4   |   descartes 3   |   esmano si|no",
            "COMBATE      iniciar (reparto real + INICIO_COMBATE)   |   nueva (siguiente mano)",
            "             envido [rival]   |   truco [rival]   |   jugar [n]   |   auto   |   descartar 1 3",
            "             (el rival siempre juega su primera carta, como el juego real)",
            "VER          estado   |   cls   |   Ctrl+V pega un escenario entero",
            "BARRIDO      barrido [filtro]   prueba cada joker SOLO en un combate completo y lista los que tiran excepcion",
            "CHEQUEOS     esperar puntos 150   (puntos, rival, multtruco, multenvido, jokers, pesos, manos, ...)",
            "ESCENARIOS   guardar nombre   |   correr nombre   |   correr todos   |   lista   |   exportar   |   copiar",
            "OTROS        reiniciar (todo limpio)   |   comentarios con #",
            "FLUJO TIPICO reiniciar -> joker ... -> mano ... -> rival ... -> envido -> truco -> auto -> esperar puntos N"
        };
        for (String s : l) e.info(s);
    }
}
