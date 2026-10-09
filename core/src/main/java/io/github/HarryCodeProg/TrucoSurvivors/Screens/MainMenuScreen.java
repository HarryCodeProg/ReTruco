package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.FisicaTiltArrastre;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.UITheme;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorGuardado;

import java.util.ArrayList;

public class MainMenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseWorld;
    private BitmapFont font;
    private Boton botonJugar;
    private Boton botonSalir;
    private float tiempoTranscurrido = 0;
    private Texture texturaVacia;
    private BitmapFont miFuentePersonalizada;
    private Background fondoPlasma;
    private Boton botonOpciones;
    private Boton botonColeccion;
    private TextureRegion cartaDecorativa;
    private float tiempoDecoracion = 0f;
    private String subtituloActual;
    private Texture logo1;
    private Texture logo2;
    private final float espadaXOriginal = 565f;
    private final float espadaYOriginal = 580f;
    private float espadaX = espadaXOriginal;
    private float espadaY = espadaYOriginal;
    private TextureRegion espadaUno;
    private boolean arrastrandoEspada = false;
    private float offsetEspadaX;
    private float offsetEspadaY;
    private final FisicaTiltArrastre fisicaTiltEspada = new FisicaTiltArrastre();
    private float rotacionEspada = 0f;
    private enum PanelMenu { PRINCIPAL, PERSONALIZAR, CONTINUAR_O_NUEVA }
    private PanelMenu panelActual = PanelMenu.PRINCIPAL;
    private Boton botonPersonalizar;
    private Boton botonVolverPersonalizar;
    private Boton botonContinuar;
    private Boton botonPartidaNueva;
    private Boton botonVolverContinuar;
    private float scrollY = 0f;
    private float maxScrollY = 0f;
    private float listaAltoVisible = 0f;
    private float listaAltoTotal = 0f;
    private Rectangle areaLista = new Rectangle();
    private boolean arrastrandoLista = false;
    private float dragListaStartY = 0f;
    private float scrollYStart = 0f;
    private InputProcessor inputProcessorAnterior;
    private static final float FILA_ALTO = 44f;
    private static final float FILA_GAP = 2f;
    private static final float PANEL_MUSICA_ANCHO = 420f;
    private static final float PANEL_PAD_X = 22f;
    private static final float HEADER_ALTO = 145f;
    private static final float FOOTER_ALTO = 95f;
    private final ArrayList<Rectangle> filasCanciones = new ArrayList<>();
    private float panelMusicaX, panelMusicaY, panelMusicaAlto;
    private final String[] frasesSubtitulo = {
        "EL TRUCO REINVENTADO",
    };

    public MainMenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        mouseWorld = new Vector3();
        font = game.getFuenteTitulo();
        float centroX = viewport.getWorldWidth() / 2f;
        float anchoBoton = 220f;
        float altoBoton = 58f;
        botonJugar = new Boton(centroX - anchoBoton / 2f, 405f, anchoBoton, altoBoton, "JUGAR", Boton.TipoColor.VERDE, Accion.JUGAR_CARTA);
        botonColeccion = new Boton(centroX - anchoBoton / 2f, 325f, anchoBoton, altoBoton, "COLECCIÓN", Boton.TipoColor.VIOLETA, Accion.COLECCION);
        botonOpciones = new Boton(centroX - anchoBoton / 2f, 245f, anchoBoton, altoBoton, "OPCIONES", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonPersonalizar = new Boton(centroX - anchoBoton / 2f, 165f, anchoBoton, altoBoton, "PERSONALIZAR", Boton.TipoColor.DORADO, Accion.OPCIONES);
        botonSalir = new Boton(centroX - anchoBoton / 2f, 85f, anchoBoton, altoBoton, "SALIR", Boton.TipoColor.BORDO, Accion.IR_AL_MAZO);
        botonContinuar = new Boton(centroX - anchoBoton / 2f, 325f, anchoBoton, altoBoton, "CONTINUAR", Boton.TipoColor.VERDE, Accion.JUGAR_CARTA);
        botonPartidaNueva = new Boton(centroX - anchoBoton / 2f, 245f, anchoBoton, altoBoton, "PARTIDA NUEVA", Boton.TipoColor.BORDO, Accion.JUGAR_CARTA);
        botonVolverContinuar = new Boton(centroX - anchoBoton / 2f, 165f, anchoBoton, altoBoton, "VOLVER", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        calcularLayoutMusica();
        cartaDecorativa = game.getAtlasCartas().findRegion("back");
        logo1 = new Texture("ui/logo1.png");
        logo2 = new Texture("ui/logo2.png");
        espadaUno = game.getAtlasCartas().findRegion("1_espada");
        int indiceRandom = com.badlogic.gdx.math.MathUtils.random(0, frasesSubtitulo.length - 1);
        subtituloActual = frasesSubtitulo[indiceRandom];
        prepararFondo();
    }

    /** Arma las filas de canciones y el botón Volver en base a la cantidad real de pistas, sin dejar huecos. */
    private void calcularLayoutMusica() {
        float centroX = viewport.getWorldWidth() / 2f;
        String[][] pistas = game.getPistasMusica();
        int cantidad = pistas.length;
        int MAX_FILAS_VISIBLES = 4; // Ajusta cuántas canciones quieres ver a la vez
        int filasParaMostrar = Math.min(cantidad, MAX_FILAS_VISIBLES);
        listaAltoVisible = filasParaMostrar * FILA_ALTO + Math.max(0, filasParaMostrar - 1) * FILA_GAP;
        listaAltoTotal = cantidad * FILA_ALTO + Math.max(0, cantidad - 1) * FILA_GAP;
        maxScrollY = Math.max(0, listaAltoTotal - listaAltoVisible);
        panelMusicaAlto = HEADER_ALTO + listaAltoVisible + FOOTER_ALTO;
        panelMusicaX = centroX - PANEL_MUSICA_ANCHO / 2f;
        panelMusicaY = 85f;
        // Definimos exactamente qué área ocupan los ítems en pantalla
        areaLista.set(panelMusicaX, panelMusicaY + FOOTER_ALTO, PANEL_MUSICA_ANCHO, listaAltoVisible);
        filasCanciones.clear();
        float anchoFila = PANEL_MUSICA_ANCHO - PANEL_PAD_X * 2f;
        float xFila = panelMusicaX + PANEL_PAD_X;
        float yTopeLocal = areaLista.y + areaLista.height;
        for (int i = 0; i < cantidad; i++) {
            // Calculamos las filas basándonos en el tope superior del Área de la Lista
            float yFila = yTopeLocal - (i + 1) * FILA_ALTO - i * FILA_GAP;
            filasCanciones.add(new Rectangle(xFila, yFila, anchoFila, FILA_ALTO));
        }
        float anchoVolver = 180f;
        float yVolver = panelMusicaY + (FOOTER_ALTO - 50f) / 2f;
        botonVolverPersonalizar = new Boton(centroX - anchoVolver / 2f, yVolver, anchoVolver, 48f, "VOLVER", Boton.TipoColor.BORDO, Accion.OPCIONES);
    }

    private void prepararFondo() {
        this.fondoPlasma = new Background();
        this.fondoPlasma.setTema(game.getConfiguracionJuego().getFondoIndex());
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);
        actualizarEspadaDecorativa();
        // --- LÓGICA DE ACTUALIZACIÓN Y CLICS ---
        if (panelActual == PanelMenu.PRINCIPAL) {
            botonJugar.update(mouseWorld.x, mouseWorld.y);
            botonColeccion.update(mouseWorld.x, mouseWorld.y);
            botonOpciones.update(mouseWorld.x, mouseWorld.y);
            botonPersonalizar.update(mouseWorld.x, mouseWorld.y);
            botonSalir.update(mouseWorld.x, mouseWorld.y);
            if (botonJugar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                if (game.hayGuardadoDisponible()) {
                    panelActual = PanelMenu.CONTINUAR_O_NUEVA;
                } else {
                    game.setScreen(new GameScreenV2(game));
                    dispose();
                }
                return;
            }
            if (botonColeccion.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                game.setScreen(new ColeccionScreen(game));
                dispose();
                return;
            }
            if (botonOpciones.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                game.setScreen(new OpcionesScreen(game));
                dispose();
                return;
            }
            if (botonPersonalizar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                panelActual = PanelMenu.PERSONALIZAR;
            }
            if (botonSalir.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                Gdx.app.exit();
                return;
            }
        } else if (panelActual == PanelMenu.CONTINUAR_O_NUEVA) {
            botonContinuar.update(mouseWorld.x, mouseWorld.y);
            botonPartidaNueva.update(mouseWorld.x, mouseWorld.y);
            botonVolverContinuar.update(mouseWorld.x, mouseWorld.y);
            if (botonContinuar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                game.setScreen(new GameScreenV2(game));
                dispose();
                return;
            }
            if (botonPartidaNueva.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                game.getPerfilJugador().iniciarNuevaRun();
                game.reiniciarDesbloqueoRivales();
                io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorGuardado.borrarGuardado();
                game.setScreen(new GameScreenV2(game));
                dispose();
                return;
            }
            if (botonVolverContinuar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                panelActual = PanelMenu.PRINCIPAL;
            }
        } else if (panelActual == PanelMenu.PERSONALIZAR) {
            botonVolverPersonalizar.update(mouseWorld.x, mouseWorld.y);
            if (botonVolverPersonalizar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                panelActual = PanelMenu.PRINCIPAL;
            }
            boolean clickAceptado = false;
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (areaLista.contains(mouseWorld.x, mouseWorld.y)) {
                    arrastrandoLista = true;
                    dragListaStartY = mouseWorld.y;
                    scrollYStart = scrollY;
                }
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && arrastrandoLista) {
                float deltaY = mouseWorld.y - dragListaStartY;
                scrollY = scrollYStart + deltaY;
                scrollY = MathUtils.clamp(scrollY, 0, maxScrollY);
            } else if (arrastrandoLista) {
                // El usuario soltó el clic
                arrastrandoLista = false;
                // Si el mouse casi no se movió, lo consideramos un clic para elegir canción
                if (Math.abs(mouseWorld.y - dragListaStartY) < 10f) {
                    clickAceptado = true;
                }
            }
            if (clickAceptado) {
                for (int i = 0; i < filasCanciones.size(); i++) {
                    Rectangle r = filasCanciones.get(i);
                    // Comprobamos la colisión sumando el offset actual del scroll
                    if (mouseWorld.x >= r.x && mouseWorld.x <= r.x + r.width &&
                        mouseWorld.y >= r.y + scrollY && mouseWorld.y <= r.y + scrollY + r.height) {
                        game.cambiarPistaMusica(i);
                        break;
                    }
                }
            }
        }
        // --- FIN LÓGICA DE ACTUALIZACIÓN ---
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiempoDecoracion += delta;
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        dibujarDecoracion(game.batch);
        game.batch.end();
        game.batch.begin();
        Texture logo = logo1;
        float logoAncho = 420f;
        float logoAlto = 140f;
        float logoX = (viewport.getWorldWidth() - logoAncho) / 2f;
        float logoY = 550f;
        game.batch.setColor(Color.WHITE);
        game.batch.draw(logo, logoX, logoY, logoAncho, logoAlto);
        if (espadaUno != null) {
            float espadaAncho = 62f;
            float espadaAlto = 88f;
            game.batch.setColor(Color.WHITE);
            game.batch.draw(espadaUno, espadaX, espadaY, espadaAncho / 2f, espadaAlto / 2f, espadaAncho, espadaAlto, 1f, 1f, rotacionEspada);
        }
        BitmapFont fontUI = game.getFuenteUI();
        fontUI.setColor(1f, 1f, 1f, 0.75f);
        GlyphLayout subtituloLayout = new GlyphLayout(fontUI, subtituloActual);
        fontUI.draw(game.batch, subtituloActual, 640f - subtituloLayout.width / 2f, 550f);
        fontUI.setColor(1f, 1f, 1f, 0.55f);
        fontUI.draw(game.batch, "v1.0.10", 1160f, 30f);
        // --- RENDERIZADO DE PANELES ---
        if (panelActual == PanelMenu.PRINCIPAL) {
            dibujarCajaBotones(game.batch);
            botonJugar.render(game.batch);
            botonColeccion.render(game.batch);
            botonOpciones.render(game.batch);
            botonPersonalizar.render(game.batch);
            botonSalir.render(game.batch);
        } else if (panelActual == PanelMenu.CONTINUAR_O_NUEVA) {
            dibujarCajaContinuar(game.batch);
            botonContinuar.render(game.batch);
            botonPartidaNueva.render(game.batch);
            botonVolverContinuar.render(game.batch);
        } else if (panelActual == PanelMenu.PERSONALIZAR) {
            dibujarPanelMusica(game.batch, delta);
        }
        game.batch.end();
    }

    private void dibujarCajaContinuar(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float anchoBoton = 220f;
        float altoBoton = 58f;
        float padding = 20f;
        float boxW = anchoBoton + (padding * 2f);
        // La caja envuelve desde Y=165 hasta Y=325 + altoBoton
        float boxH = (325f + altoBoton - 165f) + (padding * 2f);
        float boxX = (viewport.getWorldWidth() / 2f) - (boxW / 2f);
        float boxY = 165f - padding;
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 12f, new Color(0.12f, 0.15f, 0.17f, 1f));
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, new Color(0.20f, 0.25f, 0.28f, 1f));
    }

    private void dibujarPanelMusica(SpriteBatch batch, float delta) {
        Texture pixel = game.getPixelBlanco();
        float x = panelMusicaX, y = panelMusicaY, w = PANEL_MUSICA_ANCHO, h = panelMusicaAlto;
        // Sombra desplazada
        dibujarRectRedondeado(batch, pixel, x + 6f, y - 8f, w, h, 12f, UITheme.SOMBRA);
        // Borde exterior negro grueso
        dibujarRectRedondeado(batch, pixel, x - 4f, y - 4f, w + 8f, h + 8f, 13f, UITheme.BORDE);
        // Panel interior
        dibujarRectRedondeado(batch, pixel, x, y, w, h, 10f, UITheme.PANEL_PRINCIPAL);
        // Línea dorada superior
        batch.setColor(UITheme.DORADO);
        batch.draw(pixel, x + 18f, y + h - 6f, w - 36f, 3f);
        batch.setColor(Color.WHITE);
        BitmapFont fTitulo = game.getFuentePrincipal();
        BitmapFont fUI = game.getFuenteUI();
        BitmapFont fNum = game.getFuenteNumeros();
        float centroX = x + w / 2f;
        float cursorY = y + h - 30f;
        // Título
        fTitulo.getData().setScale(1.05f);
        fTitulo.setColor(UITheme.DORADO_BRILLANTE);
        fTitulo.draw(batch, "MUSICA DE FONDO", x, cursorY, w, Align.center, false);
        fTitulo.getData().setScale(1f);
        fTitulo.setColor(Color.WHITE);
        cursorY -= 26f;
        // Subtítulo
        fUI.setColor(UITheme.TEXTO_SECUNDARIO);
        fUI.draw(batch, "Selecciona una cancion", x, cursorY, w, Align.center, false);
        fUI.setColor(Color.WHITE);
        cursorY -= 28f;
        // Reproduciendo ahora
        int indiceActual = game.getIndicePistaActual();
        String[][] pistas = game.getPistasMusica();
        String nombreActual = (indiceActual >= 0 && indiceActual < pistas.length) ? pistas[indiceActual][0] : "-";
        fUI.getData().setScale(0.65f);
        fUI.setColor(UITheme.TEXTO_SECUNDARIO);
        fUI.draw(batch, "REPRODUCIENDO", x, cursorY, w, Align.center, false);
        fUI.getData().setScale(1f);
        cursorY -= 20f;
        fNum.setColor(UITheme.TEXTO_PRINCIPAL);
        fNum.draw(batch, nombreActual, x, cursorY, w, Align.center, false);
        fUI.setColor(Color.WHITE);
        fNum.setColor(Color.WHITE);
        cursorY -= 22f;
        // Barritas tipo ecualizador, animadas
        float barraBaseX = centroX - 24f;
        float barraY = cursorY - 10f;
        for (int b = 0; b < 4; b++) {
            float fase = tiempoDecoracion * 6f + b * 1.4f;
            float altoBarra = 5f + (float) (Math.abs(Math.sin(fase)) * 11f);
            batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.9f);
            batch.draw(pixel, barraBaseX + b * 14f, barraY, 8f, altoBarra);
        }
        batch.setColor(Color.WHITE);
        // Filas de canciones
        // --- INICIO ZONA DE SCROLL (ENMASCARADO) ---
        batch.flush(); // Fundamental limpiar el batch antes de cortar
        Rectangle scissor = new Rectangle();
        Rectangle clipBounds = new Rectangle(areaLista.x, areaLista.y, areaLista.width, areaLista.height);
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), clipBounds, scissor);
        // pushScissors devuelve false si la pantalla es inválida (ej: al minimizar el juego)
        boolean enmascaradoExitoso = ScissorStack.pushScissors(scissor);
        if (enmascaradoExitoso) {
            boolean mouseDentroDePanel = areaLista.contains(mouseWorld.x, mouseWorld.y);
            for (int i = 0; i < filasCanciones.size(); i++) {
                Rectangle rBase = filasCanciones.get(i);
                // Calculamos la posición visual actual basada en el scroll
                float filaYActual = rBase.y + scrollY;
                // Optimización: No dibujar lo que está fuera de la caja
                if (filaYActual + rBase.height < areaLista.y || filaYActual > areaLista.y + areaLista.height) {
                    continue;
                }
                boolean seleccionada = (i == indiceActual);
                // El hover ahora solo se activa si no estás arrastrando y el mouse da en el lugar
                boolean hover = mouseDentroDePanel && !arrastrandoLista &&
                    mouseWorld.x >= rBase.x && mouseWorld.x <= rBase.x + rBase.width &&
                    mouseWorld.y >= filaYActual && mouseWorld.y <= filaYActual + rBase.height;
                Color colorFondo;
                Color colorBorde;
                Color colorTexto;
                if (seleccionada) {
                    colorFondo = UITheme.DORADO;
                    colorBorde = UITheme.NARANJA;
                    colorTexto = new Color(0.08f, 0.08f, 0.08f, 1f);
                } else if (hover) {
                    colorFondo = UITheme.AZUL_HOVER;
                    colorBorde = UITheme.AZUL;
                    colorTexto = UITheme.TEXTO_PRINCIPAL;
                } else {
                    colorFondo = UITheme.PANEL_SECUNDARIO;
                    colorBorde = UITheme.AZUL;
                    colorTexto = UITheme.TEXTO_PRINCIPAL;
                }
                dibujarRectRedondeado(batch, pixel, rBase.x - 2f, filaYActual - 2f, rBase.width + 4f, rBase.height + 4f, 7f, colorBorde);
                dibujarRectRedondeado(batch, pixel, rBase.x, filaYActual, rBase.width, rBase.height, 6f, colorFondo);
                float indicadorX = rBase.x + 14f;
                float indicadorCy = filaYActual + rBase.height / 2f;
                if (seleccionada) {
                    batch.setColor(colorTexto);
                    batch.draw(pixel, indicadorX, indicadorCy - 6f, 3f, 12f);
                    batch.draw(pixel, indicadorX + 3f, indicadorCy - 4f, 3f, 8f);
                    batch.draw(pixel, indicadorX + 6f, indicadorCy - 2f, 3f, 4f);
                    batch.setColor(Color.WHITE);
                }
                String nombre = pistas[i][0].toUpperCase();
                fUI.setColor(colorTexto);
                fUI.draw(batch, nombre, rBase.x + 34f, filaYActual + rBase.height / 2f + 7f, rBase.width - 46f, Align.left, false);
                fUI.setColor(Color.WHITE);
                if (i < filasCanciones.size() - 1) {
                    batch.setColor(UITheme.BORDE.r, UITheme.BORDE.g, UITheme.BORDE.b, 0.5f);
                    batch.draw(pixel, rBase.x, filaYActual - FILA_GAP, rBase.width, FILA_GAP);
                    batch.setColor(Color.WHITE);
                }
            }
            batch.flush(); // Dibujamos las canciones recortadas
            ScissorStack.popScissors(); // Quitamos la máscara solo si la agregamos con éxito
        }
        // --- FIN ZONA SCROLL ---
        // DIBUJAR BARRA DE DESPLAZAMIENTO (Scrollbar)
        if (maxScrollY > 0) {
            float barraTotalH = areaLista.height;
            float barraVisibleH = (listaAltoVisible / listaAltoTotal) * barraTotalH;
            float proporcionScroll = scrollY / maxScrollY;
            // Le cambiamos el nombre a barraScrollY
            float barraScrollY = areaLista.y + barraTotalH - barraVisibleH - (proporcionScroll * (barraTotalH - barraVisibleH));
            batch.setColor(UITheme.BORDE.r, UITheme.BORDE.g, UITheme.BORDE.b, 0.6f);
            dibujarRectRedondeado(batch, pixel, areaLista.x + areaLista.width - 8f, areaLista.y, 6f, barraTotalH, 3f, batch.getColor());
            batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, 0.9f);
            // Usamos el nuevo nombre aquí
            dibujarRectRedondeado(batch, pixel, areaLista.x + areaLista.width - 8f, barraScrollY, 6f, barraVisibleH, 3f, batch.getColor());
            batch.setColor(Color.WHITE);
        }
        botonVolverPersonalizar.render(batch);
    }

    private void dibujarCajaBotones(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float anchoBoton = 220f;
        float altoBoton = 58f;
        float padding = 20f;
        float boxW = anchoBoton + (padding * 2f);
        float boxH = (405f + altoBoton - 85f) + (padding * 2f);
        float boxX = (viewport.getWorldWidth() / 2f) - (boxW / 2f);
        float boxY = 85f - padding;
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 12f, new Color(0.12f, 0.15f, 0.17f, 1f));
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, new Color(0.20f, 0.25f, 0.28f, 1f));
    }

    private void actualizarEspadaDecorativa() {
        if (espadaUno == null) return;
        float ancho = 62f;
        float alto = 88f;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            boolean encima = mouseWorld.x >= espadaX && mouseWorld.x <= espadaX + ancho && mouseWorld.y >= espadaY && mouseWorld.y <= espadaY + alto;
            if (encima) {
                arrastrandoEspada = true;
                offsetEspadaX = mouseWorld.x - espadaX;
                offsetEspadaY = mouseWorld.y - espadaY;
                fisicaTiltEspada.iniciarArrastre(mouseWorld.x);
            }
        }
        if (arrastrandoEspada) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                espadaX = mouseWorld.x - offsetEspadaX;
                espadaY = mouseWorld.y - offsetEspadaY;
                fisicaTiltEspada.actualizarArrastre(mouseWorld.x, Gdx.graphics.getDeltaTime());
                rotacionEspada = fisicaTiltEspada.getAngulo();
            } else {
                arrastrandoEspada = false;
                espadaX = espadaXOriginal;
                espadaY = espadaYOriginal;
                fisicaTiltEspada.reset();
                rotacionEspada = 0f;
            }
        }
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

    private void dibujarDecoracion(SpriteBatch batch) {
        if (cartaDecorativa == null) return;
        float movimiento = (float) Math.sin(tiempoDecoracion * 0.8f) * 8f;
        float movimiento2 = (float) Math.sin(tiempoDecoracion * 0.65f + 2f) * 10f;
        batch.setColor(1f, 1f, 1f, 0.90f);
        batch.draw(cartaDecorativa, 85f, 270f + movimiento, 55f, 82.5f, 110f, 165f, 1f, 1f, -16f);
        batch.draw(cartaDecorativa, 1140f, 330f + movimiento2, 55f, 82.5f, 110f, 165f, 1f, 1f, 14f);
        batch.draw(cartaDecorativa, 135f, 120f - movimiento2 * 0.5f, 42f, 63f, 84f, 126f, 1f, 1f, 10f);
        batch.draw(cartaDecorativa, 1100f, 120f + movimiento * 0.5f, 42f, 63f, 84f, 126f, 1f, 1f, -9f);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override
    public void show() {
        inputProcessorAnterior = Gdx.input.getInputProcessor();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (panelActual == PanelMenu.PERSONALIZAR) {
                    scrollY += amountY * 30f; // Velocidad de la rueda
                    scrollY = MathUtils.clamp(scrollY, 0, maxScrollY);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(inputProcessorAnterior);
    }
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        if (fondoPlasma != null) fondoPlasma.dispose();
        if (texturaVacia != null) texturaVacia.dispose();
        if (miFuentePersonalizada != null) miFuentePersonalizada.dispose();
    }
}
