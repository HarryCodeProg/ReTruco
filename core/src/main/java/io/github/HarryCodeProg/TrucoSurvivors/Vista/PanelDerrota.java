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

public class PanelDerrota {

    // --- Layout calculado por constantes: cada bloque suma su alto, nunca se solapan ---
    private static final float PANEL_W = 520f;
    private static final float PAD_TOP = 36f;
    private static final float TITULO_ALTO = 46f;
    private static final float ESPACIO_TRAS_TITULO = 20f;
    private static final float FILA_ALTO = 40f;
    private static final float FILA_GAP = 8f;
    private static final int CANTIDAD_FILAS = 5; // jugadas, descartadas, compradas, ganado, gastado
    private static final float ESPACIO_ANTES_CAJA = 18f;
    private static final float CAJA_ALTO = 130f;
    private static final float ESPACIO_ANTES_BOTONES = 24f;
    private static final float BOTON_ALTO = 52f;
    private static final float BOTON_GAP = 14f;
    private static final float PAD_BOTTOM = 30f;

    private static final float FILAS_ALTO_TOTAL = CANTIDAD_FILAS * FILA_ALTO + (CANTIDAD_FILAS - 1) * FILA_GAP;
    private static final float BOTONES_ALTO_TOTAL = 2 * BOTON_ALTO + BOTON_GAP;

    private static final float PANEL_H = PAD_TOP + TITULO_ALTO + ESPACIO_TRAS_TITULO
        + FILAS_ALTO_TOTAL + ESPACIO_ANTES_CAJA + CAJA_ALTO
        + ESPACIO_ANTES_BOTONES + BOTONES_ALTO_TOTAL + PAD_BOTTOM;

    private static final float PANEL_X = (1280f - PANEL_W) / 2f;
    private static final float PANEL_Y = (720f - PANEL_H) / 2f;

    private final Main game;
    private final Boton botonPartidaNueva;
    private final Boton botonMenuPrincipal;
    private final Runnable alPartidaNueva;
    private final Runnable alMenuPrincipal;
    private final Juego juego;
    private final Jugador jugador;
    private final DatosRival rivalDerrotado;

    public PanelDerrota(Main game, Juego juego, Jugador jugador, DatosRival rivalDerrotado,
                        Runnable alPartidaNueva, Runnable alMenuPrincipal) {
        this.game = game;
        this.juego = juego;
        this.jugador = jugador;
        this.rivalDerrotado = rivalDerrotado;
        this.alPartidaNueva = alPartidaNueva;
        this.alMenuPrincipal = alMenuPrincipal;

        float botonW = 240f;
        float centroX = PANEL_X + PANEL_W / 2f;
        // Posicionados de abajo hacia arriba, relativos a PANEL_Y, usando las mismas constantes del layout
        float yMenuPrincipal = PANEL_Y + PAD_BOTTOM;
        float yPartidaNueva = yMenuPrincipal + BOTON_ALTO + BOTON_GAP;

        botonPartidaNueva = new Boton(centroX - botonW / 2f, yPartidaNueva, botonW, BOTON_ALTO, "PARTIDA NUEVA", Boton.TipoColor.ROJO, Accion.IR_AL_MAZO);
        botonMenuPrincipal = new Boton(centroX - botonW / 2f, yMenuPrincipal, botonW, BOTON_ALTO, "MENU PRINCIPAL", Boton.TipoColor.ROJO_OSCURO, Accion.IR_AL_MAZO);
    }

    public void update(float mouseX, float mouseY) {
        botonPartidaNueva.update(mouseX, mouseY);
        botonMenuPrincipal.update(mouseX, mouseY);
        if (botonPartidaNueva.fueCliqueado(mouseX, mouseY) && alPartidaNueva != null) alPartidaNueva.run();
        if (botonMenuPrincipal.fueCliqueado(mouseX, mouseY) && alMenuPrincipal != null) alMenuPrincipal.run();
    }

    public void render(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();

        batch.setColor(0f, 0f, 0f, 0.78f);
        batch.draw(pixel, 0, 0, 1280, 720);

        dibujarRectRedondeado(batch, pixel, PANEL_X - 6f, PANEL_Y - 10f, PANEL_W + 12f, PANEL_H + 12f, 12f, UITheme.SOMBRA);
        dibujarRectRedondeado(batch, pixel, PANEL_X - 4f, PANEL_Y - 4f, PANEL_W + 8f, PANEL_H + 8f, 13f, UITheme.BORDE);
        dibujarRectRedondeado(batch, pixel, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 10f, UITheme.PANEL_PRINCIPAL);

        BitmapFont f = game.getFuenteTitulo(); // misma fuente para todo el panel, como pediste

        float cursorY = PANEL_Y + PANEL_H - PAD_TOP;

        // Título
        f.getData().setScale(0.75f);
        f.setColor(UITheme.ROJO);
        f.draw(batch, "FIN DE PARTIDA", PANEL_X, cursorY, PANEL_W, Align.center, false);
        f.setColor(Color.WHITE);
        cursorY -= TITULO_ALTO + ESPACIO_TRAS_TITULO;

        // Filas de estadísticas
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

        // Caja "Derrotada por"
        float cajaTop = cursorY;
        float cajaBottom = cajaTop - CAJA_ALTO;
        dibujarRectRedondeado(batch, pixel, PANEL_X + 30f, cajaBottom, PANEL_W - 60f, CAJA_ALTO, 8f, UITheme.PANEL_SECUNDARIO);

        f.getData().setScale(0.55f);
        f.setColor(UITheme.TEXTO_SECUNDARIO);
        f.draw(batch, "DERROTADA POR", PANEL_X, cajaTop - 30f, PANEL_W, Align.center, false);

        f.getData().setScale(0.95f);
        f.setColor(UITheme.ROJO);
        String nombreRival = rivalDerrotado != null ? rivalDerrotado.getNombre() : "???";
        f.draw(batch, nombreRival, PANEL_X, cajaTop - 75f, PANEL_W, Align.center, false);

        f.getData().setScale(1f);
        f.setColor(Color.WHITE);

        botonPartidaNueva.render(batch);
        botonMenuPrincipal.render(batch);
        batch.setColor(Color.WHITE);
    }

    private float dibujarFila(SpriteBatch batch, BitmapFont f, String label, String valor, float y) {
        Texture pixel = game.getPixelBlanco();
        float x = PANEL_X + 24f;
        float w = PANEL_W - 48f;
        dibujarRectRedondeado(batch, pixel, x, y - FILA_ALTO, w, FILA_ALTO, 6f, UITheme.PANEL_SECUNDARIO);

        f.getData().setScale(0.6f);
        f.setColor(UITheme.TEXTO_PRINCIPAL);
        f.draw(batch, label, x + 16f, y - FILA_ALTO / 2f + 8f);

        GlyphLayout gl = new GlyphLayout(f, valor);
        f.setColor(UITheme.DORADO);
        f.draw(batch, valor, x + w - gl.width - 16f, y - FILA_ALTO / 2f + 8f);

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
