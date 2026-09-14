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
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;

public class MainMenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseWorld;
    private BitmapFont font;
    // Botones del menú
    private Boton botonJugar;
    private Boton botonSalir;
    // Fondo estético (Plasma igual a GameScreen)
    private float tiempoTranscurrido = 0;
    private Texture texturaVacia;
    private BitmapFont miFuentePersonalizada;
    private Background fondoPlasma;
    private Boton botonOpciones;
    private Boton botonColeccion;
    private TextureRegion cartaDecorativa;
    private float tiempoDecoracion = 0f;
    private String subtituloActual;
    private final String[] frasesSubtitulo = {
        "EL TRUCO REINVENTADO",
        "¡QUIERO RETRUCO!",
        "MÁS MENTIRAS QUE EN POLÍTICA",
        "SÓLO PARA VALIENTES",
        "¡FALTA ENVÍDO Y TRUCO!",
        "NO TE ACHIQUES AHORA",
        "CON UN 4 DE COPAS",
        "EL ARTE DEL ENGAÑO",
        "TENGO QUE BAJAR YO A ARREGLAR TODO",
        "¿QUE ES UNA MILLA?",
        "RUSH B",
        "Ph'nglui mglw'nafh Cthulhu R'lyeh wgah'nagl fhtagn",
        "MEU DEUS CARALHO",
        "TENES QUE CERRAR EL ESTADIO",
        ".RAR",
        "NO ME LA CONTES",
        "COMIENZA LA PARTIDA YA",
        "ÑAM ÑAM ÑAM",
        "A LA GRANDE LE PUSE CUCA",
        "ROMPE TODO EL MONO",
        "BAJA UN CAMBIO",
        "HALF-LIFE 3 CONFIRMED",
        "THE CAKE IS A LIE",
        "THE PRINCESS IS IN ANOTHER CASTLE",
        "PRESS F TO PAY RESPECTS",
        "FINISH HIM",
        "PRAISE THE SUN",
        "GOTTA GO FAST",
        "MISSION FAILED",
        "MUDA MUDA MUDA MUDA",
        "SASAGEYO",
        "1.21 GIGAWATTS",
        "ONE",
        "PAPERCUT",
        "SACA LA BANDERA QUE LA REPRESENTAS MAL",
        "115",
        "JUST FUCK MY SISTER",
        "NO THANKS BRO",
        "COMANDO",
        "NO RUSSIAN",
        "DANGO",
        "YO TE ELIJO",
        "ME LO QUITARON CARMINE",
        "RIP AND TEAR",
        "DOJIMA NO RYU",
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
        botonSalir = new Boton(centroX - anchoBoton / 2f, 165f, anchoBoton, altoBoton, "SALIR", Boton.TipoColor.BORDO, Accion.IR_AL_MAZO);
        cartaDecorativa = game.getAtlasCartas().findRegion("back");
        int indiceRandom = com.badlogic.gdx.math.MathUtils.random(0, frasesSubtitulo.length - 1);
        subtituloActual = frasesSubtitulo[indiceRandom];
        prepararFondo();
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

        botonJugar.update(mouseWorld.x, mouseWorld.y);
        botonColeccion.update(mouseWorld.x, mouseWorld.y);
        botonOpciones.update(mouseWorld.x, mouseWorld.y);
        botonSalir.update(mouseWorld.x, mouseWorld.y);

        if (botonJugar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            game.setScreen(new GameScreenV2(game));
            dispose();
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
        if (botonSalir.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            Gdx.app.exit();
            return;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiempoDecoracion += delta;

        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        dibujarDecoracion(game.batch);
        game.batch.end();

        game.batch.begin();
        BitmapFont fontTitulo = game.getFuenteTitulo();
        float escalaTituloOriginal = fontTitulo.getScaleX();
        fontTitulo.getData().setScale(1.35f);
        GlyphLayout titleLayout = new GlyphLayout(fontTitulo, "ReTruco");
        float titleX = viewport.getWorldWidth() / 2f - titleLayout.width / 2f;
        // Sombra del título
        fontTitulo.setColor(0f, 0f, 0f, 0.45f);
        fontTitulo.draw(game.batch, "ReTruco", titleX + 4f, 590f - 5f);
        // Título
        fontTitulo.setColor(1f, 1f, 1f, 1f);
        fontTitulo.draw(game.batch, "ReTruco", titleX, 590f);
        fontTitulo.getData().setScale(escalaTituloOriginal);

        BitmapFont fontUI = game.getFuenteUI();
        fontUI.setColor(1f, 1f, 1f, 0.75f);
        GlyphLayout subtituloLayout = new GlyphLayout(fontUI, subtituloActual);
        fontUI.draw(game.batch, subtituloActual, 640f - subtituloLayout.width / 2f, 545f);
        fontUI.setColor(1f, 1f, 1f, 0.55f);
        fontUI.draw(game.batch, "v1.0.10", 1160f, 30f);

        // Dibujamos la caja contenedora justo antes de los botones
        dibujarCajaBotones(game.batch);

        botonJugar.render(game.batch);
        botonColeccion.render(game.batch);
        botonOpciones.render(game.batch);
        botonSalir.render(game.batch);
        game.batch.end();
    }

    private void dibujarCajaBotones(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float anchoBoton = 220f;
        float altoBoton = 58f;
        float padding = 20f;

        // Calculamos la caja para que envuelva todos los botones
        float boxW = anchoBoton + (padding * 2f);
        // Desde el inicio del botón Salir (165) hasta el tope del botón Jugar (405 + 58)
        float boxH = (405f + altoBoton - 165f) + (padding * 2f);

        float boxX = (viewport.getWorldWidth() / 2f) - (boxW / 2f);
        float boxY = 165f - padding;

        // Borde oscuro grueso exterior
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 12f, new Color(0.12f, 0.15f, 0.17f, 1f));
        // Fondo gris azulado interior (Color similar a la imagen)
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, new Color(0.20f, 0.25f, 0.28f, 1f));
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
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (fondoPlasma != null) {
            fondoPlasma.dispose();
        }
        if (texturaVacia != null) texturaVacia.dispose();
        //if (font != null) font.dispose();
        if (miFuentePersonalizada != null) miFuentePersonalizada.dispose();
    }
}
