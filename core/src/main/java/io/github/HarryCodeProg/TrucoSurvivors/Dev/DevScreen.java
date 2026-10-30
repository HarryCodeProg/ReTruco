package io.github.HarryCodeProg.TrucoSurvivors.Dev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.AreaElementos;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.MainMenuScreen;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;

import java.util.ArrayList;

/**
 * Entorno de desarrollo: consola de comandos + tablero de jokers y cartas con los tooltips reales del juego.
 * Se abre con F9 desde el menu, solo si existe el archivo "dev.flag" junto al juego (o -Dretruco.dev=true).
 */
public class DevScreen implements Screen {

    /** Las builds para testers no traen dev.flag, asi que la pantalla no existe para ellos. */
    public static boolean disponible() {
        return System.getProperty("retruco.dev") != null || Gdx.files.local("dev.flag").exists();
    }

    private static final float IZQ_X = 20f;
    private static final float IZQ_W = 580f;
    private static final float LOG_X = 620f;
    private static final float LOG_Y = 78f;
    private static final float LOG_W = 640f;
    private static final float LOG_H = 590f;
    private static final float LINE_H = 17f;
    private static final float JOKER_W = 46f, JOKER_H = 66f, Y_JOKERS = 540f;
    private static final float CARTA_W = 50f, CARTA_H = 72f, CARTA_PASO = 56f;
    private static final float Y_RIVAL = 432f, Y_JUGADOR = 322f, X_MESA = 330f;

    private static final Color C_INFO = new Color(0.80f, 0.83f, 0.88f, 1f);
    private static final Color C_OK = new Color(0.45f, 0.90f, 0.55f, 1f);
    private static final Color C_ERROR = new Color(1f, 0.42f, 0.42f, 1f);
    private static final Color C_PASO = new Color(0.95f, 0.85f, 0.50f, 1f);
    private static final Color C_TITULO = new Color(0.40f, 0.85f, 1f, 1f);
    private static final Color C_ECO = new Color(0.62f, 0.75f, 1f, 1f);

    private static final class Visual {
        final Color color;
        final String texto;
        Visual(Color color, String texto) { this.color = color; this.texto = texto; }
    }

    private final Main game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Viewport viewport = new FitViewport(1280, 720, camera);
    private final Vector3 mouse = new Vector3();
    private final GlyphLayout layout = new GlyphLayout();
    private final EntornoDev entorno = new EntornoDev();
    private final ComandosDev comandos = new ComandosDev(entorno);
    private final StringBuilder entrada = new StringBuilder();
    private final ArrayList<String> historialEntrada = new ArrayList<>();
    private int posHistorial = 0;
    private float tiempo = 0f;
    private boolean salirPendiente = false;
    private InputProcessor inputAnterior;

    private final ArrayList<Visual> visuales = new ArrayList<>();
    private int procesadas = 0;
    private int epocaVista = 0;
    private int scroll = 0;

    private final ArrayList<VistaJoker> vistasJokers = new ArrayList<>();
    private final ArrayList<VistaCarta> vistasCartas = new ArrayList<>();
    private AreaElementos<VistaJoker> areaJokers;

    public DevScreen(Main game) {
        this.game = game;
    }

    // ======================= ENTRADA =======================

