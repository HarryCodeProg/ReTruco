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
    private static final Color NEGRO_IZQ = new Color(0.04f, 0.05f, 0.06f, 1f);
    private static final Color NEGRO_IZQ_S = new Color(0.02f, 0.02f, 0.03f, 1f);
    private static final Color GRIS_AZULADO = new Color(0.24f, 0.31f, 0.40f, 1f);
    private static final Color GRIS_AZULADO_S = new Color(0.12f, 0.15f, 0.20f, 1f);
    private static final Color AZUL_PUNTOS = new Color(0.22f, 0.28f, 0.35f, 1f);
    private static final Color AZUL_PUNTOS_S = new Color(0.11f, 0.14f, 0.18f, 1f);
    private static final Color MARRON_AREA = new Color(0.45f, 0.25f, 0.05f, 1f);
    private static final Color MARRON_AREA_S = new Color(0.22f, 0.12f, 0.02f, 1f);
    private static final Color OLIVA_PUNTOS = new Color(0.35f, 0.3f, 0.2f, 1f);
    private static final Color OLIVA_PUNTOS_S = new Color(0.17f, 0.15f, 0.1f, 1f);
    private static final Color VERDE_OSCURO = new Color(0.18f, 0.32f, 0.22f, 1f);
    private static final Color VERDE_OSCURO_S = new Color(0.09f, 0.16f, 0.11f, 1f);
    private static final Color MARRON_OSCURO = new Color(0.38f, 0.25f, 0.1f, 1f);
    private static final Color MARRON_OSCURO_S = new Color(0.19f, 0.12f, 0.05f, 1f);
    private static final Color GRIS_OSCURO_DER = new Color(0.15f, 0.16f, 0.18f, 1f);
    private static final Color GRIS_OSCURO_DER_S = new Color(0.07f, 0.08f, 0.09f, 1f);
    private static final Color ORO = new Color(0.92f, 0.73f, 0.25f, 1f);
    private static final Color FONDO_PANEL = new Color(0.08f, 0.11f, 0.14f, 1f);

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
        // =========================================================
        // FONDO GENERAL LISO
        // =========================================================
        shapeRenderer.setColor(FONDO_PANEL);
        dibujarRectanguloRedondeado(fondoX, 0f, anchoFondo, altoFondo, RADIO_ESQUINA * 2f);
        // Linea dorada en el borde derecho
        shapeRenderer.setColor(new Color(0.85f, 0.65f, 0.2f, 1f));
        shapeRenderer.rect(fondoX + anchoFondo - 2f, 0f, 2f, altoFondo);
        
        // =========================================================
        // POSICIONES EXACTAS DE LAS FILAS
        // =========================================================
        float currentY = y + 70f;
        float rivalTrucoY = currentY;
        float rivalEnvidoY = rivalTrucoY - ESPACIO_LINEA;
        float puntosRivalY = rivalEnvidoY - ESPACIO_LINEA;
        float metaY = puntosRivalY - ESPACIO_LINEA;
        float puntosJugadorY = metaY - ESPACIO_LINEA;
        float jugadorTrucoY = puntosJugadorY - ESPACIO_LINEA;
        float jugadorEnvidoY = jugadorTrucoY - ESPACIO_LINEA;
        float manosY = jugadorEnvidoY - ESPACIO_LINEA;
        float descartesY = manosY - ESPACIO_LINEA;
        float pesosY = descartesY - ESPACIO_LINEA - 5f;

        // CAJAS
        // Rival
        dibujarCajaBaseYMultiplicador(x, rivalTrucoY, NEGRO_IZQ, NEGRO_IZQ_S, GRIS_AZULADO, GRIS_AZULADO_S);
        dibujarCajaBaseYMultiplicador(x, rivalEnvidoY, NEGRO_IZQ, NEGRO_IZQ_S, GRIS_AZULADO, GRIS_AZULADO_S);
        dibujarCajaSimple(x, puntosRivalY, AZUL_PUNTOS, AZUL_PUNTOS_S);
        // META Y LIGA
        dibujarCajaSimple(x, metaY, MARRON_AREA, MARRON_AREA_S);
        dibujarCajaSimple(x, puntosJugadorY, OLIVA_PUNTOS, OLIVA_PUNTOS_S);
        // Jugador
        dibujarCajaBaseYMultiplicador(x, jugadorTrucoY, VERDE_OSCURO, VERDE_OSCURO_S, MARRON_OSCURO, MARRON_OSCURO_S);
        dibujarCajaBaseYMultiplicador(x, jugadorEnvidoY, MARRON_OSCURO, MARRON_OSCURO_S, GRIS_OSCURO_DER, GRIS_OSCURO_DER_S);
        dibujarCajaSimple(x, manosY, VERDE_OSCURO, VERDE_OSCURO_S);
        dibujarCajaSimple(x, descartesY, MARRON_OSCURO, MARRON_OSCURO_S);
        // Borde dorado para el dinero
        dibujarCajaConBorde(x, pesosY, new Color(0.9f, 0.7f, 0.2f, 1f), new Color(0.5f, 0.3f, 0.05f, 1f), new Color(0.1f, 0.1f, 0.1f, 1f));
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
            float pesosY = currentY - 5f;
            String textoPesos = "$" + jugador.getPesos();
            fuente.setColor(Color.WHITE);
            if (iconoPeso != null) {
                batch.draw(iconoPeso, x + 8, pesosY + 2, 30, 30);
                dibujarTextoCentrado(batch, fuente, textoPesos, x + 25, anchoCajaDoble - 25, pesosY, Color.WHITE);
            } else {
                dibujarTextoCentrado(batch, fuente, textoPesos, x, anchoCajaDoble, pesosY, Color.WHITE);
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

    private void dibujarCajaBaseYMultiplicador(float x, float y, Color f1, Color s1, Color f2, Color s2) {
        shapeRenderer.setColor(s1);
        dibujarRectanguloRedondeado(x, y - 3, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(f1);
        dibujarRectanguloRedondeado(x, y, ANCHO_CAJA_BASE, ALTO_CAJA, RADIO_ESQUINA);
        float xCajaRoja = x + ANCHO_CAJA_BASE + ESPACIO_X;
        shapeRenderer.setColor(s2);
        dibujarRectanguloRedondeado(xCajaRoja, y - 3, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA);
        shapeRenderer.setColor(f2);
        dibujarRectanguloRedondeado(xCajaRoja, y, ANCHO_CAJA_MULT, ALTO_CAJA, RADIO_ESQUINA);
    }
    
    private void dibujarCajaConBorde(float x, float y, Color borde, Color bordeOscuro, Color fondo) {
        float anchoTotal = ANCHO_CAJA_BASE + ESPACIO_X + ANCHO_CAJA_MULT;
        shapeRenderer.setColor(bordeOscuro);
        dibujarRectanguloRedondeado(x - 2f, y - 5f, anchoTotal + 4f, ALTO_CAJA + 4f, RADIO_ESQUINA);
        shapeRenderer.setColor(borde);
        dibujarRectanguloRedondeado(x - 2f, y - 2f, anchoTotal + 4f, ALTO_CAJA + 4f, RADIO_ESQUINA);
        shapeRenderer.setColor(fondo);
        dibujarRectanguloRedondeado(x, y, anchoTotal, ALTO_CAJA, RADIO_ESQUINA - 1f);
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
