package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    private String rivalNombre = "";
    private Texture iconoPeso;

    public PanelPuntajes() {
        if (Gdx.files.internal("ui/peso.png").exists()) {
            iconoPeso = new Texture("ui/peso.png");
        }
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
        currentY -= ESPACIO_LINEA;
        dibujarCajaSimple(x, currentY, ROJO, ROJO_SOMBRA);

        shapeRenderer.end();
    }

    public void setRivalNombre(String nombre) {
        this.rivalNombre = (nombre == null) ? "" : nombre;
    }

    public void renderTextos(SpriteBatch batch, BitmapFont fuente, Juego juego, Jugador jugador, Jugador rival,
                             float x, float y, GestorAnimacionResolucion gestorAnimacion,
                             double puntosTrucoDisplay, double multTrucoDisplay,
                             double puntosEnvidoDisplay, double multEnvidoDisplay) {
        String nombreARender = (rival != null && rival.getNombre() != null && !rival.getNombre().isEmpty())
            ? rival.getNombre()
            : this.rivalNombre;
        if (nombreARender != null && !nombreARender.isEmpty()) {
            float anchoPanelTotal = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
            float escalaOriginal = fuente.getScaleX();
            fuente.getData().setScale(escalaOriginal * 1.4f); // más grande
            fuente.setColor(Color.WHITE);
            GlyphLayout nameLayout = new GlyphLayout();
            nameLayout.setText(fuente, nombreARender);
            float nameX = x + (anchoPanelTotal - nameLayout.width) / 2f; // centrado en el ancho del panel
            float nameY = y + ALTO_CAJA + 55f; // mas arriba, por encima de toda la primera fila
            fuente.draw(batch, nombreARender, nameX, nameY);
            fuente.getData().setScale(escalaOriginal); // restaurar escala original para el resto del texto
        }
        // Aseguramos que el tinte global del batch no oscurezca las fuentes
        batch.setColor(Color.WHITE);
        // defensas contra juego == null
        int puntosRival = (juego != null) ? (int) juego.getPuntosRival() : 0;
        int puntajeMeta = (juego != null) ? (int) juego.getPuntajeMeta() : 0;
        int puntosJugador = (juego != null) ? (int) juego.getPuntosJugador() : 0;
        float currentY = y;
        float xSeparador = x + ANCHO_CAJA_BASE;
        // Rival Truco
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) (rival != null ? rival.getMultiplicadorTruco() : 0)),
            xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Rival Envido
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) (rival != null ? rival.getMultiplicadorEnvido() : 0)),
            xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Puntos Rival
        dibujarTextoCentrado(batch, fuente, String.valueOf(puntosRival),
            x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Meta
        dibujarTextoCentrado(batch, fuente, String.valueOf(puntajeMeta),
            x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Puntos Jugador
        dibujarTextoCentrado(batch, fuente, String.valueOf(puntosJugador),
            x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Truco Jugador (Base X Mult)
        boolean animacionActiva = gestorAnimacion != null && gestorAnimacion.isActiva();
        double puntosTrucoAMostrar = animacionActiva ? puntosTrucoDisplay : 0;
        double multTrucoAMostrar = animacionActiva ? multTrucoDisplay : (jugador != null ? jugador.getMultiplicadorTruco() : 0);
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) puntosTrucoAMostrar),
            x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) multTrucoAMostrar),
            xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Envido Jugador (Base X Mult)
        double puntosEnvidoAMostrar = animacionActiva ? puntosEnvidoDisplay : 0;
        double multEnvidoAMostrar = animacionActiva ? multEnvidoDisplay : (jugador != null ? jugador.getMultiplicadorEnvido() : 0);
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) puntosEnvidoAMostrar),
            x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuente, String.valueOf((int) multEnvidoAMostrar),
            xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        int descartesActuales = (juego != null) ? juego.getDescartesActuales() : 0;
        dibujarTextoCentrado(batch, fuente, String.valueOf(descartesActuales), x, ANCHO_CAJA_SIMPLE + 100f, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        if (jugador != null) {
            String textoPesos = "$" + jugador.getPesos();
            fuente.setColor(Color.WHITE);
            if (iconoPeso != null) {
                batch.draw(iconoPeso, x + 20, currentY - 5, 28, 28);
                fuente.draw(batch, textoPesos, x + 55, currentY + 18);
            } else {
                // Si no hay icono, centramos el texto en el ancho del panel
                float anchoPanelTotal = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
                dibujarTextoCentrado(batch, fuente, textoPesos, x, anchoPanelTotal, currentY, Color.GOLD);
            }
        }
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
        if (iconoPeso != null) iconoPeso.dispose();
    }
}
