package io.github.HarryCodeProg.TrucoSurvivors.Vista;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import io.github.HarryCodeProg.TrucoSurvivors.Estados.Accion;
import io.github.HarryCodeProg.TrucoSurvivors.Main;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionJuego;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;
import io.github.HarryCodeProg.TrucoSurvivors.Screens.Background;

import java.util.ArrayList;

/**
 * Panel de Pausa que se superpone al GameScreenV2.
 * Contiene sub-paneles: PRINCIPAL, OPCIONES y PERSONALIZAR.
 */
public class PanelPausa {

    public enum Resultado { NINGUNO, REANUDAR, VOLVER_MENU }

    private enum SubPanel { PRINCIPAL, OPCIONES_MENU, OPCIONES_VIDEO, OPCIONES_AUDIO, OPCIONES_FONDO, PERSONALIZAR }

    private final Main game;
    private final ConfiguracionJuego config;
    private SubPanel subPanel = SubPanel.PRINCIPAL;
    private Resultado resultado = Resultado.NINGUNO;

    // --- Visibilidad y animación ---
    private boolean visible = false;
    private float alpha = 0f;
    private static final float FADE_SPEED = 5f;
    private boolean recienAbierto = false;

    // --- Constantes layout ---
    private static final float PANEL_W = 360f;
    private static final float PANEL_H = 400f;
    private static final float PANEL_X = (1280f - PANEL_W) / 2f;
    private static final float PANEL_Y = (720f - PANEL_H) / 2f;
    private static final float BTN_W = 280f;
    private static final float BTN_H = 52f;
    private static final float BTN_X = PANEL_X + (PANEL_W - BTN_W) / 2f;

    // --- Botones panel principal ---
    private final Boton botonReanudar;
    private final Boton botonOpciones;
    private final Boton botonPersonalizar;
    private final Boton botonVolverMenu;

    // --- Botones opciones (menú) ---
    private final Boton botonOpVideo;
    private final Boton botonOpAudio;
    private final Boton botonOpFondo;
    private final Boton botonOpVolver;
    private final Boton botonOpVelocidad;

    // --- Botones opciones video ---
    private final Boton botonModoVentana, botonResolucion, botonVsync;
    private final Boton botonAplicarVideo, botonDescartarVideo;
    private int modoVentanaPendiente, resolucionPendiente;
    private boolean vsyncPendiente;

    // --- Botones opciones audio ---
    private final Boton botonGenMenos, botonGenMas;
    private final Boton botonMusMenos, botonMusMas;
    private final Boton botonEfxMenos, botonEfxMas;
    private final Boton botonAudioVolver;

    // --- Botones opciones fondo ---
    private final Boton botonFondoAnt, botonFondoSig;
    private final Boton botonAplicarFondo, botonDescartarFondo;
    private int fondoIndexPendiente;

    private PanelPersonalizar panelPersonalizar;
    private final java.util.function.Supplier<Background> fondoActual;
    private final com.badlogic.gdx.graphics.OrthographicCamera camera;

