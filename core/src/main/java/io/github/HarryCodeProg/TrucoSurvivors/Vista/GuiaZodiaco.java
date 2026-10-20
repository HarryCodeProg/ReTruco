package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.SignoZodiaco;

/** Guía consultable sin consumir ni volver a sortear un signo. */
public class GuiaZodiaco {
    private boolean abierta;
    private boolean hover;
    private boolean hoverVolver;
    // Coordenadas del panel centrado
    private static final float PANEL_W = 1100f;
    private static final float PANEL_H = 650f; // Más alto para que entre todo
    private static final float PANEL_X = (1280f - PANEL_W) / 2f; // = 90
    private static final float PANEL_Y = (720f - PANEL_H) / 2f;  // = 35
    // Coordenadas del botón Volver
    private static final float BTN_W = 200f;
    private static final float BTN_H = 50f;
    private static final float BTN_X = PANEL_X + (PANEL_W - BTN_W) / 2f;
    private static final float BTN_Y = PANEL_Y + 15f; // Más pegado al borde inferior

    public boolean estaAbierta() { return abierta; }

    public boolean update(float x, float y, boolean click) {
        hover = x >= 1080 && x <= 1270 && y >= 38 && y <= 78;
        if (abierta) {
            // Detectar hover del botón VOLVER
            hoverVolver = x >= BTN_X && x <= BTN_X + BTN_W && y >= BTN_Y && y <= BTN_Y + BTN_H;
            // Cerrar con Esc o click en Volver
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || (click && hoverVolver)) {
                abierta = false;
                return true;
            }
            return true;
        }

