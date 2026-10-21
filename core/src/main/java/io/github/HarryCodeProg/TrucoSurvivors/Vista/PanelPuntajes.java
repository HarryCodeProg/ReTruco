package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.FormateadorPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorAnimacionResolucion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class PanelPuntajes {
    private static final Color BORDE_PANEL = new Color(0.18f, 0.22f, 0.28f, 1f);
    private static final Color SEPARADOR_PANEL = new Color(0.35f, 0.40f, 0.48f, 0.55f);
    private static final Color FONDO_PANEL = new Color(0.08f, 0.11f, 0.14f, 1f);
    public static final float ESPACIO_LINEA = 60f;
    public static final float ALTO_CAJA = 34f;
    public static final float ANCHO_CAJA_BASE = 72f;
    public static final float ANCHO_CAJA_MULT = 68f;
    public static final float ESPACIO_X = 18f;
    private static final float RADIO_ESQUINA = 6f;
    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();
    private String rivalNombre = "";
    private static final String SPRITE_MONEDA_PATH = "ui/unpeso-spritesheet.png";
    private static final int FRAMES_MONEDA = 12;
    private static final float FPS_MONEDA = 12f;
    private static final float TAMANO_MONEDA = 30f;
    private Texture spriteMoneda;
    private Animation<TextureRegion> animacionMoneda;
    private float estadoAnimacionMoneda = 0f;
    private static final float ALTO_PANTALLA = 720f; // alto del mundo virtual, no de la ventana

    public PanelPuntajes() {
        if (Gdx.files.internal(SPRITE_MONEDA_PATH).exists()) {
            spriteMoneda = new Texture(Gdx.files.internal(SPRITE_MONEDA_PATH));
            int w = spriteMoneda.getWidth();
            int h = spriteMoneda.getHeight();
            int frameW = w / FRAMES_MONEDA;
            int frameH = h;
            TextureRegion[][] tmp = TextureRegion.split(spriteMoneda, frameW, frameH);
            TextureRegion[] frames = new TextureRegion[FRAMES_MONEDA];
            for (int i = 0; i < FRAMES_MONEDA; i++) frames[i] = tmp[0][i];
            animacionMoneda = new Animation<>(1f / FPS_MONEDA, frames);
            animacionMoneda.setPlayMode(Animation.PlayMode.LOOP);
        }
        // ───────────────────────────────────────────────────────

        shapeRenderer = new ShapeRenderer();
    }

    public void renderFondosYCajas(OrthographicCamera camera, float x, float y) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        float margenX = 15f;
        float anchoFondo = (ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT) + (margenX * 2f);
        //float altoFondo = Gdx.graphics.getHeight();
        float altoFondo = ALTO_PANTALLA;
        float fondoX = x - margenX;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.015f, 0.018f, 0.025f, 0.75f));
        dibujarRectanguloRedondeado(fondoX - 3f, -3f, anchoFondo + 6f, altoFondo + 6f, RADIO_ESQUINA + 2f);
        shapeRenderer.setColor(FONDO_PANEL);
        dibujarRectanguloRedondeado(fondoX, 0f, anchoFondo, altoFondo, RADIO_ESQUINA);
        shapeRenderer.setColor(BORDE_PANEL);
        dibujarRectanguloRedondeado(fondoX + 2f, 2f, anchoFondo - 4f, altoFondo - 4f, RADIO_ESQUINA - 1f);
        shapeRenderer.setColor(UITheme.DORADO);
        shapeRenderer.rect(fondoX + anchoFondo - 3f, 0f, 3f, altoFondo);

        float currentY = y + 70f;

        // ELIMINADO: rivalTrucoY y rivalEnvidoY
        float puntosRivalY = currentY;
        float metaY = puntosRivalY - ESPACIO_LINEA;
        float puntosJugadorY = metaY - ESPACIO_LINEA;
        float jugadorTrucoY = puntosJugadorY - ESPACIO_LINEA;
        float jugadorEnvidoY = jugadorTrucoY - ESPACIO_LINEA;
        float manosY = jugadorEnvidoY - ESPACIO_LINEA;
        float descartesY = manosY - ESPACIO_LINEA;
        float pesosY = descartesY - ESPACIO_LINEA - 5f;
        // BLOQUE RIVAL (Solo puntos)
        dibujarCajaSimple(x, puntosRivalY, UITheme.AZUL, UITheme.BORDE);
        dibujarSeparador(x, puntosRivalY - 11f, anchoFondo - margenX * 2f);
        // BLOQUE OBJETIVO
        dibujarCajaSimple(x, metaY, new Color(0.3f, 0.3f, 0.3f, 1f), UITheme.BORDE); // Fondo gris
        dibujarCajaSimple(x, puntosJugadorY, new Color(0.3f, 0.3f, 0.3f, 1f), UITheme.BORDE); // Fondo gris
        dibujarSeparador(x, puntosJugadorY - 11f, anchoFondo - margenX * 2f);
        // BLOQUE JUGADOR
        dibujarCajaBaseYMultiplicador(x, jugadorTrucoY, UITheme.TURQUESA, UITheme.BORDE, UITheme.ROJO, UITheme.BORDE);
        dibujarCajaBaseYMultiplicador(x, jugadorEnvidoY, UITheme.ENVIDO, UITheme.BORDE, UITheme.ROJO, UITheme.BORDE);
        dibujarCajaSimple(x, manosY, new Color(0.1f, 0.15f, 0.3f, 1f), UITheme.BORDE); // Fondo Azul Oscuro
        dibujarCajaSimple(x, descartesY, UITheme.ROJO, UITheme.BORDE);
        // DINERO
        dibujarCajaConBorde(x, pesosY, UITheme.DORADO, UITheme.BORDE, UITheme.PANEL_PRINCIPAL);
        shapeRenderer.end();
    }

    private void dibujarSeparador(float x, float y, float ancho) {
        shapeRenderer.setColor(new Color(0.02f, 0.025f, 0.035f, 0.9f));
        shapeRenderer.rect(x, y - 2f, ancho, 4f);
        shapeRenderer.setColor(SEPARADOR_PANEL);
        shapeRenderer.rect(x + 4f, y, ancho - 8f, 1f);
    }

    public void setRivalNombre(String nombre) {
        this.rivalNombre = (nombre == null) ? "" : nombre;
    }

    public void renderTextos(SpriteBatch batch, BitmapFont fuente, Juego juego, Jugador jugador, Jugador rival,
                             float x, float y, GestorAnimacionResolucion gestorAnimacion,
                             double puntosTrucoDisplay, double multTrucoDisplay,
                             double puntosEnvidoDisplay, double multEnvidoDisplay) {
        String nombreARender = (rival != null && rival.getNombre() != null && !rival.getNombre().isEmpty()) ? rival.getNombre() : this.rivalNombre;
        float anchoCajaDoble = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
        BitmapFont fuenteNumeros = Main.getInstance().getFuenteNumeros();
        BitmapFont fuenteUI = Main.getInstance().getFuenteUI();
        float escalaUI = fuenteUI.getScaleX();
        fuenteUI.getData().setScale(0.68f);
        fuenteUI.setColor(UITheme.DORADO);
        //dibujarTextoCentrado(batch, fuenteUI, "MARCADOR", x, anchoCajaDoble, Gdx.graphics.getHeight() - 31f, UITheme.DORADO);
        dibujarTextoCentrado(batch, fuenteUI, "MARCADOR", x, anchoCajaDoble, ALTO_PANTALLA - 31f, UITheme.DORADO);
        fuenteUI.getData().setScale(escalaUI);
        if (nombreARender != null && !nombreARender.isEmpty()) {
            float escalaOriginal = fuente.getScaleX();
            fuente.getData().setScale(escalaOriginal * 1.25f);
            fuente.setColor(UITheme.DORADO);
            GlyphLayout nameLayout = new GlyphLayout();
            nameLayout.setText(fuente, nombreARender);
            float nameX = x + (anchoCajaDoble - nameLayout.width) / 2f;
            //float nameY = Gdx.graphics.getHeight() - 56f;
            float nameY = ALTO_PANTALLA - 56f;
            fuente.draw(batch, nombreARender, nameX, nameY);
            fuente.getData().setScale(escalaOriginal);
        }
        batch.setColor(Color.WHITE);
        // Convertimos estas variables a double para no perder precisión
        double puntosRival = (juego != null) ? juego.getPuntosRival() : 0;
        double puntajeMeta = (juego != null) ? juego.getPuntajeMeta() : 0;
        double puntosJugador = (juego != null) ? juego.getPuntosJugador() : 0;
        float currentY = y + 70f;
        float xSeparador = x + ANCHO_CAJA_BASE;
        float anchoCajaSimple = anchoCajaDoble;
        // Puntos Rival
        dibujarEtiqueta(batch, "PUNTOS RIVAL", x, currentY, anchoCajaSimple);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(puntosRival), x, anchoCajaSimple, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Meta (Fondo gris, Números Rojos)
        dibujarEtiqueta(batch, "META", x, currentY, anchoCajaSimple);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(puntajeMeta), x, anchoCajaSimple, currentY, Color.RED);
        currentY -= ESPACIO_LINEA;
        // Puntos Jugador (Fondo gris, Números Blancos)
        dibujarEtiqueta(batch, "PUNTOS JUGADOR", x, currentY, anchoCajaSimple);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(puntosJugador), x, anchoCajaSimple, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Truco Jugador
        dibujarEtiqueta(batch, "TRUCO", x, currentY, anchoCajaDoble);
        boolean animacionActiva = gestorAnimacion != null && gestorAnimacion.isActiva();
        boolean animandoTruco = animacionActiva && (puntosTrucoDisplay != 0 || multTrucoDisplay != 1);
        boolean animandoEnvido = animacionActiva && (puntosEnvidoDisplay != 0 || multEnvidoDisplay != 1);
        double puntosTrucoAMostrar = animandoTruco ? puntosTrucoDisplay : 0;
        double multTrucoAMostrar = animandoTruco ? multTrucoDisplay : (jugador != null ? jugador.getMultiplicadorTruco() : 0);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(puntosTrucoAMostrar), x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(multTrucoAMostrar), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Envido Jugador
        dibujarEtiqueta(batch, "ENVIDO", x, currentY, anchoCajaDoble);
        double puntosEnvidoAMostrar = animacionActiva ? puntosEnvidoDisplay : 0;
        double multEnvidoAMostrar = animacionActiva ? multEnvidoDisplay : (jugador != null ? jugador.getMultiplicadorEnvido() : 0);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(puntosEnvidoAMostrar), x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, FormateadorPuntaje.formatear(multEnvidoAMostrar), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Manos
        dibujarEtiqueta(batch, "MANOS", x, currentY, anchoCajaSimple);
        int handsActuales = (juego != null) ? juego.getJugador().getManosActuales() : 0;
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(handsActuales), x, anchoCajaSimple, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Descartes
        dibujarEtiqueta(batch, "DESCARTES", x, currentY, anchoCajaSimple);
        int descartesActuales = (juego != null) ? juego.getDescartesActuales() : 0;
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(descartesActuales), x, anchoCajaSimple, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // ───moneda animada ──────────────────────────────
        if (jugador != null) {
            float pesosY = currentY - 5f;
            String textoPesos = "$" + FormateadorPuntaje.formatear(jugador.getPesos());
            fuente.setColor(UITheme.DORADO); // Por si acaso
            if (animacionMoneda != null) {
                estadoAnimacionMoneda += Gdx.graphics.getDeltaTime();
                TextureRegion frame = animacionMoneda.getKeyFrame(estadoAnimacionMoneda, true);
                batch.draw(frame, x + 8f, pesosY + 2f, TAMANO_MONEDA, TAMANO_MONEDA);
                // Cambiamos Color.WHITE por UITheme.DORADO
                dibujarTextoCentrado(batch, fuente, textoPesos, x + 25, anchoCajaDoble - 25, pesosY, UITheme.DORADO);
            } else {
                // Cambiamos Color.WHITE por UITheme.DORADO
                dibujarTextoCentrado(batch, fuente, textoPesos, x, anchoCajaDoble, pesosY, UITheme.DORADO);
            }
        }
    }

    private void dibujarTextoCentrado(SpriteBatch batch, BitmapFont fuente, String texto, float x, float anchoCaja, float y, Color color) {
        if (fuente.getScaleX() == 0 || fuente.getScaleY() == 0) {
            fuente.getData().setScale(1f);
        }
        fuente.setColor(color);
        float escalaBase = fuente.getScaleX();
        layout.setText(fuente, texto);
        float margen = 8f;
        float anchoDisponible = anchoCaja - margen;
        if (layout.width > anchoDisponible && anchoDisponible > 0) {
            float factor = anchoDisponible / layout.width;
            float escalaMinima = 0.45f;
            float nuevaEscala = Math.max(escalaBase * factor, escalaBase * escalaMinima);
            fuente.getData().setScale(nuevaEscala);
            layout.setText(fuente, texto);
        }
        float xTexto = x + (anchoCaja - layout.width) / 2f;
        float yTexto = y + (ALTO_CAJA + layout.height) / 2f;
        fuente.draw(batch, texto, xTexto, yTexto);
        fuente.getData().setScale(escalaBase);
    }

    private void dibujarCajaSimple(float x, float y, Color colorFrente, Color colorSombra) {
        float anchoTotal = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.45f));
        dibujarRectanguloRedondeado(x + 3f, y - 5f, anchoTotal, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(colorSombra);
        dibujarRectanguloRedondeado(x, y - 3f, anchoTotal, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(new Color(colorFrente.r * 0.82f, colorFrente.g * 0.82f, colorFrente.b * 0.82f, 1f));
        dibujarRectanguloRedondeado(x, y, anchoTotal, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(colorFrente);
        dibujarRectanguloRedondeado(x + 1f, y + 1f, anchoTotal - 2f, ALTO_CAJA - 2f, RADIO_ESQUINA);
    }

    private void dibujarCajaBaseYMultiplicador(float x, float y, Color f1, Color s1, Color f2, Color s2) {
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.45f));
        dibujarRectanguloRedondeado(x + 3f, y - 5f, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(s1);
        dibujarRectanguloRedondeado(x, y - 3f, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(f1);
        dibujarRectanguloRedondeado(x, y, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA);
        float xCajaRoja = x + ANCHO_CAJA_BASE + ESPACIO_X;
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.45f));
        dibujarRectanguloRedondeado(xCajaRoja + 3f, y - 5f, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(s2);
        dibujarRectanguloRedondeado(xCajaRoja, y - 3f, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(f2);
        dibujarRectanguloRedondeado(xCajaRoja, y, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA);
    }

    private void dibujarCajaConBorde(float x, float y, Color borde, Color bordeOscuro, Color fondo) {
        float anchoTotal = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.50f));
        dibujarRectanguloRedondeado(x - 1f, y - 6f, anchoTotal + 4f, ALTO_CAJA + 5f, RADIO_ESQUINA + 2f);
        shapeRenderer.setColor(bordeOscuro);
        dibujarRectanguloRedondeado(x - 2f, y - 4f, anchoTotal + 4f, ALTO_CAJA + 4f, RADIO_ESQUINA + 2f);
        shapeRenderer.setColor(borde);
        dibujarRectanguloRedondeado(x - 2f, y - 2f, anchoTotal + 4f, ALTO_CAJA + 4f, RADIO_ESQUINA + 1f);
        shapeRenderer.setColor(fondo);
        dibujarRectanguloRedondeado(x, y, anchoTotal, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(new Color(1f, 0.85f, 0.35f, 0.30f));
        shapeRenderer.rect(x + 8f, y + ALTO_CAJA - 3f, anchoTotal - 16f, 2f);
    }

    private void dibujarRectanguloRedondeado(float x, float y, float width, float height, float radius) {
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height);
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius);
        shapeRenderer.circle(x + radius, y + radius, radius);
        shapeRenderer.circle(x + width - radius, y + radius, radius);
        shapeRenderer.circle(x + radius, y + height - radius, radius);
        shapeRenderer.circle(x + width - radius, y + height - radius, radius);
    }

    private void dibujarEtiqueta(SpriteBatch batch, String texto, float x, float y, float ancho) {
        BitmapFont fuenteEtiqueta = Main.getInstance().getFuentePrincipal();
        float escalaOriginal = fuenteEtiqueta.getScaleX();
        fuenteEtiqueta.getData().setScale(escalaOriginal * 0.7f);
        fuenteEtiqueta.setColor(new Color(0.95f, 0.95f, 0.95f, 1f));
        layout.setText(fuenteEtiqueta, texto.toUpperCase());
        float xTexto = x + (ancho - layout.width) / 2f;
        float yTexto = y + ALTO_CAJA + 10f;
        fuenteEtiqueta.draw(batch, texto.toUpperCase(), xTexto, yTexto);
        fuenteEtiqueta.getData().setScale(escalaOriginal);
        fuenteEtiqueta.setColor(Color.WHITE);
    }

    public void dispose() {
        shapeRenderer.dispose();
        if (spriteMoneda != null) spriteMoneda.dispose();
    }
}