    public PanelPausa(Main game, java.util.function.Supplier<Background> fondoActual, com.badlogic.gdx.graphics.OrthographicCamera camera) {
        this.game = game;
        this.fondoActual = fondoActual;
        this.camera = camera;
        this.config = game.getConfiguracionJuego();

        float cy = PANEL_Y + PANEL_H - 120f;
        float gap = 66f;

        // Panel principal
        botonReanudar    = new Boton(BTN_X, cy,          BTN_W, BTN_H, "REANUDAR",        Boton.TipoColor.VERDE,   Accion.OPCIONES);
        botonOpciones    = new Boton(BTN_X, cy - gap,     BTN_W, BTN_H, "OPCIONES",         Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonPersonalizar= new Boton(BTN_X, cy - gap*2f, BTN_W, BTN_H, "PERSONALIZAR", Boton.TipoColor.DORADO, Accion.OPCIONES);
        botonVolverMenu  = new Boton(BTN_X, cy - gap*3f,  BTN_W, BTN_H, "VOLVER AL MENÚ",  Boton.TipoColor.BORDO,   Accion.OPCIONES);

        // Opciones menú
        float ox = PANEL_X + (PANEL_W - 260f) / 2f;
        botonOpVideo = new Boton(ox, cy, 260f, 44f, "VIDEO", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonOpAudio = new Boton(ox, cy - 54f, 260f, 44f, "AUDIO", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonOpFondo = new Boton(ox, cy - 108f, 260f, 44f, "FONDO", Boton.TipoColor.CELESTE, Accion.OPCIONES);
        botonOpVelocidad = new Boton(ox, cy - 162f, 260f, 44f, "ACTIVACIONES: x" + config.getVelocidadActivaciones(), Boton.TipoColor.DORADO, Accion.OPCIONES);
        botonOpVolver = new Boton(ox, cy - 216f, 260f, 44f, "VOLVER", Boton.TipoColor.BORDO, Accion.OPCIONES);

        // Video
        float vx = PANEL_X + 20f;
        float vw = PANEL_W - 40f;
        modoVentanaPendiente = config.getModoVentana();
        resolucionPendiente  = config.getResolucion();
        vsyncPendiente       = config.isVsync();
        botonModoVentana    = new Boton(vx, cy,          vw, BTN_H, ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente], Accion.OPCIONES);
        botonResolucion     = new Boton(vx, cy - gap,    vw, BTN_H, ConfiguracionJuego.RESOLUCIONES[resolucionPendiente],   Accion.OPCIONES);
        botonVsync          = new Boton(vx, cy - gap*2f, vw, BTN_H, "VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"), Accion.OPCIONES);
        botonAplicarVideo   = new Boton(vx,             cy - gap*3f, (vw-10f)/2f, BTN_H, "APLICAR",   Accion.OPCIONES);
        botonDescartarVideo = new Boton(vx+(vw+10f)/2f, cy - gap*3f, (vw-10f)/2f, BTN_H, "DESCARTAR", Accion.OPCIONES);

        // Audio
        float ax = PANEL_X + (PANEL_W - 200f) / 2f;
        float am = ax - 90f, ap = ax + 155f;
        botonGenMenos = new Boton(am, cy,          45f, BTN_H, "-", Accion.OPCIONES);
        botonGenMas   = new Boton(ap, cy,          45f, BTN_H, "+", Accion.OPCIONES);
        botonMusMenos = new Boton(am, cy - gap,    45f, BTN_H, "-", Accion.OPCIONES);
        botonMusMas   = new Boton(ap, cy - gap,    45f, BTN_H, "+", Accion.OPCIONES);
        botonEfxMenos = new Boton(am, cy - gap*2f, 45f, BTN_H, "-", Accion.OPCIONES);
        botonEfxMas   = new Boton(ap, cy - gap*2f, 45f, BTN_H, "+", Accion.OPCIONES);
        botonAudioVolver = new Boton(ox, cy - gap*3f, 260f, BTN_H, "VOLVER", Boton.TipoColor.BORDO, Accion.OPCIONES);

        // Fondo
        botonFondoAnt      = new Boton(PANEL_X + 20f,              cy,          60f, BTN_H, "<", Accion.OPCIONES);
        botonFondoSig      = new Boton(PANEL_X + PANEL_W - 80f,    cy,          60f, BTN_H, ">", Accion.OPCIONES);
        botonAplicarFondo  = new Boton(vx,              cy - gap, (vw-10f)/2f, BTN_H, "APLICAR",   Accion.OPCIONES);
        botonDescartarFondo= new Boton(vx+(vw+10f)/2f, cy - gap, (vw-10f)/2f, BTN_H, "DESCARTAR", Accion.OPCIONES);

    }

    // ─── Ciclo de vida ───────────────────────────────────────────────
    public void abrir() {
        visible = true;
        resultado = Resultado.NINGUNO;
        subPanel = SubPanel.PRINCIPAL;
        recienAbierto = true;
    }

    public void cerrar() {
        if (panelPersonalizar != null) panelPersonalizar.cerrar();
        visible = false;
    }

    public boolean isVisible() { return visible; }

    public Resultado update(float delta, float mouseX, float mouseY) {

        // Fade
        if (visible) alpha = Math.min(1f, alpha + FADE_SPEED * delta);
        else         alpha = Math.max(0f, alpha - FADE_SPEED * delta);

        if (!visible) return Resultado.NINGUNO;

        // Ignorar el input del frame en que se abrió el panel
        if (recienAbierto) {
            recienAbierto = false;
            return Resultado.NINGUNO;
        }

        // Escape cierra el panel desde sub-paneles, o directamente reanuda desde principal
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (subPanel == SubPanel.PRINCIPAL) {
                cerrar();
                return Resultado.REANUDAR;
            } else {
                if (subPanel == SubPanel.PERSONALIZAR && !panelPersonalizar.retroceder()) return Resultado.NINGUNO;
                subPanel = SubPanel.PRINCIPAL;
                return Resultado.NINGUNO;
            }
        }

        switch (subPanel) {
            case PRINCIPAL:      return updatePrincipal(mouseX, mouseY);
            case OPCIONES_MENU:  updateOpcionesMenu(mouseX, mouseY); break;
            case OPCIONES_VIDEO: updateOpcionesVideo(mouseX, mouseY); break;
            case OPCIONES_AUDIO: updateOpcionesAudio(mouseX, mouseY); break;
            case OPCIONES_FONDO: updateOpcionesFondo(mouseX, mouseY); break;
            case PERSONALIZAR:
                if (panelPersonalizar.update(delta, mouseX, mouseY, Gdx.input.justTouched())) subPanel = SubPanel.PRINCIPAL;
                break;
        }
        return Resultado.NINGUNO;
    }

