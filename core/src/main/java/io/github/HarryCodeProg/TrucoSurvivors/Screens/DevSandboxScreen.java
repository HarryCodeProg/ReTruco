package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.HarryCodeProg.TrucoSurvivors.Activacion.ContextoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Activacion.GestorJokers;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.EventoJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Rareza;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Mazo;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolutorSecuencia;

import java.util.ArrayList;

/**
 * Pantalla de desarrollo (sandbox) para probar jokers sin jugar una run completa.
 *
 * Capacidades:
 *  - Buscar/filtrar y equipar cualquier joker del catálogo (individual o combinaciones).
 *  - Armar la mano y la mesa (jugador y rival) con cartas concretas.
 *  - Disparar cualquier EventoJuego a mano.
 *  - Resolver puntuación de Truco/Envido y ver el desglose chips/mult paso a paso con colores.
 *  - Panel de estado en tiempo real: chips, mult y puntaje final siempre visibles.
 *  - Botón "Resetear acumulados": reinicia el acumulado de cada joker equipado sin quitarlos.
 *  - Inspeccionar acumulados y estado de cada joker equipado.
 *
 * Acceso: F12 desde el menú principal (oculto en el build público). ESC/F12 vuelven al menú.
 * Atajo de teclado R: resetear acumulados de todos los jokers equipados.
 */
public class DevSandboxScreen implements Screen, InputProcessor {

    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 mouse = new Vector3();
    private final GlyphLayout layout = new GlyphLayout();

    // --- Modelo ---
    private Jugador jugador;
    private Jugador rival;
    private Juego juego;
    private GestorJokers gestorJokers;
    private ResolutorSecuencia resolutor;
    private PoolJokersTienda pool;
    private final ArrayList<Joker> catalogo = new ArrayList<>();
    private final EventoJuego[] eventos = EventoJuego.values();

    // --- Estado de UI ---
    private String busqueda = "";
    private Rareza filtroRareza = null; // null = todas
    private int catScroll = 0;
    private int eventoScroll = 0;
    private int wheel = 0;
    private int numeroCarta = 7;
    private Palo paloCarta = Palo.ESPADA;
    private final ArrayList<String> logLineas = new ArrayList<>();

    // --- Estado de la última resolución (para panel de estado y desglose coloreado) ---
    private double estadoChips   = 0;
    private double estadoMult    = 1;
    private double estadoPuntaje = 0;
    private boolean hayResultado = false;
    /** Pasos de la última resolución para mostrar con colores. */
    private final ArrayList<ResolucionPuntaje.PasoResolucion> ultimosPasos = new ArrayList<>();

    private InputProcessor inputAnterior;

    // ------------------------------------------------------------------ Geometría (viewport 1280x720)
    private static final float LOG_Y     = 8f;
    private static final float LOG_H     = 148f;
    private static final float PANEL_TOP = 712f;
    private static final float PANEL_BOT = LOG_Y + LOG_H + 8f; // 164

    private static final float CAT_X = 8f;
    private static final float CAT_W = 344f;
    private static final float SCE_X = 360f;
    private static final float SCE_W = 500f;
    private static final float EV_X  = 868f;
    private static final float EV_W  = 404f;

    // ------------------------------------------------------------------ Colores por tipo de paso
    /** Base (puntos iniciales y puntaje base de carta) */
    private static final Color COL_BASE  = new Color(0.55f, 0.85f, 0.55f, 1f);
    /** Suma de chips */
    private static final Color COL_CHIPS = new Color(0.35f, 0.65f, 1.00f, 1f);
    /** Suma de mult (+) */
    private static final Color COL_SMULT = new Color(1.00f, 0.62f, 0.20f, 1f);
    /** Multiplicador de mult (×) */
    private static final Color COL_MMULT = new Color(0.85f, 0.35f, 1.00f, 1f);
    /** Balance / equilibrio */
    private static final Color COL_BAL   = new Color(0.70f, 0.90f, 0.70f, 1f);
    /** Etiquetas genéricas / texto secundario */
    private static final Color COL_LABEL = new Color(0.70f, 0.72f, 0.80f, 1f);

    public DevSandboxScreen(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.near = -1000f;
        this.camera.far  = 1000f;
        this.viewport = new FitViewport(1280, 720, camera);
    }

    // ------------------------------------------------------------------ Screen

    @Override
    public void show() {
        construirEscenario();

        inputAnterior = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(this);

        log("Sandbox listo. Equipá jokers a la izquierda, armá cartas al centro, dispará eventos a la derecha.");
        log("F12/ESC: volver al menú.  R: resetear acumulados de jokers equipados.");
    }

