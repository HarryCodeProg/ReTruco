package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.DatosRival;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;

import java.util.ArrayList;

public class SeleccionRivalScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 mouseWorld;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture pixelBlanco;
    private ArrayList<DatosRival> listaRivales;
    private ArrayList<Boton> botonesJugar; // Un botón por rival activo
    private Background fondoPlasma;
    private float tiempoTranscurrido = 0;

    // Paginación
    private int paginaActual = 0;
    private int inicioIndice = 0;

    // Colores temáticos para el patrón (Verde, Amarillo, Rojo)
    private final Color colorVerde = new Color(0.2f, 0.85f, 0.3f, 1f);
    private final Color colorAmarillo = new Color(0.95f, 0.8f, 0.2f, 1f);
    private final Color colorRojo = new Color(0.9f, 0.25f, 0.25f, 1f);
    private final Color colorBloqueado = new Color(0.3f, 0.3f, 0.35f, 0.6f);

    public SeleccionRivalScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        mouseWorld = new Vector3();
        font = new BitmapFont();
        layout = new GlyphLayout();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        pixelBlanco = new Texture(pixmap);
        pixmap.dispose();

        inicializarRivales();
        prepararFondo();
    }

    private void inicializarRivales() {
        this.listaRivales = game.getListaRivales();
        botonesJugar = new ArrayList<>();

        int indiceDesbloqueado = 0;
        for (int i = 0; i < listaRivales.size(); i++) {
            if (listaRivales.get(i).isDesbloqueado()) {
                indiceDesbloqueado = i;
                break;
            }
        }

        paginaActual = indiceDesbloqueado / 3;
        inicioIndice = paginaActual * 3;

        float anchoCuadro = 300;
        float espacio = 50;
        float xInicial = (1280 - (anchoCuadro * 3 + espacio * 2)) / 2f;
        float yCuadro = 180;

        for (int slot = 0; slot < 3; slot++) {
            float cuadroX = xInicial + slot * (anchoCuadro + espacio);
            Boton btn = new Boton(cuadroX + (anchoCuadro - 180) / 2f, yCuadro + 20, 180, 45, "JUGAR", Accion.JUGAR_CARTA);
            botonesJugar.add(btn);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.app.log("PANTALLA", "Renderizando SeleccionRivalScreen");
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);
        // --- LÓGICA / ENTRADA ---
        for (int slot = 0; slot < 3; slot++) {
            int indiceRivalActual = inicioIndice + slot;
            if (indiceRivalActual < listaRivales.size()) {
                DatosRival rival = listaRivales.get(indiceRivalActual);
                Boton btn = botonesJugar.get(slot);
                btn.setHabilitado(rival.isDesbloqueado());
                btn.update(mouseWorld.x, mouseWorld.y);
                if (btn.fueCliqueado()) {
                    game.setScreen(new GameScreenV2(game, rival));
                    return;
                }
            }
        }
        // --- RENDERIZADO ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // 1. Fondo Shader
        tiempoTranscurrido += delta;
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        game.batch.end();
        game.batch.setShader(null);
        // 2. Interfaz y Cuadros
        game.batch.begin();
        BitmapFont fuenteTitulo = game.getFuenteTitulo();
        layout.setText(fuenteTitulo, "SELECCIONA TU RIVAL");
        fuenteTitulo.draw(game.batch, "SELECCIONA TU RIVAL", 1280 / 2f - layout.width / 2f, 650);
        BitmapFont fuenteNormal = game.getFuentePrincipal();
        float anchoCuadro = 300;
        float altoCuadro = 410;
        float espacio = 50;
        float xInicial = (1280 - (anchoCuadro * 3 + espacio * 2)) / 2f;
        float yCuadro = 170;
        for (int slot = 0; slot < 3; slot++) {
            int indiceRival = inicioIndice + slot;
            if (indiceRival < listaRivales.size()) {
                DatosRival rival = listaRivales.get(indiceRival);
                float cuadroX = xInicial + slot * (anchoCuadro + espacio);
                // Obtener color característico del rival según su posición (0 = Verde, 1 = Amarillo, 2 = Rojo)
                Color colorRival = obtenerColorRival(indiceRival);
                Color colorBorde = rival.isDesbloqueado() ? colorRival : colorBloqueado;
                // A) Borde exterior (Marco de 3px de grosor)
                float grosorBorde = 3f;
                game.batch.setColor(colorBorde);
                game.batch.draw(pixelBlanco, cuadroX - grosorBorde, yCuadro - grosorBorde,
                    anchoCuadro + (grosorBorde * 2), altoCuadro + (grosorBorde * 2));
                // B) Fondo Principal del Cuadro
                if (rival.isDesbloqueado()) {
                    game.batch.setColor(0.10f, 0.10f, 0.14f, 0.92f);
                } else {
                    game.batch.setColor(0.06f, 0.06f, 0.08f, 0.85f);
                }
                game.batch.draw(pixelBlanco, cuadroX, yCuadro, anchoCuadro, altoCuadro);
                // C) Contenedor / Sombreo del Nombre (Cabecera)
                game.batch.setColor(0.04f, 0.04f, 0.06f, 0.8f);
                game.batch.draw(pixelBlanco, cuadroX + 10, yCuadro + altoCuadro - 55, anchoCuadro - 20, 45);
                // Texto Nombre
                game.batch.setColor(Color.WHITE);
                layout.setText(fuenteNormal, rival.getNombre());
                fuenteNormal.draw(game.batch, rival.getNombre(), cuadroX + (anchoCuadro - layout.width) / 2f, yCuadro + altoCuadro - 25);
                // D) Contenedor / Sombreo de Descripción
                game.batch.setColor(0.03f, 0.03f, 0.05f, 0.5f);
                game.batch.draw(pixelBlanco, cuadroX + 15, yCuadro + 145, anchoCuadro - 30, 195);
                // Texto Descripción
                game.batch.setColor(Color.WHITE);
                fuenteNormal.draw(game.batch, rival.getDescripcion(), cuadroX + 25, yCuadro + 325, anchoCuadro - 50, 1, true);
                // E) Placa de Meta (Mismo color que el borde del rival)
                game.batch.setColor(colorBorde);
                game.batch.draw(pixelBlanco, cuadroX + 20, yCuadro + 85, anchoCuadro - 40, 38);
                // Texto Meta (se dibuja en color blanco/oscuro para contrastar con la placa)
                String puntosTxt = "Meta: " + (int)rival.getPuntosMeta() + " pts";
                layout.setText(fuenteNormal, puntosTxt);
                game.batch.setColor(Color.WHITE);
                fuenteNormal.draw(game.batch, puntosTxt, cuadroX + (anchoCuadro - layout.width) / 2f, yCuadro + 110);
                // F) Dibujar Botón "JUGAR"
                botonesJugar.get(slot).render(game.batch);
            }
        }
        game.batch.end();
    }

    /**
     * Devuelve el color del rival en secuencia Verde -> Amarillo -> Rojo
     */
    private Color obtenerColorRival(int indice) {
        int patron = indice % 3;
        switch (patron) {
            case 0:
                return colorVerde;
            case 1:
                return colorAmarillo;
            case 2:
                return colorRojo;
            default:
                return colorVerde;
        }
    }

    private void prepararFondo() {
        this.fondoPlasma = new Background();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (pixelBlanco != null) pixelBlanco.dispose();
        if (fondoPlasma != null) fondoPlasma.dispose();
        if (font != null) font.dispose();
    }
}