        if (hover && click) { abierta = true; return true; }
        return false;
    }

    public void renderResumen(SpriteBatch batch, Main game, RuedaZodiaco.Estado estado, SignoZodiaco resultado) {
        texto(batch, game, "ZODÍACO", 1080, 236, 190, 0.75f, 1, true);
        String mensaje = estado == RuedaZodiaco.Estado.SIN_GIRAR ? "Tocá la rueda para girar"
            : estado == RuedaZodiaco.Estado.GIRANDO ? "Tu destino está girando..."
            : estado == RuedaZodiaco.Estado.CONSUMIENDO ? "Signo consumido" : "Tocá la rueda para consumir";
        if (resultado != null) {
            texto(batch, game, resultado.getNombre(), 1080, 210, 190, 0.85f, 1, true);
            texto(batch, game, resultado.getDescripcion(), 1080, 182, 190, 0.7f, 1, false);
        }
        texto(batch, game, mensaje, 1080, 100, 190, 0.54f, 1, false);
        texto(batch, game, "Ver los 12 signos", 1080, 64, 190, 0.65f, hover ? 1 : 0.8f, true);
    }

    public void render(SpriteBatch batch, Main game) {
        if (!abierta) return;
        Texture pixel = game.getPixelBlanco();
        float packedColor = batch.getPackedColor();
        // 1. Fondo oscuro fullscreen
        batch.setColor(0.025f, 0.035f, 0.07f, 0.80f);
        batch.draw(pixel, 0, 0, 1280, 720);
        // 2. Fondo del panel centrado
        dibujarRectRedondeado(batch, pixel, PANEL_X - 4f, PANEL_Y - 4f, PANEL_W + 8f, PANEL_H + 8f, 16f, UITheme.BORDE);
        dibujarRectRedondeado(batch, pixel, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 14f, new Color(0.06f, 0.08f, 0.12f, 1f));
        // 3. Título y subtítulo
        texto(batch, game, "LOS DOCE SIGNOS", PANEL_X, PANEL_Y + PANEL_H - 40f, PANEL_W, 1.2f, 1, true);
        texto(batch, game, "Cada giro revela un signo. Leé su efecto y tocá la rueda para consumirlo.",
            PANEL_X, PANEL_Y + PANEL_H - 75f, PANEL_W, 0.65f, 1, false);
        // 4. Grilla de signos (3 columnas x 4 filas)
        SignoZodiaco[] signos = SignoZodiaco.values();
        float gridX = PANEL_X + 40f;
        float gridY = PANEL_Y + PANEL_H - 110f; // Arranca un poco más abajo del título
        float cellW = (PANEL_W - 80f) / 3f; // ~340px de ancho
        float cellH = 130f;
        for (int i = 0; i < signos.length && i < 12; i++) {
            int col = i % 3;
            int row = i / 3;
            float cx = gridX + col * cellW;
            float cy = gridY - row * cellH;
            SignoZodiaco s = signos[i];
            // Aro e Icono (alineados a la izquierda de la celda)
            aro(batch, game, cx + 35f, cy - 35f, 32f, 0f, 0.28f);
            TextureRegion region = buscarIconoZodiaco(game, s);
            if (region != null) {
                batch.setColor(Color.WHITE);
                batch.draw(region, cx + 10f, cy - 60f, 50f, 50f);
            }
            // Nombre (arriba a la derecha del icono)
            texto(batch, game, s.getNombre(), cx + 75f, cy + 10f, cellW - 85f, 0.8f, 1, true);
            // --- CAJA BLANCA PARA LA DESCRIPCIÓN ---
            float boxX = cx + 75f;
            float boxY = cy - 75f;
            float boxW = cellW - 85f;
            float boxH = 55f;
            // Borde negro exterior
            dibujarRectRedondeado(batch, pixel, boxX - 2f, boxY - 2f, boxW + 4f, boxH + 4f, 8f, UITheme.BORDE);
            // Fondo blanco
            dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 6f, Color.WHITE);
            // Texto negro adentro
            textoDescripcion(batch, game, s.getDescripcion(), boxX, boxY + boxH - 8f, boxW, 0.65f);
        }
        // 5. Botón VOLVER
        dibujarBotonVolver(batch, game);
        batch.setPackedColor(packedColor);
    }

    static void textoDescripcion(SpriteBatch batch, Main game, String texto, float x, float y, float ancho, float escala) {
        // Usamos la fuente de descripción de los tooltips (m6x11plus) que es muy legible en negro
        BitmapFont font = game.getFuenteTooltipDescripcion();
        float sx = font.getData().scaleX, sy = font.getData().scaleY;
        float color = font.getColor().toFloatBits();
        font.getData().setScale(escala);
        font.setColor(0.1f, 0.1f, 0.1f, 1f); // Negro casi puro, igual que la imagen
        font.draw(batch, texto, x, y, ancho, Align.center, true);
        font.getData().setScale(sx, sy);
        Color.abgr8888ToColor(font.getColor(), color);
    }

    static void texto(SpriteBatch batch, Main game, String texto, float x, float y,
                      float ancho, float escala, float alpha, boolean dorado) {
        textoConFuente(batch, game.getFuentePrincipal(), texto, x, y, ancho, escala, alpha, dorado);
    }

    static void textoPixel(SpriteBatch batch, Main game, String texto, float x, float y,
                           float ancho, float escala, float alpha, boolean dorado) {
        // Usa la fuente de tooltip (m6x11plus) que es mucho más legible en tamaños chicos
        textoConFuente(batch, game.getFuenteTooltipDescripcion(), texto, x, y, ancho, escala, alpha, dorado);
    }

    private static void textoConFuente(SpriteBatch batch, BitmapFont font, String texto, float x, float y,
                                       float ancho, float escala, float alpha, boolean dorado) {
        float sx = font.getData().scaleX, sy = font.getData().scaleY;
        float color = font.getColor().toFloatBits();
        font.getData().setScale(escala);

        // Si es el botón y está en hover, lo hacemos oscuro para que contraste con el fondo dorado
        if (!dorado && x == BTN_X) {
            font.setColor(0.1f, 0.1f, 0.1f, alpha);
        } else {
            font.setColor(dorado ? 0.96f : 0.87f, dorado ? 0.82f : 0.89f, dorado ? 0.48f : 0.95f, alpha);
        }

        font.draw(batch, texto, x, y, ancho, Align.center, true);
        font.getData().setScale(sx, sy);
        Color.abgr8888ToColor(font.getColor(), color);
    }

    private void dibujarBotonVolver(SpriteBatch batch, Main game) {
        Texture pixel = game.getPixelBlanco();
        // Sombra
        dibujarRectRedondeado(batch, pixel, BTN_X + 3f, BTN_Y - 4f, BTN_W, BTN_H, 12f, new Color(0f, 0f, 0f, 0.45f));
        // Borde
        dibujarRectRedondeado(batch, pixel, BTN_X, BTN_Y, BTN_W, BTN_H, 10f, UITheme.BORDE);
        // Fondo (Se ilumina si pasas el mouse)
        Color colorFondo = hoverVolver ? UITheme.DORADO_BRILLANTE : UITheme.PANEL_SECUNDARIO;
        dibujarRectRedondeado(batch, pixel, BTN_X + 2f, BTN_Y + 2f, BTN_W - 4f, BTN_H - 4f, 8f, colorFondo);

        texto(batch, game, "VOLVER", BTN_X, BTN_Y + BTN_H / 2f + 6f, BTN_W, 0.9f, 1, !hoverVolver);
    }

    private TextureRegion buscarIconoZodiaco(Main game, SignoZodiaco s) {
        TextureRegion r = game.getAtlasZodiaco().findRegion(s.getNombreRegion());
        if (r == null) r = game.getAtlasZodiaco().findRegion(s.name().toLowerCase());
        if (r == null && s.getNombreRegion().contains("_")) {
            String sinPrefijo = s.getNombreRegion().substring(s.getNombreRegion().indexOf('_') + 1);
            r = game.getAtlasZodiaco().findRegion(sinPrefijo);
        }
        return r;
    }

    static void aro(SpriteBatch batch, Main game, float x, float y, float radio, float giro, float alpha) {
        batch.setColor(0.95f, 0.78f, 0.4f, alpha);
        for (int i = 0; i < 96; i++) {
            float a = giro + i * 360f / 96;
            float b = giro + (i + 1) * 360f / 96;
            float px = x + MathUtils.cosDeg(a) * radio, py = y + MathUtils.sinDeg(a) * radio;
            float dx = (MathUtils.cosDeg(b) - MathUtils.cosDeg(a)) * radio;
            float dy = (MathUtils.sinDeg(b) - MathUtils.sinDeg(a)) * radio;
            batch.draw(game.getPixelBlanco(), px, py, 0, 0.6f,
                (float)Math.sqrt(dx * dx + dy * dy), 1.2f, 1, 1,
                MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees, 0, 0, 1, 1, false, false);
        }
    }

    private void dibujarRectRedondeado(SpriteBatch batch, Texture pixel,
                                       float x, float y, float w, float h,
                                       float radio, Color color) {
        if (w <= 0f || h <= 0f) return;
        radio = Math.min(radio, Math.min(w, h) / 2f);
        batch.setColor(color);
        batch.draw(pixel, x + radio, y, w - radio * 2f, h);
        batch.draw(pixel, x, y + radio, radio, h - radio * 2f);
        batch.draw(pixel, x + w - radio, y + radio, radio, h - radio * 2f);
        int pasos = Math.max(2, (int) radio);
        for (int i = 0; i < pasos; i++) {
            float dy = i + 0.5f;
            float distancia = radio - dy;
            float raiz = (float) Math.sqrt(Math.max(0f, radio * radio - distancia * distancia));
            float inset = radio - raiz;
            float rowWidth = raiz;
            batch.draw(pixel, x + inset, y + h - i - 1f, rowWidth, 1f);
            batch.draw(pixel, x + w - radio, y + h - i - 1f, rowWidth, 1f);
            batch.draw(pixel, x + inset, y + i, rowWidth, 1f);
            batch.draw(pixel, x + w - radio, y + i, rowWidth, 1f);
        }
        batch.setColor(Color.WHITE);
    }
}
