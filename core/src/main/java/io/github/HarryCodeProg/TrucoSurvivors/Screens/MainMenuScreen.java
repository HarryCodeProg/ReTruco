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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.FisicaTiltArrastre;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.PanelPersonalizar;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputAdapter;

public class MainMenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseWorld;
    private BitmapFont font;
    private Boton botonJugar;
    private Boton botonSalir;
    private Background fondoPlasma;
    private Boton botonOpciones;
    private Boton botonColeccion;
    private TextureRegion cartaDecorativa;
    private float tiempoDecoracion = 0f;
    private String subtituloActual;
    private Texture logo1;

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

    private enum EstadoMenu {
        PRINCIPAL,
        CONTINUAR_O_NUEVA,
        PERSONALIZAR,
        OPCIONES,
        COLECCION
    }
    private EstadoMenu estadoActual = EstadoMenu.PRINCIPAL;

    private Boton botonPersonalizar;
    private Boton botonContinuar;
    private Boton botonPartidaNueva;
    private Boton botonVolverContinuar;

    private InputProcessor inputProcessorAnterior;

    // Paneles modulares (ya no son Screens, son paneles internos)
    private OpcionesScreen panelOpciones;
    private PanelPersonalizar panelPersonalizar;
    private ColeccionScreen panelColeccion; // Asumo que la adaptaste igual que OpcionesScreen

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

        // Botones menú principal
        botonJugar = new Boton(centroX - anchoBoton / 2f, 405f, anchoBoton, altoBoton, "JUGAR", Boton.TipoColor.VERDE, Accion.JUGAR_CARTA);
        botonColeccion = new Boton(centroX - anchoBoton / 2f, 325f, anchoBoton, altoBoton, "COLECCIÓN", Boton.TipoColor.VIOLETA, Accion.COLECCION);
        botonOpciones = new Boton(centroX - anchoBoton / 2f, 245f, anchoBoton, altoBoton, "OPCIONES", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonPersonalizar = new Boton(centroX - anchoBoton / 2f, 165f, anchoBoton, altoBoton, "PERSONALIZAR", Boton.TipoColor.DORADO, Accion.OPCIONES);
        botonSalir = new Boton(centroX - anchoBoton / 2f, 85f, anchoBoton, altoBoton, "SALIR", Boton.TipoColor.BORDO, Accion.IR_AL_MAZO);

        // Botones continuar o nueva
        botonContinuar = new Boton(centroX - anchoBoton / 2f, 325f, anchoBoton, altoBoton, "CONTINUAR", Boton.TipoColor.VERDE, Accion.JUGAR_CARTA);
        botonPartidaNueva = new Boton(centroX - anchoBoton / 2f, 245f, anchoBoton, altoBoton, "PARTIDA NUEVA", Boton.TipoColor.BORDO, Accion.JUGAR_CARTA);
        botonVolverContinuar = new Boton(centroX - anchoBoton / 2f, 165f, anchoBoton, altoBoton, "VOLVER", Boton.TipoColor.CELESTE, Accion.OPCIONES);

        // Inicialización de paneles
        panelOpciones = new OpcionesScreen(game);
        panelPersonalizar = new PanelPersonalizar(game, fondoPlasma); // Se pasa el fondo
        panelColeccion = new ColeccionScreen(game); // Recordá adaptarla para que no sea Screen

        cartaDecorativa = game.getAtlasCartas().findRegion("back");
        logo1 = new Texture("ui/logo1.png");
        espadaUno = game.getAtlasCartas().findRegion("1_espada");
        int indiceRandom = com.badlogic.gdx.math.MathUtils.random(0, frasesSubtitulo.length - 1);
        subtituloActual = frasesSubtitulo[indiceRandom];
        prepararFondo();

        // Importante: re-instanciar los paneles que dependan del fondo después de prepararlo
        panelPersonalizar = new PanelPersonalizar(game, fondoPlasma);
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

        boolean click = Gdx.input.justTouched();

        // --- LÓGICA DE ACTUALIZACIÓN POR ESTADO ---
        if (estadoActual == EstadoMenu.PRINCIPAL) {
            botonJugar.update(mouseWorld.x, mouseWorld.y);
            botonColeccion.update(mouseWorld.x, mouseWorld.y);
            botonOpciones.update(mouseWorld.x, mouseWorld.y);
            botonPersonalizar.update(mouseWorld.x, mouseWorld.y);
            botonSalir.update(mouseWorld.x, mouseWorld.y);

            if (botonJugar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                if (game.hayGuardadoDisponible()) {
                    estadoActual = EstadoMenu.CONTINUAR_O_NUEVA;
                } else {
                    game.setScreen(new GameScreen(game));
                    dispose();
                }
                return;
            }
            if (botonColeccion.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                panelColeccion.abrir();
                estadoActual = EstadoMenu.COLECCION;
            }
            if (botonOpciones.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                panelOpciones.abrir();
                estadoActual = EstadoMenu.OPCIONES;
            }
            if (botonPersonalizar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                panelPersonalizar.abrir();
                estadoActual = EstadoMenu.PERSONALIZAR;
            }
            if (botonSalir.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                Gdx.app.exit();
                return;
            }
        }
        else if (estadoActual == EstadoMenu.CONTINUAR_O_NUEVA) {
            botonContinuar.update(mouseWorld.x, mouseWorld.y);
            botonPartidaNueva.update(mouseWorld.x, mouseWorld.y);
            botonVolverContinuar.update(mouseWorld.x, mouseWorld.y);
            if (botonContinuar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                game.setScreen(new GameScreen(game));
                dispose();
                return;
            }
            if (botonPartidaNueva.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                game.getPerfilJugador().iniciarNuevaRun();
                game.reiniciarDesbloqueoRivales();
                io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorGuardado.borrarGuardado();
                game.setScreen(new GameScreen(game));
                dispose();
                return;
            }
            if (botonVolverContinuar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
                estadoActual = EstadoMenu.PRINCIPAL;
            }
        }
        else if (estadoActual == EstadoMenu.PERSONALIZAR) {
            if (panelPersonalizar.update(delta, mouseWorld.x, mouseWorld.y, click)) {
                estadoActual = EstadoMenu.PRINCIPAL;
            }
        }
        else if (estadoActual == EstadoMenu.OPCIONES) {
            if (panelOpciones.update(delta, mouseWorld.x, mouseWorld.y, click)) {
                estadoActual = EstadoMenu.PRINCIPAL;
            }
        }
        else if (estadoActual == EstadoMenu.COLECCION) {
            if (panelColeccion.update(delta, mouseWorld.x, mouseWorld.y, click)) {
                estadoActual = EstadoMenu.PRINCIPAL;
            }
        }
        // --- RENDERIZADO ---
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiempoDecoracion += delta;
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        dibujarDecoracion(game.batch);
        game.batch.end();
        game.batch.begin();
        // Logo y espada siempre visibles en el menú principal (o podés condicionarlo)
        float logoAncho = 420f, logoAlto = 140f;
        float logoX = (viewport.getWorldWidth() - logoAncho) / 2f;
        game.batch.setColor(Color.WHITE);
        game.batch.draw(logo1, logoX, 550f, logoAncho, logoAlto);
        if (espadaUno != null) {
            float espadaAncho = 62f, espadaAlto = 88f;
            game.batch.setColor(Color.WHITE);
            game.batch.draw(espadaUno, espadaX, espadaY, espadaAncho / 2f, espadaAlto / 2f, espadaAncho, espadaAlto, 1f, 1f, rotacionEspada);
        }
        BitmapFont fontUI = game.getFuenteUI();
        fontUI.setColor(1f, 1f, 1f, 0.75f);
        GlyphLayout subtituloLayout = new GlyphLayout(fontUI, subtituloActual);
        fontUI.draw(game.batch, subtituloActual, 640f - subtituloLayout.width / 2f, 550f);
        fontUI.setColor(1f, 1f, 1f, 0.55f);
        fontUI.draw(game.batch, "v1.0.10", 1160f, 30f);
        // Render de paneles según estado
        if (estadoActual == EstadoMenu.PRINCIPAL) {
            dibujarCajaBotones(game.batch);
            botonJugar.render(game.batch);
            botonColeccion.render(game.batch);
            botonOpciones.render(game.batch);
            botonPersonalizar.render(game.batch);
            botonSalir.render(game.batch);
        } else if (estadoActual == EstadoMenu.CONTINUAR_O_NUEVA) {
            dibujarCajaContinuar(game.batch);
            botonContinuar.render(game.batch);
            botonPartidaNueva.render(game.batch);
            botonVolverContinuar.render(game.batch);
        } else if (estadoActual == EstadoMenu.PERSONALIZAR) {
            panelPersonalizar.render(game.batch,camera, delta);
        } else if (estadoActual == EstadoMenu.OPCIONES) {
            panelOpciones.render(game.batch, delta);
        } else if (estadoActual == EstadoMenu.COLECCION) {
            panelColeccion.render(game.batch, delta);
        }
        game.batch.end();
    }

    private void dibujarCajaContinuar(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float anchoBoton = 220f, altoBoton = 58f, padding = 20f;
        float boxW = anchoBoton + (padding * 2f);
        float boxH = (325f + altoBoton - 165f) + (padding * 2f);
        float boxX = (viewport.getWorldWidth() / 2f) - (boxW / 2f);
        float boxY = 165f - padding;
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 12f, new Color(0.12f, 0.15f, 0.17f, 1f));
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, new Color(0.20f, 0.25f, 0.28f, 1f));
    }

    private void dibujarCajaBotones(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        float anchoBoton = 220f, altoBoton = 58f, padding = 20f;
        float boxW = anchoBoton + (padding * 2f);
        float boxH = (405f + altoBoton - 85f) + (padding * 2f);
        float boxX = (viewport.getWorldWidth() / 2f) - (boxW / 2f);
        float boxY = 85f - padding;
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 12f, new Color(0.12f, 0.15f, 0.17f, 1f));
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, new Color(0.20f, 0.25f, 0.28f, 1f));
    }

    private void actualizarEspadaDecorativa() {
        if (espadaUno == null) return;
        float ancho = 62f, alto = 88f;
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
                if (estadoActual == EstadoMenu.PERSONALIZAR) {
                    return panelPersonalizar.scrolled(amountY);
                }
                return false;
            }
        });
    }

    @Override public void hide() { Gdx.input.setInputProcessor(inputProcessorAnterior); }
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        if (fondoPlasma != null) fondoPlasma.dispose();
        if (logo1 != null) logo1.dispose();
    }
}
