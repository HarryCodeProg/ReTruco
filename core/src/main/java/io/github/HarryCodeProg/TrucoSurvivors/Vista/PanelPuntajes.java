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
import io.github.HarryCodeProg.TrucoSurvivors.Main;
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
    private static final Color AZUL_HANDS = new Color(0.12f, 0.25f, 0.45f, 1f);
    private static final Color AZUL_HANDS_SOMBRA = new Color(0.06f, 0.12f, 0.25f, 1f);
    private static final Color ORO = new Color(0.92f, 0.73f, 0.25f, 1f);
    private static final Color FONDO_PANEL = new Color(0.045f, 0.055f, 0.085f, 0.96f);

    // Ajuste de espaciado para que no se caiga de la pantalla
    public static final float ESPACIO_LINEA = 50f;
    public static final float ALTO_CAJA = 34f;
    public static final float ANCHO_CAJA_BASE = 72f;
    public static final float ANCHO_CAJA_MULT = 68f;
    public static final float ESPACIO_X = 18f;
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
        shapeRenderer.setColor(new Color(0.44f, 0.34f, 0.16f, 0.95f));
        dibujarRectanguloRedondeado(fondoX - 2, 0f, anchoFondo + 4, altoFondo, RADIO_ESQUINA * 2f);
        shapeRenderer.setColor(FONDO_PANEL);
        dibujarRectanguloRedondeado(fondoX, 0f, anchoFondo, altoFondo, RADIO_ESQUINA * 2f);
        shapeRenderer.setColor(new Color(0.92f, 0.73f, 0.25f, 0.85f));
        shapeRenderer.rect(fondoX + 12f, altoFondo - 34f, anchoFondo - 24f, 2f);
        // 2. Cajas del panel por fila (Subimos todo sumando 70f)
        float currentY = y + 70f;
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
        dibujarCajaSimple(x, currentY, AZUL_HANDS, AZUL_HANDS_SOMBRA); // Hands
        currentY -= ESPACIO_LINEA;
        dibujarCajaSimple(x, currentY, ROJO, ROJO_SOMBRA); // Descartes
        shapeRenderer.end();
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
        fuenteUI.setColor(ORO);
        dibujarTextoCentrado(batch, fuenteUI, "MARCADOR", x, anchoCajaDoble, Gdx.graphics.getHeight() - 31f, ORO);
        fuenteUI.getData().setScale(escalaUI);
        if (nombreARender != null && !nombreARender.isEmpty()) {
            float escalaOriginal = fuente.getScaleX();
            fuente.getData().setScale(escalaOriginal * 1.25f);
            fuente.setColor(Color.WHITE);
            GlyphLayout nameLayout = new GlyphLayout();
            nameLayout.setText(fuente, nombreARender);
            float nameX = x + (anchoCajaDoble - nameLayout.width) / 2f;
            float nameY = Gdx.graphics.getHeight() - 56f;
            fuente.draw(batch, nombreARender, nameX, nameY);
            fuente.getData().setScale(escalaOriginal);
        }
        batch.setColor(Color.WHITE);
        int puntosRival = (juego != null) ? (int) juego.getPuntosRival() : 0;
        int puntajeMeta = (juego != null) ? (int) juego.getPuntajeMeta() : 0;
        int puntosJugador = (juego != null) ? (int) juego.getPuntosJugador() : 0;
        // Aplicamos el mismo offset inicial para los textos
        float currentY = y + 70f;
        float xSeparador = x + ANCHO_CAJA_BASE;
        float anchoCajaSimple = anchoCajaDoble; // Homologamos el ancho
        // Rival Truco
        dibujarEtiqueta(batch, "TRUCO", x, currentY, anchoCajaDoble);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf((int) (rival != null ? rival.getMultiplicadorTruco() : 0)), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Rival Envido
        dibujarEtiqueta(batch, "ENVIDO", x, currentY, anchoCajaDoble);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf((int) (rival != null ? rival.getMultiplicadorEnvido() : 0)), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Puntos Rival
        dibujarEtiqueta(batch, "PUNTOS RIVAL", x, currentY, anchoCajaSimple);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(puntosRival), x, anchoCajaSimple, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Meta
        dibujarEtiqueta(batch, "META", x, currentY, anchoCajaSimple);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(puntajeMeta), x, anchoCajaSimple, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Puntos Jugador
        dibujarEtiqueta(batch, "PUNTOS JUGADOR", x, currentY, anchoCajaSimple);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(puntosJugador), x, anchoCajaSimple, currentY, Color.BLACK);
        currentY -= ESPACIO_LINEA;
        // Truco Jugador
        dibujarEtiqueta(batch, "TRUCO", x, currentY, anchoCajaDoble);
        boolean animacionActiva = gestorAnimacion != null && gestorAnimacion.isActiva();
        double puntosTrucoAMostrar = animacionActiva ? puntosTrucoDisplay : 0;
        double multTrucoAMostrar = animacionActiva ? multTrucoDisplay : (jugador != null ? jugador.getMultiplicadorTruco() : 0);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf((int) puntosTrucoAMostrar), x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf((int) multTrucoAMostrar), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Envido Jugador
        dibujarEtiqueta(batch, "ENVIDO", x, currentY, anchoCajaDoble);
        double puntosEnvidoAMostrar = animacionActiva ? puntosEnvidoDisplay : 0;
        double multEnvidoAMostrar = animacionActiva ? multEnvidoDisplay : (jugador != null ? jugador.getMultiplicadorEnvido() : 0);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf((int) puntosEnvidoAMostrar), x, ANCHO_CAJA_BASE, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, "X", xSeparador, ESPACIO_X, currentY, Color.WHITE);
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf((int) multEnvidoAMostrar), xSeparador + ESPACIO_X, ANCHO_CAJA_MULT, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Hands
        dibujarEtiqueta(batch, "MANOS", x, currentY, anchoCajaSimple);
        int handsActuales = (juego != null) ? juego.getJugador().getManosActuales() : 0;
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(handsActuales), x, anchoCajaSimple, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Descartes
        dibujarEtiqueta(batch, "DESCARTES", x, currentY, anchoCajaSimple);
        int descartesActuales = (juego != null) ? juego.getDescartesActuales() : 0;
        dibujarTextoCentrado(batch, fuenteNumeros, String.valueOf(descartesActuales), x, anchoCajaSimple, currentY, Color.WHITE);
        currentY -= ESPACIO_LINEA;
        // Pesos
        if (jugador != null) {
            String textoPesos = "$" + jugador.getPesos();
            fuente.setColor(Color.WHITE);
            if (iconoPeso != null) {
                batch.draw(iconoPeso, x + 20, currentY - 5, 28, 28);
                fuente.draw(batch, textoPesos, x + 55, currentY + 18);
            } else {
                dibujarTextoCentrado(batch, fuente, textoPesos, x, anchoCajaDoble, currentY, Color.GOLD);
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
        float margen = 8f; // pequeño padding para que no toque el borde de la caja
        float anchoDisponible = anchoCaja - margen;
        // FIX: si el texto no entra en la caja, escalar hacia abajo proporcionalmente (estilo Balatro)
        if (layout.width > anchoDisponible && anchoDisponible > 0) {
            float factor = anchoDisponible / layout.width;
            float escalaMinima = 0.45f; // no reducir más allá de esto, para que siga siendo legible
            float nuevaEscala = Math.max(escalaBase * factor, escalaBase * escalaMinima);
            fuente.getData().setScale(nuevaEscala);
            layout.setText(fuente, texto); // recalcular layout con la escala nueva
        }
        float xTexto = x + (anchoCaja - layout.width) / 2f;
        float yTexto = y + (ALTO_CAJA + layout.height) / 2f;
        fuente.draw(batch, texto, xTexto, yTexto);
        fuente.getData().setScale(escalaBase); // FIX: restaurar siempre la escala original para no afectar otros draws
    }

    private void dibujarCajaSimple(float x, float y, Color colorFrente, Color colorSombra) {
        float anchoTotal = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
        shapeRenderer.setColor(colorSombra);
        dibujarRectanguloRedondeado(x, y - 3, anchoTotal, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(colorFrente);
        dibujarRectanguloRedondeado(x, y, anchoTotal, ALTO_CAJA, RADIO_ESQUINA);
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
        shapeRenderer.circle(x + width - radius, y + radius, radius);
        shapeRenderer.circle(x + radius, y + height - radius, radius);
        shapeRenderer.circle(x + width - radius, y + height - radius, radius);
    }

    private void dibujarEtiqueta(SpriteBatch batch, String texto, float x, float y, float ancho) {
        BitmapFont fuenteUI = Main.getInstance().getFuenteUI();
        fuenteUI.getData().setScale(0.52f);
        fuenteUI.setColor(new Color(0.70f, 0.74f, 0.84f, 1f));
        layout.setText(fuenteUI, texto.toUpperCase());
        float xTexto = x + (ancho - layout.width) / 2f;
        // Se ajustó a +12f por la compresión del espaciado
        float yTexto = y + ALTO_CAJA + 11f;
        fuenteUI.draw(batch, texto.toUpperCase(), xTexto, yTexto);
        fuenteUI.getData().setScale(0.9f);
        fuenteUI.setColor(Color.WHITE);
    }

    public void dispose() {
        shapeRenderer.dispose();
        if (iconoPeso != null) iconoPeso.dispose();
    }
}
