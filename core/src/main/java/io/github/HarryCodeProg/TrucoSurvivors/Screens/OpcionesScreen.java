package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;

public class OpcionesScreen implements Screen {
    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Vector3 mouseWorld;
    private final BitmapFont font;
    private final ConfiguracionJuego config;
    private Background fondoPlasma;

    private enum Panel { MENU, VIDEO, AUDIO, FONDO }
    private Panel panel = Panel.MENU;

    // Menú principal
    private final Boton botonVideo, botonAudio, botonFondo, botonVolver;

    // Panel video
    private final Boton botonModoVentana, botonResolucion, botonVsync, botonAplicarVideo, botonDescartarVideo;
    private int modoVentanaPendiente, resolucionPendiente;
    private boolean vsyncPendiente;

    // Panel audio
    private final Boton botonGeneralMenos, botonGeneralMas, botonMusicaMenos, botonMusicaMas;
    private final Boton botonEfectosMenos, botonEfectosMas, botonVolverAudio;

    // Panel fondo
    private final Boton botonFondoAnterior, botonFondoSiguiente, botonAplicarFondo, botonDescartarFondo;
    private int fondoIndexPendiente;

    public OpcionesScreen(Main game) {
        this.game = game;
        this.config = game.getConfiguracionJuego();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        mouseWorld = new Vector3();
        font = game.getFuenteTitulo();
        fondoPlasma = new Background();
        fondoPlasma.setTema(config.getFondoIndex()); // Cargamos el fondo guardado

        float centroX = viewport.getWorldWidth() / 2f;
        float anchoBoton = 220f;
        float xCentral = centroX - anchoBoton / 2f;

        botonVideo = new Boton(xCentral, 430f, anchoBoton, 60f, "VIDEO", Accion.OPCIONES);
        botonAudio = new Boton(xCentral, 350f, anchoBoton, 60f, "AUDIO", Accion.OPCIONES);
        botonFondo = new Boton(xCentral, 270f, anchoBoton, 60f, "FONDO", Accion.OPCIONES);
        botonVolver = new Boton(xCentral, 150f, anchoBoton, 60f, "VOLVER", Accion.IR_AL_MAZO);

        float x = centroX - 180f;
        modoVentanaPendiente = config.getModoVentana();
        resolucionPendiente = config.getResolucion();
        vsyncPendiente = config.isVsync();
        botonModoVentana = new Boton(x, 430f, 360f, 50f, ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente], Accion.OPCIONES);
        botonResolucion = new Boton(x, 350f, 360f, 50f, ConfiguracionJuego.RESOLUCIONES[resolucionPendiente], Accion.OPCIONES);
        botonVsync = new Boton(x, 270f, 360f, 50f, "VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"), Accion.OPCIONES);
        botonAplicarVideo = new Boton(x, 150f, 170f, 50f, "APLICAR", Accion.OPCIONES);
        botonDescartarVideo = new Boton(x + 190f, 150f, 170f, 50f, "DESCARTAR", Accion.OPCIONES);

        float anchoFlecha = 45f;
        float xMenos = centroX - 150f, xMas = centroX + 105f;
        botonGeneralMenos = new Boton(xMenos, 430f, anchoFlecha, 50f, "-", Accion.OPCIONES);
        botonGeneralMas = new Boton(xMas, 430f, anchoFlecha, 50f, "+", Accion.OPCIONES);
        botonMusicaMenos = new Boton(xMenos, 350f, anchoFlecha, 50f, "-", Accion.OPCIONES);
        botonMusicaMas = new Boton(xMas, 350f, anchoFlecha, 50f, "+", Accion.OPCIONES);
        botonEfectosMenos = new Boton(xMenos, 270f, anchoFlecha, 50f, "-", Accion.OPCIONES);
        botonEfectosMas = new Boton(xMas, 270f, anchoFlecha, 50f, "+", Accion.OPCIONES);
        botonVolverAudio = new Boton(centroX - 110f, 150f, 220f, 50f, "VOLVER", Accion.OPCIONES);

        fondoIndexPendiente = config.getFondoIndex();
        botonFondoAnterior = new Boton(centroX - 250f, 320f, 60f, 50f, "<", Accion.OPCIONES);
        botonFondoSiguiente = new Boton(centroX + 190f, 320f, 60f, 50f, ">", Accion.OPCIONES);
        botonAplicarFondo = new Boton(centroX - 180f, 150f, 170f, 50f, "APLICAR", Accion.OPCIONES);
        botonDescartarFondo = new Boton(centroX + 10f, 150f, 170f, 50f, "DESCARTAR", Accion.OPCIONES);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        switch (panel) {
            case MENU: updateMenu(); break;
            case VIDEO: updateVideo(); break;
            case AUDIO: updateAudio(); break;
            case FONDO: updateFondo(); break;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        fondoPlasma.render(game.batch, delta);
        font.draw(game.batch, "OPCIONES", 0, 580f, 1280f, com.badlogic.gdx.utils.Align.center, false);
        switch (panel) {
            case MENU: renderMenu(); break;
            case VIDEO: renderVideo(); break;
            case AUDIO: renderAudio(); break;
            case FONDO: renderFondo(); break;
        }
        game.batch.end();
    }

    private void updateMenu() {
        botonVideo.update(mouseWorld.x, mouseWorld.y);
        botonAudio.update(mouseWorld.x, mouseWorld.y);
        botonFondo.update(mouseWorld.x, mouseWorld.y);
        botonVolver.update(mouseWorld.x, mouseWorld.y);
        if (botonVideo.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            modoVentanaPendiente = config.getModoVentana();
            resolucionPendiente = config.getResolucion();
            vsyncPendiente = config.isVsync();
            botonModoVentana.setTexto(ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente]);
            botonResolucion.setTexto(ConfiguracionJuego.RESOLUCIONES[resolucionPendiente]);
            botonVsync.setTexto("VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"));
            panel = Panel.VIDEO;
        }
        if (botonAudio.fueCliqueado(mouseWorld.x, mouseWorld.y)) panel = Panel.AUDIO;
        if (botonFondo.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            fondoIndexPendiente = config.getFondoIndex();
            fondoPlasma.setTema(fondoIndexPendiente);
            panel = Panel.FONDO;
        }
        if (botonVolver.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    private void updateVideo() {
        botonModoVentana.update(mouseWorld.x, mouseWorld.y);
        botonResolucion.update(mouseWorld.x, mouseWorld.y);
        botonVsync.update(mouseWorld.x, mouseWorld.y);
        botonAplicarVideo.update(mouseWorld.x, mouseWorld.y);
        botonDescartarVideo.update(mouseWorld.x, mouseWorld.y);
        if (botonModoVentana.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            modoVentanaPendiente = (modoVentanaPendiente + 1) % ConfiguracionJuego.MODOS_VENTANA.length;
            botonModoVentana.setTexto(ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente]);
        }
        if (botonResolucion.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            resolucionPendiente = (resolucionPendiente + 1) % ConfiguracionJuego.RESOLUCIONES.length;
            botonResolucion.setTexto(ConfiguracionJuego.RESOLUCIONES[resolucionPendiente]);
        }
        if (botonVsync.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            vsyncPendiente = !vsyncPendiente;
            botonVsync.setTexto("VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"));
        }
        if (botonAplicarVideo.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            config.setModoVentana(modoVentanaPendiente);
            config.setResolucion(resolucionPendiente);
            config.setVsync(vsyncPendiente);
            config.aplicarVideo();
            config.guardar();
            panel = Panel.MENU;
        }
        if (botonDescartarVideo.fueCliqueado(mouseWorld.x, mouseWorld.y)) panel = Panel.MENU;
    }

    private void updateAudio() {
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        botonGeneralMenos.update(mouseWorld.x, mouseWorld.y); botonGeneralMas.update(mouseWorld.x, mouseWorld.y);
        botonMusicaMenos.update(mouseWorld.x, mouseWorld.y); botonMusicaMas.update(mouseWorld.x, mouseWorld.y);
        botonEfectosMenos.update(mouseWorld.x, mouseWorld.y); botonEfectosMas.update(mouseWorld.x, mouseWorld.y);
        botonVolverAudio.update(mouseWorld.x, mouseWorld.y);
        float paso = 0.1f; boolean cambio = false;
        if (botonGeneralMenos.fueCliqueado(mouseWorld.x, mouseWorld.y)) { config.setVolumenGeneral(config.getVolumenGeneral() - paso); cambio = true; }
        if (botonGeneralMas.fueCliqueado(mouseWorld.x, mouseWorld.y)) { config.setVolumenGeneral(config.getVolumenGeneral() + paso); cambio = true; }
        if (botonMusicaMenos.fueCliqueado(mouseWorld.x, mouseWorld.y)) { config.setVolumenMusica(config.getVolumenMusica() - paso); cambio = true; }
        if (botonMusicaMas.fueCliqueado(mouseWorld.x, mouseWorld.y)) { config.setVolumenMusica(config.getVolumenMusica() + paso); cambio = true; }
        if (botonEfectosMenos.fueCliqueado(mouseWorld.x, mouseWorld.y)) { config.setVolumenEfectos(config.getVolumenEfectos() - paso); cambio = true; }
        if (botonEfectosMas.fueCliqueado(mouseWorld.x, mouseWorld.y)) { config.setVolumenEfectos(config.getVolumenEfectos() + paso); cambio = true; }
        if (cambio) {
            config.aplicarAudio(sonidos);
            game.getMusicaFondo().setVolume(0.05f * config.getVolumenMusica());
            config.guardar();
            if (sonidos != null) sonidos.reproducirConVariacion("seleccionar");
        }
        if (botonVolverAudio.fueCliqueado(mouseWorld.x, mouseWorld.y)) panel = Panel.MENU;
    }

    private void updateFondo() {
        botonFondoAnterior.update(mouseWorld.x, mouseWorld.y);
        botonFondoSiguiente.update(mouseWorld.x, mouseWorld.y);
        botonAplicarFondo.update(mouseWorld.x, mouseWorld.y);
        botonDescartarFondo.update(mouseWorld.x, mouseWorld.y);

        if (botonFondoSiguiente.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            fondoIndexPendiente = (fondoIndexPendiente + 1) % Background.TEMAS.length;
            fondoPlasma.setTema(fondoIndexPendiente); // ¡Previsualiza en vivo!
        }
        if (botonFondoAnterior.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            fondoIndexPendiente = (fondoIndexPendiente - 1 + Background.TEMAS.length) % Background.TEMAS.length;
            fondoPlasma.setTema(fondoIndexPendiente); // ¡Previsualiza en vivo!
        }
        if (botonAplicarFondo.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            config.setFondoIndex(fondoIndexPendiente);
            config.guardar();
            panel = Panel.MENU;
        }
        if (botonDescartarFondo.fueCliqueado(mouseWorld.x, mouseWorld.y)) {
            fondoPlasma.setTema(config.getFondoIndex()); // Restaura el original
            panel = Panel.MENU;
        }
    }

    private void renderMenu() {
        botonVideo.render(game.batch);
        botonAudio.render(game.batch);
        botonFondo.render(game.batch);
        botonVolver.render(game.batch);
    }

    private void renderVideo() {
        botonModoVentana.render(game.batch); botonResolucion.render(game.batch);
        botonVsync.render(game.batch); botonAplicarVideo.render(game.batch); botonDescartarVideo.render(game.batch);
    }

    private void renderAudio() {
        BitmapFont f = game.getFuentePrincipal();
        dibujarFilaVolumen(f, "General", config.getVolumenGeneral(), 445f);
        dibujarFilaVolumen(f, "Música", config.getVolumenMusica(), 365f);
        dibujarFilaVolumen(f, "Efectos", config.getVolumenEfectos(), 285f);
        botonGeneralMenos.render(game.batch); botonGeneralMas.render(game.batch);
        botonMusicaMenos.render(game.batch); botonMusicaMas.render(game.batch);
        botonEfectosMenos.render(game.batch); botonEfectosMas.render(game.batch);
        botonVolverAudio.render(game.batch);
    }

    private void renderFondo() {
        botonFondoAnterior.render(game.batch);
        botonFondoSiguiente.render(game.batch);
        botonAplicarFondo.render(game.batch);
        botonDescartarFondo.render(game.batch);

        // Dibujamos el nombre del tema actual seleccionado en el centro
        BitmapFont f = game.getFuentePrincipal();
        String nombreTema = Background.TEMAS[fondoIndexPendiente].nombre;
        f.draw(game.batch, nombreTema, 0, 360f, 1280f, com.badlogic.gdx.utils.Align.center, false);
    }

    private void dibujarFilaVolumen(BitmapFont f, String etiqueta, float valor, float y) {
        float centroX = viewport.getWorldWidth() / 2f;
        String texto = etiqueta + ": " + Math.round(valor * 100f) + "%";
        f.draw(game.batch, texto, centroX - 300f, y + 35f, 600f, com.badlogic.gdx.utils.Align.center, false);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void show() {} @Override public void pause() {} @Override public void resume() {} @Override public void hide() {}
    @Override public void dispose() { if (fondoPlasma != null) fondoPlasma.dispose(); }
}
