package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;

import java.util.ArrayList;

public class GestorVisualVictoria {
    private final Main game;
    private final Boton botonCobrar;
    private Texture iconoPesoVictoria;

    private int pesosVictoria = 0;
    private int pesosManos = 0;
    private int pesosDescartes = 0;
    private int pesosInteres = 0;
    private int pesosExtras = 0;

    // Clase interna para guardar dinámicamente qué jokers dieron plata
    public static class RecompensaJoker {
        public String nombre;
        public int monto;
        public RecompensaJoker(String nombre, int monto) {
            this.nombre = nombre;
            this.monto = monto;
        }
    }
    private final ArrayList<RecompensaJoker> jokersActivados = new ArrayList<>();

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

    private Runnable alTerminarSalida;

    public GestorVisualVictoria(Main game, Runnable alTerminarSalida) {
        this.game = game;
        this.alTerminarSalida = alTerminarSalida;
        this.botonCobrar = new Boton(0f, 0f, 420f, 65f, "COBRAR", Boton.TipoColor.DORADO, Accion.CONTINUAR_TIENDA);

        if (Gdx.files.internal("ui/peso.png").exists()) {
            this.iconoPesoVictoria = new Texture("ui/peso.png");
        }
    }

    // Ahora preparamos TODA la info de base
    public void preparar(int pesosVictoria, int pesosManos, int pesosDescartes, int pesosInteres) {
        this.pesosVictoria = pesosVictoria;
        this.pesosManos = pesosManos;
        this.pesosDescartes = pesosDescartes;
        this.pesosInteres = pesosInteres;
        this.pesosExtras = 0;
        this.pesosVictoriaMostrados = 0;
        this.jokersActivados.clear();
        this.conteoMonedasTerminado = false;
        this.cronometroMoneda = 0f;
    }

    public void agregarRecompensaJoker(String nombre, int cantidad) {
        this.jokersActivados.add(new RecompensaJoker(nombre, cantidad));
    }

    public void sumarPesosExtras(int cantidad) {
        this.pesosExtras += cantidad;
    }

    private int totalVictoria() {
        int total = pesosVictoria + pesosManos + pesosDescartes + pesosInteres + pesosExtras;
        for (RecompensaJoker j : jokersActivados) {
            total += j.monto;
        }
        return total;
    }

    public void iniciarEntrada() {
        victoriaY = -720f;
        victoriaObjetivoY = 0f;
        victoriaEntrando = true;
        victoriaSaliendo = false;
        conteoMonedasTerminado = (totalVictoria() <= 0);
        cronometroMoneda = 0f;
    }

    public void iniciarSalida() {
        if (victoriaEntrando || victoriaSaliendo) return;
        victoriaSaliendo = true;
    }