    private Resultado updatePrincipal(float mx, float my) {
        botonReanudar.update(mx, my);
        botonOpciones.update(mx, my);
        botonPersonalizar.update(mx, my);
        botonVolverMenu.update(mx, my);

        if (botonReanudar.fueCliqueado(mx, my)) {
            cerrar();
            return Resultado.REANUDAR;
        }
        if (botonOpciones.fueCliqueado(mx, my)) {
            subPanel = SubPanel.OPCIONES_MENU;
        }
        if (botonPersonalizar.fueCliqueado(mx, my)) {
            panelPersonalizar = new PanelPersonalizar(game, fondoActual.get());
            panelPersonalizar.abrir();
            subPanel = SubPanel.PERSONALIZAR;
        }
        if (botonVolverMenu.fueCliqueado(mx, my)) {
            cerrar();
            return Resultado.VOLVER_MENU;
        }
        return Resultado.NINGUNO;
    }

    private void updateOpcionesMenu(float mx, float my) {
        botonOpVelocidad.setTexto("ACTIVACIONES: x" + config.getVelocidadActivaciones());
        botonOpVelocidad.update(mx, my);
        if (botonOpVelocidad.fueCliqueado(mx, my)) {
            int actual = config.getVelocidadActivaciones();
            config.setVelocidadActivaciones(actual == 1 ? 2 : actual == 2 ? 4 : 1);
            config.guardar();
            botonOpVelocidad.setTexto("ACTIVACIONES: x" + config.getVelocidadActivaciones());
        }
        botonOpVideo.update(mx, my);
        botonOpAudio.update(mx, my);
        botonOpFondo.update(mx, my);
        botonOpVolver.update(mx, my);
        if (botonOpVideo.fueCliqueado(mx, my)) {
            modoVentanaPendiente = config.getModoVentana();
            resolucionPendiente  = config.getResolucion();
            vsyncPendiente       = config.isVsync();
            botonModoVentana.setTexto(ConfiguracionJuego.MODOS_VENTANA[modoVentanaPendiente]);
            botonResolucion.setTexto(ConfiguracionJuego.RESOLUCIONES[resolucionPendiente]);
            botonVsync.setTexto("VSYNC: " + (vsyncPendiente ? "ACTIVADO" : "DESACTIVADO"));
            subPanel = SubPanel.OPCIONES_VIDEO;
        }
        if (botonOpAudio.fueCliqueado(mx, my)) subPanel = SubPanel.OPCIONES_AUDIO;
        if (botonOpFondo.fueCliqueado(mx, my)) {
            fondoIndexPendiente = config.getFondoIndex();
            subPanel = SubPanel.OPCIONES_FONDO;
        }
        if (botonOpVolver.fueCliqueado(mx, my)) subPanel = SubPanel.PRINCIPAL;
    }

