package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class PanelVictoriaFinal {

    private static final float PANEL_W = 520f;
    private static final float PAD_TOP = 28f;
    private static final float TITULO_ALTO = 44f;
    private static final float ESPACIO_TRAS_TITULO = 14f;
    private static final float FILA_ALTO = 34f;
    private static final float FILA_GAP = 6f;
    private static final int CANTIDAD_FILAS = 5;
    private static final float ESPACIO_ANTES_CAJA = 14f;
    private static final float CAJA_ALTO = 90f;
    private static final float ESPACIO_ANTES_BOTONES = 18f;
    private static final float BOTON_ALTO = 46f;
    private static final float BOTON_GAP = 10f;
    private static final float PAD_BOTTOM = 24f;

    private static final float FILAS_ALTO_TOTAL = CANTIDAD_FILAS * FILA_ALTO + (CANTIDAD_FILAS - 1) * FILA_GAP;
    private static final float BOTONES_ALTO_TOTAL = 3 * BOTON_ALTO + 2 * BOTON_GAP;

    private static final float PANEL_H = PAD_TOP + TITULO_ALTO + ESPACIO_TRAS_TITULO
        + FILAS_ALTO_TOTAL + ESPACIO_ANTES_CAJA + CAJA_ALTO
        + ESPACIO_ANTES_BOTONES + BOTONES_ALTO_TOTAL + PAD_BOTTOM;

    private static final float PANEL_X = (1280f - PANEL_W) / 2f;
    private static final float PANEL_Y = (720f - PANEL_H) / 2f;

    private final Main game;
    private final Juego juego;
    private final Jugador jugador;
    private final DatosRival ultimoRival;
    private final Boton botonPartidaNueva;
    private final Boton botonInfinito;
    private final Boton botonMenu;
    private final Runnable alPartidaNueva;
    private final Runnable alInfinito;
    private final Runnable alMenu;

    public PanelVictoriaFinal(Main game, Juego juego, Jugador jugador, DatosRival ultimoRival,
                              Runnable alPartidaNueva, Runnable alInfinito, Runnable alMenu) {
        this.game = game;
        this.juego = juego;
        this.jugador = jugador;
        this.ultimoRival = ultimoRival;
        this.alPartidaNueva = alPartidaNueva;
        this.alInfinito = alInfinito;
        this.alMenu = alMenu;

        float botonW = 260f;
        float x = PANEL_X + PANEL_W / 2f - botonW / 2f;
        float yMenu = PANEL_Y + PAD_BOTTOM;
        float yPartida = yMenu + BOTON_ALTO + BOTON_GAP;
        float yInfinito = yPartida + BOTON_ALTO + BOTON_GAP;

        botonInfinito = new Boton(x, yInfinito, botonW, BOTON_ALTO, "MODO INFINITO", Boton.TipoColor.ROJO, Accion.IR_AL_MAZO);
        botonPartidaNueva = new Boton(x, yPartida, botonW, BOTON_ALTO, "PARTIDA NUEVA", Boton.TipoColor.ROJO, Accion.IR_AL_MAZO);
        botonMenu = new Boton(x, yMenu, botonW, BOTON_ALTO, "MENU PRINCIPAL", Boton.TipoColor.ROJO_OSCURO, Accion.IR_AL_MAZO);
    }

    public void update(float mouseX, float mouseY) {
        botonInfinito.update(mouseX, mouseY);
        botonPartidaNueva.update(mouseX, mouseY);
        botonMenu.update(mouseX, mouseY);
        if (botonInfinito.fueCliqueado(mouseX, mouseY) && alInfinito != null) { alInfinito.run(); return; }
        if (botonPartidaNueva.fueCliqueado(mouseX, mouseY) && alPartidaNueva != null) { alPartidaNueva.run(); return; }
        if (botonMenu.fueCliqueado(mouseX, mouseY) && alMenu != null) { alMenu.run(); }
    }

    public void render(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();

        batch.setColor(0f, 0f, 0f, 0.78f);
        batch.draw(pixel, 0, 0, 1280, 720);

        dibujarRectRedondeado(batch, pixel, PANEL_X - 6f, PANEL_Y - 10f, PANEL_W + 12f, PANEL_H + 12f, 12f, UITheme.SOMBRA);
        dibujarRectRedondeado(batch, pixel, PANEL_X - 4f, PANEL_Y - 4f, PANEL_W + 8f, PANEL_H + 8f, 13f, UITheme.BORDE);
        dibujarRectRedondeado(batch, pixel, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 10f, UITheme.PANEL_PRINCIPAL);

        BitmapFont f = game.getFuenteTitulo();
        float cursorY = PANEL_Y + PANEL_H - PAD_TOP;

        f.getData().setScale(0.75f);
        f.setColor(UITheme.DORADO);
        f.draw(batch, "¡VICTORIA!", PANEL_X, cursorY, PANEL_W, Align.center, false);
        f.setColor(Color.WHITE);
        cursorY -= TITULO_ALTO + ESPACIO_TRAS_TITULO;

        cursorY = dibujarFila(batch, f, "Cartas jugadas", String.valueOf(juego.getCartasJugadasTotal()), cursorY);
        cursorY -= FILA_GAP;
        cursorY = dibujarFila(batch, f, "Cartas descartadas", String.valueOf(juego.getCartasDescartadasTotal()), cursorY);
        cursorY -= FILA_GAP;
        cursorY = dibujarFila(batch, f, "Cartas compradas", String.valueOf(juego.getCartasCompradasTotal()), cursorY);
        cursorY -= FILA_GAP;
        cursorY = dibujarFila(batch, f, "Dinero ganado", "$" + (int) jugador.getTotalPesosGanados(), cursorY);
        cursorY -= FILA_GAP;
        cursorY = dibujarFila(batch, f, "Dinero gastado", "$" + (int) jugador.getTotalPesosGastados(), cursorY);
        cursorY -= ESPACIO_ANTES_CAJA;

        float cajaTop = cursorY;
        dibujarRectRedondeado(batch, pixel, PANEL_X + 30f, cajaTop - CAJA_ALTO, PANEL_W - 60f, CAJA_ALTO, 8f, UITheme.PANEL_SECUNDARIO);

        f.getData().setScale(0.5f);
        f.setColor(UITheme.TEXTO_SECUNDARIO);
        f.draw(batch, "ÚLTIMO RIVAL DERROTADO", PANEL_X, cajaTop - 22f, PANEL_W, Align.center, false);

        f.getData().setScale(0.85f);
        f.setColor(UITheme.DORADO);
        String nombre = ultimoRival != null ? ultimoRival.getNombre() : "???";
        f.draw(batch, nombre, PANEL_X, cajaTop - 54f, PANEL_W, Align.center, false);

        f.getData().setScale(1f);
        f.setColor(Color.WHITE);

        botonInfinito.render(batch);
        botonPartidaNueva.render(batch);
        botonMenu.render(batch);
        batch.setColor(Color.WHITE);
    }

    private float dibujarFila(SpriteBatch batch, BitmapFont f, String label, String valor, float y) {
        Texture pixel = game.getPixelBlanco();
        float x = PANEL_X + 24f;
        float w = PANEL_W - 48f;
        dibujarRectRedondeado(batch, pixel, x, y - FILA_ALTO, w, FILA_ALTO, 6f, UITheme.PANEL_SECUNDARIO);

        f.getData().setScale(0.55f);
        f.setColor(UITheme.TEXTO_PRINCIPAL);
        f.draw(batch, label, x + 16f, y - FILA_ALTO / 2f + 7f);

        GlyphLayout gl = new GlyphLayout(f, valor);
        f.setColor(UITheme.DORADO);
        f.draw(batch, valor, x + w - gl.width - 16f, y - FILA_ALTO / 2f + 7f);

        f.getData().setScale(1f);
        f.setColor(Color.WHITE);
        return y - FILA_ALTO;
    }

    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel, float x, float y, float width, float height, float radio, Color color) {
        if (width <= 0f || height <= 0f) return;
        radio = Math.min(radio, Math.min(width, height) / 2f);
        batch.setColor(color);
        batch.draw(pixel, x + radio, y, width - radio * 2f, height);
        batch.draw(pixel, x, y + radio, radio, height - radio * 2f);
        batch.draw(pixel, x + width - radio, y + radio, radio, height - radio * 2f);
        int pasos = Math.max(2, (int) radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            batch.draw(pixel, x + inset, y + i, width - inset * 2f, 1f);
            batch.draw(pixel, x + inset, y + height - i - 1f, width - inset * 2f, 1f);
        }
        batch.setColor(Color.WHITE);
    }
}
