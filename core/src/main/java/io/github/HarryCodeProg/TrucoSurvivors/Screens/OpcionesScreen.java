package io.github.HarryCodeProg.TrucoSurvivors.Screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.Boton;
import io.github.HarryCodeProg.TrucoSurvivors.Vista.UITheme;

public class OpcionesScreen {
    private final Main game;
    private final BitmapFont font;
    private final ConfiguracionJuego config;
    private enum Panel { MENU, VIDEO, AUDIO }
    private Panel panel = Panel.MENU;
    // Menú principal
    private final Boton botonVideo, botonAudio, botonVolver;
    // Panel video
    private final Boton botonModoVentana, botonResolucion, botonVsync, botonAplicarVideo, botonDescartarVideo;
    private int modoVentanaPendiente, resolucionPendiente;
    private boolean vsyncPendiente;
    // Panel audio
    private final Boton botonGeneralMenos, botonGeneralMas, botonMusicaMenos, botonMusicaMas;
    private final Boton botonEfectosMenos, botonEfectosMas, botonVolverAudio;

    public OpcionesScreen(Main game) {
        this.game = game;
        this.config = game.getConfiguracionJuego();
        this.font = game.getFuenteTitulo();
        float centroX = 1280f / 2f;
        float anchoBoton = 220f;
        float xCentral = centroX - anchoBoton / 2f;
        // Menú principal
        botonVideo = new Boton(xCentral, 420f, anchoBoton, 60f, "VIDEO",  Accion.OPCIONES);
        botonAudio = new Boton(xCentral, 330f, anchoBoton, 60f, "AUDIO",  Accion.OPCIONES);
        botonVolver = new Boton(xCentral, 200f, anchoBoton, 60f, "VOLVER", Accion.IR_AL_MAZO);
        float x = centroX - 180f;
        modoVentanaPendiente = config.getModoVentana();
        resolucionPendiente = config.getResolucion();
        vsyncPendiente = config.isVsync();
        botonModoVentana = new Boton(x, 430f, 360f, 50f, ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente], Accion.OPCIONES);
        botonResolucion = new Boton(x, 350f, 360f, 50f, ConfiguracionJuego.RESOLUCIONES[resolucionPendiente],   Accion.OPCIONES);
        botonVsync = new Boton(x, 270f, 360f, 50f, "VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"), Accion.OPCIONES);
        botonAplicarVideo = new Boton(x, 150f, 170f, 50f, "APLICAR",   Accion.OPCIONES);
        botonDescartarVideo = new Boton(x + 190f, 150f, 170f, 50f, "DESCARTAR", Accion.OPCIONES);
        float anchoFlecha = 45f;
        float xMenos = centroX - 150f, xMas = centroX + 105f;
        botonGeneralMenos = new Boton(xMenos, 430f, anchoFlecha, 50f, "-", Accion.OPCIONES);
        botonGeneralMas   = new Boton(xMas,   430f, anchoFlecha, 50f, "+", Accion.OPCIONES);
        botonMusicaMenos  = new Boton(xMenos, 350f, anchoFlecha, 50f, "-", Accion.OPCIONES);
        botonMusicaMas    = new Boton(xMas,   350f, anchoFlecha, 50f, "+", Accion.OPCIONES);
        botonEfectosMenos = new Boton(xMenos, 270f, anchoFlecha, 50f, "-", Accion.OPCIONES);
        botonEfectosMas   = new Boton(xMas,   270f, anchoFlecha, 50f, "+", Accion.OPCIONES);
        botonVolverAudio  = new Boton(centroX - 110f, 150f, 220f, 50f, "VOLVER", Accion.OPCIONES);
    }

    public void abrir() {
        panel = Panel.MENU;
    }

    public boolean update(float delta, float mx, float my, boolean click) {
        switch (panel) {
            case MENU:
                if (updateMenu(mx, my)) return true;
                break;
            case VIDEO:
                updateVideo(mx, my);
                break;
            case AUDIO:
                updateAudio(mx, my);
                break;
        }
        return false;
    }

    public void render(SpriteBatch batch, float delta) {
        dibujarFondoPanel(batch);
        //font.draw(batch, "OPCIONES", 0, 560f, 1280f, Align.center, false);
        switch (panel) {
            case MENU:  renderMenu(batch);  break;
            case VIDEO: renderVideo(batch); break;
            case AUDIO: renderAudio(batch); break;
        }
    }

    private void dibujarFondoPanel(SpriteBatch batch) {
        Texture pixel = game.getPixelBlanco();
        // Por defecto, tamaño para los menús de Video y Audio
        float boxW = 400f;
        float boxH = 380f;
        float boxX = (1280f - boxW) / 2f;
        float boxY = 130f; // 150f de los botones - 20f de padding
        // Si estamos en el menú principal, es más angosto
        if (panel == Panel.MENU) {
            boxW = 280f;
            boxH = 320f;
            boxX = (1280f - boxW) / 2f;
            boxY = 180f; // 200f de los botones - 20f de padding
        }
        // Sombra
        dibujarRectRedondeado(batch, pixel, boxX + 6f, boxY - 8f, boxW, boxH, 12f, UITheme.SOMBRA);
        // Borde negro
        dibujarRectRedondeado(batch, pixel, boxX - 4f, boxY - 4f, boxW + 8f, boxH + 8f, 13f, UITheme.BORDE);
        // Fondo interior
        dibujarRectRedondeado(batch, pixel, boxX, boxY, boxW, boxH, 10f, UITheme.PANEL_PRINCIPAL);
        // Línea dorada decorativa arriba
        batch.setColor(UITheme.DORADO);
        batch.draw(pixel, boxX + 18f, boxY + boxH - 6f, boxW - 36f, 3f);
        batch.setColor(Color.WHITE);
    }

    private boolean updateMenu(float mx, float my) {
        botonVideo.update(mx, my);
        botonAudio.update(mx, my);
        botonVolver.update(mx, my);
        if (botonVideo.fueCliqueado(mx, my)) {
            modoVentanaPendiente = config.getModoVentana();
            resolucionPendiente  = config.getResolucion();
            vsyncPendiente       = config.isVsync();
            botonModoVentana.setTexto(ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente]);
            botonResolucion.setTexto(ConfiguracionJuego.RESOLUCIONES[resolucionPendiente]);
            botonVsync.setTexto("VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"));
            panel = Panel.VIDEO;
        }
        if (botonAudio.fueCliqueado(mx, my)) {
            panel = Panel.AUDIO;
        }
        if (botonVolver.fueCliqueado(mx, my)) {
            return true;
        }
        return false;
    }

    private void updateVideo(float mx, float my) {
        botonModoVentana.update(mx, my);
        botonResolucion.update(mx, my);
        botonVsync.update(mx, my);
        botonAplicarVideo.update(mx, my);
        botonDescartarVideo.update(mx, my);
        if (botonModoVentana.fueCliqueado(mx, my)) {
            modoVentanaPendiente = (modoVentanaPendiente + 1) % ConfiguracionJuego.MODOS_VENTANA.length;
            botonModoVentana.setTexto(ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente]);
        }
        if (botonResolucion.fueCliqueado(mx, my)) {
            resolucionPendiente = (resolucionPendiente + 1) % ConfiguracionJuego.RESOLUCIONES.length;
            botonResolucion.setTexto(ConfiguracionJuego.RESOLUCIONES[resolucionPendiente]);
        }
        if (botonVsync.fueCliqueado(mx, my)) {
            vsyncPendiente = !vsyncPendiente;
            botonVsync.setTexto("VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"));
        }
        if (botonAplicarVideo.fueCliqueado(mx, my)) {
            config.setModoVentana(modoVentanaPendiente);
            config.setResolucion(resolucionPendiente);
            config.setVsync(vsyncPendiente);
            config.aplicarVideo();
            config.guardar();
            panel = Panel.MENU;
        }
        if (botonDescartarVideo.fueCliqueado(mx, my)) {
            panel = Panel.MENU;
        }
    }

    private void updateAudio(float mx, float my) {
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        botonGeneralMenos.update(mx, my); botonGeneralMas.update(mx, my);
        botonMusicaMenos.update(mx, my);  botonMusicaMas.update(mx, my);
        botonEfectosMenos.update(mx, my); botonEfectosMas.update(mx, my);
        botonVolverAudio.update(mx, my);
        float paso = 0.1f; boolean cambio = false;
        if (botonGeneralMenos.fueCliqueado(mx, my)) { config.setVolumenGeneral(config.getVolumenGeneral() - paso); cambio = true; }
        if (botonGeneralMas.fueCliqueado(mx, my))   { config.setVolumenGeneral(config.getVolumenGeneral() + paso); cambio = true; }
        if (botonMusicaMenos.fueCliqueado(mx, my))  { config.setVolumenMusica(config.getVolumenMusica() - paso); cambio = true; }
        if (botonMusicaMas.fueCliqueado(mx, my))    { config.setVolumenMusica(config.getVolumenMusica() + paso); cambio = true; }
        if (botonEfectosMenos.fueCliqueado(mx, my)) { config.setVolumenEfectos(config.getVolumenEfectos() - paso); cambio = true; }
        if (botonEfectosMas.fueCliqueado(mx, my))   { config.setVolumenEfectos(config.getVolumenEfectos() + paso); cambio = true; }
        if (cambio) {
            config.aplicarAudio(sonidos);
            game.getMusicaFondo().setVolume(0.5f * config.getVolumenMusica());
            config.guardar();
            if (sonidos != null) sonidos.reproducirConVariacion("seleccionar");
        }
        if (botonVolverAudio.fueCliqueado(mx, my)) {
            panel = Panel.MENU;
        }
    }

    private void renderMenu(SpriteBatch batch) {
        botonVideo.render(batch);
        botonAudio.render(batch);
        botonVolver.render(batch);
    }

    private void renderVideo(SpriteBatch batch) {
        botonModoVentana.render(batch); botonResolucion.render(batch);
        botonVsync.render(batch); botonAplicarVideo.render(batch); botonDescartarVideo.render(batch);
    }

    private void renderAudio(SpriteBatch batch) {
        BitmapFont f = game.getFuentePrincipal();
        dibujarFilaVolumen(batch, f, "General", config.getVolumenGeneral(), 445f);
        dibujarFilaVolumen(batch, f, "Música",  config.getVolumenMusica(),  365f);
        dibujarFilaVolumen(batch, f, "Efectos", config.getVolumenEfectos(), 285f);
        botonGeneralMenos.render(batch); botonGeneralMas.render(batch);
        botonMusicaMenos.render(batch);  botonMusicaMas.render(batch);
        botonEfectosMenos.render(batch); botonEfectosMas.render(batch);
        botonVolverAudio.render(batch);
    }

    private void dibujarFilaVolumen(SpriteBatch batch, BitmapFont f, String etiqueta, float valor, float y) {
        float centroX = 1280f / 2f;
        String texto = etiqueta + ": " + Math.round(valor * 100f) + "%";
        f.draw(batch, texto, centroX - 300f, y + 35f, 600f, Align.center, false);
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
}
