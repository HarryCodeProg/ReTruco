package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

    public MainMenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        mouseWorld = new Vector3();
        font = game.getFuenteTitulo();
        //font.getData().setScale(3.0f); // Título grande
        // Creamos los botones centrados en la pantalla
        // Boton(x, y, ancho, alto, "Texto", Accion_Provisoria)
        float centroX = viewport.getWorldWidth() / 2f;
        float anchoBoton = 200f;
        botonJugar = new Boton(centroX - anchoBoton / 2f, 400f, anchoBoton, 60f, "JUGAR", Accion.JUGAR_CARTA);
        botonSalir = new Boton(centroX - anchoBoton / 2f, 280f, anchoBoton, 60f, "SALIR", Accion.IR_AL_MAZO);
        prepararFondo();
    }

    private void prepararFondo() {
        // Inicializamos el fondo reutilizable encapsulado
        this.fondoPlasma = new Background();
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);
        // Actualizar entrada de los botones
        botonJugar.update(mouseWorld.x, mouseWorld.y);
        botonSalir.update(mouseWorld.x, mouseWorld.y);
        // --- LÓGICA DE CLICKS ---
        if (botonJugar.fueCliqueado(mouseWorld.x, mouseWorld.y)) {;
            game.setScreen(new GameScreenV2(game));
            this.dispose();
            return;
        }
        if (botonSalir.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            Gdx.app.exit();
            return;
        }
        // --- RENDERIZADO ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Dibujar Fondo Shader
        tiempoTranscurrido += delta;
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        //game.batch.draw(texturaVacia, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        game.batch.end();
        game.batch.setShader(null);
        // Dibujar Textos y Botones
        game.batch.begin();
        // Un texto de título fachero
        GlyphLayout titleLayout = new GlyphLayout(font, "ReTruco");
        float centroX = viewport.getWorldWidth() / 2f;
        float titleX = centroX - titleLayout.width / 2f;
        font.draw(game.batch, "ReTruco", titleX, 580f);
        // Dibujamos tus botones (Podés setearles texto adentro si tu clase Boton lo permite,
        // o dibujar un font.draw encima de cada botón temporalmente)
        botonJugar.render(game.batch);
        botonSalir.render(game.batch);
        // Textos provisionales sobre los botones si tu botón no dibuja texto:
        // font.draw(game.batch, "JUGAR", X, Y);
        game.batch.end();
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