    private void updateOpcionesVideo(float mx, float my) {
        botonModoVentana.update(mx, my); botonResolucion.update(mx, my);
        botonVsync.update(mx, my); botonAplicarVideo.update(mx, my); botonDescartarVideo.update(mx, my);
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
            config.aplicarVideo(); config.guardar();
            subPanel = SubPanel.OPCIONES_MENU;
        }
        if (botonDescartarVideo.fueCliqueado(mx, my)) subPanel = SubPanel.OPCIONES_MENU;
    }

    private void updateOpcionesAudio(float mx, float my) {
        GestorSonidos sonidos = Main.getInstance().getGestorSonidos();
        botonGenMenos.update(mx,my); botonGenMas.update(mx,my);
        botonMusMenos.update(mx,my); botonMusMas.update(mx,my);
        botonEfxMenos.update(mx,my); botonEfxMas.update(mx,my);
        botonAudioVolver.update(mx, my);
        float paso = 0.1f; boolean cambio = false;
        if (botonGenMenos.fueCliqueado(mx,my)) { config.setVolumenGeneral(config.getVolumenGeneral()-paso); cambio=true; }
        if (botonGenMas.fueCliqueado(mx,my))   { config.setVolumenGeneral(config.getVolumenGeneral()+paso); cambio=true; }
        if (botonMusMenos.fueCliqueado(mx,my)) { config.setVolumenMusica(config.getVolumenMusica()-paso); cambio=true; }
        if (botonMusMas.fueCliqueado(mx,my))   { config.setVolumenMusica(config.getVolumenMusica()+paso); cambio=true; }
        if (botonEfxMenos.fueCliqueado(mx,my)) { config.setVolumenEfectos(config.getVolumenEfectos()-paso); cambio=true; }
        if (botonEfxMas.fueCliqueado(mx,my))   { config.setVolumenEfectos(config.getVolumenEfectos()+paso); cambio=true; }
        if (cambio) {
            config.aplicarAudio(sonidos);
            game.getMusicaFondo().setVolume(0.5f * config.getVolumenMusica());
            config.guardar();
            if (sonidos != null) sonidos.reproducirConVariacion("seleccionar");
        }
        if (botonAudioVolver.fueCliqueado(mx, my)) subPanel = SubPanel.OPCIONES_MENU;
    }

    private void updateOpcionesFondo(float mx, float my) {
        botonFondoAnt.update(mx,my); botonFondoSig.update(mx,my);
        botonAplicarFondo.update(mx,my); botonDescartarFondo.update(mx,my);
        if (botonFondoSig.fueCliqueado(mx,my)) {
            fondoIndexPendiente = (fondoIndexPendiente + 1) % Background.TEMAS.length;
        }
        if (botonFondoAnt.fueCliqueado(mx,my)) {
            fondoIndexPendiente = (fondoIndexPendiente - 1 + Background.TEMAS.length) % Background.TEMAS.length;
        }
        if (botonAplicarFondo.fueCliqueado(mx,my)) {
            config.setFondoIndex(fondoIndexPendiente);
            config.guardar();
            subPanel = SubPanel.OPCIONES_MENU;
        }
        if (botonDescartarFondo.fueCliqueado(mx,my)) {
            fondoIndexPendiente = config.getFondoIndex();
            subPanel = SubPanel.OPCIONES_MENU;
        }
    }

    public boolean scrolled(float amountY) {
        return visible && subPanel == SubPanel.PERSONALIZAR && panelPersonalizar.scrolled(amountY);
    }

    // ─── Render ──────────────────────────────────────────────────────
    public void render(SpriteBatch batch, float mouseX, float mouseY) {
        if (alpha <= 0f) return;
        Texture pixel = game.getPixelBlanco();
        BitmapFont fTitulo = game.getFuenteTitulo();
        BitmapFont fUI     = game.getFuenteUI();
        BitmapFont fPrinc  = game.getFuentePrincipal();

        if (subPanel == SubPanel.PERSONALIZAR) {
            renderDimBackground(batch, pixel);
            panelPersonalizar.render(batch, camera, Gdx.graphics.getDeltaTime());
            return;
        }

        renderDimBackground(batch, pixel);
        renderPanelBase(batch, pixel);

        // Título del sub-panel
        String titulo = subPanelTitulo();
        fTitulo.getData().setScale(0.85f);
        fTitulo.setColor(UITheme.DORADO_BRILLANTE.r, UITheme.DORADO_BRILLANTE.g, UITheme.DORADO_BRILLANTE.b, alpha);
        fTitulo.draw(batch, titulo, PANEL_X, PANEL_Y + PANEL_H - 22f, PANEL_W, Align.center, false);
        fTitulo.getData().setScale(1f);
        fTitulo.setColor(Color.WHITE);

        switch (subPanel) {
            case PRINCIPAL:      renderPrincipal(batch); break;
            case OPCIONES_MENU:  renderOpcionesMenu(batch); break;
            case OPCIONES_VIDEO: renderOpcionesVideo(batch); break;
            case OPCIONES_AUDIO: renderOpcionesAudio(batch, fPrinc, mouseX, mouseY); break;
            case OPCIONES_FONDO: renderOpcionesFondo(batch, fUI); break;
        }
    }

    private String subPanelTitulo() {
        switch (subPanel) {
            case OPCIONES_MENU:  return "OPCIONES";
            case OPCIONES_VIDEO: return "VIDEO";
            case OPCIONES_AUDIO: return "AUDIO";
            case OPCIONES_FONDO: return "FONDO";
            default: return "PAUSA";
        }
    }

    private void renderDimBackground(SpriteBatch batch, Texture pixel) {
        batch.setColor(0f, 0f, 0f, 0.6f * alpha);
        batch.draw(pixel, 0, 0, 1280f, 720f);
        batch.setColor(Color.WHITE);
    }

    private void renderPanelBase(SpriteBatch batch, Texture pixel) {
        // Sombra
        dibujarRect(batch, pixel, PANEL_X + 7f, PANEL_Y - 9f, PANEL_W, PANEL_H, 14f, new Color(0.01f, 0.015f, 0.025f, 0.7f * alpha));
        // Borde exterior
        dibujarRect(batch, pixel, PANEL_X - 4f, PANEL_Y - 4f, PANEL_W + 8f, PANEL_H + 8f, 14f, new Color(UITheme.BORDE.r, UITheme.BORDE.g, UITheme.BORDE.b, alpha));
        // Cuerpo del panel
        dibujarRect(batch, pixel, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 12f, new Color(UITheme.PANEL_PRINCIPAL.r, UITheme.PANEL_PRINCIPAL.g, UITheme.PANEL_PRINCIPAL.b, alpha));
        // Línea dorada superior
        batch.setColor(UITheme.DORADO.r, UITheme.DORADO.g, UITheme.DORADO.b, alpha);
        batch.draw(pixel, PANEL_X + 18f, PANEL_Y + PANEL_H - 5f, PANEL_W - 36f, 3f);
        batch.setColor(Color.WHITE);
    }

    private void renderPrincipal(SpriteBatch batch) {
        setAlpha(botonReanudar); botonReanudar.render(batch);
        setAlpha(botonOpciones); botonOpciones.render(batch);
        setAlpha(botonPersonalizar); botonPersonalizar.render(batch);
        setAlpha(botonVolverMenu); botonVolverMenu.render(batch);
    }

    private void renderOpcionesMenu(SpriteBatch batch) {
        setAlpha(botonOpVideo);  botonOpVideo.render(batch);
        setAlpha(botonOpAudio);  botonOpAudio.render(batch);
        setAlpha(botonOpFondo);  botonOpFondo.render(batch);
        setAlpha(botonOpVelocidad); botonOpVelocidad.render(batch);
        setAlpha(botonOpVolver); botonOpVolver.render(batch);
    }

    private void renderOpcionesVideo(SpriteBatch batch) {
        setAlpha(botonModoVentana); botonModoVentana.render(batch);
        setAlpha(botonResolucion);  botonResolucion.render(batch);
        setAlpha(botonVsync);       botonVsync.render(batch);
        setAlpha(botonAplicarVideo);   botonAplicarVideo.render(batch);
        setAlpha(botonDescartarVideo); botonDescartarVideo.render(batch);
    }

    private void renderOpcionesAudio(SpriteBatch batch, BitmapFont fPrinc, float mx, float my) {
        dibujarFilaVolumen(batch, fPrinc, "General", config.getVolumenGeneral(),
            botonGenMenos.getY() + BTN_H / 2f + 8f, mx);
        dibujarFilaVolumen(batch, fPrinc, "Música", config.getVolumenMusica(),
            botonMusMenos.getY() + BTN_H / 2f + 8f, mx);
        dibujarFilaVolumen(batch, fPrinc, "Efectos", config.getVolumenEfectos(),
            botonEfxMenos.getY() + BTN_H / 2f + 8f, mx);
        setAlpha(botonGenMenos); botonGenMenos.render(batch);
        setAlpha(botonGenMas);   botonGenMas.render(batch);
        setAlpha(botonMusMenos); botonMusMenos.render(batch);
        setAlpha(botonMusMas);   botonMusMas.render(batch);
        setAlpha(botonEfxMenos); botonEfxMenos.render(batch);
        setAlpha(botonEfxMas);   botonEfxMas.render(batch);
        setAlpha(botonAudioVolver); botonAudioVolver.render(batch);
    }

    private void renderOpcionesFondo(SpriteBatch batch, BitmapFont fUI) {
        String nombreTema = Background.TEMAS[fondoIndexPendiente].nombre;
        fUI.setColor(UITheme.TEXTO_PRINCIPAL.r, UITheme.TEXTO_PRINCIPAL.g, UITheme.TEXTO_PRINCIPAL.b, alpha);
        fUI.draw(batch, nombreTema, PANEL_X, PANEL_Y + PANEL_H - 80f, PANEL_W, Align.center, false);
        fUI.setColor(Color.WHITE);
        setAlpha(botonFondoAnt);      botonFondoAnt.render(batch);
        setAlpha(botonFondoSig);      botonFondoSig.render(batch);
        setAlpha(botonAplicarFondo);  botonAplicarFondo.render(batch);
        setAlpha(botonDescartarFondo);botonDescartarFondo.render(batch);
    }

    // ─── Helpers ─────────────────────────────────────────────────────
    private void setAlpha(Boton b) {
        // Los botones usan alpha internamente – no hay API, así que simplemente los dibujamos normal.
        // Si la clase Boton soporta color tint, se puede añadir aquí.
    }

    private Color withAlpha(Color src, float a) {
        return new Color(src.r, src.g, src.b, src.a * a);
    }

    private void dibujarRect(SpriteBatch batch, Texture pixel,
                             float x, float y, float width, float height,
                             float radio, Color color) {
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

    private void dibujarFilaVolumen(SpriteBatch batch, BitmapFont f, String etiqueta, float valor, float y, float mx) {
        String texto = etiqueta + ": " + Math.round(valor * 100f) + "%";
        f.setColor(UITheme.TEXTO_PRINCIPAL.r, UITheme.TEXTO_PRINCIPAL.g, UITheme.TEXTO_PRINCIPAL.b, alpha);
        f.draw(batch, texto, PANEL_X, y, PANEL_W, Align.center, false);
        f.setColor(Color.WHITE);
    }
}
