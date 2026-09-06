package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import io.github.HarryCodeProg.TrucoSurvivors.Jugador;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;

/**
 * Encapsula todo el estado, lógica de actualización y renderizado del panel de Victoria.
 * Se encarga del slide-in, slide-out, el conteo animado de monedas y el botón continuar.
 */
public class GestorVisualVictoria {
    private final Main game;
    private final Boton botonContinuarVictoria;
    private Texture iconoPesoVictoria;

    // Estado y variables extraídas de GameScreenV2
    private int pesosVictoria = 0;
    private int pesosInteres = 0;
    private int pesosExtras = 0;
    private int pesosVictoriaMostrados = 0;
    private float victoriaY = -720f;
    private float victoriaObjetivoY = 0f;
    private boolean victoriaEntrando = false;
    private boolean victoriaSaliendo = false;
    private final float victoriaVelocidad = 900f;
    private float cronometroMoneda = 0f;
    private static final float DELAY_MONEDA = 0.045f;
    private boolean conteoMonedasTerminado = false;
    private float iconoMonedaEscala = 1f;

    // Callback para notificar a GameScreenV2 cuando el panel termina de salir
    private Runnable alTerminarSalida;

    public GestorVisualVictoria(Main game, Runnable alTerminarSalida) {
        this.game = game;
        this.alTerminarSalida = alTerminarSalida;
        this.botonContinuarVictoria = new Boton(1280 / 2f - 100f, 55f, 200f, 55f, "CONTINUAR", Accion.CONTINUAR_TIENDA);

        if (Gdx.files.internal("ui/peso.png").exists()) {
            this.iconoPesoVictoria = new Texture("ui/peso.png");
        }
    }

    /** Prepara los números antes de iniciar la animación de entrada. */
    public void preparar(int pesosVictoria, int pesosInteres) {
        this.pesosVictoria = pesosVictoria;
        this.pesosInteres = pesosInteres;
        this.pesosExtras = 0;
        this.pesosVictoriaMostrados = 0;
        this.conteoMonedasTerminado = false;
        this.cronometroMoneda = 0f;
    }

    /** Inicia la animación de slide-in. */
    public void iniciarEntrada() {
        victoriaY = -720f;
        victoriaObjetivoY = 0f;
        victoriaEntrando = true;
        victoriaSaliendo = false;
        conteoMonedasTerminado = (totalVictoria() <= 0);
        cronometroMoneda = 0f;
    }

    /** Inicia la animación de slide-out. */
    public void iniciarSalida() {
        if (victoriaEntrando || victoriaSaliendo) return;
        victoriaSaliendo = true;
    }

    /** Suma pesos extras (de jokers) al total de la victoria. */
    public void sumarPesosExtras(int cantidad) {
        this.pesosExtras += cantidad;
    }

    private int totalVictoria() {
        return pesosVictoria + pesosInteres + pesosExtras;
    }

    /** Maneja la lógica de slide, conteo de monedas e inputs del botón. */
    public void update(float delta, Vector3 mouseWorld, boolean modalBloqueante) {
        // 1. Lógica de transiciones
        if (victoriaEntrando) {
            victoriaY += victoriaVelocidad * delta;
            if (victoriaY >= victoriaObjetivoY) {
                victoriaY = victoriaObjetivoY;
                victoriaEntrando = false;
            }
        }

        if (victoriaSaliendo) {
            victoriaY -= victoriaVelocidad * delta;
            if (victoriaY <= -720f) {
                victoriaY = -720f;
                victoriaSaliendo = false;
                if (alTerminarSalida != null) alTerminarSalida.run();
            }
        }

        // 2. Lógica de conteo animado de monedas (solo si no estamos transicionando)
        if (!victoriaEntrando && !victoriaSaliendo && !conteoMonedasTerminado) {
            cronometroMoneda += delta;
            if (cronometroMoneda >= DELAY_MONEDA) {
                cronometroMoneda = 0f;
                int total = totalVictoria();
                if (pesosVictoriaMostrados < total) {
                    pesosVictoriaMostrados++;
                    iconoMonedaEscala = 1.4f; // pulso
                    GestorSonidos sonidos = game.getGestorSonidos();
                    if (sonidos != null) sonidos.reproducirSonidoGanarPeso();
                }
                if (pesosVictoriaMostrados >= total) {
                    conteoMonedasTerminado = true;
                    GestorSonidos sonidos = game.getGestorSonidos();
                    if (sonidos != null) sonidos.reproducirSonidoFinalGanancia(total);
                }
            }
        }

        // Decaimiento del pulso del icono
        if (iconoMonedaEscala > 1f) {
            iconoMonedaEscala -= delta * 3f;
            if (iconoMonedaEscala < 1f) iconoMonedaEscala = 1f;
        }

        // 3. Inputs del Botón
        if (!modalBloqueante && botonContinuarVictoria != null) {
            // Compensamos el slide para el hit-testing del botón
            float mundoX = mouseWorld.x;
            float mundoY = mouseWorld.y - victoriaY;

            botonContinuarVictoria.update(mundoX, mundoY);

            if (Gdx.input.justTouched()) {
                if (!conteoMonedasTerminado) {
                    // Click salta el conteo instantáneamente
                    pesosVictoriaMostrados = totalVictoria();
                    conteoMonedasTerminado = true;
                    GestorSonidos sonidos = game.getGestorSonidos();
                    if (sonidos != null) sonidos.reproducirSonidoFinalGanancia(totalVictoria());
                } else if (botonContinuarVictoria.fueCliqueado(mundoX, mundoY)) {
                    iniciarSalida();
                }
            }
        }
    }

