package io.github.HarryCodeProg.TrucoSurvivors.Modelo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorSonidos;

public class ConfiguracionJuego {
    public static final String[] MODOS_VENTANA = {"PANTALLA COMPLETA", "VENTANA", "VENTANA SIN BORDES"};
    public static final String[] RESOLUCIONES = {"1280x720", "1360x768", "1600x900", "1920x1080"};
    private static final String PREFS_NOMBRE = "truco-survivors-config";
    private int modoVentana = 0;
    private int resolucion = 0;
    private boolean vsync = true;
    private float volumenGeneral = 1f;
    private float volumenMusica = 1f;
    private float volumenEfectos = 1f;
    private int fondoIndex = 0;
    private int musicaIndex = 0;

    public void cargar() {
        Preferences p = Gdx.app.getPreferences(PREFS_NOMBRE);
        modoVentana = p.getInteger("modoVentana", 0);
        resolucion = p.getInteger("resolucion", 0);
        vsync = p.getBoolean("vsync", true);
        volumenGeneral = p.getFloat("volumenGeneral", 1f);
        volumenMusica = p.getFloat("volumenMusica", 1f);
        volumenEfectos = p.getFloat("volumenEfectos", 1f);
        fondoIndex = p.getInteger("fondoIndex", 0); // ya debería estar, lo agrego por si faltaba
        musicaIndex = p.getInteger("musicaIndex", 0);
    }

    public void guardar() {
        Preferences p = Gdx.app.getPreferences(PREFS_NOMBRE);
        p.putInteger("modoVentana", modoVentana);
        p.putInteger("resolucion", resolucion);
        p.putBoolean("vsync", vsync);
        p.putFloat("volumenGeneral", volumenGeneral);
        p.putFloat("volumenMusica", volumenMusica);
        p.putFloat("volumenEfectos", volumenEfectos);
        p.putInteger("fondoIndex", fondoIndex);
        p.putInteger("musicaIndex", musicaIndex);
        p.flush();
    }

    public int getMusicaIndex() { return musicaIndex; }
    public void setMusicaIndex(int v) { musicaIndex = v; }

    /** Aplica modo de ventana / resolución / vsync ahora mismo. "Sin bordes" queda como ventana
     * normal (no se puede sacar el borde real sin GLFW, y core no puede depender del módulo lwjgl3). */
    public void aplicarVideo() {
        String[] partes = RESOLUCIONES[resolucion].split("x");
        int ancho = Integer.parseInt(partes[0]);
        int alto = Integer.parseInt(partes[1]);
        switch (modoVentana) {
            case 0:
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                break;
            case 1:
            case 2:
                Gdx.graphics.setWindowedMode(ancho, alto);
                break;
        }
        Gdx.graphics.setVSync(vsync);
    }

    public void aplicarAudio(GestorSonidos gestorSonidos) {
        if (gestorSonidos == null) return;
        gestorSonidos.setVolumenGeneral(volumenGeneral);
        gestorSonidos.setVolumenMusica(volumenMusica);
        gestorSonidos.setVolumenEfectos(volumenEfectos);
    }

    public int getModoVentana() { return modoVentana; }
    public void setModoVentana(int v) { modoVentana = v; }
    public int getResolucion() { return resolucion; }
    public void setResolucion(int v) { resolucion = v; }
    public boolean isVsync() { return vsync; }
    public void setVsync(boolean v) { vsync = v; }
    public float getVolumenGeneral() { return volumenGeneral; }
    public void setVolumenGeneral(float v) { volumenGeneral = clamp(v); }
    public float getVolumenMusica() { return volumenMusica; }
    public void setVolumenMusica(float v) { volumenMusica = clamp(v); }
    public float getVolumenEfectos() { return volumenEfectos; }
    public void setVolumenEfectos(float v) { volumenEfectos = clamp(v); }

    private float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }

    public int getFondoIndex() { return fondoIndex; }
    public void setFondoIndex(int fondoIndex) { this.fondoIndex = fondoIndex; }
}
