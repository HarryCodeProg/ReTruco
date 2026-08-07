package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorAnimacionResolucion;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.Juego;

public class PanelPuntajes {
    // Colores del estilo Balatro
    private static final Color ROJO = new Color(1f, 0.25f, 0.2f, 1f);
    private static final Color ROJO_SOMBRA = new Color(0.7f, 0.15f, 0.1f, 1f);
    private static final Color BLANCO = new Color(0.85f, 0.85f, 0.85f, 1f);
    private static final Color BLANCO_SOMBRA = new Color(0.35f, 0.35f, 0.35f, 1f);
    private static final Color AMARILLO = new Color(0.85f, 0.65f, 0.1f, 1f);
    private static final Color AMARILLO_SOMBRA = new Color(0.35f, 0.23f, 0.02f, 1f);
    private static final Color NEGRO = new Color(0.05f, 0.05f, 0.05f, 1f);
    private static final Color NEGRO_SOMBRA = new Color(0f, 0f, 0f, 1f);
    private static final Color TURQUESA = new Color(0.15f, 0.70f, 0.60f, 1f);
    private static final Color TURQUESA_SOMBRA = new Color(0.05f, 0.32f, 0.28f, 1f);
    private static final Color BRONCE = new Color(0.70f, 0.45f, 0.22f, 1f);
    private static final Color BRONCE_SOMBRA = new Color(0.38f, 0.22f, 0.08f, 1f);

    public static final float ESPACIO_LINEA = 45f;
    public static final float ALTO_CAJA = 36f;
    public static final float ANCHO_CAJA_BASE = 80f;
    public static final float ANCHO_CAJA_MULT = 80f;
    public static final float ANCHO_CAJA_SIMPLE = 90f;
    public static final float ESPACIO_X = 30f;
    private static final float RADIO_ESQUINA = 6f;

    private final ShapeRenderer shapeRenderer;
    private final GlyphLayout layout = new GlyphLayout();