    @Override
    public void show() {
        inputAnterior = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override public boolean keyTyped(char c) {
                if (c == '\b') {
                    if (entrada.length() > 0) entrada.setLength(entrada.length() - 1);
                    return true;
                }
                if (c == '\r' || c == '\n') { ejecutarEntrada(); return true; }
                if (c >= 32 && c != 127) { entrada.append(c); return true; }
                return false;
            }

            @Override public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.ESCAPE:
                    case Input.Keys.F9:
                        salirPendiente = true;
                        return true;
                    case Input.Keys.UP:
                        if (!historialEntrada.isEmpty()) {
                            posHistorial = Math.max(0, posHistorial - 1);
                            entrada.setLength(0);
                            entrada.append(historialEntrada.get(posHistorial));
                        }
                        return true;
                    case Input.Keys.DOWN:
                        if (posHistorial < historialEntrada.size() - 1) {
                            posHistorial++;
                            entrada.setLength(0);
                            entrada.append(historialEntrada.get(posHistorial));
                        } else {
                            posHistorial = historialEntrada.size();
                            entrada.setLength(0);
                        }
                        return true;
                    case Input.Keys.PAGE_UP: scroll += 12; return true;
                    case Input.Keys.PAGE_DOWN: scroll = Math.max(0, scroll - 12); return true;
                    case Input.Keys.V:
                        if (ctrlApretado()) { pegar(); return true; }
                        return false;
                    default:
                        return false;
                }
            }

            @Override public boolean scrolled(float amountX, float amountY) {
                scroll = Math.max(0, scroll - (int) (amountY * 3));
                return true;
            }
        });
        reconstruirVistas();
    }

    private boolean ctrlApretado() {
        return Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
    }

    private void pegar() {
        String texto = Gdx.app.getClipboard().getContents();
        if (texto == null || texto.isEmpty()) return;
        if (texto.contains("\n")) {
            comandos.ejecutarTexto(texto);
            scroll = 0;
            reconstruirVistas();
        } else {
            entrada.append(texto.trim());
        }
    }

    private void ejecutarEntrada() {
        String linea = entrada.toString().trim();
        entrada.setLength(0);
        if (linea.isEmpty()) return;
        historialEntrada.add(linea);
        posHistorial = historialEntrada.size();
        entorno.eco("> " + linea);
        comandos.ejecutar(linea);
        scroll = 0;
        reconstruirVistas();
    }

    @Override public void hide() { Gdx.input.setInputProcessor(inputAnterior); }

    // ======================= VISTAS =======================

    private void reconstruirVistas() {
        for (VistaJoker v : vistasJokers) v.dispose();
        vistasJokers.clear();
        for (Joker j : entorno.getJugador().getJokers()) {
            VistaJoker v = new VistaJoker(j, game.getAtlasJokers());
            v.setTamaño(JOKER_W, JOKER_H);
            vistasJokers.add(v);
        }
        areaJokers = new AreaElementos<VistaJoker>(IZQ_X, Y_JOKERS, IZQ_W, JOKER_H, JOKER_W, JOKER_H, 4f);
        areaJokers.distribuir(vistasJokers, null);
        for (VistaJoker v : vistasJokers) v.setPosition(v.getHandTargetX(), areaJokers.getY());

        vistasCartas.clear();
        agregarFila(entorno.getRival().getMano(), IZQ_X, Y_RIVAL);
        agregarFila(entorno.getJuego().getMesa().getMesaRival(), X_MESA, Y_RIVAL);
        agregarFila(entorno.getJugador().getMano(), IZQ_X, Y_JUGADOR);
        agregarFila(entorno.getJuego().getMesa().getMesaJugador(), X_MESA, Y_JUGADOR);
    }

    private void agregarFila(ArrayList<Carta> cartas, float x0, float y) {
        float x = x0;
        for (Carta c : cartas) {
            VistaCarta v = new VistaCarta(c, false, game.getAtlasCartas());
            v.setTamaño(CARTA_W, CARTA_H);
            v.setPosition(x, y);
            v.setHandPosition(x, y);
            vistasCartas.add(v);
            x += CARTA_PASO;
        }
    }

    // ======================= LOG =======================

    private static Color colorDe(EntornoDev.Tipo t) {
        switch (t) {
            case OK: return C_OK;
            case ERROR: return C_ERROR;
            case PASO: return C_PASO;
            case TITULO: return C_TITULO;
            case ECO: return C_ECO;
            default: return C_INFO;
        }
    }

    private void sincronizarLog(BitmapFont fuente) {
        if (epocaVista != entorno.getEpocaLog()) {
            visuales.clear();
            procesadas = 0;
            epocaVista = entorno.getEpocaLog();
        }
        ArrayList<EntornoDev.Linea> log = entorno.getLog();
        while (procesadas < log.size()) {
            EntornoDev.Linea l = log.get(procesadas++);
            agregarVisual(fuente, colorDe(l.tipo), l.texto);
        }
        while (visuales.size() > 4000) visuales.remove(0);
    }

    /** Parte el texto en tantas lineas visuales como hagan falta para que entre en el panel. */
    private void agregarVisual(BitmapFont fuente, Color color, String texto) {
        float ancho = LOG_W - 24f;
        String resto = texto;
        boolean primera = true;
        if (resto.isEmpty()) { visuales.add(new Visual(color, "")); return; }
        while (!resto.isEmpty()) {
            layout.setText(fuente, resto);
            int corte = resto.length();
            if (layout.width > ancho) {
                int lo = 1, hi = resto.length();
                while (lo < hi) {
                    int mid = (lo + hi + 1) / 2;
                    layout.setText(fuente, resto.substring(0, mid));
                    if (layout.width <= ancho) lo = mid; else hi = mid - 1;
                }
                corte = lo;
                int espacio = resto.lastIndexOf(' ', corte);
                if (espacio > corte / 2) corte = espacio;
            }
            String parte = resto.substring(0, corte);
            visuales.add(new Visual(color, primera ? parte : "     " + parte.trim()));
            resto = resto.substring(corte).trim();
            primera = false;
        }
    }

    // ======================= RENDER =======================

    @Override
    public void render(float delta) {
        tiempo += delta;
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        if (salirPendiente) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }

        actualizarHover(delta);
        BitmapFont fuente = game.getFuenteTooltipDescripcion();
        sincronizarLog(fuente);

        ScreenUtils.clear(0.05f, 0.06f, 0.09f, 1f);
        SpriteBatch batch = game.batch;
        batch.begin();
        dibujarPaneles(batch);
        dibujarTextosIzquierda(batch, fuente);
        for (VistaJoker v : vistasJokers) v.render(batch);
        for (VistaCarta v : vistasCartas) v.render(batch, game);
        dibujarLog(batch, fuente);
        dibujarEntrada(batch, fuente);
        dibujarTooltips(batch);
        batch.end();
    }

    private void actualizarHover(float delta) {
        float mx = mouse.x, my = mouse.y;
        for (VistaJoker v : vistasJokers) v.update(mx, my, delta);
        VistaJoker ganador = null;
        for (VistaJoker v : vistasJokers) if (v.isHover()) ganador = v;
        for (VistaJoker v : vistasJokers) if (v != ganador) v.limpiarHover();
        for (VistaCarta v : vistasCartas) v.update(mx, my, delta);
    }

    private void rect(SpriteBatch batch, Texture pixel, float x, float y, float w, float h, float r, float g, float b, float a) {
        batch.setColor(r, g, b, a);
        batch.draw(pixel, x, y, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void dibujarPaneles(SpriteBatch batch) {
        Texture px = game.getPixelBlanco();
        rect(batch, px, IZQ_X - 6f, 70f, IZQ_W + 12f, 650f, 0.09f, 0.11f, 0.15f, 1f);
        rect(batch, px, LOG_X - 6f, LOG_Y - 6f, LOG_W + 12f, LOG_H + 12f, 0.02f, 0.025f, 0.04f, 1f);
        rect(batch, px, 20f, 16f, 1240f, 40f, 0.12f, 0.15f, 0.20f, 1f);
        rect(batch, px, 20f, 54f, 1240f, 2f, 0.85f, 0.65f, 0.15f, 1f);
        rect(batch, px, IZQ_X, Y_JOKERS - 6f, IZQ_W, JOKER_H + 12f, 0.06f, 0.08f, 0.11f, 1f);
    }

    private void texto(SpriteBatch batch, BitmapFont f, Color c, String s, float x, float y) {
        f.setColor(c);
        f.draw(batch, s, x, y);
        f.setColor(Color.WHITE);
    }

    private void dibujarTextosIzquierda(SpriteBatch batch, BitmapFont f) {
        BitmapFont ui = game.getFuenteUI();
        ui.setColor(0.95f, 0.78f, 0.25f, 1f);
        ui.draw(batch, "ENTORNO DE DESARROLLO", IZQ_X, 706f);
        ui.setColor(Color.WHITE);
        texto(batch, f, C_INFO, entorno.resumenLinea1(), IZQ_X, 676f);
        texto(batch, f, C_INFO, entorno.resumenLinea2(), IZQ_X, 656f);
        texto(batch, f, C_TITULO, "Jokers " + vistasJokers.size() + "/" + EntornoDev.JOKERS_MAX + "   (pasa el mouse para ver el cartel)",
            IZQ_X, Y_JOKERS + JOKER_H + 32f);
        texto(batch, f, C_TITULO, "Rival   mano  /  mesa", IZQ_X, Y_RIVAL + CARTA_H + 22f);
        texto(batch, f, C_TITULO, "Jugador   mano  /  mesa", IZQ_X, Y_JUGADOR + CARTA_H + 22f);
        texto(batch, f, C_PASO, "Santos: " + nombresSantos(), IZQ_X, 296f);
        String[] atajos = {
            "ESC / F9       volver al menu",
            "Ctrl+V         pegar un escenario (varias lineas)",
            "Arriba/Abajo   historial de comandos",
            "PgUp / PgDn    desplazar el log",
            "ayuda          lista completa de comandos",
            "",
            "Tip: al probar un bug de un tester, pega el archivo",
            "de la carpeta logs y mira el paso a paso."
        };
        float y = 250f;
        for (String a : atajos) { texto(batch, f, C_INFO, a, IZQ_X, y); y -= LINE_H + 2f; }
    }

    private String nombresSantos() {
        StringBuilder sb = new StringBuilder();
        for (io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo s : entorno.getJugador().getSantos()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(s.getNombre());
        }
        return sb.length() == 0 ? "ninguno" : sb.toString();
    }

    private void dibujarLog(SpriteBatch batch, BitmapFont f) {
        int visibles = (int) (LOG_H / LINE_H);
        int maxScroll = Math.max(0, visuales.size() - visibles);
        if (scroll > maxScroll) scroll = maxScroll;
        int fin = visuales.size() - scroll;
        int inicio = Math.max(0, fin - visibles);
        float y = LOG_Y + LOG_H - 4f;
        for (int i = inicio; i < fin; i++) {
            Visual v = visuales.get(i);
            f.setColor(v.color);
            f.draw(batch, v.texto, LOG_X + 6f, y);
            y -= LINE_H;
        }
        f.setColor(Color.WHITE);
        if (scroll > 0) texto(batch, f, C_PASO, "(" + scroll + " lineas arriba del final - PgDn)", LOG_X + LOG_W - 260f, LOG_Y + 18f);
    }

    private void dibujarEntrada(SpriteBatch batch, BitmapFont f) {
        boolean cursor = ((int) (tiempo * 2f)) % 2 == 0;
        String linea = "> " + entrada + (cursor ? "_" : " ");
        texto(batch, f, Color.WHITE, linea, 32f, 44f);
    }

    private void dibujarTooltips(SpriteBatch batch) {
        for (VistaJoker v : vistasJokers) {
            if (v.isHover()) { v.renderCartelStats(batch, game, entorno.getJuego()); return; }
        }
        for (VistaCarta v : vistasCartas) {
            if (v.isHover()) { v.renderCartelStats(batch, game); return; }
        }
    }

    // ======================= CICLO DE VIDA =======================

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        entorno.cerrar();
        for (VistaJoker v : vistasJokers) v.dispose();
        vistasJokers.clear();
    }
}
