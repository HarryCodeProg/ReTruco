package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Carta;
import io.github.HarryCodeProg.TrucoSurvivors.Cartas.Palo;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Jokers.Joker;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolJokersTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.PoolSantosTienda;
import io.github.HarryCodeProg.TrucoSurvivors.Santos.Santo;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaCarta;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaJoker;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.VistaSanto;

import java.util.ArrayList;

public class ColeccionScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 mouseWorld;
    private final Boton botonJokers;
    private final Boton botonCartas;
    private final Boton botonSantos;
    private final Boton botonAnterior;
    private final Boton botonSiguiente;
    private final Boton botonVolver;
    private enum Tipo {
        JOKERS,
        CARTAS,
        SANTOS
    }
    private Tipo tipoActual = Tipo.JOKERS;
    private int paginaActual = 0;
    private final ArrayList<VistaCarta> vistasCartas = new ArrayList<>();
    private final ArrayList<VistaSanto> vistasSantos = new ArrayList<>();
    private final ArrayList<Joker> jokers = new ArrayList<>();
    private final ArrayList<Carta> cartas = new ArrayList<>();
    private final ArrayList<Santo> santos = new ArrayList<>();
    private static final int COLUMNAS = 5;
    private static final int FILAS = 3;
    private static final int POR_PAGINA = 15;
    private static final float X_INICIAL = 220f;
    private static final float Y_INICIAL = 460f;
    private static final float ESPACIO_X = 185f;
    private static final float ESPACIO_Y = 165f;
    private static final float ANCHO_ITEM = 100f;
    private static final float ALTO_CARTA = 150f;
    private static final float ALTO_JOKER = 110f;
    private static final float ALTO_SANTO = 120f;
    private Background fondoPlasma;
    private final VistaJoker[] vistasJokers = new VistaJoker[150]; // FIX: indexado por id real, no por orden de pool
    private static final float ALTO_ITEM = 150f;

    public ColeccionScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        mouseWorld = new Vector3();
        botonJokers = new Boton(350f, 625f, 170f, 45f, "JOKERS", Boton.TipoColor.VIOLETA, Accion.COLECCION);
        botonCartas = new Boton(555f, 625f, 170f, 45f, "CARTAS", Boton.TipoColor.CIAN, Accion.COLECCION);
        botonSantos = new Boton(760f, 625f, 170f, 45f, "SANTOS", Boton.TipoColor.DORADO, Accion.COLECCION);
        botonAnterior = new Boton(500f, 35f, 90f, 50f, "<-", Boton.TipoColor.CELESTE, Accion.COLECCION);
        botonSiguiente = new Boton(690f, 35f, 90f, 50f, "->", Boton.TipoColor.CELESTE, Accion.COLECCION);
        botonVolver = new Boton(40f, 35f, 130f, 50f, "VOLVER", Boton.TipoColor.BLANCO, Accion.OPCIONES);
        fondoPlasma = new Background();
        fondoPlasma.setTema(game.getConfiguracionJuego().getFondoIndex());
        PoolJokersTienda poolJokers = new PoolJokersTienda();
        jokers.addAll(poolJokers.crearTodos());
        PoolSantosTienda poolSantos = new PoolSantosTienda();
        santos.addAll(poolSantos.crearTodos());
        for (int numero = 1; numero <= 12; numero++) {
            for (Palo palo : Palo.values()) {
                cartas.add(new Carta(numero, palo));
            }
        }
        construirVistas();
    }

    private void construirVistas() {
        java.util.Arrays.fill(vistasJokers, null); // FIX
        vistasCartas.clear();
        vistasSantos.clear();
        for (Joker joker : jokers) {
            VistaJoker vista = new VistaJoker(joker, game.getAtlasJokers());
            vista.setTamaño(ANCHO_ITEM, ALTO_ITEM);
            int idx = joker.getId() - 1; // FIX: id 1..150 -> índice 0..149
            if (idx >= 0 && idx < vistasJokers.length) {
                vistasJokers[idx] = vista;
            }
        }
        for (Carta carta : cartas) {
            VistaCarta vista = new VistaCarta(carta, false, game.getAtlasCartas());
            vista.setTamaño(ANCHO_ITEM, ALTO_ITEM);
            vistasCartas.add(vista);
        }
        for (Santo santo : santos) {
            VistaSanto vista = new VistaSanto(santo, game.getAtlasSantos());
            vista.setTamaño(ANCHO_ITEM, ALTO_ITEM);
            vistasSantos.add(vista);
        }
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);
        botonJokers.update(mouseWorld.x, mouseWorld.y);
        botonCartas.update(mouseWorld.x, mouseWorld.y);
        botonSantos.update(mouseWorld.x, mouseWorld.y);
        botonAnterior.update(mouseWorld.x, mouseWorld.y);
        botonSiguiente.update(mouseWorld.x, mouseWorld.y);
        botonVolver.update(mouseWorld.x, mouseWorld.y);
        if (botonJokers.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            if (tipoActual != Tipo.JOKERS) {
                tipoActual = Tipo.JOKERS;
                paginaActual = 0;
            }
        }
        if (botonCartas.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            if (tipoActual != Tipo.CARTAS) {
                tipoActual = Tipo.CARTAS;
                paginaActual = 0;
            }
        }
        if (botonSantos.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            if (tipoActual != Tipo.SANTOS) {
                tipoActual = Tipo.SANTOS;
                paginaActual = 0;
            }
        }
        int totalPaginas = obtenerTotalPaginas();
        if (botonAnterior.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            if (paginaActual > 0) paginaActual--;
        }
        if (botonSiguiente.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            if (paginaActual < totalPaginas - 1) paginaActual++;
        }
        if (botonVolver.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return;
        }
        actualizarVistas(mouseWorld.x, mouseWorld.y, delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        game.batch.begin();
        BitmapFont font = game.getFuenteTitulo();
        GlyphLayout titleLayout = new GlyphLayout(font, "COLECCIÓN");
        font.draw(game.batch, "COLECCIÓN", 640f - titleLayout.width / 2f, 700f);
        botonJokers.render(game.batch);
        botonCartas.render(game.batch);
        botonSantos.render(game.batch);
        dibujarVistasActuales(game.batch);
        botonAnterior.render(game.batch);
        botonSiguiente.render(game.batch);
        botonVolver.render(game.batch);
        game.batch.end();
    }

    private int obtenerTotalPaginas() {
        int total;
        switch (tipoActual) {
            case JOKERS:
                total = vistasJokers.length;
                break;
            case CARTAS:
                total = cartas.size();
                break;
            case SANTOS:
                total = santos.size();
                break;
            default:
                total = 0;
                break;
        }
        return Math.max(1, (int) Math.ceil(total / (float) POR_PAGINA));
    }

    private void actualizarVistas(float mouseX, float mouseY, float delta) {
        if (tipoActual == Tipo.JOKERS) {
            int inicio = paginaActual * POR_PAGINA;
            int fin = Math.min(inicio + POR_PAGINA, vistasJokers.length);
            for (int i = inicio; i < fin; i++) {
                if (vistasJokers[i] != null) vistasJokers[i].update(mouseX, mouseY, delta);
            }
        }
        if (tipoActual == Tipo.CARTAS) {
            int inicio = paginaActual * POR_PAGINA;
            int fin = Math.min(inicio + POR_PAGINA, vistasCartas.size());
            for (int i = inicio; i < fin; i++) {
                vistasCartas.get(i).update(mouseX, mouseY, delta);
            }
        }
        if (tipoActual == Tipo.SANTOS) {
            int inicio = paginaActual * POR_PAGINA;
            int fin = Math.min(inicio + POR_PAGINA, vistasSantos.size());
            for (int i = inicio; i < fin; i++) {
                vistasSantos.get(i).update(mouseX, mouseY, delta);
            }
        }
    }

    private void dibujarVistasActuales(SpriteBatch batch) {
        int inicio = paginaActual * POR_PAGINA;
        VistaJoker jokerHover = null;
        VistaCarta cartaHover = null;
        VistaSanto santoHover = null;
        for (int posicion = 0; posicion < POR_PAGINA; posicion++) {
            int indice = inicio + posicion;
            int fila = posicion / COLUMNAS;
            int columna = posicion % COLUMNAS;
            float x = X_INICIAL + columna * ESPACIO_X;
            float y = Y_INICIAL - fila * ESPACIO_Y;
            if (tipoActual == Tipo.JOKERS) {
                if (indice < vistasJokers.length && vistasJokers[indice] != null) {
                    VistaJoker vista = vistasJokers[indice];
                    vista.setPosition(x, y);
                    vista.setTooltipLateral(fila == 1); // <--- NUEVO
                    vista.render(batch);
                    if (vista.isHover()) jokerHover = vista;
                } else if (indice < vistasJokers.length) {
                    dibujarJokerBack(batch, x, y);
                }
            }
            if (tipoActual == Tipo.CARTAS) {
                if (indice < vistasCartas.size()) {
                    VistaCarta vista = vistasCartas.get(indice);
                    vista.setPosition(x, y);
                    vista.setTooltipLateral(fila == 1); // <--- NUEVO
                    vista.render(batch, game);
                    if (vista.isHover()) cartaHover = vista;
                }
            }
            if (tipoActual == Tipo.SANTOS) {
                if (indice < vistasSantos.size()) {
                    VistaSanto vista = vistasSantos.get(indice);
                    vista.setPosition(x, y);
                    vista.setTooltipLateral(fila == 1); // <--- NUEVO
                    vista.render(batch);
                    if (vista.isHover()) santoHover = vista;
                }
            }
        }
        if (jokerHover != null) jokerHover.renderCartelStats(batch, game, null); // sin Juego activo en colección
        if (cartaHover != null) cartaHover.renderCartelStats(batch, game);
        if (santoHover != null) santoHover.renderCartelStats(batch, game);
    }

    private void dibujarJokerBack(SpriteBatch batch, float x, float y) {
        TextureRegion back = game.getAtlasJokers().findRegion("back");
        if (back == null) return;
        batch.setColor(Color.WHITE);
        batch.draw(back, x, y, ANCHO_ITEM / 2f, ALTO_ITEM / 2f, ANCHO_ITEM, ALTO_ITEM, 1f, 1f, 0f);
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
    }
}