    private void construirEscenario() {
        jugador = new Jugador("Dev");
        rival   = new Jugador("Rival");
        jugador.setTamañoJokers(30); // holgura para combinar jokers
        jugador.setMazo(Mazo.crearMazoBase());
        rival.setMazo(Mazo.crearMazoBase());

        juego        = new Juego(jugador, rival, Mazo.crearMazoBase(), true);
        gestorJokers = new GestorJokers(jugador);
        resolutor    = new ResolutorSecuencia(gestorJokers, juego);

        pool = new PoolJokersTienda();
        catalogo.clear();
        catalogo.addAll(pool.crearTodos());

        hayResultado = false;
        estadoChips = 0; estadoMult = 1; estadoPuntaje = 0;
        ultimosPasos.clear();
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        float mx = mouse.x;
        float my = mouse.y;
        boolean click = Gdx.input.justTouched();

        // Teclas globales
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            volverAlMenu();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && busqueda.length() > 0) {
            busqueda = busqueda.substring(0, busqueda.length() - 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetearAcumulados();
        }

        // Rueda del mouse
        if (wheel != 0) {
            if (mx < SCE_X) catScroll = clamp(catScroll - wheel, 0, Math.max(0, catalogoFiltrado().size() - filasVisiblesCatalogo()));
            else if (mx >= EV_X) eventoScroll = clamp(eventoScroll - wheel, 0, Math.max(0, filasEventos() - filasVisiblesEventos()));
            wheel = 0;
        }

        if (click) {
            manejarClick(mx, my);
        }

        // --- Dibujo ---
        Gdx.gl.glClearColor(0.04f, 0.05f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        dibujarFondoPaneles();
        dibujarCatalogo(mx, my);
        dibujarEscenario(mx, my);
        dibujarEventos(mx, my);
        dibujarPanelEstado();
        dibujarLog();
        dibujarBarraTitulo();
        game.batch.end();
    }

    // ------------------------------------------------------------------ Input

    private void manejarClick(float mx, float my) {
        // Catálogo: lista de jokers
        ArrayList<Joker> filtrados = catalogoFiltrado();
        float listTop = 622f;
        float listBot = PANEL_BOT + 2f;
        float rowH = 34f;
        int visibles = (int) ((listTop - listBot) / rowH);
        for (int i = 0; i < visibles && i + catScroll < filtrados.size(); i++) {
            float ry = listTop - (i + 1) * rowH;
            if (en(CAT_X + 6f, ry, CAT_W - 12f, rowH - 2f)) {
                equiparJoker(filtrados.get(i + catScroll));
                return;
            }
        }
        if (clicRareza(mx, my)) return;
        if (clicAcciones(mx, my)) return;
        if (clicSelectorCarta(mx, my)) return;
        if (clicEventos(mx, my)) return;
        if (clicEquipados(mx, my)) return;
        if (clicZonasCartas(mx, my)) return;
    }

    private boolean clicRareza(float mx, float my) {
        float y = 634f;
        float h = 24f;
        float x = CAT_X + 6f;
        float w = (CAT_W - 12f - 5f * 4f) / 6f;
        if (en(x, y, w, h)) { filtroRareza = null; return true; }
        x += w + 4f;
        for (Rareza r : Rareza.values()) {
            if (en(x, y, w, h)) { filtroRareza = r; return true; }
            x += w + 4f;
        }
        return false;
    }

    private boolean clicAcciones(float mx, float my) {
        // Fila 1: RESOLVER TRUCO | RESOLVER ENVIDO | LIMPIAR TODO
        float y1 = 681f;
        float h  = 26f;
        float w  = (EV_W - 12f - 2f * 6f) / 3f;
        float x  = EV_X + 6f;
        if (en(x, y1, w, h)) { resolverTruco();   return true; } x += w + 6f;
        if (en(x, y1, w, h)) { resolverEnvido();  return true; } x += w + 6f;
        if (en(x, y1, w, h)) { limpiarTodo();     return true; }

        // Fila 2: RESETEAR ACUMULADOS
        if (en(EV_X + 6f, 651f, EV_W - 12f, h)) { resetearAcumulados(); return true; }

        return false;
    }

    private boolean clicSelectorCarta(float mx, float my) {
        if (en(SCE_X + 6f,   268f, 30f, 26f)) { numeroCarta = clamp(numeroCarta - 1, 1, 12); return true; }
        if (en(SCE_X + 96f,  268f, 30f, 26f)) { numeroCarta = clamp(numeroCarta + 1, 1, 12); return true; }
        if (en(SCE_X + 150f, 268f, 30f, 26f)) { paloCarta = cicloPalo(paloCarta, -1);         return true; }
        if (en(SCE_X + 260f, 268f, 30f, 26f)) { paloCarta = cicloPalo(paloCarta, +1);         return true; }
        float y  = 216f;
        float w  = (SCE_W - 12f - 3f * 6f) / 4f;
        float xb = SCE_X + 6f;
        if (en(xb, y, w, 40f)) { agregarCartaA("manoJugador"); return true; } xb += w + 6f;
        if (en(xb, y, w, 40f)) { agregarCartaA("mesaJugador"); return true; } xb += w + 6f;
        if (en(xb, y, w, 40f)) { agregarCartaA("mesaRival");   return true; } xb += w + 6f;
        if (en(xb, y, w, 40f)) { agregarCartaA("manoRival");   return true; }
        return false;
    }

    private boolean clicEventos(float mx, float my) {
        float listTop = 632f;
        float listBot = PANEL_BOT + 2f;
        float cellH   = 24f;
        int visibles  = (int) ((listTop - listBot) / cellH);
        int cols      = 2;
        float cellW   = (EV_W - 12f - 4f) / 2f;
        for (int i = 0; i < visibles; i++) {
            int idx = i + eventoScroll;
            if (idx >= filasEventos()) break;
            for (int c = 0; c < cols; c++) {
                int eventoIdx = idx * cols + c;
                if (eventoIdx >= eventos.length) continue;
                float ex = EV_X + 6f + c * (cellW + 4f);
                float ey = listTop - (i + 1) * cellH;
                if (en(ex, ey, cellW, cellH - 2f)) {
                    dispararEvento(eventos[eventoIdx]);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean clicEquipados(float mx, float my) {
        ArrayList<Joker> jokers = jugador.getJokers();
        float y = 668f;
        float h = 66f;
        float x = SCE_X + 6f;
        for (Joker j : new ArrayList<>(jokers)) {
            if (en(x, y, 58f, h)) {
                jugador.eliminarJoker(j);
                log("Quitado: " + j.getNombre());
                return true;
            }
            x += 62f;
        }
        return false;
    }

    private boolean clicZonasCartas(float mx, float my) {
        if (en(SCE_X + 6f, 556f, SCE_W - 12f, 44f)) { quitarUltima(jugador.getMano());                 return true; }
        if (en(SCE_X + 6f, 476f, SCE_W - 12f, 44f)) { quitarUltima(juego.getMesa().getMesaJugador()); return true; }
        if (en(SCE_X + 6f, 396f, SCE_W - 12f, 44f)) { quitarUltima(juego.getMesa().getMesaRival());   return true; }
        if (en(SCE_X + 6f, 316f, SCE_W - 12f, 44f)) { quitarUltima(rival.getMano());                  return true; }
        return false;
    }

    private void quitarUltima(ArrayList<Carta> lista) {
        if (lista.isEmpty()) return;
        Carta c = lista.remove(lista.size() - 1);
        log("Quitada carta: " + c.getNumero() + " de " + c.paloToString());
    }

    // ------------------------------------------------------------------ Acciones

    private void equiparJoker(Joker plantilla) {
        Joker nuevo = pool.crearPorId(plantilla.getId());
        if (nuevo == null) { log("No se pudo crear joker id " + plantilla.getId()); return; }
        if (jugador.agregarJoker(nuevo)) {
            log("Equipado [" + nuevo.getId() + "] " + nuevo.getNombre()
                + "  (" + nuevo.getRareza().name() + ") " + nuevo.getDescripcion());
        } else {
            log("Sin espacio para equipar " + nuevo.getNombre());
        }
    }

    private void dispararEvento(EventoJuego ev) {
        ContextoJuego ctx = juego.crearContexto();
        ArrayList<Carta> mj = juego.getMesa().getMesaJugador();
        ArrayList<Carta> mr = juego.getMesa().getMesaRival();
        if (!mj.isEmpty()) ctx.setCartaEnResolucion(mj.get(mj.size() - 1));
        if (!mr.isEmpty()) ctx.setCartaOponenteEnResolucion(mr.get(mr.size() - 1));
        try {
            gestorJokers.disparar(ev, ctx, juego);
            juego.getGestorRasgos().dispararEvento(ev, ctx, juego);
            log(">> Evento disparado: " + ev.name());
            registrarEstadoJokers();
        } catch (Throwable t) {
            log("!!! EXCEPCIÓN en " + ev.name() + ": " + t.getClass().getSimpleName() + " - " + t.getMessage());
            volcarStackTrace(t);
        }
    }

    private void resolverTruco() {
        ArrayList<Carta> cartas = new ArrayList<>(juego.getMesa().getMesaJugador());
        if (cartas.isEmpty()) { log("No hay cartas en la mesa del jugador para resolver Truco."); return; }
        ResolucionPuntaje res = new ResolucionPuntaje(0, jugador.getMultiplicadorTruco());
        ContextoJuego ctx = juego.crearContexto();
        ctx.setResolucionActual(res);
        try {
            resolutor.resolver(cartas, ctx, EventoJuego.AL_PUNTUAR_CARTA, EventoJuego.ANTES_DE_SUMAR_TRUCO);
            capturarResultado(res);
            log("==== RESOLVER TRUCO (" + cartas.size() + " cartas) ====");
            for (ResolucionPuntaje.PasoResolucion p : res.getLog()) {
                log("   " + p.toString());
            }
            log("   TOTAL = " + res.calcularPuntajeFinal());
        } catch (Throwable t) {
            log("!!! EXCEPCIÓN al resolver Truco: " + t.getClass().getSimpleName() + " - " + t.getMessage());
            volcarStackTrace(t);
        }
    }

    private void resolverEnvido() {
        ArrayList<Carta> cartas = jugador.getCartasEnvidoGanador();
        if (cartas.isEmpty()) { log("No hay cartas en la mano para resolver Envido."); return; }
        ResolucionPuntaje res = new ResolucionPuntaje(0, jugador.getMultiplicadorEnvido());
        ContextoJuego ctx = juego.crearContexto();
        ctx.setResolucionActual(res);
        try {
            resolutor.resolverEnvido(cartas, ctx, EventoJuego.AL_PUNTUAR_CARTA_ENVIDO, EventoJuego.ANTES_DE_SUMAR_ENVIDO);
            capturarResultado(res);
            log("==== RESOLVER ENVIDO (" + cartas.size() + " cartas) ====");
            for (ResolucionPuntaje.PasoResolucion p : res.getLog()) {
                log("   " + p.toString());
            }
            log("   TOTAL = " + res.calcularPuntajeFinal());
        } catch (Throwable t) {
            log("!!! EXCEPCIÓN al resolver Envido: " + t.getClass().getSimpleName() + " - " + t.getMessage());
            volcarStackTrace(t);
        }
    }

    /** Guarda chips/mult/puntaje final y los pasos de la última resolución para mostrarlos en el panel. */
    private void capturarResultado(ResolucionPuntaje res) {
        estadoChips   = res.getChips();
        estadoMult    = res.getMult();
        estadoPuntaje = res.calcularPuntajeFinal();
        hayResultado  = true;
        ultimosPasos.clear();
        ultimosPasos.addAll(res.getLog());
    }

    private void limpiarTodo() {
        for (Joker j : new ArrayList<>(jugador.getJokers())) jugador.eliminarJoker(j);
        jugador.getMano().clear();
        rival.getMano().clear();
        juego.getMesa().limpiarMesa();
        logLineas.clear();
        hayResultado = false;
        ultimosPasos.clear();
        log("Escenario reiniciado.");
    }

    /**
     * Resetea el acumulado de todos los jokers equipados sin quitarlos.
     * Útil para re-testear el mismo combo desde cero sin perder la configuración.
     */
    private void resetearAcumulados() {
        int n = 0;
        for (Joker j : jugador.getJokers()) {
            j.setAcumulado(0);
            n++;
        }
        if (n > 0) {
            log(">> Acumulados reseteados (" + n + " jokers).");
            registrarEstadoJokers();
        } else {
            log(">> No hay jokers equipados.");
        }
    }

    private void agregarCartaA(String zona) {
        Carta c = new Carta(numeroCarta, paloCarta);
        switch (zona) {
            case "manoJugador": jugador.getMano().add(c);               break;
            case "mesaJugador": juego.getMesa().agregarCartaJugador(c); break;
            case "mesaRival":   juego.getMesa().agregarCartaRival(c);   break;
            case "manoRival":   rival.getMano().add(c);                 break;
        }
        log("Carta agregada a " + zona + ": " + numeroCarta + " de " + paloCarta.name());
    }

    private void registrarEstadoJokers() {
        log("   estado: multTruco=" + jugador.getMultiplicadorTruco()
            + " multEnvido=" + jugador.getMultiplicadorEnvido());
        for (Joker j : jugador.getJokers()) {
            String extra = j.getEstadoExtra();
            log("      [" + j.getId() + "] " + j.getNombre()
                + "  acumulado=" + j.getAcumulado()
                + (extra != null ? "  estado=" + extra : ""));
        }
    }

    private void volcarStackTrace(Throwable t) {
        for (StackTraceElement e : t.getStackTrace()) {
            log("     at " + e.toString());
            if (logLineas.size() > 400) break;
        }
    }

    // ------------------------------------------------------------------ Dibujo

    private void dibujarBarraTitulo() {
        BitmapFont f = game.getFuenteUI();
        f.setColor(Color.WHITE);
        f.draw(game.batch, "SANDBOX DEV", 10f, 718f);
        f.setColor(new Color(0.6f, 0.65f, 0.75f, 1f));
        f.draw(game.batch, "F12/ESC volver  |  R: resetear acumulados", 820f, 718f);
        f.setColor(Color.WHITE);
    }

    private void dibujarFondoPaneles() {
        rect(CAT_X, PANEL_BOT, CAT_W, PANEL_TOP - PANEL_BOT, new Color(0.08f, 0.09f, 0.12f, 0.95f));
        rect(SCE_X, PANEL_BOT, SCE_W, PANEL_TOP - PANEL_BOT, new Color(0.07f, 0.08f, 0.11f, 0.95f));
        rect(EV_X,  PANEL_BOT, EV_W,  PANEL_TOP - PANEL_BOT, new Color(0.08f, 0.09f, 0.12f, 0.95f));
        rect(6f, LOG_Y, 1268f, LOG_H, new Color(0.05f, 0.05f, 0.07f, 0.98f));
    }

    private void dibujarCatalogo(float mx, float my) {
        BitmapFont titulo = game.getFuenteUI();
        BitmapFont lista  = game.getFuenteTooltipDescripcion();

        titulo.setColor(Color.WHITE);
        titulo.draw(game.batch, "JOKERS", CAT_X + 6f, 700f);

        // Buscador
        rect(CAT_X + 6f, 664f, CAT_W - 12f, 34f, new Color(0.12f, 0.14f, 0.18f, 1f));
        String q  = busqueda.isEmpty() ? "Escribí para buscar (id o nombre)..." : busqueda;
        Color  cq = busqueda.isEmpty() ? new Color(0.5f, 0.5f, 0.55f, 1f) : Color.WHITE;
        lista.setColor(cq);
        lista.draw(game.batch, q, CAT_X + 12f, 689f);
        lista.setColor(Color.WHITE);

        // Filtro rareza
        float y = 634f;
        float h = 24f;
        float x = CAT_X + 6f;
        float w = (CAT_W - 12f - 5f * 4f) / 6f;
        dibujarChipFiltro(x, y, w, h, "TODAS", null);
        x += w + 4f;
        for (Rareza r : Rareza.values()) {
            dibujarChipFiltro(x, y, w, h, r.name().toUpperCase(), r);
            x += w + 4f;
        }

        // Lista
        ArrayList<Joker> filtrados = catalogoFiltrado();
        float listTop = 622f;
        float listBot = PANEL_BOT + 2f;
        float rowH    = 34f;
        int visibles  = (int) ((listTop - listBot) / rowH);
        for (int i = 0; i < visibles && i + catScroll < filtrados.size(); i++) {
            Joker j  = filtrados.get(i + catScroll);
            float ry = listTop - (i + 1) * rowH;
            boolean hover = en(CAT_X + 6f, ry, CAT_W - 12f, rowH - 2f);
            rect(CAT_X + 6f, ry, CAT_W - 12f, rowH - 2f,
                 hover ? new Color(0.18f, 0.21f, 0.28f, 1f) : new Color(0.10f, 0.11f, 0.15f, 1f));
            rect(CAT_X + 6f, ry, 4f, rowH - 2f, new Color(j.getRareza().getColor()));
            dibujarJoker(j, CAT_X + 12f, ry + 2f, 26f, 28f);
            lista.setColor(Color.WHITE);
            lista.draw(game.batch, "[" + j.getId() + "] " + j.getNombre(), CAT_X + 46f, ry + 21f);

            // Descripción corta al hacer hover
            if (hover) {
                lista.setColor(COL_LABEL);
                String desc = j.getDescripcion();
                if (desc != null && !desc.isEmpty()) {
                    String descCorta = desc.length() > 50 ? desc.substring(0, 48) + "…" : desc;
                    lista.draw(game.batch, descCorta, CAT_X + 46f, ry + 8f);
                }
                lista.setColor(Color.WHITE);
            }
        }
    }

    private void dibujarChipFiltro(float x, float y, float w, float h, String texto, Rareza r) {
        boolean activo = (r == null && filtroRareza == null) || (r != null && r == filtroRareza);
        Color base = activo ? new Color(0.30f, 0.55f, 0.35f, 1f) : new Color(0.12f, 0.13f, 0.17f, 1f);
        rect(x, y, w, h, base);
        BitmapFont f = game.getFuenteTooltipDescripcion();
        f.setColor(activo ? Color.WHITE : COL_LABEL);
        f.draw(game.batch, texto, x + 4f, y + 16f);
        f.setColor(Color.WHITE);
    }

    private void dibujarEscenario(float mx, float my) {
        BitmapFont titulo = game.getFuenteUI();
        BitmapFont label  = game.getFuenteTooltipDescripcion();
        titulo.setColor(Color.WHITE);
        titulo.draw(game.batch, "ESCENARIO", SCE_X + 6f, 700f);

        // Jokers equipados
        label.setColor(Color.WHITE);
        label.draw(game.batch, "Jokers equipados (clic = quitar):", SCE_X + 6f, 664f);
        float x = SCE_X + 6f;
        float y = 668f;
        for (Joker j : new ArrayList<>(jugador.getJokers())) {
            boolean hover = en(x, y, 58f, 66f);
            rect(x - 2f, y - 2f, 62f, 70f, hover ? new Color(0.20f, 0.24f, 0.32f, 1f) : new Color(0.10f, 0.11f, 0.15f, 1f));
            dibujarJoker(j, x + 2f, y + 2f, 54f, 62f);
            // Acumulado debajo del icono
            label.setColor(new Color(0.55f, 0.85f, 0.55f, 1f));
            label.draw(game.batch, String.format("%.0f", j.getAcumulado()), x + 4f, y - 2f);
            label.setColor(Color.WHITE);
            x += 62f;
        }

        // Zonas de cartas
        dibujarZonaCartas("Mano jugador", jugador.getMano(),                SCE_X + 6f, 556f, mx, my);
        dibujarZonaCartas("Mesa jugador", juego.getMesa().getMesaJugador(), SCE_X + 6f, 476f, mx, my);
        dibujarZonaCartas("Mesa rival",   juego.getMesa().getMesaRival(),   SCE_X + 6f, 396f, mx, my);
        dibujarZonaCartas("Mano rival",   rival.getMano(),                  SCE_X + 6f, 316f, mx, my);

        // Selector de carta
        label.setColor(Color.WHITE);
        label.draw(game.batch, "Carta: " + numeroCarta + " de " + paloCarta.name(), SCE_X + 6f, 306f);
        dibujarMiniBoton(SCE_X + 6f,   268f, 30f, 26f, "-");
        dibujarMiniBoton(SCE_X + 96f,  268f, 30f, 26f, "+");
        dibujarMiniBoton(SCE_X + 150f, 268f, 30f, 26f, "<");
        dibujarMiniBoton(SCE_X + 260f, 268f, 30f, 26f, ">");
        dibujarCarta(numeroCarta, paloCarta, SCE_X + 320f, 250f, 34f, 48f);

        // Botones de agregar carta
        float yb = 216f;
        float wb = (SCE_W - 12f - 3f * 6f) / 4f;
        float xb = SCE_X + 6f;
        dibujarBotonPlano(xb, yb, wb, 40f, "A mano");   xb += wb + 6f;
        dibujarBotonPlano(xb, yb, wb, 40f, "A mesa P"); xb += wb + 6f;
        dibujarBotonPlano(xb, yb, wb, 40f, "A mesa R"); xb += wb + 6f;
        dibujarBotonPlano(xb, yb, wb, 40f, "A mano R");
    }

    private void dibujarZonaCartas(String nombre, ArrayList<Carta> cartas, float x, float y, float mx, float my) {
        BitmapFont label = game.getFuenteTooltipDescripcion();
        boolean hover = en(x, y, SCE_W - 12f, 44f);
        rect(x, y, SCE_W - 12f, 44f, hover ? new Color(0.16f, 0.18f, 0.24f, 1f) : new Color(0.10f, 0.11f, 0.15f, 1f));
        label.setColor(COL_LABEL);
        label.draw(game.batch, nombre + " (" + cartas.size() + ")", x + 6f, y + 38f);
        float cx = x + 110f;
        for (Carta c : cartas) {
            dibujarCarta(c.getNumero(), c.getPalo(), cx, y + 2f, 26f, 38f);
            cx += 30f;
        }
    }

    private void dibujarEventos(float mx, float my) {
        BitmapFont titulo = game.getFuenteUI();
        BitmapFont lista  = game.getFuenteTooltipDescripcion();

        // ---- Fila 1: RESOLVER TRUCO | RESOLVER ENVIDO | LIMPIAR TODO ----
        float y1 = 681f;
        float h  = 26f;
        float w  = (EV_W - 12f - 2f * 6f) / 3f;
        float x  = EV_X + 6f;
        dibujarBotonPlano(x, y1, w, h, "RESOLVER TRUCO");  x += w + 6f;
        dibujarBotonPlano(x, y1, w, h, "RESOLVER ENVIDO"); x += w + 6f;
        dibujarBotonPlano(x, y1, w, h, "LIMPIAR TODO");

        // ---- Fila 2: RESETEAR ACUMULADOS (botón morado) ----
        float y2 = 651f;
        float w2 = EV_W - 12f;
        rect(EV_X + 6f, y2, w2, h, new Color(0.20f, 0.12f, 0.35f, 1f));
        lista.setColor(new Color(0.82f, 0.65f, 1f, 1f));
        layout.setText(lista, "RESETEAR ACUMULADOS  (R)");
        lista.draw(game.batch, "RESETEAR ACUMULADOS  (R)",
                   EV_X + 6f + (w2 - layout.width) / 2f, y2 + (h + layout.height) / 2f - 4f);
        lista.setColor(Color.WHITE);

        titulo.setColor(Color.WHITE);
        titulo.draw(game.batch, "EVENTOS", EV_X + 6f, 638f);

        float listTop = 626f;
        float listBot = PANEL_BOT + 2f;
        float cellH   = 24f;
        int visibles  = (int) ((listTop - listBot) / cellH);
        int cols      = 2;
        float cellW   = (EV_W - 12f - 4f) / 2f;
        for (int i = 0; i < visibles; i++) {
            int fila = i + eventoScroll;
            if (fila >= filasEventos()) break;
            for (int c = 0; c < cols; c++) {
                int idx = fila * cols + c;
                if (idx >= eventos.length) continue;
                float ex    = EV_X + 6f + c * (cellW + 4f);
                float ey    = listTop - (i + 1) * cellH;
                boolean hov = en(ex, ey, cellW, cellH - 2f);
                rect(ex, ey, cellW, cellH - 2f,
                     hov ? new Color(0.20f, 0.24f, 0.32f, 1f) : new Color(0.11f, 0.12f, 0.16f, 1f));
                lista.setColor(Color.WHITE);
                lista.draw(game.batch, eventos[idx].name(), ex + 4f, ey + 16f);
            }
        }
    }

    /**
     * Panel de estado en tiempo real + desglose coloreado.
     *
     * Zona inferior del panel central: muestra chips, mult y puntaje final de la última resolución.
     * Zona inferior del panel izquierdo: desglose paso a paso con color por tipo de operación.
     */
    private void dibujarPanelEstado() {
        // ----- Bloque de estado (centro inferior) -----
        float px = SCE_X;
        float ph = 50f;
        float py = PANEL_BOT;
        float pw = SCE_W;

        rect(px, py, pw, ph, new Color(0.05f, 0.07f, 0.10f, 1f));
        // Borde superior azulado
        rect(px, py + ph - 2f, pw, 2f, new Color(0.25f, 0.40f, 0.65f, 0.9f));

        BitmapFont titulo = game.getFuenteUI();
        BitmapFont f      = game.getFuenteTooltipDescripcion();

        titulo.setColor(COL_LABEL);
        titulo.draw(game.batch, "ESTADO", px + 6f, py + ph - 4f);

        if (!hayResultado) {
            f.setColor(COL_LABEL);
            f.draw(game.batch, "Resolví Truco o Envido para ver resultados.", px + 6f, py + ph - 20f);
        } else {
            f.setColor(COL_CHIPS);
            f.draw(game.batch, String.format("Chips: %.0f", estadoChips), px + 6f, py + ph - 18f);

            f.setColor(COL_SMULT);
            f.draw(game.batch, String.format("Mult: %.2f", estadoMult), px + 128f, py + ph - 18f);

            f.setColor(Color.WHITE);
            f.draw(game.batch, String.format("= %.0f pts", estadoPuntaje), px + 270f, py + ph - 18f);

            f.setColor(COL_LABEL);
            f.draw(game.batch,
                   String.format("multTruco=%.2fx  multEnvido=%.2fx",
                       jugador.getMultiplicadorTruco(), jugador.getMultiplicadorEnvido()),
                   px + 6f, py + 14f);
        }
        f.setColor(Color.WHITE);

        // ----- Desglose coloreado (izquierda inferior) -----
        if (!ultimosPasos.isEmpty()) {
            dibujarDesglose();
        }
    }

    /**
     * Dibuja el desglose paso a paso con colores en la zona inferior del panel izquierdo.
     *
     * Colores:
     *   BASE        → verde
     *   SUMA_CHIPS  → azul
     *   SUMA_MULT   → naranja
     *   MULT_MULT   → violeta
     *   BALANCE     → verde claro
     */
    private void dibujarDesglose() {
        BitmapFont f  = game.getFuenteTooltipDescripcion();
        float px      = CAT_X;
        float pw      = CAT_W;
        float ph      = 156f;
        float py      = PANEL_BOT;
        float lineH   = 14f;
        int maxLineas = (int) (ph / lineH);

        rect(px, py, pw, ph, new Color(0.05f, 0.06f, 0.09f, 1f));
        // Borde superior morado
        rect(px, py + ph - 2f, pw, 2f, new Color(0.40f, 0.28f, 0.65f, 0.9f));

        BitmapFont titulo = game.getFuenteUI();
        titulo.setColor(new Color(0.75f, 0.65f, 1.0f, 1f));
        titulo.draw(game.batch, "DESGLOSE", px + 6f, py + ph - 4f);

        int start = Math.max(0, ultimosPasos.size() - (maxLineas - 1));
        float ly  = py + ph - 20f;
        for (int i = start; i < ultimosPasos.size(); i++) {
            ResolucionPuntaje.PasoResolucion p = ultimosPasos.get(i);
            f.setColor(colorDePaso(p.tipo));
            // Paso + snapshot [chips × mult]
            String snapshot = String.format(" [%.0f × %.2f]", p.chipsActual, p.multActual);
            f.draw(game.batch, p.toString() + snapshot, px + 6f, ly);
            ly -= lineH;
        }
        f.setColor(Color.WHITE);
    }

    private Color colorDePaso(ResolucionPuntaje.TipoPaso tipo) {
        switch (tipo) {
            case BASE:       return COL_BASE;
            case SUMA_CHIPS: return COL_CHIPS;
            case SUMA_MULT:  return COL_SMULT;
            case MULT_MULT:  return COL_MMULT;
            case BALANCE:    return COL_BAL;
            default:         return Color.WHITE;
        }
    }

    private void dibujarLog() {
        BitmapFont f = game.getFuenteTooltipDescripcion();
        f.setColor(new Color(0.85f, 0.9f, 0.95f, 1f));
        float lineH  = 15f;
        int visibles = (int) ((LOG_H - 10f) / lineH);
        int start    = Math.max(0, logLineas.size() - visibles);
        float y      = LOG_Y + LOG_H - 14f;
        for (int i = start; i < logLineas.size(); i++) {
            f.draw(game.batch, logLineas.get(i), 10f, y);
            y -= lineH;
        }
        f.setColor(Color.WHITE);
    }

    // ------------------------------------------------------------------ Primitivas

    private void dibujarJoker(Joker j, float x, float y, float w, float h) {
        TextureRegion r = game.getAtlasJokers().findRegion(j.getNombreRegion());
        if (r != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(r, x, y, w, h);
        } else {
            rect(x, y, w, h, new Color(0.2f, 0.2f, 0.25f, 1f));
        }
    }

    private void dibujarCarta(int numero, Palo palo, float x, float y, float w, float h) {
        Carta tmp       = new Carta(numero, palo);
        TextureRegion r = game.getAtlasCartas().findRegion(tmp.getNombreRegion());
        if (r != null) {
            game.batch.setColor(Color.WHITE);
            game.batch.draw(r, x, y, w, h);
        } else {
            rect(x, y, w, h, new Color(0.9f, 0.9f, 0.9f, 1f));
        }
    }

    private void dibujarBotonPlano(float x, float y, float w, float h, String texto) {
        rect(x, y, w, h, new Color(0.16f, 0.30f, 0.20f, 1f));
        BitmapFont f = game.getFuenteTooltipDescripcion();
        f.setColor(Color.WHITE);
        layout.setText(f, texto);
        f.draw(game.batch, texto, x + (w - layout.width) / 2f, y + (h + layout.height) / 2f - 4f);
    }

    private void dibujarMiniBoton(float x, float y, float w, float h, String texto) {
        rect(x, y, w, h, new Color(0.20f, 0.22f, 0.28f, 1f));
        BitmapFont f = game.getFuenteTooltipDescripcion();
        f.setColor(Color.WHITE);
        layout.setText(f, texto);
        f.draw(game.batch, texto, x + (w - layout.width) / 2f, y + (h + layout.height) / 2f - 4f);
    }

    private void rect(float x, float y, float w, float h, Color color) {
        game.batch.setColor(color);
        game.batch.draw(game.getPixelBlanco(), x, y, w, h);
        game.batch.setColor(Color.WHITE);
    }

    // ------------------------------------------------------------------ Utilidades

    private ArrayList<Joker> catalogoFiltrado() {
        ArrayList<Joker> out = new ArrayList<>();
        String q = busqueda.trim().toLowerCase();
        for (Joker j : catalogo) {
            if (filtroRareza != null && j.getRareza() != filtroRareza) continue;
            if (!q.isEmpty()) {
                boolean match = j.getNombre().toLowerCase().contains(q)
                    || j.getNombreArchivo().toLowerCase().contains(q)
                    || String.valueOf(j.getId()).equals(q);
                if (!match) continue;
            }
            out.add(j);
        }
        return out;
    }

    private int filasVisiblesCatalogo() {
        return (int) ((622f - (PANEL_BOT + 2f)) / 34f);
    }

    private int filasEventos() {
        return (int) Math.ceil(eventos.length / 2.0);
    }

    private int filasVisiblesEventos() {
        return (int) ((626f - (PANEL_BOT + 2f)) / 24f);
    }

    private boolean en(float x, float y, float w, float h) {
        return mouse.x >= x && mouse.x <= x + w && mouse.y >= y && mouse.y <= y + h;
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private Palo cicloPalo(Palo p, int dir) {
        Palo[] palos = Palo.values();
        int idx = 0;
        for (int i = 0; i < palos.length; i++) if (palos[i] == p) idx = i;
        idx = (idx + dir + palos.length) % palos.length;
        return palos[idx];
    }

    private void log(String linea) {
        logLineas.add(linea);
        if (logLineas.size() > 600) {
            logLineas.subList(0, logLineas.size() - 600).clear();
        }
    }

    private void volverAlMenu() {
        if (juego != null) juego.desconectar();
        game.setScreen(new MainMenuScreen(game));
        dispose();
    }

    // ------------------------------------------------------------------ InputProcessor

    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode)   { return false; }

    @Override
    public boolean keyTyped(char character) {
        if (character == '\b') return false; // backspace manejado aparte
        if (character >= 32 && character < 127) {
            if (busqueda.length() < 40) busqueda += character;
        }
        return true;
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button)   { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer)          { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY)                         { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        wheel += (int) amountY;
        return true;
    }

    // ------------------------------------------------------------------ Resto de Screen

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == this) {
            Gdx.input.setInputProcessor(inputAnterior);
        }
    }

    @Override
    public void dispose() {
        if (juego != null) juego.desconectar();
        if (Gdx.input.getInputProcessor() == this) {
            Gdx.input.setInputProcessor(inputAnterior);
        }
    }
}
