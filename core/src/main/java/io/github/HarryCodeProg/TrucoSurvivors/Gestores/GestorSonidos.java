package io.github.HarryCodeProg.TrucoSurvivors.Gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

public class GestorSonidos implements Disposable {
    private final HashMap<String, Sound> sonidos;
    private float volumenGeneral = 1f;   // FIX: master, multiplica a todo
    private float volumenEfectos = 0.2f;   // FIX: multiplica efectos (todo lo cargado acá hoy son efectos)
    private float volumenMusica = 1f;    // FIX: reservado para cuando haya música de fondo
    private boolean silenciado = false;
    private boolean alternarReparto = false;

    public GestorSonidos() {
        sonidos = new HashMap<>();
        cargarSonidos();
    }

    private void cargarSonidos() {
        cargar("seleccionar", "sonidos/seleccionar.ogg");
        cargar("deseleccionar", "sonidos/deseleccionar.ogg");
        cargar("reparto1", "sonidos/reparto1.ogg");
        cargar("reparto2", "sonidos/reparto2.ogg");
        cargar("activar_carta", "sonidos/activar_carta.ogg");
        cargar("activar_joker", "sonidos/activar_joker.ogg");
        cargar("ganar-peso1", "sonidos/gano-peso-1.ogg");
        cargar("ganar-peso2", "sonidos/gano-peso-2.ogg");
        cargar("ganar-peso3", "sonidos/gano-peso-3.ogg");
        cargar("gastar-peso1", "sonidos/peso-gastado-1.ogg");
        cargar("gastar-peso2", "sonidos/peso-gastado-2.ogg");
        cargar("gano-mas-1", "sonidos/gano-mas-1.ogg");
        cargar("gano-mas-20", "sonidos/gano-mas-20.ogg");
        cargar("gano-mas-50", "sonidos/gano-mas-50.ogg");
        cargar("button-click", "sonidos/button-click.ogg");
        cargar("spin", "sonidos/spin.ogg");
        cargar("slide", "sonidos/slide.ogg");
        cargar("slide-inver", "sonidos/slide-inver.ogg");
    }

    private void cargar(String clave, String ruta) {
        try {
            Sound s = Gdx.audio.newSound(Gdx.files.internal(ruta));
            sonidos.put(clave, s);
        } catch (Exception e) {
            Gdx.app.error("GestorSonidos", "Error al cargar el sonido: " + ruta, e);
        }
    }

    public void reproducirSonidoClick() {
        reproducirConVariacion("button-click");
    }

    public void reproducir(String clave) {
        reproducir(clave, 1.0f, 1.0f);
    }

    public void reproducirConVariacion(String clave) {
        float pitchAleatorio = MathUtils.random(0.92f, 1.08f);
        reproducir(clave, 1.0f, pitchAleatorio);
    }

    public void reproducir(String clave, float volumenRelativo, float pitch) {
        if (silenciado) return;
        Sound s = sonidos.get(clave);
        if (s != null) {
            // FIX: todos los sonidos cargados hoy son efectos -> volumenGeneral * volumenEfectos
            float volumenFinal = volumenGeneral * volumenEfectos * volumenRelativo;
            s.play(volumenFinal, pitch, 0f);
        }
    }

    public void reproducirSonidoReparto() {
        String clave = alternarReparto ? "reparto1" : "reparto2";
        alternarReparto = !alternarReparto;
        reproducirConVariacion(clave);
    }

    public void reproducirSonidoGanarPeso() {
        int idx = MathUtils.random(1, 3);
        reproducirConVariacion("ganar-peso" + idx);
    }

    public void reproducirSonidoGastarPeso() {
        int idx = MathUtils.random(1, 2);
        reproducirConVariacion("gastar-peso" + idx);
    }

    public void reproducirSonidoFinalGanancia(int totalPesos) {
        if (totalPesos >= 50) reproducir("gano-mas-50");
        else if (totalPesos >= 20) reproducir("gano-mas-20");
        else if (totalPesos >= 1) reproducir("gano-mas-1");
    }

    public void setVolumenGeneral(float volumen) { this.volumenGeneral = MathUtils.clamp(volumen, 0f, 1f); }
    public float getVolumenGeneral() { return volumenGeneral; }
    public void setVolumenEfectos(float volumen) { this.volumenEfectos = MathUtils.clamp(volumen, 0f, 0.5f); }
    public float getVolumenEfectos() { return volumenEfectos; }
    public void setVolumenMusica(float volumen) { this.volumenMusica = MathUtils.clamp(volumen, 0f, 1f); } // reservado, sin música implementada aún
    public float getVolumenMusica() { return volumenMusica; }

    // FIX: alias viejo, por si algo más en el código todavía llama setVolumenGlobal
    public void setVolumenGlobal(float volumen) { setVolumenGeneral(volumen); }

    public void toggleMute() { this.silenciado = !this.silenciado; }

    @Override
    public void dispose() {
        for (Sound s : sonidos.values()) {
            if (s != null) s.dispose();
        }
        sonidos.clear();
    }
}