    public void update(float delta, Vector3 mouseWorld, boolean modalBloqueante) {
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
        if (!victoriaEntrando && !victoriaSaliendo && !conteoMonedasTerminado) {
            cronometroMoneda += delta;
            if (cronometroMoneda >= DELAY_MONEDA) {
                cronometroMoneda = 0f;
                int total = totalVictoria();
                if (pesosVictoriaMostrados < total) {
                    pesosVictoriaMostrados++;
                    iconoMonedaEscala = 1.4f;
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
        if (iconoMonedaEscala > 1f) {
            iconoMonedaEscala -= delta * 3f;
            if (iconoMonedaEscala < 1f) iconoMonedaEscala = 1f;
        }
        botonCobrar.setTexto("Cobrar: $" + pesosVictoriaMostrados);
        if (!modalBloqueante && botonCobrar != null) {
            float mundoX = mouseWorld.x;
            float mundoY = mouseWorld.y - victoriaY;
            // El botón se actualiza a sí mismo leyendo el mouse
            botonCobrar.update(mundoX, mundoY);
            // 1. Si tocamos la pantalla mientras caen las monedas, salteamos la animación
            if (Gdx.input.justTouched() && !conteoMonedasTerminado) {
                pesosVictoriaMostrados = totalVictoria();
                conteoMonedasTerminado = true;
                GestorSonidos sonidos = game.getGestorSonidos();
                if (sonidos != null) sonidos.reproducirSonidoFinalGanancia(totalVictoria());
            }
            // 2. Si ya terminó de contar, escuchamos si el botón confirmó un click (al soltar)
            if (conteoMonedasTerminado && botonCobrar.consumirClick()) {
                iniciarSalida();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        Matrix4 original = batch.getProjectionMatrix().cpy();
        batch.setProjectionMatrix(original.cpy().translate(0, victoriaY, 0));
        dibujarPanelContenido(batch);
        batch.setProjectionMatrix(original);
    }

    private void dibujarPanelContenido(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        // 1. Calcular cuántas filas tenemos para que el panel crezca dinámicamente
        int cantidadFilas = 1; // Victoria (siempre está)
        if (pesosManos > 0) cantidadFilas++;
        if (pesosDescartes > 0) cantidadFilas++;
        if (pesosInteres > 0) cantidadFilas++;
        cantidadFilas += jokersActivados.size();
        if (pesosExtras > 0) cantidadFilas++;
        float altoFila = 35f;
        float panelAncho = 640f;
        // Panel base mínimo de 360f, se estira si hay más de 5 filas
        float panelAlto = Math.max(360f, 160f + (cantidadFilas * altoFila));
        float panelX = (1280f - panelAncho) / 2f;
        float panelY = (720f - panelAlto) / 2f;
        dibujarRectRedondeado(batch, pixel, panelX + 8f, panelY - 8f, panelAncho, panelAlto, 16f, new Color(0f, 0f, 0f, 0.5f));
        dibujarRectRedondeado(batch, pixel, panelX - 4f, panelY - 4f, panelAncho + 8f, panelAlto + 8f, 18f, new Color(0.1f, 0.1f, 0.1f, 1f));
        dibujarRectRedondeado(batch, pixel, panelX, panelY, panelAncho, panelAlto, 14f, new Color(0.18f, 0.20f, 0.23f, 1f));
        // 2. Botón
        float botonW = 440f;
        float botonX = panelX + (panelAncho - botonW) / 2f;
        float botonY = panelY + panelAlto - 85f; // Siempre anclado arriba
        botonCobrar.setPosition(botonX, botonY);
        botonCobrar.render(batch);
        // 3. Línea divisora
        float lineaY = botonY - 20f;
        batch.setColor(0.12f, 0.12f, 0.14f, 1f);
        batch.draw(pixel, panelX + 35f, lineaY, panelAncho - 70f, 3f);
        batch.setColor(Color.WHITE);
        // 4. Dibujar Filas de arriba hacia abajo
        BitmapFont fuente = game.getFuentePrincipal();
        float filaY = lineaY - 35f;
        dibujarFilaDetalle(batch, fuente, "Victoria", pesosVictoria, panelX + 45f, filaY, panelAncho - 90f);
        if (pesosManos > 0) {
            filaY -= altoFila;
            dibujarFilaDetalle(batch, fuente, "Manos Restantes", pesosManos, panelX + 45f, filaY, panelAncho - 90f);
        }
        if (pesosDescartes > 0) {
            filaY -= altoFila;
            dibujarFilaDetalle(batch, fuente, "Descartes Restantes", pesosDescartes, panelX + 45f, filaY, panelAncho - 90f);
        }
        if (pesosInteres > 0) {
            filaY -= altoFila;
            dibujarFilaDetalle(batch, fuente, "Interés", pesosInteres, panelX + 45f, filaY, panelAncho - 90f);
        }
        for (RecompensaJoker j : jokersActivados) {
            filaY -= altoFila;
            dibujarFilaDetalle(batch, fuente, j.nombre, j.monto, panelX + 45f, filaY, panelAncho - 90f);
        }
        if (pesosExtras > 0) {
            filaY -= altoFila;
            dibujarFilaDetalle(batch, fuente, "Extra", pesosExtras, panelX + 45f, filaY, panelAncho - 90f);
        }
    }

    private void dibujarFilaDetalle(SpriteBatch batch, BitmapFont fuente, String etiqueta, int valor, float xIzq, float y, float anchoUtil) {
        fuente.setColor(0.85f, 0.85f, 0.85f, 1f);
        fuente.draw(batch, etiqueta, xIzq, y);
        fuente.setColor(0.95f, 0.75f, 0.2f, 1f);
        String textoValor = "+$" + valor;
        GlyphLayout layout = new GlyphLayout(fuente, textoValor);
        fuente.draw(batch, textoValor, xIzq + anchoUtil - layout.width, y);
        fuente.setColor(Color.WHITE);
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

    public void dispose() {
        if (iconoPesoVictoria != null) iconoPesoVictoria.dispose();
    }
}