    public PanelPuntajes() {
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * Dibuja EXCLUSIVAMENTE los fondos y las cajas geométricas.
     * DEBE llamarse mientras el SpriteBatch esté CERRADO.
     */
    public void renderFondosYCajas(OrthographicCamera camera, float x, float y) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        float margenX = 15f;
        float anchoFondo = (ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT) + (margenX * 2f);
        float altoFondo = Gdx.graphics.getHeight();
        float fondoX = x - margenX;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Fondo general del panel
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.4f));
        dibujarRectanguloRedondeado(fondoX + 4, 0f, anchoFondo, altoFondo, RADIO_ESQUINA * 2f);
        shapeRenderer.setColor(new Color(0.25f, 0.25f, 0.25f, 1f));
        dibujarRectanguloRedondeado(fondoX - 2, 0f, anchoFondo + 4, altoFondo, RADIO_ESQUINA * 2f);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.11f, 0.95f));
        dibujarRectanguloRedondeado(fondoX, 0f, anchoFondo, altoFondo, RADIO_ESQUINA * 2f);

        // 2. Cajas del panel por fila
        float currentY = y;
        dibujarCajaBaseYMultiplicador(x, currentY, NEGRO, NEGRO_SOMBRA); // Rival Truco
        currentY -= ESPACIO_LINEA;
        dibujarCajaBaseYMultiplicador(x, currentY, NEGRO, NEGRO_SOMBRA); // Rival Envido
        currentY -= ESPACIO_LINEA;
        dibujarCajaSimple(x, currentY, BLANCO, BLANCO_SOMBRA); // Puntos Rival
        currentY -= ESPACIO_LINEA;
        dibujarCajaSimple(x, currentY, AMARILLO, AMARILLO_SOMBRA); // Meta
        currentY -= ESPACIO_LINEA;
        dibujarCajaSimple(x, currentY, BLANCO, BLANCO_SOMBRA); // Puntos Jugador
        currentY -= ESPACIO_LINEA;
        dibujarCajaBaseYMultiplicador(x, currentY, TURQUESA, TURQUESA_SOMBRA); // Jugador Truco
        currentY -= ESPACIO_LINEA;
        dibujarCajaBaseYMultiplicador(x, currentY, BRONCE, BRONCE_SOMBRA); // Jugador Envido

        shapeRenderer.end();
    }

    public void renderTextos(SpriteBatch batch, BitmapFont fuente, Juego juego, Jugador jugador, Jugador rival,
                             float x, float y, GestorAnimacionResolucion gestorAnimacion,
                             double puntosTrucoDisplay, double multTrucoDisplay,
                             double puntosEnvidoDisplay, double multEnvidoDisplay) {

        // Aseguramos que el tinte global del batch no oscurezca las fuentes
        batch.setColor(Color.WHITE);

        float currentY = y;
        float xSeparador = x + ANCHO_CAJA_BASE;

        // Rival Truco
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) rival.getMultiplicadorTruco()), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;

        // Rival Envido
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) rival.getMultiplicadorEnvido()), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;

        // Puntos Rival (caja simple de ancho ANCHO_CAJA_SIMPLE + 100f)
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) juego.getPuntosRival()), x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;

        // Meta
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) juego.getPuntajeMeta()), x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;

        // Puntos Jugador
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) juego.getPuntosJugador()), x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;

        // Truco Jugador (Base X Mult)
        boolean animacionActiva = gestorAnimacion != null && gestorAnimacion.isActiva();
        double puntosTrucoAMostrar = animacionActiva ? puntosTrucoDisplay : 0;
        double multTrucoAMostrar = animacionActiva ? multTrucoDisplay : jugador.getMultiplicadorTruco();
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) puntosTrucoAMostrar), x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) multTrucoAMostrar), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;

        // Envido Jugador (Base X Mult)
        double puntosEnvidoAMostrar = animacionActiva ? puntosEnvidoDisplay : 0;
        double multEnvidoAMostrar = animacionActiva ? multEnvidoDisplay : jugador.getMultiplicadorEnvido();
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) puntosEnvidoAMostrar), x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) multEnvidoAMostrar), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
    }

    private void dibujarTextoCentrado(SpriteBatch batch, BitmapFont fuente, String texto, float x, float anchoCaja, float y, Color color) {
        // Garantizar escala legible por si viene reseteada a 0 en otra pantalla
        if (fuente.getScaleX() == 0 || fuente.getScaleY() == 0) {
            fuente.getData().setScale(1f);
        }

        fuente.setColor(color);
        layout.setText(fuente, texto);

        float xTexto = x + (anchoCaja - layout.width) / 2f;
        // Centrado vertical preciso
        float yTexto = y + (ALTO_CAJA + layout.height) / 2f;

        fuente.draw(batch, texto, xTexto, yTexto);
    }

    private void dibujarCajaSimple(float x, float y, Color colorFrente, Color colorSombra) {
        shapeRenderer.setColor(colorSombra);
        dibujarRectanguloRedondeado(x, y - 3, ANCHO_CAJA_SIMPLE + 100f, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(colorFrente);
        dibujarRectanguloRedondeado(x, y, ANCHO_CAJA_SIMPLE + 100f, ALTO_CAJA, RADIO_ESQUINA);
    }

    private void dibujarCajaBaseYMultiplicador(float x, float y, Color colorFrente, Color colorSombra) {
        shapeRenderer.setColor(colorSombra);
        dibujarRectanguloRedondeado(x, y - 3, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(colorFrente);
        dibujarRectanguloRedondeado(x, y, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA);

        float xCajaRoja = x + ANCHO_CAJA_BASE + ESPACIO_X;
        shapeRenderer.setColor(ROJO_SOMBRA);
        dibujarRectanguloRedondeado(xCajaRoja, y - 3, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(ROJO);
        dibujarRectanguloRedondeado(xCajaRoja, y, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA);
    }

    private void dibujarRectanguloRedondeado(float x, float y, float width, float height, float radius) {
        shapeRenderer.rect(x + radius, y, width - 2 * radius, height);
        shapeRenderer.rect(x, y + radius, width, height - 2 * radius);
        shapeRenderer.circle(x + radius, y + radius, radius);
        shapeRenderer.circle(x + width - radius, y + radius, radius); // Corregido: antes decía y + width - radius
        shapeRenderer.circle(x + radius, y + height - radius, radius);
        shapeRenderer.circle(x + width - radius, y + height - radius, radius);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