    /** Renderiza el panel completo con slide. */
    public void draw(SpriteBatch batch) {
        Matrix4 original = batch.getProjectionMatrix().cpy();
        // Aplicamos la matriz de transformación para el slide
        batch.setProjectionMatrix(original.cpy().translate(0, victoriaY, 0));
        dibujarPanelContenido(batch);
        batch.setProjectionMatrix(original); // Restauramos
    }

    private void dibujarPanelContenido(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float panelX = 340f, panelY = 30f, panelAncho = 600f, panelAlto = 650f;

        // Fondo oscuro
        batch.setColor(0.06f, 0.07f, 0.1f, 0.96f);
        batch.draw(pixel, panelX, panelY, panelAncho, panelAlto);

        // Borde superior dorado
        batch.setColor(0.95f, 0.78f, 0.28f, 1f);
        batch.draw(pixel, panelX, panelY + panelAlto - 6f, panelAncho, 6f);
        batch.setColor(Color.WHITE);

        // Título
        BitmapFont titulo = game.getFuenteTitulo();
        String textoTitulo = "¡VICTORIA!";
        GlyphLayout layout = new GlyphLayout(titulo, textoTitulo);
        titulo.setColor(Color.GOLD);
        titulo.draw(batch, textoTitulo, panelX + (panelAncho - layout.width) / 2f, panelY + panelAlto - 40f);
        titulo.setColor(Color.WHITE);

        // Filas detalladas
        BitmapFont fuente = game.getFuentePrincipal();
        float filaY = panelY + panelAlto - 130f;
        dibujarFilaDetalle(batch, fuente, "Victoria", pesosVictoria, panelX, filaY, panelAncho);

        filaY -= 50f;
        dibujarFilaDetalle(batch, fuente, "Interés", pesosInteres, panelX, filaY, panelAncho);

        if (pesosExtras > 0) {
            filaY -= 50f;
            dibujarFilaDetalle(batch, fuente, "Extra", pesosExtras, panelX, filaY, panelAncho);
        }

        // Total grande centrado con icono
        float totalY = panelY + 170f;
        String textoTotal = "$" + pesosVictoriaMostrados;
        BitmapFont fuenteNumeros = game.getFuenteNumeros();
        GlyphLayout layoutTotal = new GlyphLayout(fuenteNumeros, textoTotal);

        float iconoSize = 40f * iconoMonedaEscala;
        float anchoConjunto = layoutTotal.width + 10f + iconoSize;
        float xConjunto = panelX + (panelAncho - anchoConjunto) / 2f;

        if (iconoPesoVictoria != null) {
            batch.draw(iconoPesoVictoria, xConjunto, totalY - iconoSize / 2f, iconoSize, iconoSize);
        }

        fuenteNumeros.setColor(Color.GOLD);
        fuenteNumeros.draw(batch, textoTotal, xConjunto + iconoSize + 10f, totalY + layoutTotal.height / 2f);
        fuenteNumeros.setColor(Color.WHITE);

        // Botón Continuar
        if (botonContinuarVictoria != null) {
            botonContinuarVictoria.render(batch);
        }
    }

    private void dibujarFilaDetalle(SpriteBatch batch, BitmapFont fuente, String etiqueta, int valor, float panelX, float y, float panelAncho) {
        fuente.setColor(Color.LIGHT_GRAY);
        fuente.draw(batch, etiqueta, panelX + 60f, y);
        fuente.setColor(Color.WHITE);
        String textoValor = "+$" + valor;
        GlyphLayout layout = new GlyphLayout(fuente, textoValor);
        fuente.draw(batch, textoValor, panelX + panelAncho - 60f - layout.width, y);
    }

    public void dispose() {
        if (iconoPesoVictoria != null) iconoPesoVictoria.dispose();
    }
}
